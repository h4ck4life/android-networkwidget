package com.eccproductions.networkwidget;

import java.text.DateFormat;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class NetworkWidgetProvider extends AppWidgetProvider {

	//variables
	RemoteViews widgetView;
	ComponentName networkWidget;
	DateFormat timeFormat;
	private final static String LOG = "com.eccproductions.networkwidget";
	
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		Log.i(LOG, "NetworkWidgetProvider.onEnabled Called");
		super.onEnabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		//Log that an update was called
		Log.i(LOG, "NetworkWidgetProvider.onUpdate called");
		
		// Get id's of ALL running versions of this widget
		ComponentName thisWidget = new ComponentName(context, NetworkWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		
		//Build an intent to call UpdateWidgetService
		Intent intent = new Intent(context.getApplicationContext(), UpdateWidgetService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
		
		//update the widget using UpdateWidgetService
		context.startService(intent);
	}
}