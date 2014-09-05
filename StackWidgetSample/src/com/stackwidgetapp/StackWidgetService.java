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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.imageloaders.ImageLoader;
import com.stackwidgetapp.constant.AppPreferences;
import com.stackwidgetapp.constant.Constants;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

public class StackWidgetService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
	}
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

	private Context mContext;

	private int mAppWidgetId;

	ArrayList<HashMap<String, String>> mPost;

	ImageLoader mImageLoader;

	Bitmap bmpDummy;

	public StackRemoteViewsFactory(Context context, Intent intent) {
		mContext = context;

		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		mImageLoader = new ImageLoader(mContext);

		bmpDummy = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.ic_launcher);
		if (intent.hasExtra("data")) {

			mPost = new ArrayList<HashMap<String, String>>();

			mPost.addAll((ArrayList<HashMap<String, String>>) intent
					.getSerializableExtra("data"));

		} else {
			populateListItem();
		}

	}

	private void populateListItem() {

		if (FetchDataService.mDataFetchList != null)
			mPost = (ArrayList<HashMap<String, String>>) FetchDataService.mDataFetchList
					.clone();
		else
			mPost = new ArrayList<HashMap<String, String>>();

	}

	public void onCreate() {

		// In onCreate() you setup any connections / cursors to your data
		// source. Heavy lifting,
		// for example downloading or creating content etc, should be deferred
		// to onDataSetChanged()
		// or getViewAt(). Taking more than 20 seconds in this call will result
		// in an ANR.

	}

	public void onDestroy() {

		// In onDestroy() you should tear down anything that was setup for your
		// data source,
		// eg. cursors, connections, etc.

	}

	public int getCount() {
		return mPost.size();
	}

	public RemoteViews getViewAt(int position) {
		// position will always range from 0 to getCount() - 1.

		// We construct a remote views item based on our widget item xml file,
		// and set the
		// text based on the position.

		RemoteViews rv = new RemoteViews(mContext.getPackageName(),
				R.layout.stackrow);

		/*
		 * The fill-intent used to pass the clicked position to onReceiver()
		 * StackWidgetProvider class ,so that you can perform any action
		 */
		Bundle extras = new Bundle();
		extras.putInt(StackWidgetProvider.EXTRA_ITEM, position);
		Intent fillInIntent = new Intent();
		fillInIntent.putExtras(extras);
		rv.setOnClickFillInIntent(R.id.click_item, fillInIntent);

		HashMap<String, String> song = new HashMap<String, String>();
		song = mPost.get(position);
		rv.setTextViewText(R.id.data_text, song.get(Constants.testTextTag));

		Bitmap bmp = mImageLoader.getBitmapStack(song
				.get(Constants.testImageTag));

		if (bmp == null) {
			
			rv.setImageViewBitmap(R.id.data_image, bmpDummy);

		} else {
			
			rv.setImageViewBitmap(R.id.data_image, bmp);

		}

		// Return the remote views object.
		return rv;
	}

	public RemoteViews getLoadingView() {
		// You can create a custom loading view (for instance when getViewAt()
		// is slow.) If you
		// return null here, you will get the default loading view.
		return null;
	}

	public int getViewTypeCount() {
		return 1;
	}

	public long getItemId(int position) {
		return position;
	}

	public boolean hasStableIds() {
		return true;
	}

	public void onDataSetChanged() {
		
		// Notify widget when change in data like listview

		if (FetchDataService.mDataFetchList != null)
			mPost = (ArrayList<HashMap<String, String>>) FetchDataService.mDataFetchList
					.clone();

	}
}