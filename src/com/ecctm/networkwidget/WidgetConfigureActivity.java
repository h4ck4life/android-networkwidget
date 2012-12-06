package com.ecctm.networkwidget;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class WidgetConfigureActivity extends Activity {

	// Variables
	private int widgetId;
	private int updateFrequencyMins;
	private static final String LOG = "com.ecctm.networkwidget";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Log that onCreate was called
		Log.d(LOG, "WidgetConfigureActivity.onCreate called.");
		super.onCreate(savedInstanceState);
		
		// Set our widgetId to an invalid (temporary) ID
		widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
		
		// Attempt to get the actual widgetId
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		if(extras != null)
		{
			// extras had something, so set it.
			widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		if(widgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
		{
			// extras returned a useless ID, we're bailing out.
			finish();
		}
		
		// In case the user presses BACK, Make not adding a widget the default
		setResult(RESULT_CANCELED);
		
		// Call the layout so our configure activity is usable
		setContentView(R.layout.widget_configure_activity_layout);
		
		//Set things up for our update frequency seekBar
		updateFrequencyMins = 30;
		final TextView frequencySeekResult = (TextView)findViewById(R.id.widget_configure_activity_setting_frequency_seekBar_result);
		final SeekBar frequencySeek = (SeekBar)findViewById(R.id.widget_configure_activity_setting_frequency_seekBar);
		
		//listen to our seekbar
		frequencySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// We range from 1 to 60 minutes (0-59 then +1)
				updateFrequencyMins = progress + 1;
				frequencySeekResult.setText(updateFrequencyMins + " Minutes");
			}
		});
	}

	public void onCancel(View source) {
		// Log that onCancel was called
		Log.d(LOG, "WidgetConfigureActivity.onCancel called.");

		// Set our result to canceled
		setResult(RESULT_CANCELED);
		// Bail out of this
		finish();
	}
	
	public void onRadioButtonClicked(View view) {
		// Log that onRadioButtonClicked was called
		Log.d(LOG, "WidgetConfigureActivity.onRadioButtonClicked called.");

		//load up the preferences list
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editPreferences = preferences.edit();
		
		// Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.widget_configure_activity_setting_background_radio_dark:
	            if (checked){
	            	// We've selected a Dark widget background
	        		Log.d(LOG, "WidgetConfigureActivity.onRadioButtonClicked - Dark Widget");

	            	editPreferences.putInt(WidgetProvider.WIDGET_BACKGROUND_VALUE+"_"+widgetId, 0);
	            }
	            break;
	        case R.id.widget_configure_activity_setting_background_radio_light:
	            if (checked) {
	                // We've selected a Light widget background
	        		Log.d(LOG, "WidgetConfigureActivity.onRadioButtonClicked - Light Widget");
	        		
	        		editPreferences.putInt(WidgetProvider.WIDGET_BACKGROUND_VALUE+"_"+widgetId, 1);
	            }
	            break;
	    }
	    //close and save our preferences now we're done with them
		editPreferences.commit();
	}
	
	public void onConfirm(View source) {
		// Log that onConfirm was called
		Log.d(LOG, "WidgetConfigureActivity.onConfirm called.");
		
		//Store our configuration
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editPreferences = preferences.edit();
		/*
		Add config changes here - not unlike this:
		editPreferences.putInt(WidgetProvider.EXTRA_COLOR_VALUE+"_"+widgetID, color);
		*/
		editPreferences.commit();
		
		// Call for the widgets' first update
		Log.d(LOG, "WidgetConfigureActivity.onConfirm - Preparing first onUpdate broadcast.");
		
		// Setup for creating our onUpdate broadcast
		final Context context = WidgetConfigureActivity.this;
		//AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		//ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetConfigureActivity.class.getName());
		//int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
		
		// Create and package our broadcast
		Intent firstUpdate = new Intent(context, WidgetProvider.class);
		firstUpdate.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		firstUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		
		// Send our first onUpdate broadcast
		context.sendBroadcast(firstUpdate);
		Log.d(LOG, "WidgetConfigureActivity.onConfirm - Sent first onUpdate broadcast.");
		
		// TODO Make our AlarmManager
		
		//Create and launch the alarmManager
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.appendPath("" + widgetId);
		Uri uri = uriBuilder.build();
		
		// Create our alarmUpdate Intent
		Intent alarmUpdate = new Intent(context, WidgetProvider.class);
		//Set an action so we can filter it
		alarmUpdate.setAction(WidgetProvider.UPDATE_ONE);
		//add the instance to identify it later
		alarmUpdate.setData(uri);
		
		// Add the widget's identifiers to the HashMap list
		WidgetProvider.addUri(widgetId, uri);
		
		alarmUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		
		//create the alarm info
		PendingIntent pendingAlarmUpdate = PendingIntent.getBroadcast(WidgetConfigureActivity.this, 0, alarmUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
		
		//Create Alarm
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis()+((updateFrequencyMins*60)*1000), ((updateFrequencyMins*60)*1000), pendingAlarmUpdate);
		
		// Log that onConfirm was called
		Log.d(LOG, "WidgetConfigureActivity.onConfirm - Created alarm.");
		Log.d(LOG, "WidgetConfigureActivity.onConfirm - ACTION: " + WidgetProvider.UPDATE_ONE + ", URI: " + uriBuilder.build().toString() + ", Minutes: " + updateFrequencyMins);

		// Return the original widgetId, found in onCreate()
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}

}
