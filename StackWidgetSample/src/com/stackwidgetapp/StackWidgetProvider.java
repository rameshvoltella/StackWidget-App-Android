/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stackwidgetapp;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.stackwidgetapp.constant.AppPreferences;
import com.stackwidgetapp.constant.Constants;

public class StackWidgetProvider extends AppWidgetProvider {

	public static final String TOAST_ACTION = "com.stackwidgetapp.TOAST_ACTION";
	public static final String EXTRA_ITEM = "com.stackwidgetapp.EXTRA_ITEM";

	// String to be sent on Broadcast as soon as Data is Fetched
	// should be included on WidgetProvider manifest intent action
	// to be recognized by this WidgetProvider to receive broadcast
	public static final String DATA_FETCHED = "com.stackwidgetapp.DATA_FETCHED";

	/*
	 * this method is called every 30 mins as specified on widgetinfo.xml this
	 * method is also called on every phone reboot from this method nothing is
	 * updated right now but instead RetmoteFetchService class is called this
	 * service will fetch data,and send broadcast to WidgetProvider this
	 * broadcast will be received by WidgetProvider onReceive which in turn
	 * updates the widget
	 */

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			// Store the widget id to the Preference in case if the data get
			// Lost due to the system override during the service call for
			// Datafetching
			AppPreferences AppPreferences_widget = new AppPreferences(context,
					Constants.WidgetInfo);
			AppPreferences_widget.SaveIntData(Constants.stackWidgettempId,
					appWidgetIds[i]);
			/* Start the data Fetching Service */
			Intent serviceIntent = new Intent(context, FetchDataService.class);
			serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);
			context.startService(serviceIntent);

		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);

	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);

	}

	private RemoteViews updateWidgetListView(Context context, int appWidgetId) {

		// which layout to show on widget
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);

		// RemoteViews Service needed to provide adapter for ListView
		Intent svcIntent = new Intent(context, StackWidgetService.class);

		// passing app widget id to that RemoteViews Service
		svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		// setting a unique Uri to the intent

		svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

		if (FetchDataService.mDataFetchList != null) {

			svcIntent.putExtra("data", FetchDataService.mDataFetchList);

		}

		// setting adapter to listview of the widget
		remoteViews.setRemoteAdapter(appWidgetId, R.id.stack_view, svcIntent);

		// setting an empty view in case of no data
		remoteViews.setEmptyView(R.id.stack_view, R.id.empty_view);

		Intent toastIntent = new Intent(context, StackWidgetProvider.class);
		toastIntent.setAction(StackWidgetProvider.TOAST_ACTION);
		toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context,
				0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setPendingIntentTemplate(R.id.stack_view,
				toastPendingIntent);

		return remoteViews;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		if (intent.getAction().equals(DATA_FETCHED)) {

			int appWidgetId = intent.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);

			RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);

			// update and notify widget when data arrives

			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,
					R.id.stack_view);

		}

		if (intent.getAction().equals(TOAST_ACTION)) {
			// The click form the widget item is received here
			int appWidgetId = intent.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
			context.startActivity(new Intent(context, MainActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(
							"click",
							"Clicked Movie is \n --- "
									+ Constants.text[viewIndex]));
		}

	}

}