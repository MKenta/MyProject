package com.jojon.ameyoke;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class MyService extends Service {
	private Timer timer=null;
	Handler mHandler=new Handler();
	MainActivity mainActivity=new MainActivity();
  @Override
  public void onCreate() {
    Log.d("MyService", "onCreate");
    Toast.makeText(this, "Push通知がONになりました", Toast.LENGTH_SHORT).show();
  }
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d("MyService", "onStartCommand");
    
    timer=new Timer(true);
    timer.schedule(new TimerTask() {		
		@Override
		public void run() {
			mHandler.post(new Runnable() {				
				@Override
				public void run() {
					Log.d("MyService", "Timerrun");		
					mainActivity.weatherCheck();
				}
			});			
		}
	},1000,300*1000);
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    Log.d("MyService", "onDestroy");
    // タイマー停止
    if( timer != null ){
      timer.cancel();
      timer = null;
    }
    Toast.makeText(this, "Push通知がOFFになりました", Toast.LENGTH_SHORT).show();
  }

  @Override
  public IBinder onBind(Intent arg0) {
    Log.d("MyService", "onBind");
    return null;
  }

}

