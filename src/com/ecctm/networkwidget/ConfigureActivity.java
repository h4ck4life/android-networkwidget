package com.ecctm.networkwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ConfigureActivity extends Activity {

	// Variables
	private int widgetID;
	private final static String LOG = "com.ecctm.networkwidget";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//Log that our ConfigureActivity was called
		Log.d(LOG, "ConfigureActivity.onCreate called.");
		super.onCreate(savedInstanceState);
		
		//set our widgetID to a fail-safe before we attempt to retrieve it
		widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
		
		//Get our widgetID
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null)
		{
			//extras must have an ID, so assign it
			widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		if(widgetID == AppWidgetManager.INVALID_APPWIDGET_ID)
		{
			//Log that our widgetID was invalid
			Log.d(LOG, "widgetID passed to ConfigureActivity invalid - quitting.");
			
			//our id is invalid, lets bail
			finish();
		}
		//If the user presses BACK, don't add a widget.
		setResult(RESULT_CANCELED);
		
		// set our ContentView
		setContentView(R.layout.config_layout);
	}
	
	public void onBtnConfirm(View source) {
		//Log that btn_configure_confirm was pressed
		Log.d(LOG, "ConfigureActivity.onBtnConfirm called.");
		
		//setup to pass our first widget update
		final Context context = ConfigureActivity.this;
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName thisAppWidget = new ComponentName(context.getPackageName(), ConfigureActivity.class.getName());
		Intent firstUpdate = new Intent(context, NetworkWidgetProvider.class);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
		
		//package and send our first update call
		firstUpdate.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		firstUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		context.sendBroadcast(firstUpdate);

		//Log that we've sent our firstUpdate
		Log.d(LOG, "ConfigureActivity.onBtnConfirm - firstUpdate sent.");
		
		//return our original widgetID
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
		setResult(RESULT_OK, resultValue);
		finish();
	}
	
}
