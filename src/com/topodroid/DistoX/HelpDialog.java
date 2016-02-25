/** @file HelpDialog.java
 *
 * @author marco corvi
 * @date nov 2013
 *
 * @brief TopoDroid help dialog 
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.topodroid.DistoX;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Dialog;
// import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.view.Window;

// import android.graphics.*;
import android.view.View;
import android.widget.Button;

import android.widget.AdapterView;
// import android.widget.AdapterView.OnItemClickListener;

// import android.widget.TextView;
import android.widget.ListView;

import android.view.View;
import android.view.View.OnClickListener;

import android.util.Log;

class HelpDialog extends Dialog
                 implements OnClickListener
{
  private Context mContext;
  private ListView    mList;
  private HelpAdapter mAdapter;

  private int mIcons[];
  private int mMenus[];
  private int mIconTexts[];
  private int mMenuTexts[];
  private int mNr0;
  private int mNr1;

  private Button mBtnManual;

  // TODO list of help entries
  HelpDialog( Context context, int icons[], int menus[], int texts1[], int texts2[], int n0, int n1 )
  {
    super( context );
    mContext = context;
    mIcons = icons;
    mMenus = menus;
    mIconTexts = texts1;
    mMenuTexts = texts2;
    mNr0 = n0;
    mNr1 = n1; // offset of menus
    // Log.v("DistoX", "HELP buttons " + mNr0 + " menus " + mNr1 );
  }

  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );

    setContentView(R.layout.help_dialog);
    setTitle( mContext.getResources().getString( R.string.HELP ) );

    mBtnManual = (Button) findViewById( R.id.button_manual );
    mBtnManual.setOnClickListener( this );

    mList = (ListView) findViewById(R.id.help_list);
    // mList.setOnItemClickListener( this );
    mList.setDividerHeight( 2 );

    // Log.v( TopoDroidApp.TAG, "HelpDialog ... createAdapters" );
    createAdapter();
    mList.setAdapter( mAdapter );
    mList.invalidate();
  }

  void createAdapter()
  {
    // Log.v("DistoX", "HELP create adapter");
    mAdapter = new HelpAdapter( mContext, this, R.layout.item, new ArrayList<HelpEntry>() );
    // int np = mIcons.length;
    for ( int i=0; i<mNr0; ++i ) {
      mAdapter.add( new HelpEntry( mContext, mIcons[i], mIconTexts[i], false ) );
    }
    if ( mMenus != null ) {
      // int nm = mMenus.length;
      for ( int i=0; i<mNr1; ++i ) {
        mAdapter.add( new HelpEntry( mContext, mMenus[i], mMenuTexts[i], true ) );
      }
    }
  }

  @Override 
  public void onClick( View v ) 
  {
    dismiss();
    mContext.startActivity( new Intent( Intent.ACTION_VIEW ).setClass( mContext, UserManualActivity.class ) );
  }

}

