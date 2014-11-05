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
	static String time="�ǂݍ��ݒ�";
	boolean pushFlag;
	 @Override
		public String doInBackground(Void... params) {
		try {			    
		    StringBuilder builder = new StringBuilder();
		    //HttpClient�̃C���X�^���X�����iHTTP���N�G�X�g�𑗂邽�߂ɕK�v�j
		    HttpClient client = new DefaultHttpClient();
		    //HttpGet�̃C���X�^���X�����iGET���N�G�X�g�𑗂邽�߂ɕK�v)
		    //URL��coordinates (���W)���擾����URL�Ɍ���������(�����)
//		    nowCoord.setLatitudeE6(33926963);
//		    nowCoord.setLongitudeE6(1346596150);
		    HttpGet httpGet = new HttpGet(
						  "http://weather.olp.yahooapis.jp/v1/place?appid=dj0zaiZpPUZickhJWDdMZ05tYSZzPWNvbnN1bWVyc2VjcmV0Jng9OWE-&coordinates="+String.valueOf(nowCoord.getLongitude())+","+String.valueOf(nowCoord.getLatitude())+"&output=json");
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
			    System.out.println("finally����");
		    }
		    // �������JSON�I�u�W�F�N�g�ɕϊ�����
		    try {
		    	JsonNode node = new ObjectMapper().readTree(builder.toString());
		    	if (node != null){	 				    
				    System.out.println(node.path("ResultInfo").path("Copyright").asText());
				    JsonNode secondnode = node.path("Feature");
				    String[] rainfallString = new String[6];//������̍~�J��
				    String[] rainfallDateString = new String[6];//�~�J����
				    Double[] rainfallDouble = new Double[6];//�����^�̍~�J��
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
					    	/* �~�J�J�n*/
					    		if(i==0)
					    			rainfallflag = true;
					    		if((10<rainfallDouble[i]) && (20>rainfallDouble[i])&&!rainfallflag){
					    			/*�~�J�� ��*/
					    			power=2;
					    			time=String.valueOf(5*(i+1))+"����";
					    		}else if(20 <= rainfallDouble[i]&&!rainfallflag){
					    			/*�~�J�ʁ@��*/		
					    			power=3;
					    			time=String.valueOf(5*(i+1))+"����";
					    		}else if(!rainfallflag){ 
					    			/*�~�J�� ��*/
					    			power=1;
					    			time=String.valueOf(5*(i+1))+"����";
					    		}
					    		break;
					    }else{
			    			power=0;
			    			time="�J�͍~��܂���";			    	
					    }
			    	}
				    if(rainfallflag){
				    	for(i = 0; i < 6; i++){
				    		if(rainfallDouble[i]==0){
				    			/*�~�J�̏I���*/
				    			System.out.println(rainfallDateString[i]+"�ɉJ����݂܂�");
				    			time=String.valueOf(10*(i+1))+"����ɉJ����݂܂�";
				    			break;
				    		}else{
				    			if((10<rainfallDouble[i]) && (20>rainfallDouble[i])){
					    			/*�~�J�� ��*/
					    			power=2;				    				
				    			}else if (20 <= rainfallDouble[i]) {
					    			/*�~�J�ʁ@��*/		
					    			power=3;
								}else{
					    			/*�~�J�� ��*/
					    			power=1;
								}
				    			System.out.println("�J��1���Ԉȏ��݂܂���");
				    			time="�J��1���Ԉȏ��݂܂���";
				    		}
				    	}
				    }
				}
				System.out.println();//���s
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