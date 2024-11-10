/** @file ExportGeo.java
 *
 * @author marco corvi
 * @date nov 2011
 *
 * @brief general GEO info for georeferenced exports
 * --------------------------------------------------------
 *  Copyright This software is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.topodroid.c3out;

import com.topodroid.utils.TDLog;
import com.topodroid.TDX.TglParser;
import com.topodroid.TDX.Vector3D;
import com.topodroid.TDX.Cave3DStation;
import com.topodroid.TDX.Cave3DFix;
import com.topodroid.mag.Geodetic;

import java.util.Locale;
import java.util.List;
import java.util.ArrayList;

public class ExportGeo
{
  private Cave3DFix origin = null;
  private double lat, lng, h_geo;
  private double s_radius, e_radius;
  private Cave3DStation zero;

  double mConv = 0; 

  public boolean hasGeo = false;

  /** get E coord 
   * @param st station
   */
  double getE( Vector3D st ) { return hasGeo? lng + (st.x - zero.x) * e_radius : st.x; }

  /** get E coord without convergence
   * @param st station
   *
   *    E=x+Dy*C
   *              x=0  
   *           :  /       Dy*C > 0
   *       x<0 : /  x>0
   *           :/
   *   --------0----------------
   *          /:   Dy*C < 0
   */
  double getENC( Vector3D st )
  { 
    if ( ! hasGeo ) return st.x;
    double x = (st.x - zero.x) + (st.y - zero.y) * mConv;
    return lng + x * e_radius;
  }

  /** get N coord 
   * @param st station
   */
  double getN( Vector3D st ) { return hasGeo? lat + (st.y - zero.y) * s_radius : st.y; }

  /** get N coord without convergence
   * @param st station
   */
  double getNNC( Vector3D st ) 
  { 
    if ( ! hasGeo ) return st.y;
    double y = (st.y - zero.y) - (st.x - zero.x) * mConv;
    return lat + y * s_radius;
  }

  /** get Z coord 
   * @param st station
   */
  double getZ( Vector3D st ) { return hasGeo? h_geo + (st.z - zero.z) : st.z; }

  /** ???
   * @param data        data parser
   * @param decl        magnetic declination (unused)
   * @param use_conv    whether to apply meridian convergence
   * @note always called with use_conv=false
   */
  protected boolean getGeolocalizedData( TglParser data, double decl, boolean use_conv )
  {
    // TDLog.v( "KML get geo-localized data. Declination " + decl );
    List< Cave3DFix > fixes = data.getFixes();
    if ( fixes.size() == 0 ) {
      // TDLog.v( "KML no geo-localization");
      return false;
    }

    origin = null;
    for ( Cave3DFix fix : fixes ) {
      if ( ! fix.hasWGS84 ) continue;
      // if ( fix.cs == null ) continue;
      // if ( ! fix.cs.name.equals("long-lat") ) continue;
      for ( Cave3DStation st : data.getStations() ) {
        if ( st.getFullName().equals( fix.getFullName() ) ) {
          origin = fix;
          zero   = st;
          break;
        }
      }
      if ( origin != null ) break;
    }
    if ( origin == null ) {
      // TDLog.v( "KML no geolocalized origin");
      return false;
    }

    // origin has coordinates ( e, n, z ) these are assumed lat-long
    // altitude is assumed wgs84
    lat = origin.latitude;
    lng = origin.longitude;
    mConv = use_conv ? Geodetic.meridianConvergenceFactor( origin.latitude ) : 0.0;
    double h_ell = origin.a_ellip;
    h_geo = origin.z; // KML uses Geoid altitude (unless altitudeMode is set)
    // TDLog.v( "KML origin " + lat + " N " + lng + " E " + h_geo );

    s_radius = 1.0 / Geodetic.meridianRadiusExact( lat, h_ell );
    e_radius = 1.0 / Geodetic.parallelRadiusExact( lat, h_ell );
// FIXME_ELLIPSOID
    // s_radius = 1.0 / Geodetic.meridianRadiusEllipsoid( lat, h_ell );
    // e_radius = 1.0 / Geodetic.parallelRadiusEllipsoid( lat, h_ell );
    hasGeo = true;
    return true;
  }

}
