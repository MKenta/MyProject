package com.jojon.ameyoke;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Struct;
import java.util.ArrayList;

import javax.security.auth.PrivateCredentialPermission;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;

import jp.co.yahoo.android.maps.CircleOverlay;
import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapActivity;
import jp.co.yahoo.android.maps.MapController;
import jp.co.yahoo.android.maps.MapView;
import jp.co.yahoo.android.maps.MyLocationOverlay;
import jp.co.yahoo.android.maps.Overlay;
import jp.co.yahoo.android.maps.OverlayItem;
import jp.co.yahoo.android.maps.PinOverlay;
import jp.co.yahoo.android.maps.PopupOverlay;
import jp.co.yahoo.android.maps.routing.RouteOverlay;
import jp.co.yahoo.android.maps.routing.RouteOverlay.RouteOverlayListener;
import jp.co.yahoo.android.maps.weather.WeatherOverlay;
import jp.co.yahoo.android.maps.weather.WeatherOverlay.WeatherOverlayListener;
import android.R.string;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

// 3. 「Yahoo!地図」を表示したいアクティビティを（jp.co.yahoo.android.maps.）MapActivity を継承して作成する
@SuppressLint("NewApi")
public class RainMapActivity extends MapActivity implements WeatherOverlayListener,RouteOverlayListener {
	
	//色変更などの処理のために大域変数で円のレイヤーを作っておく
	CircleOverlay circleOverlay;
	
	//現在値保存用のファイル名
	String pointFileName = "location_temp";
	
	//緯度,経度を格納する整数型配列(ファイル保存)
	int point[] = new int[2];
	
	//雨雲レーダーのオンオフ
	boolean radar = true;
	
	//表示する雨雲の時間
	int radarTime = 0;
	
	//現在の座標を格納
	private GeoPoint nowCoord;
	
	//本部
	Context context=this;
	RouteOverlayListener routeOverlayListener=this;
	
	//検索結果を格納
	ArrayList<GeoPoint> goalPoints=new ArrayList<GeoPoint>();
	ArrayList<String> goalNames=new ArrayList<String>();
	int GoalNum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_rainmap);
	    
	    //リソースクラス
	    final Resources res = getResources();
	    
	    //マップ関連の宣言および設定
	    final MapView mapView = new MapView(this, "dj0zaiZpPVNFc3FWbDFuYW9kdCZzPWNvbnN1bWVyc2VjcmV0Jng9ZjA-");
	    MapController mapController = mapView.getMapController();
	    mapController.setZoom(6);
	    mapView.setScalebar(true);
	    
		Button btn1 = (Button)findViewById(R.id.btn1);
	    Button btn2 = (Button)findViewById(R.id.btn2);
	    Button btn3 = (Button)findViewById(R.id.btn3);
	    Button btn4 = (Button)findViewById(R.id.btn4);
	    Button btn5 = (Button)findViewById(R.id.btn5);
	    final Button radarSwitch = (Button)findViewById(R.id.radarSwitch);
	    final Button currentLocation = (Button)findViewById(R.id.currentLocation);
	    final TextView timeSet = (TextView)findViewById(R.id.timeSet);
	    final TextView radarSwitchText = (TextView)findViewById(R.id.radarSwitchText);
	    
	    RelativeLayout lmap = (RelativeLayout)findViewById(R.id.rainmap);
	    
	    //MyLocationOverlayインスタンス作成
	    final MyLocationOverlay _overlay = new MyLocationOverlay(getApplicationContext(), mapView);
	    mapController.setCenter(_overlay.getMyLocation());
	    
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
			point = new int[2];	//ぬるぽした時は再宣言すればいいのです
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
					
					new AsyncTask<Void, Void, String>() {
						String DEBUG="0";
					    //HTTP通信はAsyncタスク内でやらないとビルドで落ちる
					    @Override
						protected String doInBackground(Void... params) {
						try { 
						    StringBuilder builder = new StringBuilder();
						    //HttpClientのインスタンスを作る（HTTPリクエストを送るために必要）
						    HttpClient client = new DefaultHttpClient();
						    //HttpGetのインスタンスを作る（GETリクエストを送るために必要)
						    //URLのcoordinates (座標)を取得してURLに結合したい(やって)
						    String lat = String.valueOf(nowCoord.getLatitude());
						    String lon = String.valueOf(nowCoord.getLongitude());
						    
						    HttpGet httpGet = new HttpGet(
						    		"http://search.olp.yahooapis.jp/OpenLocalPlatform/V1/localSearch?&query=%E3%82%B3%E3%83%B3%E3%83%93%E3%83%8B&appid=dj0zaiZpPUZickhJWDdMZ05tYSZzPWNvbnN1bWVyc2VjcmV0Jng9OWE-&output=json&lat="+lat+"&lon="+lon+"&dist=10");
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
								JsonNode node = null;
								try {
									node = new ObjectMapper().readTree(builder.toString());
								} catch (JsonProcessingException e) {
									// TODO 自動生成された catch ブロック
									e.printStackTrace();
								} catch (IOException e) {
									// TODO 自動生成された catch ブロック
									e.printStackTrace();
								}
								if (node != null){
								System.out.println(node.path("ResultInfo").path("Count").asText());
								    JsonNode secondnode = node.path("Feature");
								    GoalNum=Integer.parseInt(node.path("ResultInfo").path("Count").asText());
						    		final ArrayList<RouteOverlay> routeOverlay=new ArrayList<RouteOverlay>();
								    for(int i=0;i<Integer.parseInt(node.path("ResultInfo").path("Count").asText());i++){
								    	routeOverlay.add(new RouteOverlay(context, "dj0zaiZpPUZickhJWDdMZ05tYSZzPWNvbnN1bWVyc2VjcmV0Jng9OWE-"));
								    	System.out.println(secondnode.path(i).path("Name").asText());
								    	String nameString=secondnode.path(i).path("Name").asText();
								    	goalNames.add(nameString);
									    System.out.println(secondnode.path(i).path("Geometry").path("Coordinates").asText());
									    String tmpString=secondnode.path(i).path("Geometry").path("Coordinates").asText();
									    String tmpArray[]=tmpString.split(",");
									  //  Log.d("JON", tmpArray[0]);
									    String aaString=tmpArray[0];
									    String bbString=tmpArray[1];
									    GeoPoint tmp=new GeoPoint((int)(Double.parseDouble(bbString)*1000000),(int)(Double.parseDouble(aaString)*1000000));
									    goalPoints.add(tmp);
									    
									    //ピン建て
									    final PinOverlay pinOverlay=new PinOverlay(PinOverlay.PIN_VIOLET);
									    mapView.getOverlays().add(pinOverlay);
									    final PopupOverlay popupOverlay=new PopupOverlay(){
									    	@Override
									    	public void onTap(OverlayItem item){
									    		Log.d("JON", "routeSearch");
									    		GeoPoint goalPoint=pinOverlay.getCenter();
									    		routeOverlay.get(routeOverlay.size()-1).setRoutePos(nowCoord, goalPoint, RouteOverlay.TRAFFIC_CAR);
									    		routeOverlay.get(routeOverlay.size()-1).setRouteOverlayListener(routeOverlayListener);
									    		routeOverlay.get(routeOverlay.size()-1).search();
										    	//出発地ピンを非表示
										    	routeOverlay.get(routeOverlay.size()-1).setStartPinVisible(false);
										    	//目的地ピンを非表示
										    	routeOverlay.get(routeOverlay.size()-1).setGoalPinVisible(false);
									    		for(int j=0;j<routeOverlay.size();j++){
									    			if(routeOverlay.get(j).getGoalPos()!=goalPoint){
									    				routeOverlay.get(j).cancel();
									    			}
									    		}
									    		mapView.getOverlays().add(routeOverlay.get(routeOverlay.size()-1));
									    	}
									    };
									    
									    mapView.getOverlays().add(popupOverlay);
									    pinOverlay.setOnFocusChangeListener(popupOverlay);
									    pinOverlay.addPoint(tmp, nameString, "ここまでの案内を表示する");
								    }
								}
								System.out.println();//改行
						    } catch (Exception e) {
						    	e.printStackTrace();
						    	Log.d("JON","Not Catch1");
							return "";
						    }
						    return builder.toString();
							} catch (Exception e) {
							    e.getStackTrace();
						    	Log.d("JON","Not Catch:"+DEBUG);
							    return "";
							}
					    }
					    
					    @Override
						protected void onPostExecute(String result) {
						System.out.println();
					    }
					    
				    }.execute();
					
					
					
	                //地図移動
	                mapView.getMapController().animateTo(nowCoord);
	                
	        	    //円を表示
	        	    circleOverlay = new CircleOverlay(nowCoord, 8500, 8500){
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
	    
	    weatherOverlay.updateWeather(0);
	    
	    btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				//雨雲の時刻を60分前にする
				radarTime = -60;
				weatherOverlay.updateWeather(radarTime);
				
				//左上の文字の表示切り替え
				if(radarTime == 0){
					timeSet.setText("現在");
				}
				else if(radarTime > 0){
					timeSet.setText(radarTime + "分後");
				}
				else{
					timeSet.setText((-1 * radarTime) + "分前");
				}
			}
		});
	    
		btn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				//雨雲の時刻を5分戻す
				radarTime -= 5;
				if(radarTime < -60){
					radarTime = -60;
				}
				weatherOverlay.updateWeather(radarTime);
				
				//左上の文字の表示切り替え
				if(radarTime == 0){
					timeSet.setText("現在");
				}
				else if(radarTime > 0){
					timeSet.setText(radarTime + "分後");
				}
				else{
					timeSet.setText((-1 * radarTime) + "分前");
				}
			}
		});
		
		btn3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				//雨雲の時刻を現在に戻す
				radarTime = 0;
				weatherOverlay.updateWeather(radarTime);
				
				//左上の文字の表示切り替え
				if(radarTime == 0){
					timeSet.setText("現在");
				}
				else if(radarTime > 0){
					timeSet.setText(radarTime + "分後");
				}
				else{
					timeSet.setText((-1 * radarTime) + "分前");
				}
			}
		});
		
		btn4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				//雨雲の時刻を5分進める
				radarTime += 5;
				if(radarTime > 60){
					radarTime = 60;
				}
				weatherOverlay.updateWeather(radarTime);
				
				//左上の文字の表示切り替え
				if(radarTime == 0){
					timeSet.setText("現在");
				}
				else if(radarTime > 0){
					timeSet.setText(radarTime + "分後");
				}
				else{
					timeSet.setText((-1 * radarTime) + "分前");
				}
			}
		});
		
		btn5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				//雨雲の時刻を60分後にする
				radarTime = 60;
				weatherOverlay.updateWeather(radarTime);
				
				//左上の文字の表示切り替え
				if(radarTime == 0){
					timeSet.setText("現在");
				}
				else if(radarTime > 0){
					timeSet.setText(radarTime + "分後");
				}
				else{
					timeSet.setText((-1 * radarTime) + "分前");
				}
			}
		});
		
		radarSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				if(radar){
					mapView.getOverlays().remove(weatherOverlay);
					radarSwitch.setBackground(res.getDrawable(R.drawable.dot100_mono));
					radarSwitchText.setText(res.getString(R.string.off));
					radar = false;
					
				}
				else {
				    mapView.getOverlays().add(weatherOverlay);
					radarSwitch.setBackground(res.getDrawable(R.drawable.dot100));
					radarSwitchText.setText(res.getString(R.string.on));
				    radar = true;
				}
			}
		});
		
		currentLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				mapView.getMapController().animateTo(_overlay.getMyLocation());
			}
		});

		
		lmap.addView(mapView);
		
		
    }
    //onCreateここまで
    	/************
		 * ここからJSoN	*
		 *************/
//    private class MyTask {
//    	
//    }

    //雨雲レーダー情報の取得でエラーが発生したら通知
    @Override
    public void errorUpdateWeather(WeatherOverlay weatherOverlay, int error) {

    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
	@Override
	public void finishUpdateWeather(WeatherOverlay arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}


	@Override
	public boolean errorRouteSearch(RouteOverlay arg0, int arg1) {
		Toast.makeText(this, "unfortunately", Toast.LENGTH_LONG);
		return false;
	}


	@Override
	public boolean finishRouteSearch(RouteOverlay arg0) {
		Toast.makeText(this, "上手にできました～♪", Toast.LENGTH_LONG);
		return false;
	}

}