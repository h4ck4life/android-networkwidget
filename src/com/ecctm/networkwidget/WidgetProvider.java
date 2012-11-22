package com.ecctm.networkwidget;

import java.util.HashMap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class WidgetProvider extends AppWidgetProvider {

	//Variables
	private static final String LOG = "com.ecctm.networkwidget";
	public static final String UPDATE_ONE = "com.ecctm.networkwidget.UPDATE_ONE_WIDGET";
	private static HashMap<Integer, Uri> uris = new HashMap<Integer, Uri>();
	
	@Override
	public void onEnabled(Context context) {
		// Log that onEnabled was called
		Log.d(LOG, "WidgetProvider.onEnabled called.");
		super.onEnabled(context);
		
		// TODO Add an AlarmManager
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// Log that onReceive was called
		Log.d(LOG, "WidgetProvider.onReceive called.");
		
		// Get the action passed to onReceive
		String action = intent.getAction();
		// Log that onReceive got passed an action
		Log.d(LOG, "WidgetProvider.onReceive received action: " + action);
		
		//Check for UPDATE_ONE_WIDGET, APPWIDGET_UPDATE or APPWIDGET_TIMED_UPDATE
		if(action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) || action.equals(UPDATE_ONE)) {
			//check for a single widgetId
			int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			
			//if there is no single widgetId, call the super implementation to deal with it as normal
			if(widgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
				super.onReceive(context, intent);
			}
			//Otherwise, call onUpdate with just our singular widgetId
			else {
				this.onUpdate(context, AppWidgetManager.getInstance(context), new int[]{widgetId});
			}
		}
		// Handle other actions like normal
		else {
			super.onReceive(context, intent);
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// Log that onUpdate was called
		Log.d(LOG, "WidgetProvider.onUpdate called.");
		Log.d(LOG, "WidgetProvider.onUpdate - Number of widgetIds: " + appWidgetIds.length);
		
		for(int widgetId : appWidgetIds) {
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
	
	@Override
	public void onDisabled(Context context) {
		// Log that onDisabled was called
		Log.d(LOG, "WidgetProvider.onDisabled called.");
		super.onDisabled(context);

		// TODO Remove our AlarmManager
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// Log that onDeleted was called
		Log.d(LOG, "WidgetProvider.onDeleted called.");
		super.onDeleted(context, appWidgetIds);

		for(int widgetId : appWidgetIds) {
			// For each widgetId instance, cancel its AlarmManager
			cancelAlarmManager(context, widgetId);
		}

		
	}

	protected void cancelAlarmManager(Context context, int widgetId) {
		// Log that cancelAlarmManager was called
		Log.d(LOG, "WidgetProvider.cancelAlarmManager called.");
				
		// TODO Remove AlarmManager associated with the deleted widget(s)
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent alarmIntent = new Intent(context, WidgetConfigureActivity.class);
		
		//AlarmManager is identified by intent's action and uri
		alarmIntent.setAction(UPDATE_ONE);

		//if you don't put the uri, it will cancel ALL the AlarmManagers with action UPDATE_ONE
		alarmIntent.setData(uris.get(widgetId));
		alarmIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		//recreate the alarm data
		PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		//cancel the alarm
		alarmManager.cancel(pendingAlarmIntent);
		//Log that the alarm was cancelled
		Log.d(LOG, "WidgetProvider.cancelAlarmManager - cancelled alarm.");
		Log.d(LOG, "WidgetProvider.cancelAlarmManager - ACTION: " + UPDATE_ONE + ", URI: " + uris.get(widgetId));

		//remove the now useless uri
		uris.remove(widgetId);
	}
	
	public static void addUri(int id, Uri uri) {
		uris.put(new Integer(id), uri);
	}

}
