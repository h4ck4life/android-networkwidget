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

public class UpdateWidgetService extends Service {

	// Variables
	int networkId;
	String networkName;
	private static final String LOG = "com.ecctm.networkwidget";

	@Override
	public void onStart(Intent intent, int startId) {
		// Begin Logging
		Log.i(LOG, "UpdateWidgetService Called");

		// Set up our widget manager
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
				.getApplicationContext());

		// get ids for ALL running instances of our widget
		int[] allWidgetIds = intent
				.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

		// target this widget
		ComponentName networkWidget = new ComponentName(
				getApplicationContext(), NetworkWidgetProvider.class);

		// get ids for ALL running instances of our widget
		int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(networkWidget);

		// Log these ids
		Log.w(LOG, "From Intent " + String.valueOf(allWidgetIds.length));
		Log.w(LOG, "Direct " + String.valueOf(allWidgetIds2.length));

		// begin our update
		for (int widgetId : allWidgetIds) {
			// set up TelephonyManager to get our network info
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

			/*TODO:
			 * 1) Set Network Logo from a separate .java file, to allow easy adding of new networks.
			 * 2) Data Transfer info?
			 * 	2a) Check if Wireless is enabled
			 * 	2b) Get signal coverage & display on the right
			 * 	2c) Get up/down speeds & display
			*/
			
			// setup our remoteview
			RemoteViews remoteViews = new RemoteViews(this
					.getApplicationContext().getPackageName(),
					R.layout.widget_layout);

			
			//Check AirplaneMode
			if(!isAirplaneModeOn(this)) {
				// Its off, we have networks, so...
				
				// Get info about currently connected network
				networkId = Integer.parseInt(telephonyManager.getNetworkOperator());
				networkName = telephonyManager.getNetworkOperatorName();
				Log.w(LOG, "Get Network Info - MobileOperatorID: " + networkId);
				Log.w(LOG, "Get Network Info - MobileOperatorName: " + networkName);

				// Set the text
				remoteViews.setTextViewText(R.id.widget_textview, networkName);

				// Set the mobile Logo
				switch (networkId) {
				case 27201:
					// Vodafone IE
					remoteViews.setImageViewResource(R.id.widget_imageview,
							R.drawable.image_network_vodafone);
					break;
				case 27202:	
					// o2 IE
					remoteViews.setImageViewResource(R.id.widget_imageview,
							R.drawable.image_network_o2);
					break;
				case 27205:
					// Three IE
					remoteViews.setImageViewResource(R.id.widget_imageview,
							R.drawable.image_network_three);
					break;
				default:
					// Unknown Network
					remoteViews.setImageViewResource(R.id.widget_imageview,
							R.drawable.image_network_default);
					break;
				}
			}
			else {
				// Set the text (telling us we've no network)
				networkName = "Airplane Mode Enabled";
				remoteViews.setTextViewText(R.id.widget_textview, networkName);

				// Set the default network icon
				remoteViews.setImageViewResource(R.id.widget_imageview,
						R.drawable.image_network_default);

			}
			
			// Register an onClickListener
			Intent clickIntent = new Intent(this.getApplicationContext(),
					NetworkWidgetProvider.class);

			clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
					allWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					getApplicationContext(), 0, clickIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.widget_textview_container,
					pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
		stopSelf();

		super.onStart(intent, startId);
	}

	private static boolean isAirplaneModeOn(Context context) {
		//Get AirplaneMode status
		//"True" if active
		return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
		}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
