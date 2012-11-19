package com.ecctm.networkwidget;

import java.text.DateFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class NetworkWidgetProvider extends AppWidgetProvider {

	// variables
	RemoteViews widgetView;
	ComponentName networkWidget;
	DateFormat timeFormat;
	public static String WIDGET_CLOCK_UPDATE = "com.ecctm.networkwidget.WIDGET_CLOCK_UPDATE";
	private final static String LOG = "com.ecctm.networkwidget";

	//createClockTickIntent
	private PendingIntent createClockTickIntent(Context context) {
		Intent intent = new Intent(WIDGET_CLOCK_UPDATE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}
	
	@Override
	public void onEnabled(Context context) {
		//LOG: Widget added or started
		Log.d(LOG, "NetworkWidgetProvider.onEnabled called.");
		super.onEnabled(context);
		
		//Add an AlarmManager to call for update every 3 minutes.
		Log.d(LOG, "Starting timer to update every 3 minutes.");
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MINUTE, 3);
		alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 180000, createClockTickIntent(context));
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// Log that onReceive was called
		Log.d(LOG, "NetworkWidgetProvider.onReceive called.");
		super.onReceive(context, intent);
		
		// Log that onReceive received an Intent
		Log.d(LOG, "NetworkWidgetProvider.onReceive received intent: " + intent);
		
		if(WIDGET_CLOCK_UPDATE.equals(intent.getAction())) {
			// Log that onReceive is calling for an Update
			Log.d(LOG, "NetworkWidgetProvider.onReceive is calling for an Update.");
			
			// Set up an AppWidgetManager
			ComponentName thisWidget = new ComponentName(context.getPackageName(), getClass().getName());
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			
			// Get the IDs of all instances of the widget
			int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

			// Call onUpdate to do the heavy lifting
			onUpdate(context, appWidgetManager, allWidgetIds);
		}
		
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// Log that onUpdate was called
		Log.i(LOG, "NetworkWidgetProvider.onUpdate called.");

		// Get the IDs of all instances of the widget
		ComponentName thisWidget = new ComponentName(context, NetworkWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

			// Build an intent to call UpdateWidgetService
			Intent intent = new Intent(context.getApplicationContext(),
					UpdateWidgetService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

			// update the widget using UpdateWidgetService
			context.startService(intent);
	}
	
	public void onDisabled(Context context) {
		//LOG: Widget removed or stopped
		Log.d(LOG, "NetworkWidgetProvider.onDisabled called.");
		super.onDisabled(context);
		
		//Stop the AlarmManager
		Log.d(LOG, "Turning off timer.");
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(createClockTickIntent(context));
	}
}