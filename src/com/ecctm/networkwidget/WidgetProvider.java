package com.ecctm.networkwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WidgetProvider extends AppWidgetProvider {

	//Variables
	private static final String LOG = "com.ecctm.networkwidget";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// Log that onUpdate was called
		Log.d(LOG, "WidgetProvider.onUpdate called.");
		
		for(int widgetId : appWidgetIds) 
		{
			// For each widgetId instance, call for an update
			updateAppWidget(context, appWidgetManager, widgetId);
		}
	}

	private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		// Log that updateAppWidget was called
		Log.d(LOG, "WidgetProvider.updateAppWidget called.");
				
		// Get the IDs of all instances of the widget
		ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		
		// Build an intent to call WidgetUpdateService
		Intent intent = new Intent(context.getApplicationContext(), WidgetUpdateService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
		
		// Update the widget using WidgetUpdateService
		context.startService(intent);
	}

}
