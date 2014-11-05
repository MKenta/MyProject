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
    
   //���ݒl�ۑ��p�̃t�@�C����
  	String pointFileName = "location_temp";
  	
  	//push�`�����l���p
  	String channel;
  	
	//�ܓx,�o�x���i�[���鐮���^�z��(�t�@�C���ۑ�)
	int point[] = new int[2];
	
	int select = 0;
	
	//�ʒm�̃I���I�t�ݒ�
	boolean pushSet = false;
	
	//���݂̍��W���i�[
	static GeoPoint nowCoord;	
	//�{��
	private Context context=this;	
	//���ԕϐ�
	private Time time=new Time();	
	//View�p
	String estimate="JON";
	int ipower;
	MainActivity mainActivity=this;
	
	//�^�C�}�[�֘A:�����\���ƃA�j���[�V�����p
	private Timer mainTimer;					//�^�C�}�[�p
	private MainTimerTask mainTimerTask;		//�^�C�}�^�X�N�N���X
	private Handler mHandler = new Handler();   //UI Thread�ւ�post�p�n���h��
	private boolean animation = true;			//���݂ɉ摜��؂�ւ��ăA�j���[�V����
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//�^�C�}�[�C���X�^���X����
		this.mainTimer = new Timer();
		//�^�X�N�N���X�C���X�^���X����
		this.mainTimerTask = new MainTimerTask();				
		//�^�C�}�[�X�P�W���[���ݒ聕�J�n
		this.mainTimer.schedule(mainTimerTask, 1000,500);

		//�}�b�v�֘A�̐錾����ѐݒ�
	    final MapView mapView = new MapView(this, "dj0zaiZpPVNFc3FWbDFuYW9kdCZzPWNvbnN1bWVyc2VjcmV0Jng9ZjA-");
	    mapView.getMapController().setZoom(7);
	    
	    //��߂悤
	      stopService( new Intent( MainActivity.this, MyService.class ) );
	    
	    //�}�b�v��\�����郌�C�A�E�g�̃I�u�W�F�N�g�̎擾
	    RelativeLayout lmap = (RelativeLayout)findViewById(R.id.rainmap);
	    
	    //MyLocationOverlay�C���X�^���X�쐬
	    final MyLocationOverlay _overlay = new MyLocationOverlay(getApplicationContext(), mapView);
	    
	    //���݈ʒu�擾�J�n
	  	_overlay.enableMyLocation();
	  	   	    
	  	   //�t�@�C������O��N�����̌��ݒl��ǂݍ���
	     	try {
	  		FileInputStream fileInput = openFileInput(pointFileName);
	  		ObjectInputStream pointInput = new ObjectInputStream(fileInput);
	  		point = (int[]) pointInput.readObject();
	  		pointInput.close();
	  		GeoPoint tempPoint = new GeoPoint(point);
	  		mapView.getMapController().setCenter(tempPoint);
	  	} catch (Throwable e) {
	  		// TODO �����������ꂽ catch �u���b�N
	  		e.printStackTrace();
	  		point = new int[2];
	  		Log.d("hoge", "Import failue");
	  	}
	  	    
	  	//�ʒu���X�V�����ƁA�n�}�̈ʒu���ς��悤�ݒ�
	    _overlay.runOnFirstFix(new Runnable(){
	  	    public void run() {
	  	        if (mapView.getMapController() != null) {
	  	        	
	  	        	//���݈ʒu���擾
	  	    	    nowCoord = _overlay.getMyLocation();
	  	    	    point[0] = nowCoord.getLatitudeE6();
	  	    	    point[1] = nowCoord.getLongitudeE6();
	  	    	    
	  	    	    //�t�@�C���Ɍ��ݒl��ۑ�
	  	    	    try {
	  		        	FileOutputStream fileOut = openFileOutput(pointFileName, Context.MODE_PRIVATE);
	  		        	ObjectOutputStream pointOut;
	  					pointOut = new ObjectOutputStream(fileOut);
	  		        	pointOut.writeObject(point);
	  		        	pointOut.close();
	  				} catch (Throwable e) {
	  					// TODO �����������ꂽ catch �u���b�N
	  					e.printStackTrace();
	  					Log.d("hoge", "Output failue");
	  				}
	  	            	
	  	            //�n�}�ړ�
	  	            mapView.getMapController().animateTo(nowCoord);
	  	                
	  	        	//�~��\��
	  	            circleOverlay = new CircleOverlay(nowCoord, 8000, 8000){
	  	        	    @Override
	  	        	    protected boolean onTap(){
	  	        	      //�~���^�b�`�����ۂ̏���
	  	        	      return true;
	  	        	    }
	  	        	};
	  	        	circleOverlay.setStrokeWidth(20);
	  	        	circleOverlay.setStrokeColor(Color.argb(100, 0, 0, 255));
	  	        	mapView.getOverlays().add(circleOverlay);
	  	        	    
	  	        }
	  	    }
	  	});
	    
	    //MapView��MyLocationOverlay��ǉ��B
	    mapView.getOverlays().add(_overlay);
	    
	   //WeatherOverlay���쐬
	    final WeatherOverlay weatherOverlay = new WeatherOverlay(this);
	    
	    //WeatherOverlayListener��ݒ�
	    weatherOverlay.setWeatherOverlayListener(this);

	    //MapView��WeatherOverlay��ǉ�
	    mapView.getOverlays().add(weatherOverlay);
	    
	    //���[�_�[���X�V
	    weatherOverlay.updateWeather(0);
	    
		lmap.addView(mapView);
	    /*�}�b�v���������܂�*/
		
        // �{�^���̃I�u�W�F�N�g���擾
        Button rainmapbtn = (Button)findViewById(R.id.rainmapbtn);
        Button taxibtn = (Button)findViewById(R.id.taxibtn);
        
        //���\�[�X�N���X
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
        	timeText.setText("�ǂݍ��ݒ�");        	
        	power.setImageResource(R.drawable.x1);
        }
        
        // �N���b�N�C�x���g���󂯎���悤�ɂ���
        rainmapbtn.setOnClickListener(new View.OnClickListener() {
    	   @Override
    	      public void onClick(View v) {
    		   //Auto-generated method stub
    		   // �C���e���g�̃C���X�^���X����
    		   Intent intent = new Intent(MainActivity.this, RainMapActivity.class);
    		   // ����ʂ̃A�N�e�B�r�e�B�N��
    		   startActivity(intent);
    	   }
        });
        
        taxibtn.setOnClickListener(new View.OnClickListener() {
    	   @Override
    	      public void onClick(View v) {
    		   //Auto-generated method stub
    		   // �C���e���g�̃C���X�^���X����
    		   Intent intent = new Intent(MainActivity.this, TaxiActivity.class);
    		   // ����ʂ̃A�N�e�B�r�e�B�N��
    		   startActivity(intent);
    	   }
        });
        
        //���݂̓V�C���擾���ăA�C�R����ύX
        new AsyncTask<Void, Void, String>() {
        	public String now_weather = new String();
        	//HTTP�ʐM��Async�^�X�N���ł��Ȃ��ƃr���h�ŗ�����
		    @Override
			public String doInBackground(Void... params) {
		    	String lat = "35.942756";
		    	String lon = "136.198842";
		    	
			try {
			    
			    StringBuilder builder = new StringBuilder();
			    //HttpClient�̃C���X�^���X�����iHTTP���N�G�X�g�𑗂邽�߂ɕK�v�j
			    HttpClient client = new DefaultHttpClient();
			    //HttpGet�̃C���X�^���X�����iGET���N�G�X�g�𑗂邽�߂ɕK�v)
			    //URL��coordinates (���W)���擾����URL�Ɍ���������(�����)
			    HttpGet httpGet = new HttpGet(
						"http://api.openweathermap.org/data/2.5/find?lat="+lat+"&lon="+lon+"&appid=13034033b7a9eef9a0b2cc36caf985af&cnt=1");
			    try {
			    	//���N�G�X�g���������N�����݂��邩�m�F���邽�߂ɁAHTTP���N�G�X�g�𑗂���HTTP���X�|���X���擾����
			    	HttpResponse response = client.execute(httpGet);
			    	//�ԋp���ꂽHTTP���X�|���X�̒��̃X�e�[�^�X�R�[�h�𒲂ׂ�
			    	// -> statusCode��200��������y�[�W�����݁B404��������Not found�i�y�[�W�����݂��Ȃ��j�B500��Internal server error�B
			    	int statusCode = response.getStatusLine().getStatusCode();
			    	if (statusCode == 200) {
			    		//HTTP���X�|���X��200���y�[�W�͑��݂���
			    		//���X�|���X����HTTP�G���e�B�e�B�i���́j�𐶐�
			    		HttpEntity entity = response.getEntity();
			    		//HTTP�G���e�B�e�B����R���e���g�i���g�j�𐶐�
			    		InputStream content = entity.getContent();
			    		//�R���e���g����InputStreamReader�𐶐����A�����BufferedReader�����
			    		//InputStreamReader�̓e�L�X�g�t�@�C���iInputStream�j��ǂݍ���
			    		//BufferedReader�̓e�L�X�g�t�@�C������s���ǂݍ���
			    		//�i�Q�l�jhttp://www.tohoho-web.com/java/file.htm
			    		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
			    		String line;
			    		//reader����readline()�ōs��ǂ�ŁAbuilder������(StringBuilder�N���X)�Ɋi�[���Ă����B
			    		//�����̃v���O�����̏ꍇ�Aline�͈�s�łȂ̂Ń��[�v�͉���Ă��Ȃ�
			    		//��BufferedReader���g���Ƃ��͈�ʂɂ��̂悤�ɋL�q����B
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
			    		// �����ł�finally��shutdown()���Ă��邪�AHttpClient���g���񂷏ꍇ�́A
			    		// �K�؂ȂƂ���ōs�����ƁB���R����shutdown()�����C���X�^���X�͒ʐM�ł��Ȃ��Ȃ�B
			    		client.getConnectionManager().shutdown();
			    	}
			    	// �������JSON�I�u�W�F�N�g�ɕϊ�����
			    
			    	try {
			    		JsonNode node = new ObjectMapper().readTree(builder.toString());
				
			    		if (node != null){
			    			now_weather=node.path("list").path(0).path("weather").path(0).path("main").asText();
			    		}
			    		System.out.println();//���s
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
		    		/*�A�C�R�����_��*/
		    		weatherIcon.setImageResource(R.drawable.icon_cloud3);
		    		background.setBackground(res.getDrawable(R.drawable.back_cloud2));
		    	}
		    	else if(now_weather.equals("Clear")){
		    		/*����(?)*/
		    		weatherIcon.setImageResource(R.drawable.icon_sun3);
		    		background.setBackground(res.getDrawable(R.drawable.back_sky1));
		    	}
		    	else {
		    		/*�J*/
		    		weatherIcon.setImageResource(R.drawable.icon_rain3);
		    		background.setBackground(res.getDrawable(R.drawable.back_rain3));
		    	}
		    }
        }.execute();
        
        
        /********************
         *	��������͒ʒm�̂��b	* 
         ********************/
        NCMB.initialize(this, "846665d3a9fcb4132d9264f7a629e57ed4e67ee28fe111e08867208186d27d10", "3eb6b382945857dc1b9611ce59a3db7cd6ee2b7bd544f2d5df0424d62028c762");
 		
 		//�v�b�V���ʒm�`�����l���̐ݒ�
 		subscribe();
         
 		final NCMBInstallation installation = NCMBInstallation.getCurrentInstallation();
 		installation.getRegistrationIdInBackground("575549731285", new RegistrationCallback(){
 			@Override
 			public void done(NCMBException e) {
 				if (e != null) {
 					// ���s
 					e.printStackTrace();
 				} else {
 					// ����
 					try {
 						installation.save();
 						Log.d("JON", "installation complete!");
 					} catch(NCMBException le) {
 			                if (NCMBException.DUPLICATE_VALUE.equals(le.getCode())){
 			                    // ���W�X�g���[�V����ID�̏d���G���[
 			                    // ���[�U�[�ɂ�郍�[�J���f�[�^�폜��A�A�v���ăC���X�g�[�������ꂽ�ꍇ�ɔ����B
 			                    // �ȉ��A�N���E�h���Ɋ��o�^�̏�񂩂烍�[�J���f�[�^�𕜋����鏈���B
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

 		// �v�b�V���J���o�^�̎��{
 		NCMBAnalytics.trackAppOpened(getIntent());
	}
	
	public class MainTimerTask extends TimerTask {
		@Override
		public void run() {
			//�����ɒ�����Ŏ��s�������������L�q���܂�			
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
		        // �A���[�g�_�C�A���O�̃^�C�g����ݒ肵�܂�
		        alertDialogBuilder.setTitle("�v�b�V���ʒm�̐ݒ�");
		        // �A���[�g�_�C�A���O�̃��b�Z�[�W��ݒ肵�܂�
		        alertDialogBuilder.setMessage("�v�b�V���ʒm���I�t�ɂ��܂����H");
		        // �A���[�g�_�C�A���O�̍m��{�^�����N���b�N���ꂽ���ɌĂяo�����R�[���o�b�N���X�i�[��o�^���܂�
		        alertDialogBuilder.setPositiveButton("�͂�",new DialogInterface.OnClickListener() {
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		              	      // �T�[�r�X�J�n
		              	      stopService( new Intent( MainActivity.this, MyService.class ) );
		                    	pushSet = false;
		                    }
		                });
		        // �A���[�g�_�C�A���O�̔ے�{�^�����N���b�N���ꂽ���ɌĂяo�����R�[���o�b�N���X�i�[��o�^���܂�
		        alertDialogBuilder.setNegativeButton("������",new DialogInterface.OnClickListener() {
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                    }
		                });
		        // �A���[�g�_�C�A���O�̃L�����Z�����\���ǂ�����ݒ肵�܂�
		        alertDialogBuilder.setCancelable(true);
		        AlertDialog alertDialog = alertDialogBuilder.create();
		        // �A���[�g�_�C�A���O��\�����܂�
		        alertDialog.show();
		} else {
			 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		        // �A���[�g�_�C�A���O�̃^�C�g����ݒ肵�܂�
		        alertDialogBuilder.setTitle("�v�b�V���ʒm�̐ݒ�");
		        // �A���[�g�_�C�A���O�̃��b�Z�[�W��ݒ肵�܂�
		        alertDialogBuilder.setMessage("�v�b�V���ʒm���I���ɂ��܂����H");
		        // �A���[�g�_�C�A���O�̍m��{�^�����N���b�N���ꂽ���ɌĂяo�����R�[���o�b�N���X�i�[��o�^���܂�
		        alertDialogBuilder.setPositiveButton("�͂�",new DialogInterface.OnClickListener() {
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		              	      // �T�[�r�X��~
		              	      startService( new Intent( MainActivity.this, MyService.class ) );
		                    	pushSet = true;
		                    }
		                });
		        // �A���[�g�_�C�A���O�̔ے�{�^�����N���b�N���ꂽ���ɌĂяo�����R�[���o�b�N���X�i�[��o�^���܂�
		        alertDialogBuilder.setNegativeButton("������",new DialogInterface.OnClickListener() {
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                    }
		                });
		        // �A���[�g�_�C�A���O�̃L�����Z�����\���ǂ�����ݒ肵�܂�
		        alertDialogBuilder.setCancelable(true);
		        AlertDialog alertDialog = alertDialogBuilder.create();
		        // �A���[�g�_�C�A���O��\�����܂�
		        alertDialog.show();
		}
	}

	@Override
	public void errorUpdateWeather(WeatherOverlay arg0, int arg1) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}

	@Override
	public void finishUpdateWeather(WeatherOverlay arg0) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}
	/** �`�����l����ݒ� */
	private void subscribe() {
		WifiManager wifiManager=(WifiManager)this.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo=wifiManager.getConnectionInfo();
		channel=String.valueOf(wifiInfo.getMacAddress());
		if(channel==null){
			channel="Sonic";
		}
		NCMBPush.subscribe(this, channel, MainActivity.class);
		//dispMessage("�`�����l���y"+channel+"�z��ݒ�");
	}
	/** Push�ʒm�̎��{ */
	public void sendPush(String minutes) throws JSONException {
		NCMBPush push = new NCMBPush();
		// �`�����l���̐ݒ�
		//dispMessage(channel);
		push.setChannel(channel);
		// �I�v�V�����l�̐ݒ�i�J�X�^�����V�[�o�̃t�B���^�ݒ�A�^�C�g���̐ݒ�A�z�M�[���̐ݒ�j
		JSONObject data = new JSONObject("{\"action\": \"com.jojon.RECEIVE_PUSH\", " +
				"\"title\": \"jojo\",\"dialogFlag\":\"true\", \"target\": [android]}");
		push.setData(data);
		// ���[�U��`�l�̐ݒ�
		JSONObject user = new JSONObject("{\"URL\": \"http://www.google.co.jp/\"}");
		push.setUserSettingValue(user);
		// ���b�Z�[�W�̐ݒ�
//		time.setToNow();
		push.setMessage(minutes);
		// �����z�M�t���O�̐ݒ�
		push.setImmediateDeliveryFlag(true);
		//�_�C�A���O�̗L����
		push.setDialog(true);
		push.sendInBackground(new SendCallback() {
			@Override
			public void done(NCMBException e) {
				if (e != null) {
					e.printStackTrace();
					dispMessage("Push�o�^�Ɏ��s");
				} else {
					dispMessage("Push�o�^�ɐ���");
				}
			}
		});
	}
	private void dispMessage(String message){
//		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	public void weatherCheck(){		
		//���ݒn
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}
}