package com.jojon.ameyoke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PublicKey;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class TaxiActivity extends Activity {
	
	String name[] = new String[10];
	String number[] = new String[10];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taxi);
		
		//タクシーAPI処理		
        new AsyncTask<Void, Void, String>() {
	    //HTTP通信はAsyncタスク内でやらないとビルドで落ちる
	    @Override
		public String doInBackground(Void... params) {
		try {
		    StringBuilder builder = new StringBuilder();
		    //HttpClientのインスタンスを作る（HTTPリクエストを送るために必要）
		    HttpClient client = new DefaultHttpClient();
		    //HttpGetのインスタンスを作る（GETリクエストを送るために必要)
		    //URLのcoordinates (座標)を取得してURLに結合したい(やって)
		    HttpGet httpGet = new HttpGet(
						  "http://search.olp.yahooapis.jp/OpenLocalPlatform/V1/localSearch?&query=%E3%82%BF%E3%82%AF%E3%82%B7%E3%83%BC&appid=dj0zaiZpPUZickhJWDdMZ05tYSZzPWNvbnN1bWVyc2VjcmV0Jng9OWE-&output=json&lat=36.198843&lon=135.942756&dist=50");
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
		    	String[] taxiCompany = new String[node.path("ResultInfo").path("Count").asInt()];
		    	String[] taxiTell = new String[node.path("ResultInfo").path("Count").asInt()];
		    	if (node != null){
		    		for(int i = 0; i<node.path("ResultInfo").path("Count").asInt(); i++){
		    			taxiCompany[i]=node.path("Feature").path(i).path("Name").asText();
		    			taxiTell[i]=node.path("Feature").path(i).path("Property").path("Tel1").asText();
		    			Log.v("Jon",taxiCompany[i]=node.path("Feature").path(i).path("Name").asText());
		    			Log.v("Jon",taxiTell[i]=node.path("Feature").path(i).path("Property").path("Tel1").asText());
		    			name[i] = taxiCompany[i];
		    			number[i] = taxiTell[i]; 
		    		}
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
	    		//各テキストの宣言
	    		TextView name0 = (TextView)findViewById(R.id.name0);
	    		TextView number0 = (TextView)findViewById(R.id.number0);
	    		TextView name1 = (TextView)findViewById(R.id.name1);
	    		TextView number1 = (TextView)findViewById(R.id.number1);
	    		TextView name2 = (TextView)findViewById(R.id.name2);
	    		TextView number2 = (TextView)findViewById(R.id.number2);
	    		TextView name3 = (TextView)findViewById(R.id.name3);
	    		TextView number3 = (TextView)findViewById(R.id.number3);
	    		TextView name4 = (TextView)findViewById(R.id.name4);
	    		TextView number4 = (TextView)findViewById(R.id.number4);
	    		
	    		//テキストへの代入
	    		name0.setText(name[0]);
	    		number0.setText(number[0]);
	    		name1.setText(name[1]);
	    		number1.setText(number[1]);
	    		name2.setText(name[2]);
	    		number2.setText(number[2]);
	    		name3.setText(name[3]);
	    		number3.setText(number[3]);
	    		name4.setText(name[4]);
	    		number4.setText(number[4]);
	    	}
        }.execute();
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
