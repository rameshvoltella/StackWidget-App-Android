package com.stackwidgetapp;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;

import com.stackwidgetapp.constant.AppPreferences;
import com.stackwidgetapp.constant.Constants;

public class FetchDataService extends Service {

	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	AppPreferences app;

	public static ArrayList<HashMap<String, String>> mDataFetchList;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/*
	 * Retrieve appwidget id from intent it is needed to update widget later
	 * initialize our AQuery class
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		app = new AppPreferences(this, Constants.WidgetInfo);

		try {
			if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
				appWidgetId = intent.getIntExtra(
						AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);

		} catch (NullPointerException e) {
			// Some time there may be possibility of null point exception due to
			// system override issues, so we get the widget id from stored
			// widget id in the preference from StackWidgetProvider class
			// onUpdate methord

			appWidgetId = app.getIntData(Constants.stackWidgettempId);

		}

		fetchData();

		return super.onStartCommand(intent, flags, startId);
	}

	private void fetchData() {

		mDataFetchList = new ArrayList<HashMap<String, String>>();

		// CONSTANT DATA IS FETCHED

		for (int i = 0; i < Constants.images.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Constants.testImageTag, Constants.images[i]);
			map.put(Constants.testTextTag, Constants.text[i]);
			mDataFetchList.add(map);
		}

		/**
		 * THE MAIN THING IS YOU CAN FETCH DATA FROM WEB ALSO HERE USING YOUR
		 * METHORDS
		 * 
		 * */

		// After reteving data we sends broadcast to StackWidgetProvider to
		// notify the adaptor
		Intent widgetUpdateIntent = new Intent();
		widgetUpdateIntent.setAction(StackWidgetProvider.DATA_FETCHED);
		widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				appWidgetId);
		sendBroadcast(widgetUpdateIntent);

		this.stopSelf();
	}

}