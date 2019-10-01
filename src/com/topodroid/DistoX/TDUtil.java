/* @file TDUtil.java
 *
 * @author marco corvi
 * @date jan 2014
 *
 * @grief numerical utilities
 * --------------------------------------------------------
 *  Copyright This software is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.topodroid.DistoX;

// import java.lang.Math;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Calendar;
import java.util.GregorianCalendar;

import java.io.File;

import android.media.AudioManager;
import android.media.ToneGenerator;

import android.content.Context;
import android.os.Vibrator;

// import android.util.Log;

class TDUtil
{
  // static final float M_PI  = 3.1415926536f; // Math.PI;
  // static final float M_2PI = 6.283185307f;  // 2*Math.PI;
  // static final float M_PI2 = M_PI/2;        // Math.PI/2
  // static final float M_PI4 = M_PI/4;        // Math.PI/4
  // static final float M_PI8 = M_PI/8;        // Math.PI/8
  // static final float RAD2DEG = (180.0f/M_PI);
  // static final float DEG2RAD = (M_PI/180.0f);

  static final long ZERO = 32768;
  static final long NEG  = 65536;
  static final float FV = 24000.0f;
  static final float FM = 16384.0f; // 2^14
  static final float FN = 2796f;    // 2^26 / FV

  static final float DEG2GRAD = 400.0f/360.0f;
  static final float GRAD2DEG = 360.0f/400.0f;

  static final float M2FT = 3.28084f; // meters to feet 
  static final float FT2M = 0.3048f;
  static final float IN2M = 0.0254f;
  static final float YD2M = 0.9144f;


  // static float abs( float x ) { return (float)( Math.abs(x) ); }
  // static float atan2( float y, float x ) { return (float)( Math.atan2( y, x ) ); }
  // static float acos( float x ) { return (float)( Math.acos( x ) ); }

  // static float around( float f, float f0 ) 
  // {
  //   if ( f - f0 > 180 ) return f - 360;
  //   if ( f0 - f > 180 ) return f + 360;
  //   return f;
  // }

  // static float degree2slope( float deg ) { return (float)(100 * Math.tan( deg * DEG2RAD ) ); }

  // static float slope2degree( float slp ) { return (float)( Math.atan( slp/100 ) * RAD2DEG ); }

  static void deleteFile( File f )
  {
    if ( f != null && f.exists() ) {
      if ( ! f.delete() ) TDLog.Error("File delete failed " + f.getName() );
    }
  }

  static void deleteDir( File dir )
  {
    if ( dir != null && dir.exists() ) {
      File[] files = dir.listFiles();
      if ( files != null ) {
        for (File file : files ) {
          if (file.isFile()) {
            if ( ! file.delete() ) TDLog.Error("File delete failed " + file.getName() ); 
          }
        }
      }
      if ( ! dir.delete() ) TDLog.Error("Dir delete failed " + dir.getName() );
    }
  }

  static void deleteFile( String pathname ) { deleteFile( new File( pathname ) ); }

  static void deleteDir( String dirname ) { deleteDir( new File( dirname ) ); }

  static void renameFile( String oldname, String newname )
  {
    File f1 = new File( oldname );
    File f2 = new File( newname );
    if ( f1.exists() && ! f2.exists() ) {
      if ( ! f1.renameTo( f2 ) ) TDLog.Error("File rename failed " + oldname + " " + newname );
    } 
  }

  static void moveFile( String oldname, String newname )
  {
    File f1 = new File( oldname );
    File f2 = new File( newname );
    if ( f1.exists() ) {
      if ( ! f1.renameTo( f2 ) ) TDLog.Error("File move failed " + oldname + " " + newname );
    } 
  }

  static void makeDir( String pathname )
  {
    File f = new File( pathname );
    if ( f.exists() ) return;
    if ( ! f.isDirectory() ) {
      if ( ! f.mkdirs() ) TDLog.Error("Mkdir failed " + pathname );
    }
  }
  
  // concatenate strings using a single-space separator
  // empty strings are skipped
  static String concat( String[] vals, int k )
  {
    if ( k < vals.length ) {
      StringBuilder sb = new StringBuilder();
      for ( ; k<vals.length; ++k ) if ( vals[k].length() > 0 ) {
        sb.append(vals[k]);
        break;
      }
      for (++k; k < vals.length; ++k) {
        if ( vals[k].length() > 0 ) sb.append(" ").append(vals[k]);
      }
      return sb.toString();
    }
    return "";
  }

  static String noSpaces( String s )
  {
    return ( s == null )? null 
      : s.trim().replaceAll("\\s+", "_").replaceAll("/", "-").replaceAll("\\*", "+").replaceAll("\\\\", "");
  }

  static String dropSpaces( String s )
  {
    return ( s == null )? null 
      : s.trim().replaceAll("\\s+", "");
  }

  static String currentDate()
  {
    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy.MM.dd", Locale.US );
    return sdf.format( new Date() );
  }

  static String currentDateTime()
  {
    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy.MM.dd-hh:mm", Locale.US );
    return sdf.format( new Date() );
  }


  static String getDateString( String format )
  {
    SimpleDateFormat sdf = new SimpleDateFormat( format, Locale.US );
    return sdf.format( new Date() );
  }

  static float getDatePlg( )
  {
    Calendar c = new GregorianCalendar();
    int y = c.get( Calendar.YEAR );
    int m = 1 + c.get( Calendar.MONTH );
    int d = c.get( Calendar.DAY_OF_MONTH );
    return getDatePlg( y, m, d );
  }

  static final private int[] mDaysByMonth = { 0, 31, 59, 90, 120, 151, 181,  212, 243, 273, 284, 294, 324 };
  // m: 1 .. 12
  static float getDatePlg( int y, int m, int d )
  {
    int days = 100 * 365 + 24;
    while ( y > 2000 ) {
      days += 365;
      if ( (y % 4) == 0 ) ++ days;
      -- y;
    }
    days += mDaysByMonth[ m ] + d;
    return days;
  }


  static int dateParseYear( String date )
  {
    try {
      return Integer.parseInt( date.substring(0, 4) );
    } catch ( NumberFormatException e ) { }
    return 2000;
  }

  static int dateParseMonth( String date )
  {
    int ret = 0;
    if ( date.charAt(5) == '1' ) ret += 10;
    char ch = date.charAt(6);
    if ( ch >= '0' && ch <= '9' ) ret += (ch - '0');
    return (ret > 0)? ret-1 : 0;
  }

  static int dateParseDay( String date )
  {
    int ret = 0;
    char ch = date.charAt(8);
    if ( ch >= '1' && ch <= '3' ) ret += 10*(ch - '0');
    ch = date.charAt(9);
    if ( ch >= '1' && ch <= '9' ) ret += (ch - '0');
    return Math.max(ret, 0);
  }

  static String composeDate( int y, int m, int d )
  {
    return String.format(Locale.US, "%04d.%02d.%02d", y, m+1, d );
  }

  static int year()  { return (new GregorianCalendar()).get( Calendar.YEAR ); }
  static int month() { return (new GregorianCalendar()).get( Calendar.MONTH ); }
  static int day()   { return (new GregorianCalendar()).get( Calendar.DAY_OF_MONTH); }


  static boolean slowDown( int msec ) 
  {
    try {
      Thread.sleep( msec );
    } catch ( InterruptedException e ) { return false; }
    return true;
  }

  static boolean slowDown( int msec, String msg )
  {
    try {
      Thread.sleep( msec );
    } catch ( InterruptedException e ) {
      TDLog.Error( msg + " " + e.getMessage() );
      return false;
    }
    return true;
  }

  static boolean yieldDown( int msec ) 
  {
    try {
      Thread.yield();
      Thread.sleep( msec );
    } catch ( InterruptedException e ) { return false; }
    return true;
  }

  static void ringTheBell( int duration )
  {
    // Log.v("DistoXX", "bell ...");
    // ToneGenerator toneG = new ToneGenerator( AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME );
    ToneGenerator toneG = new ToneGenerator( AudioManager.STREAM_ALARM, TDSetting.mBeepVolume );
    // for ( int i=0; i<2; ++i ) {
      toneG.startTone( ToneGenerator.TONE_PROP_PROMPT, duration ); 
      // TDUtil.slowDown( duration );
    // }
  }

  static void vibrate( Context ctx, int duration )
  {
    Vibrator vibrator = (Vibrator)ctx.getSystemService( Context.VIBRATOR_SERVICE );
    try {
      vibrator.vibrate(duration);
    } catch ( NullPointerException e ) {
      // TODO
    }
  }

}
