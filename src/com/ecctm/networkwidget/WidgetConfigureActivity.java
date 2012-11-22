package com.ecctm.networkwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class WidgetConfigureActivity extends Activity {

	// Variables
	private int widgetId;
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
	}

	public void onCancel(View source) {
		// Log that onCancel was called
		Log.d(LOG, "WidgetConfigureActivity.onCancel called.");

		// Set our result to canceled
		setResult(RESULT_CANCELED);
		// Bail out of this
		finish();
	}
	
	
	public void onConfirm(View source) {
		// Log that onConfirm was called
		Log.d(LOG, "WidgetConfigureActivity.onConfirm called.");
		
		// Call for the widgets' first update
		Log.d(LOG, "WidgetConfigureActivity.onConfirm - Preparing first onUpdate broadcast.");
		
		// Setup for creating our onUpdate broadcast
		final Context context = WidgetConfigureActivity.this;
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetConfigureActivity.class.getName());
		
		// Create and package our broadcast
		Intent firstUpdate = new Intent(context, WidgetProvider.class);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
		firstUpdate.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		firstUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		
		// Send our first onUpdate broadcast
		context.sendBroadcast(firstUpdate);
		Log.d(LOG, "WidgetConfigureActivity.onConfirm - Sent first onUpdate broadcast.");
		
		// Return the original widgetId, found in onCreate()
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}

}
