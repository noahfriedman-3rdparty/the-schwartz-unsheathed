/****************************************************************************

    Copyright 2008, 2009  Clark Scheff
    
    This file is part of The Schwartz Unsheathed.

    The Schwartz Unsheathed is free software: you can redistribute it and/or
    modify it under the terms of the GNU General Public License as published
    by the Free Software Foundation version 3 of the License.

    AndroidBreakout is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with The Schwartz Unsheathed. If not, see http://www.gnu.org/licenses
    
****************************************************************************/

package com.android.app.schwarz;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import android.hardware.SensorManager;

public class TheSchwartz extends Activity {
	public static final String PREFS_NAME = "SchwartzPrefsFile";

	private SensorManager mSensorManager = null;
	private GraphView mGraphView;
	private Menu mMenu;
	private boolean mSensitive = false;
	private boolean mFirstRun = true;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGraphView = new GraphView(this, mSensorManager);
        setContentView(mGraphView);

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int color = settings.getInt("sabreColor", 0);
        boolean bgVisible = settings.getBoolean("bgVisible", true);
        mSensitive = settings.getBoolean("sensitive", false);
        mFirstRun = settings.getBoolean("firstRun", true);
        mGraphView.setSabreColor(color);
        mGraphView.setBgVisible(bgVisible);
        mGraphView.setSensitivity(mSensitive);
    }

    @Override
    protected void onStart() {
    	super.onStart();
    	mGraphView.invalidate();
    	if(true == mFirstRun) {
    		Toast.makeText(this,R.string.schwartz, Toast.LENGTH_SHORT).show();
    		mFirstRun = false;
    	}
    }

    @Override
    protected void onStop() {
    	super.onStop();
   	   	// Save user preferences. We need an Editor object to
   	   	// make changes. All objects are from android.context.Context
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putInt("sabreColor", mGraphView.getSabreColor());
    	editor.putBoolean("bgVisible", mGraphView.getBgVisible());
    	editor.putBoolean("sensitive", mGraphView.getSensitivity());
    	editor.putBoolean("firstRun", mFirstRun);
    	// Don't forget to commit your edits!!!
    	editor.commit();
    	mGraphView.onStop();
    }
    
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }
    // Called only the first time the options menu is displayed.
	// Create the menu entries.
	// Menu adds items in the order shown.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
     
		// Parameters for menu.add are:
		// group -- Not used here.
		// id -- Used only when you want to handle and identify the click yourself.
		// title
		mMenu = menu;
		mMenu.add(0, 0, 0, R.string.zoom_toggle);
		mMenu.add(1, 1, 0, R.string.bg_toggle);
		if(false == mSensitive)
			mMenu.add(1, 2, 1, R.string.high_sensitivity);
		else
			mMenu.add(1, 2, 1, R.string.low_sensitivity);
//		menu.add(1, 2, 1, "Toggle Hum");
		return true;
	}

 	// Activity callback that lets your handle the selection in the class.
	// Return true to indicate that you've got it, false to indicate
	// that it should be handled by a declared handler object for that
	// item (handler objects are discouraged for reasons of efficiency).
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case 0:
			if(false == mGraphView.getSabreOut()) {
				Toast.makeText(this, R.string.sabre_out_warning, Toast.LENGTH_SHORT).show();
//				new AlertDialog.Builder(this)
//                .setMessage(R.string.sabre_out_warning)
//                .show();
			} else
				mGraphView.toggleZoom();
			return true;
		case 1:
			mGraphView.toggleBackground();
			return true;
		case 2:
			mSensitive = !mSensitive;
			mMenu.removeItem(2);
			mGraphView.toggleSensitivity();
			mSensitive = mGraphView.getSensitivity();
			
			if(false == mSensitive) {
				mMenu.add(1, 2, 1, R.string.high_sensitivity);
				Toast.makeText(this, R.string.low_active, Toast.LENGTH_SHORT).show();
			}
			else {
				mMenu.add(1, 2, 1, R.string.low_sensitivity);
				Toast.makeText(this, R.string.high_active, Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		return false;
	}
}