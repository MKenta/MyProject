package com.jojon.ameyoke;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.nifty.cloud.mb.NCMBDialogPushConfiguration;
import com.nifty.cloud.mb.NCMBPush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyCustomReceiver extends BroadcastReceiver {
	private static final String TAG = MyCustomReceiver.class.getSimpleName();
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive() start");
		NCMBDialogPushConfiguration dialogPushConfiguration=new NCMBDialogPushConfiguration();
		//標準的なダイアログを表示するタイプ
		dialogPushConfiguration.setDisplayType(NCMBDialogPushConfiguration.DIALOG_DISPLAY_DIALOG);

		try {
			String channel = intent.getExtras().getString("com.nifty.Channel");
			JSONObject json = null;
			try {
				json = new JSONObject(intent.getExtras().getString("com.nifty.Data"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.d(TAG, "onReceive() channel = " + channel);
			Log.d(TAG, "onReceive() data = " + json);

			StringBuilder sb =new StringBuilder();
			sb.append("channel:"+channel+"\n");

			if(null != json){
				Iterator<?> itr = json.keys();
				while (itr.hasNext()) {
					String key = (String) itr.next();
					Log.d(TAG, "onReceive() key = " + key);
					sb.append("json key:"+key+", json_data:"+json.get(key)+"\n");
				}
			}
			Log.d(TAG, sb.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		NCMBPush.dialogPushHandler(context, intent, dialogPushConfiguration);
	}
}