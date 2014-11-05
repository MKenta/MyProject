package com.jojon.ameyoke;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jojon.ameyoke.*;
import com.nifty.cloud.mb.NCMB;
import com.nifty.cloud.mb.NCMBAnalytics;
import com.nifty.cloud.mb.NCMBException;
import com.nifty.cloud.mb.NCMBInstallation;
import com.nifty.cloud.mb.NCMBPush;
import com.nifty.cloud.mb.NCMBQuery;
import com.nifty.cloud.mb.RegistrationCallback;
import com.nifty.cloud.mb.SendCallback;

import jp.co.yahoo.android.maps.CircleOverlay;
import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapView;
import jp.co.yahoo.android.maps.MyLocationOverlay;
import jp.co.yahoo.android.maps.weather.WeatherOverlay;
import jp.co.yahoo.android.maps.weather.WeatherOverlay.WeatherOverlayListener;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements WeatherOverlayListener, android.location.LocationListener {
	
    CircleOverlay circleOverlay;
    
   //現在値保存用のファイル名
  	String pointFileName = "location_temp";
  	
  	//pushチャンネル用
  	String channel;
  	
	//緯度,経度を格納する整数型配列(ファイル保存)
	int point[] = new int[2];
	
	int select = 0;
	
	//通知のオンオフ設定
	boolean pushSet = false;
	
	//現在の座標を格納
	static GeoPoint nowCoord;	
	//本体
	private Context context=this;	
	//時間変数
	private Time time=new Time();	
	//View用
	String estimate="JON";
	int ipower;
	MainActivity mainActivity=this;
	
	//タイマー関連:時刻表示とアニメーション用
	private Timer mainTimer;					//タイマー用
	private MainTimerTask mainTimerTask;		//タイマタスククラス
	private Handler mHandler = new Handler();   //UI Threadへのpost用ハンドラ
	private boolean animation = true;			//交互に画像を切り替えてアニメーション
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//タイマーインスタンス生成
		this.mainTimer = new Timer();
		//タスククラスインスタンス生成
		this.mainTimerTask = new MainTimerTask();				
		//タイマースケジュール設定＆開始
		this.mainTimer.schedule(mainTimerTask, 1000,500);

		//マップ関連の宣言および設定
	    final MapView mapView = new MapView(this, "dj0zaiZpPVNFc3FWbDFuYW9kdCZzPWNvbnN1bWVyc2VjcmV0Jng9ZjA-");
	    mapView.getMapController().setZoom(7);
	    
	    //やめよう
	      stopService( new Intent( MainActivity.this, MyService.class ) );
	    
	    //マップを表示するレイアウトのオブジェクトの取得
	    RelativeLayout lmap = (RelativeLayout)findViewById(R.id.rainmap);
	    
	    //MyLocationOverlayインスタンス作成
	    final MyLocationOverlay _overlay = new MyLocationOverlay(getApplicationContext(), mapView);
	    
	    //現在位置取得開始
	  	_overlay.enableMyLocation();
	  	   	    
	  	   //ファイルから前回起動時の現在値を読み込む
	     	try {
	  		FileInputStream fileInput = openFileInput(pointFileName);
	  		ObjectInputStream pointInput = new ObjectInputStream(fileInput);
	  		point = (int[]) pointInput.readObject();
	  		pointInput.close();
	  		GeoPoint tempPoint = new GeoPoint(point);
	  		mapView.getMapController().setCenter(tempPoint);
	  	} catch (Throwable e) {
	  		// TODO 自動生成された catch ブロック
	  		e.printStackTrace();
	  		point = new int[2];
	  		Log.d("hoge", "Import failue");
	  	}
	  	    
	  	//位置が更新されると、地図の位置も変わるよう設定
	    _overlay.runOnFirstFix(new Runnable(){
	  	    public void run() {
	  	        if (mapView.getMapController() != null) {
	  	        	
	  	        	//現在位置を取得
	  	    	    nowCoord = _overlay.getMyLocation();
	  	    	    point[0] = nowCoord.getLatitudeE6();
	  	    	    point[1] = nowCoord.getLongitudeE6();
	  	    	    
	  	    	    //ファイルに現在値を保存
	  	    	    try {
	  		        	FileOutputStream fileOut = openFileOutput(pointFileName, Context.MODE_PRIVATE);
	  		        	ObjectOutputStream pointOut;
	  					pointOut = new ObjectOutputStream(fileOut);
	  		        	pointOut.writeObject(point);
	  		        	pointOut.close();
	  				} catch (Throwable e) {
	  					// TODO 自動生成された catch ブロック
	  					e.printStackTrace();
	  					Log.d("hoge", "Output failue");
	  				}
	  	            	
	  	            //地図移動
	  	            mapView.getMapController().animateTo(nowCoord);
	  	                
	  	        	//円を表示
	  	            circleOverlay = new CircleOverlay(nowCoord, 8000, 8000){
	  	        	    @Override
	  	        	    protected boolean onTap(){
	  	        	      //円をタッチした際の処理
	  	        	      return true;
	  	        	    }
	  	        	};
	  	        	circleOverlay.setStrokeWidth(20);
	  	        	circleOverlay.setStrokeColor(Color.argb(100, 0, 0, 255));
	  	        	mapView.getOverlays().add(circleOverlay);
	  	        	    
	  	        }
	  	    }
	  	});
	    
	    //MapViewにMyLocationOverlayを追加。
	    mapView.getOverlays().add(_overlay);
	    
	   //WeatherOverlayを作成
	    final WeatherOverlay weatherOverlay = new WeatherOverlay(this);
	    
	    //WeatherOverlayListenerを設定
	    weatherOverlay.setWeatherOverlayListener(this);

	    //MapViewにWeatherOverlayを追加
	    mapView.getOverlays().add(weatherOverlay);
	    
	    //レーダーを更新
	    weatherOverlay.updateWeather(0);
	    
		lmap.addView(mapView);
	    /*マップ処理ここまで*/
		
        // ボタンのオブジェクトを取得
        Button rainmapbtn = (Button)findViewById(R.id.rainmapbtn);
        Button taxibtn = (Button)findViewById(R.id.taxibtn);
        
        //リソースクラス
        final Resources res = getResources();
        
   //     weatherCheck();
        MyAsyncTask myAsyncTask=new MyAsyncTask();
        int Power=myAsyncTask.GetPower();
    	TextView timeText=(TextView)findViewById(R.id.rainTime);
        ImageView power = (ImageView) findViewById(R.id.power);
        if(Power>0){
        	if(Power==1){
        		power.setImageResource(R.drawable.s1);
        	}
        	if(Power==2){
        		power.setImageResource(R.drawable.m1);
        	}
        	if(Power==3){
        		power.setImageResource(R.drawable.l1);
        	}
        	timeText.setText(myAsyncTask.GetTime());
        }else{
        	timeText.setText("読み込み中");        	
        	power.setImageResource(R.drawable.x1);
        }
        
        // クリックイベントを受け取れるようにする
        rainmapbtn.setOnClickListener(new View.OnClickListener() {
    	   @Override
    	      public void onClick(View v) {
    		   //Auto-generated method stub
    		   // インテントのインスタンス生成
    		   Intent intent = new Intent(MainActivity.this, RainMapActivity.class);
    		   // 次画面のアクティビティ起動
    		   startActivity(intent);
    	   }
        });
        
        taxibtn.setOnClickListener(new View.OnClickListener() {
    	   @Override
    	      public void onClick(View v) {
    		   //Auto-generated method stub
    		   // インテントのインスタンス生成
    		   Intent intent = new Intent(MainActivity.this, TaxiActivity.class);
    		   // 次画面のアクティビティ起動
    		   startActivity(intent);
    	   }
        });
        
        //現在の天気を取得してアイコンを変更
        new AsyncTask<Void, Void, String>() {
        	public String now_weather = new String();
        	//HTTP通信はAsyncタスク内でやらないとビルドで落ちる
		    @Override
			public String doInBackground(Void... params) {
		    	String lat = "35.942756";
		    	String lon = "136.198842";
		    	
			try {
			    
			    StringBuilder builder = new StringBuilder();
			    //HttpClientのインスタンスを作る（HTTPリクエストを送るために必要）
			    HttpClient client = new DefaultHttpClient();
			    //HttpGetのインスタンスを作る（GETリクエストを送るために必要)
			    //URLのcoordinates (座標)を取得してURLに結合したい(やって)
			    HttpGet httpGet = new HttpGet(
						"http://api.openweathermap.org/data/2.5/find?lat="+lat+"&lon="+lon+"&appid=13034033b7a9eef9a0b2cc36caf985af&cnt=1");
			    try {
			    	//リクエストしたリンクが存在するか確認するために、HTTPリクエストを送ってHTTPレスポンスを取得する
			    	HttpResponse response = client.execute(httpGet);
			    	//返却されたHTTPレスポンスの中のステータスコードを調べる
			    	// -> statusCodeが200だったらページが存在。404だったらNot found（ページが存在しない）。500はInternal server error。
			    	int statusCode = response.getStatusLine().getStatusCode();
			    	if (statusCode == 200) {
			    		//HTTPレスポンスが200よりページは存在する
			    		//レスポンスからHTTPエンティティ（実体）を生成
			    		HttpEntity entity = response.getEntity();
			    		//HTTPエンティティからコンテント（中身）を生成
			    		InputStream content = entity.getContent();
			    		//コンテントからInputStreamReaderを生成し、さらにBufferedReaderを作る
			    		//InputStreamReaderはテキストファイル（InputStream）を読み込む
			    		//BufferedReaderはテキストファイルを一行ずつ読み込む
			    		//（参考）http://www.tohoho-web.com/java/file.htm
			    		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
			    		String line;
			    		//readerからreadline()で行を読んで、builder文字列(StringBuilderクラス)に格納していく。
			    		//※このプログラムの場合、lineは一行でなのでループは回っていない
			    		//※BufferedReaderを使うときは一般にこのように記述する。
			    		while ((line = reader.readLine()) != null) {
			    			builder.append(line);
			    		}
				    
			    	} else {
			    		System.out.println("Failed to download file");
			    	}
			    	} catch (ClientProtocolException e) {
			    		e.printStackTrace();
			    		return "";
			    	} catch (IOException e) {
			    		e.printStackTrace();
			    		return "";
			    	}finally {
			    		// ここではfinallyでshutdown()しているが、HttpClientを使い回す場合は、
			    		// 適切なところで行うこと。当然だがshutdown()したインスタンスは通信できなくなる。
			    		client.getConnectionManager().shutdown();
			    	}
			    	// 文字列をJSONオブジェクトに変換する
			    
			    	try {
			    		JsonNode node = new ObjectMapper().readTree(builder.toString());
				
			    		if (node != null){
			    			now_weather=node.path("list").path(0).path("weather").path(0).path("main").asText();
			    		}
			    		System.out.println();//改行
			    	} catch (Exception e) {
			    		e.printStackTrace();
			    		return "";
			    	}
			    	return builder.toString();
					} catch (Exception e) {
						e.getStackTrace();
						return "";
					}
		    }
		    
		    @Override
			protected void onPostExecute(String result) {
		    	System.out.println();
				ImageView weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
				RelativeLayout background = (RelativeLayout) findViewById(R.id.background);
		    	if(now_weather.equals("Clouds")){
		    		/*アイコンを雲に*/
		    		weatherIcon.setImageResource(R.drawable.icon_cloud3);
		    		background.setBackground(res.getDrawable(R.drawable.back_cloud2));
		    	}
		    	else if(now_weather.equals("Clear")){
		    		/*晴れ(?)*/
		    		weatherIcon.setImageResource(R.drawable.icon_sun3);
		    		background.setBackground(res.getDrawable(R.drawable.back_sky1));
		    	}
		    	else {
		    		/*雨*/
		    		weatherIcon.setImageResource(R.drawable.icon_rain3);
		    		background.setBackground(res.getDrawable(R.drawable.back_rain3));
		    	}
		    }
        }.execute();
        
        
        /********************
         *	ここからは通知のお話	* 
         ********************/
        NCMB.initialize(this, "846665d3a9fcb4132d9264f7a629e57ed4e67ee28fe111e08867208186d27d10", "3eb6b382945857dc1b9611ce59a3db7cd6ee2b7bd544f2d5df0424d62028c762");
 		
 		//プッシュ通知チャンネルの設定
 		subscribe();
         
 		final NCMBInstallation installation = NCMBInstallation.getCurrentInstallation();
 		installation.getRegistrationIdInBackground("575549731285", new RegistrationCallback(){
 			@Override
 			public void done(NCMBException e) {
 				if (e != null) {
 					// 失敗
 					e.printStackTrace();
 				} else {
 					// 成功
 					try {
 						installation.save();
 						Log.d("JON", "installation complete!");
 					} catch(NCMBException le) {
 			                if (NCMBException.DUPLICATE_VALUE.equals(le.getCode())){
 			                    // レジストレーションIDの重複エラー
 			                    // ユーザーによるローカルデータ削除や、アプリ再インストールがされた場合に発生。
 			                    // 以下、クラウド側に既登録の情報からローカルデータを復旧する処理。
 			                    NCMBQuery<NCMBInstallation> query = NCMBInstallation.getQuery();
 			                    query.whereEqualTo("deviceToken", installation.get("deviceToken"));
 			                    try {
 			                        NCMBInstallation prevInstallation = query.getFirst();
 			                        String objectId = prevInstallation.getObjectId();
 			                        installation.setObjectId(objectId);
 			                        installation.save();
 			                    } catch(NCMBException le2) {
 			                        le2.printStackTrace();
 			                    }
 			                } else {
 			                    le.printStackTrace();
 			                }
 			        }
 				}
 			} 			
 		});
 		NCMBPush.setDefaultPushCallback(this, MainActivity.class);

 		// プッシュ開封登録の実施
 		NCMBAnalytics.trackAppOpened(getIntent());
	}
	
	public class MainTimerTask extends TimerTask {
		@Override
		public void run() {
			//ここに定周期で実行したい処理を記述します			
	         mHandler.post( new Runnable() {
	             public void run() {
		            Resources res = getResources();
	     	    	TextView dateText = (TextView)findViewById(R.id.date);
	    	    	time = new Time("Asia/Tokyo");
	    	    	time.setToNow();
	    	    	String date = time.year + "/" + (time.month + 1) + "/"
							+ time.monthDay + "  " + time.hour + ":"
							+ time.minute + ":" + time.second;
	    	    	dateText.setText(date);
	    	    	LinearLayout tyuui = (LinearLayout)findViewById(R.id.tyuui);
	    	    	if(animation){
		    	    	tyuui.setBackground(res.getDrawable(R.drawable.tyuui3));
	    	    		animation = false;
	    	    	}
	    	    	else{
	    	    		tyuui.setBackground(res.getDrawable(R.drawable.tyuui1));
	    	    		animation = true;
	    	    	}	    	    	
	    	    	MyAsyncTask myAsyncTask=new MyAsyncTask();		
					myAsyncTask.SetContext(context);
					myAsyncTask.SetGeoPoint(nowCoord);
					myAsyncTask.SetPushflag(false);
					myAsyncTask.execute();
					estimate=myAsyncTask.GetTime();
					ipower=myAsyncTask.GetPower();
					new changeWeather(mainActivity, estimate, ipower);
	             }
	         });
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		switch (item.getItemId()) {
		case R.id.push_pushSwitch:
			pushSwitch();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void pushSwitch() {
		if(pushSet == true){
			 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		        // アラートダイアログのタイトルを設定します
		        alertDialogBuilder.setTitle("プッシュ通知の設定");
		        // アラートダイアログのメッセージを設定します
		        alertDialogBuilder.setMessage("プッシュ通知をオフにしますか？");
		        // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
		        alertDialogBuilder.setPositiveButton("はい",new DialogInterface.OnClickListener() {
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		              	      // サービス開始
		              	      stopService( new Intent( MainActivity.this, MyService.class ) );
		                    	pushSet = false;
		                    }
		                });
		        // アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
		        alertDialogBuilder.setNegativeButton("いいえ",new DialogInterface.OnClickListener() {
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                    }
		                });
		        // アラートダイアログのキャンセルが可能かどうかを設定します
		        alertDialogBuilder.setCancelable(true);
		        AlertDialog alertDialog = alertDialogBuilder.create();
		        // アラートダイアログを表示します
		        alertDialog.show();
		} else {
			 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		        // アラートダイアログのタイトルを設定します
		        alertDialogBuilder.setTitle("プッシュ通知の設定");
		        // アラートダイアログのメッセージを設定します
		        alertDialogBuilder.setMessage("プッシュ通知をオンにしますか？");
		        // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
		        alertDialogBuilder.setPositiveButton("はい",new DialogInterface.OnClickListener() {
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		              	      // サービス停止
		              	      startService( new Intent( MainActivity.this, MyService.class ) );
		                    	pushSet = true;
		                    }
		                });
		        // アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
		        alertDialogBuilder.setNegativeButton("いいえ",new DialogInterface.OnClickListener() {
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                    }
		                });
		        // アラートダイアログのキャンセルが可能かどうかを設定します
		        alertDialogBuilder.setCancelable(true);
		        AlertDialog alertDialog = alertDialogBuilder.create();
		        // アラートダイアログを表示します
		        alertDialog.show();
		}
	}

	@Override
	public void errorUpdateWeather(WeatherOverlay arg0, int arg1) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void finishUpdateWeather(WeatherOverlay arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	/** チャンネルを設定 */
	private void subscribe() {
		WifiManager wifiManager=(WifiManager)this.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo=wifiManager.getConnectionInfo();
		channel=String.valueOf(wifiInfo.getMacAddress());
		if(channel==null){
			channel="Sonic";
		}
		NCMBPush.subscribe(this, channel, MainActivity.class);
		//dispMessage("チャンネル【"+channel+"】を設定");
	}
	/** Push通知の実施 */
	public void sendPush(String minutes) throws JSONException {
		NCMBPush push = new NCMBPush();
		// チャンネルの設定
		//dispMessage(channel);
		push.setChannel(channel);
		// オプション値の設定（カスタムレシーバのフィルタ設定、タイトルの設定、配信端末の設定）
		JSONObject data = new JSONObject("{\"action\": \"com.jojon.RECEIVE_PUSH\", " +
				"\"title\": \"jojo\",\"dialogFlag\":\"true\", \"target\": [android]}");
		push.setData(data);
		// ユーザ定義値の設定
		JSONObject user = new JSONObject("{\"URL\": \"http://www.google.co.jp/\"}");
		push.setUserSettingValue(user);
		// メッセージの設定
//		time.setToNow();
		push.setMessage(minutes);
		// 即時配信フラグの設定
		push.setImmediateDeliveryFlag(true);
		//ダイアログの有効化
		push.setDialog(true);
		push.sendInBackground(new SendCallback() {
			@Override
			public void done(NCMBException e) {
				if (e != null) {
					e.printStackTrace();
					dispMessage("Push登録に失敗");
				} else {
					dispMessage("Push登録に成功");
				}
			}
		});
	}
	private void dispMessage(String message){
//		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	public void weatherCheck(){		
		//現在地
		MyAsyncTask myAsyncTask=new MyAsyncTask();		
		myAsyncTask.SetContext(context);
		myAsyncTask.SetGeoPoint(nowCoord);
		myAsyncTask.SetPushflag(true);
		myAsyncTask.execute();
		estimate=myAsyncTask.GetTime();
		ipower=myAsyncTask.GetPower();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		nowCoord.setLatitudeE6((int)(location.getLatitude()*1000000));
		nowCoord.setLongitudeE6((int)(location.getLongitude()*1000000));
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}