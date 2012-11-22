package com.ecctm.networkwidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

/*TODO:
 * 1) Set Network Logo from a separate .java file, to allow easy adding of new networks.
 * 2) Data Transfer info?
 * 	2a) Check if Wireless is enabled
 * 	2b) Get signal coverage & display on the right
 * 	2c) Get up/down speeds & display
*/

public class WidgetUpdateService extends Service {

	// Variables
	int networkId;
	String networkName;
	private static final String LOG = "com.ecctm.networkwidget";

	@Override
	public void onStart(Intent intent, int startId) {
		// Begin Logging
		Log.d(LOG, "WidgetUpdateService.onStart called.");
		//super.onStart(intent, startId);
		
		// Set up our widget manager
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

		// get ids for ALL running instances of our widget
		int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

		// target this widget
		ComponentName networkWidget = new ComponentName(getApplicationContext(), WidgetProvider.class);

		// get ids for ALL running instances of our widget
		int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(networkWidget);

		// Log these ids
		Log.d(LOG, "Intent " + String.valueOf(allWidgetIds.length));
		Log.d(LOG, "Direct " + String.valueOf(allWidgetIds2.length));

		// begin our update
		for (int widgetId : allWidgetIds) {
			// set up TelephonyManager to get our network info
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

			//Inflate the widget's layout
			RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widget_network_layout);

			//Check AirplaneMode
			if(!isAirplaneModeOn(this)) {
				// Its off, we have networks, so...
				
				// Get info about currently connected network
				networkId = Integer.parseInt(telephonyManager.getNetworkOperator());
				networkName = telephonyManager.getNetworkOperatorName();
				Log.w(LOG, "Get Network Info - MobileOperatorID: " + networkId);
				Log.w(LOG, "Get Network Info - MobileOperatorName: " + networkName);

				// Set the text
				remoteViews.setTextViewText(R.id.widget_network_layout_network_title, networkName);

				// Set the mobile Logo
				switch (networkId) {
				case 27201:
					// Vodafone IE
					remoteViews.setImageViewResource(R.id.widget_network_layout_network_logo,
							R.drawable.image_network_vodafone);
					break;
				case 27202:	
					// o2 IE
					remoteViews.setImageViewResource(R.id.widget_network_layout_network_logo,
							R.drawable.image_network_o2);
					break;
				case 27205:
					// Three IE
					remoteViews.setImageViewResource(R.id.widget_network_layout_network_logo,
							R.drawable.image_network_three);
					break;
				default:
					// Unknown Network
					remoteViews.setImageViewResource(R.id.widget_network_layout_network_logo,
							R.drawable.image_network_default);
					break;
				}
			}
			else {
				// Set the text (telling us we've no network)
				networkName = "" + R.string.widget_network_layout_network_title_airplane;
				remoteViews.setTextViewText(R.id.widget_network_layout_network_title, networkName);

				// Set the default network icon
				remoteViews.setImageViewResource(R.id.widget_network_layout_network_logo,
						R.drawable.image_network_default);

			}
		
			// When User clicks on the label, update all widgets
			Intent clickIntent = get_ACTION_APPWIDGET_UPDATE_Intent(this.getApplicationContext());
			PendingIntent clickPendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), widgetId, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			remoteViews.setOnClickPendingIntent(R.id.widget_network_layout_container, clickPendingIntent);
			
			Log.d(LOG, "WidgetUpdateService.onStart - Updated ID: " + widgetId);
			
			// Call the AppWidgetManager to make sure changes take effect
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
			
		}
		stopSelf();
	}

	/*
	 * Utility method to ensure that when we want an Intent that fires
	 * ACTION_APPWIDGET_UPDATE, the extras are correct.
	 * 
	 * The default implementation of onReceive() will discard it if we don't
	 * add the ids of all the instances.
	 */
	protected Intent get_ACTION_APPWIDGET_UPDATE_Intent(Context context) {
		// Set up AppWidgetManager and ComponentName
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetProvider.class.getName());

		// Get values for appWidgetIds
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
		
		// Package and return them
		Intent returnIntent = new Intent(context, WidgetProvider.class);
		returnIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		returnIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		
		return returnIntent;
	}

	// Get AirplaneMode status
	private static boolean isAirplaneModeOn(Context context) {
		// Returns '1' if active
		return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
		}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
