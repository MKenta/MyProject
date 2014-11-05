package com.jojon.ameyoke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jp.co.yahoo.android.maps.GeoPoint;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jojon.ameyoke.NCMB;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



public class MyAsyncTask extends AsyncTask<Void, Void, String>{
	GeoPoint nowCoord;
	Context context;
	View view;
	TextView textView;
	ImageView imageView;
	static int power;
	static String time="読み込み中";
	boolean pushFlag;
	 @Override
		public String doInBackground(Void... params) {
		try {			    
		    StringBuilder builder = new StringBuilder();
		    //HttpClientのインスタンスを作る（HTTPリクエストを送るために必要）
		    HttpClient client = new DefaultHttpClient();
		    //HttpGetのインスタンスを作る（GETリクエストを送るために必要)
		    //URLのcoordinates (座標)を取得してURLに結合したい(やって)
//		    nowCoord.setLatitudeE6(33926963);
//		    nowCoord.setLongitudeE6(1346596150);
		    HttpGet httpGet = new HttpGet(
						  "http://weather.olp.yahooapis.jp/v1/place?appid=dj0zaiZpPUZickhJWDdMZ05tYSZzPWNvbnN1bWVyc2VjcmV0Jng9OWE-&coordinates="+String.valueOf(nowCoord.getLongitude())+","+String.valueOf(nowCoord.getLatitude())+"&output=json");
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
			    System.out.println("finallyした");
		    }
		    // 文字列をJSONオブジェクトに変換する
		    try {
		    	JsonNode node = new ObjectMapper().readTree(builder.toString());
		    	if (node != null){	 				    
				    System.out.println(node.path("ResultInfo").path("Copyright").asText());
				    JsonNode secondnode = node.path("Feature");
				    String[] rainfallString = new String[6];//文字列の降雨量
				    String[] rainfallDateString = new String[6];//降雨日時
				    Double[] rainfallDouble = new Double[6];//実数型の降雨量
				    int i =0;
				    boolean rainfallflag = false;
				    for(i = 0;i < 6;i++){
				    rainfallString[i]=secondnode.path(0).path("Property").path("WeatherList").path("Weather").path(i).path("Rainfall").asText();
				    rainfallDateString[i]=secondnode.path(0).path("Property").path("WeatherList").path("Weather").path(i).path("Date").asText();
				    Log.d("JON",rainfallDateString[i]);
				    Log.d("JON",rainfallString[i]);

				    rainfallDouble[i] = Double.parseDouble(rainfallString[i]);
				    }
				    for(i = 0; i < 6 ;i++){
				    	if( 0 < rainfallDouble[i] ){
					    	/* 降雨開始*/
					    		if(i==0)
					    			rainfallflag = true;
					    		if((10<rainfallDouble[i]) && (20>rainfallDouble[i])&&!rainfallflag){
					    			/*降雨量 中*/
					    			power=2;
					    			time=String.valueOf(5*(i+1))+"分後";
					    		}else if(20 <= rainfallDouble[i]&&!rainfallflag){
					    			/*降雨量　強*/		
					    			power=3;
					    			time=String.valueOf(5*(i+1))+"分後";
					    		}else if(!rainfallflag){ 
					    			/*降雨量 小*/
					    			power=1;
					    			time=String.valueOf(5*(i+1))+"分後";
					    		}
					    		break;
					    }else{
			    			power=0;
			    			time="雨は降りません";			    	
					    }
			    	}
				    if(rainfallflag){
				    	for(i = 0; i < 6; i++){
				    		if(rainfallDouble[i]==0){
				    			/*降雨の終わり*/
				    			System.out.println(rainfallDateString[i]+"に雨がやみます");
				    			time=String.valueOf(10*(i+1))+"分後に雨がやみます";
				    			break;
				    		}else{
				    			if((10<rainfallDouble[i]) && (20>rainfallDouble[i])){
					    			/*降雨量 中*/
					    			power=2;				    				
				    			}else if (20 <= rainfallDouble[i]) {
					    			/*降雨量　強*/		
					    			power=3;
								}else{
					    			/*降雨量 小*/
					    			power=1;
								}
				    			System.out.println("雨は1時間以上やみません");
				    			time="雨は1時間以上やみません";
				    		}
				    	}
				    }
				}
				System.out.println();//改行
			    } catch (Exception e) {
			    	e.printStackTrace();
					return "";
				}
		    if(power!=0 && pushFlag){
			    NCMB ncmb=new NCMB();
			    ncmb.sendPush(time);
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
	    }
	    
	    public void SetContext(Context mContext){
	    	context=mContext;
	    }
	    public void SetGeoPoint(GeoPoint mGeoPoint){
	    	nowCoord=mGeoPoint;
	    }
	    public void SetView(View mView){
	    	view=mView;
	    }
	    public void SetImage(ImageView mImageView){
	    	imageView=mImageView;
	    }
	    public void SetText(TextView mteTextView){
	    	textView=mteTextView;
	    }
	    public int GetPower(){
	    	return power;
	    }
	    public String GetTime(){
	    	return time;
	    }
	    public void SetPushflag(boolean flag){
	    	pushFlag=flag;
	    }
}