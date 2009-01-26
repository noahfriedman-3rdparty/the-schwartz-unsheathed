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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.*;
import android.widget.SeekBar;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.hardware.SensorManager;

public class TheSchwartz extends Activity {
	public static final String PREFS_NAME = "SchwartzPrefsFile";

	private SensorManager mSensorManager = null;
	private GraphView mGraphView;
	private Menu mMenu;
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
        mFirstRun = settings.getBoolean("firstRun", true);
        float sense = settings.getFloat("sensitivity", 5.0f);
        int red = settings.getInt("redCustom", 255);
        int green = settings.getInt("greenCustom", 255);
        int blue = settings.getInt("blueCustom", 255);
        mGraphView.setSabreColor(color);
        mGraphView.setBgVisible(bgVisible);
        mGraphView.setSenseOffset(sense);
        mGraphView.setCustomColor(red, green, blue);
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
    	editor.putBoolean("firstRun", mFirstRun);
    	editor.putFloat("sensitivity", mGraphView.getSenseOffset());
    	int color = mGraphView.getCustomColor();
    	editor.putInt("redCustom", Color.red(color));
    	editor.putInt("greenCustom", Color.green(color));
    	editor.putInt("blueCustom", Color.blue(color));
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
		mMenu.add(2, 3, 2, "Sensitivity");
		mMenu.add(2, 4, 2, "Custom Color");
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
			} else
				mGraphView.toggleZoom();
			return true;
		case 1:
			mGraphView.toggleBackground();
			return true;
		case 3:
            LayoutInflater factory = LayoutInflater.from(this);
            final View dv = factory.inflate(R.layout.sensedialog, null);
            float sensitivity = 15.0f - mGraphView.getSenseOffset();
            new AlertDialog.Builder(TheSchwartz.this)
                .setTitle("Adjust Sensitivity")
                .setIcon(R.drawable.icon)
                .setView(dv)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
    
                        /* User clicked OK so do some stuff */
                    	SeekBar sb = (SeekBar) dv.findViewById(R.id.sensitivity);
                    	float sense = 15 - sb.getProgress();
                    	mGraphView.setSenseOffset(sense);
                    	Toast.makeText(TheSchwartz.this, "Sensitivty set to "+(int)sense, Toast.LENGTH_SHORT);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked cancel so do some stuff */
                    }
                })
                .create()
                .show();
            SeekBar sb = (SeekBar) dv.findViewById(R.id.sensitivity);
            sb.setProgress((int)sensitivity);
            return true;
		case 4:
            factory = LayoutInflater.from(this);
            final View v = factory.inflate(R.layout.color_dialog, null);
            new AlertDialog.Builder(TheSchwartz.this)
                .setTitle("Color Picker")
                .setIcon(R.drawable.icon)
                .setView(v)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
    
                        /* User clicked OK so do some stuff */
                    	SeekBar sb = (SeekBar) v.findViewById(R.id.redColor);
                    	int red = sb.getProgress();
                    	sb = (SeekBar) v.findViewById(R.id.greenColor);
                    	int green = sb.getProgress();
                    	sb = (SeekBar) v.findViewById(R.id.blueColor);
                    	int blue = sb.getProgress();
                    	
                    	mGraphView.setCustomColor(red, green, blue);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked cancel so do some stuff */
                    }
                })
                .create()
                .show();
            	
            	int color = mGraphView.getCustomColor();
            	
            	SeekBar seekBar = (SeekBar) v.findViewById(R.id.redColor);
            	GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.BL_TR,
    				new int[] { 0x00000000, 0xFFFF0000 });
            	seekBar.setProgressDrawable(gd);
            	seekBar.setProgress(Color.red(color));
            
            	seekBar = (SeekBar) v.findViewById(R.id.greenColor);
            	gd = new GradientDrawable(GradientDrawable.Orientation.BL_TR,
    				new int[] { 0x00000000, 0xFF00FF00 });
            	seekBar.setProgressDrawable(gd);
            	seekBar.setProgress(Color.green(color));

            	seekBar = (SeekBar) v.findViewById(R.id.blueColor);
            	gd = new GradientDrawable(GradientDrawable.Orientation.BL_TR,
    				new int[] { 0x00000000, 0xFF0000FF });
            	seekBar.setProgressDrawable(gd);
            	seekBar.setProgress(Color.blue(color));
            	
            return true;
		}
		return false;
	}
}