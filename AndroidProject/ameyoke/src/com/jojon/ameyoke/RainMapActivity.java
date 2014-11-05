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

// 3. �uYahoo!�n�}�v��\���������A�N�e�B�r�e�B���ijp.co.yahoo.android.maps.�jMapActivity ���p�����č쐬����
@SuppressLint("NewApi")
public class RainMapActivity extends MapActivity implements WeatherOverlayListener,RouteOverlayListener {
	
	//�F�ύX�Ȃǂ̏����̂��߂ɑ��ϐ��ŉ~�̃��C���[������Ă���
	CircleOverlay circleOverlay;
	
	//���ݒl�ۑ��p�̃t�@�C����
	String pointFileName = "location_temp";
	
	//�ܓx,�o�x���i�[���鐮���^�z��(�t�@�C���ۑ�)
	int point[] = new int[2];
	
	//�J�_���[�_�[�̃I���I�t
	boolean radar = true;
	
	//�\������J�_�̎���
	int radarTime = 0;
	
	//���݂̍��W���i�[
	private GeoPoint nowCoord;
	
	//�{��
	Context context=this;
	RouteOverlayListener routeOverlayListener=this;
	
	//�������ʂ��i�[
	ArrayList<GeoPoint> goalPoints=new ArrayList<GeoPoint>();
	ArrayList<String> goalNames=new ArrayList<String>();
	int GoalNum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_rainmap);
	    
	    //���\�[�X�N���X
	    final Resources res = getResources();
	    
	    //�}�b�v�֘A�̐錾����ѐݒ�
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
	    
	    //MyLocationOverlay�C���X�^���X�쐬
	    final MyLocationOverlay _overlay = new MyLocationOverlay(getApplicationContext(), mapView);
	    mapController.setCenter(_overlay.getMyLocation());
	    
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
			point = new int[2];	//�ʂ�ۂ������͍Đ錾����΂����̂ł�
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
					
					new AsyncTask<Void, Void, String>() {
						String DEBUG="0";
					    //HTTP�ʐM��Async�^�X�N���ł��Ȃ��ƃr���h�ŗ�����
					    @Override
						protected String doInBackground(Void... params) {
						try { 
						    StringBuilder builder = new StringBuilder();
						    //HttpClient�̃C���X�^���X�����iHTTP���N�G�X�g�𑗂邽�߂ɕK�v�j
						    HttpClient client = new DefaultHttpClient();
						    //HttpGet�̃C���X�^���X�����iGET���N�G�X�g�𑗂邽�߂ɕK�v)
						    //URL��coordinates (���W)���擾����URL�Ɍ���������(�����)
						    String lat = String.valueOf(nowCoord.getLatitude());
						    String lon = String.valueOf(nowCoord.getLongitude());
						    
						    HttpGet httpGet = new HttpGet(
						    		"http://search.olp.yahooapis.jp/OpenLocalPlatform/V1/localSearch?&query=%E3%82%B3%E3%83%B3%E3%83%93%E3%83%8B&appid=dj0zaiZpPUZickhJWDdMZ05tYSZzPWNvbnN1bWVyc2VjcmV0Jng9OWE-&output=json&lat="+lat+"&lon="+lon+"&dist=10");
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
								JsonNode node = null;
								try {
									node = new ObjectMapper().readTree(builder.toString());
								} catch (JsonProcessingException e) {
									// TODO �����������ꂽ catch �u���b�N
									e.printStackTrace();
								} catch (IOException e) {
									// TODO �����������ꂽ catch �u���b�N
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
									    
									    //�s������
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
										    	//�o���n�s�����\��
										    	routeOverlay.get(routeOverlay.size()-1).setStartPinVisible(false);
										    	//�ړI�n�s�����\��
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
									    pinOverlay.addPoint(tmp, nameString, "�����܂ł̈ē���\������");
								    }
								}
								System.out.println();//���s
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
					
					
					
	                //�n�}�ړ�
	                mapView.getMapController().animateTo(nowCoord);
	                
	        	    //�~��\��
	        	    circleOverlay = new CircleOverlay(nowCoord, 8500, 8500){
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
	    
	    weatherOverlay.updateWeather(0);
	    
	    btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				//�J�_�̎�����60���O�ɂ���
				radarTime = -60;
				weatherOverlay.updateWeather(radarTime);
				
				//����̕����̕\���؂�ւ�
				if(radarTime == 0){
					timeSet.setText("����");
				}
				else if(radarTime > 0){
					timeSet.setText(radarTime + "����");
				}
				else{
					timeSet.setText((-1 * radarTime) + "���O");
				}
			}
		});
	    
		btn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				//�J�_�̎�����5���߂�
				radarTime -= 5;
				if(radarTime < -60){
					radarTime = -60;
				}
				weatherOverlay.updateWeather(radarTime);
				
				//����̕����̕\���؂�ւ�
				if(radarTime == 0){
					timeSet.setText("����");
				}
				else if(radarTime > 0){
					timeSet.setText(radarTime + "����");
				}
				else{
					timeSet.setText((-1 * radarTime) + "���O");
				}
			}
		});
		
		btn3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				//�J�_�̎��������݂ɖ߂�
				radarTime = 0;
				weatherOverlay.updateWeather(radarTime);
				
				//����̕����̕\���؂�ւ�
				if(radarTime == 0){
					timeSet.setText("����");
				}
				else if(radarTime > 0){
					timeSet.setText(radarTime + "����");
				}
				else{
					timeSet.setText((-1 * radarTime) + "���O");
				}
			}
		});
		
		btn4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				//�J�_�̎�����5���i�߂�
				radarTime += 5;
				if(radarTime > 60){
					radarTime = 60;
				}
				weatherOverlay.updateWeather(radarTime);
				
				//����̕����̕\���؂�ւ�
				if(radarTime == 0){
					timeSet.setText("����");
				}
				else if(radarTime > 0){
					timeSet.setText(radarTime + "����");
				}
				else{
					timeSet.setText((-1 * radarTime) + "���O");
				}
			}
		});
		
		btn5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				//�J�_�̎�����60����ɂ���
				radarTime = 60;
				weatherOverlay.updateWeather(radarTime);
				
				//����̕����̕\���؂�ւ�
				if(radarTime == 0){
					timeSet.setText("����");
				}
				else if(radarTime > 0){
					timeSet.setText(radarTime + "����");
				}
				else{
					timeSet.setText((-1 * radarTime) + "���O");
				}
			}
		});
		
		radarSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
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
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				mapView.getMapController().animateTo(_overlay.getMyLocation());
			}
		});

		
		lmap.addView(mapView);
		
		
    }
    //onCreate�����܂�
    	/************
		 * ��������JSoN	*
		 *************/
//    private class MyTask {
//    	
//    }

    //�J�_���[�_�[���̎擾�ŃG���[������������ʒm
    @Override
    public void errorUpdateWeather(WeatherOverlay weatherOverlay, int error) {

    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return false;
	}
	@Override
	public void finishUpdateWeather(WeatherOverlay arg0) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}


	@Override
	public boolean errorRouteSearch(RouteOverlay arg0, int arg1) {
		Toast.makeText(this, "unfortunately", Toast.LENGTH_LONG);
		return false;
	}


	@Override
	public boolean finishRouteSearch(RouteOverlay arg0) {
		Toast.makeText(this, "���ɂł��܂����`��", Toast.LENGTH_LONG);
		return false;
	}

}