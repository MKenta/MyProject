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
		
		//�^�N�V�[API����		
        new AsyncTask<Void, Void, String>() {
	    //HTTP�ʐM��Async�^�X�N���ł��Ȃ��ƃr���h�ŗ�����
	    @Override
		public String doInBackground(Void... params) {
		try {
		    StringBuilder builder = new StringBuilder();
		    //HttpClient�̃C���X�^���X�����iHTTP���N�G�X�g�𑗂邽�߂ɕK�v�j
		    HttpClient client = new DefaultHttpClient();
		    //HttpGet�̃C���X�^���X�����iGET���N�G�X�g�𑗂邽�߂ɕK�v)
		    //URL��coordinates (���W)���擾����URL�Ɍ���������(�����)
		    HttpGet httpGet = new HttpGet(
						  "http://search.olp.yahooapis.jp/OpenLocalPlatform/V1/localSearch?&query=%E3%82%BF%E3%82%AF%E3%82%B7%E3%83%BC&appid=dj0zaiZpPUZickhJWDdMZ05tYSZzPWNvbnN1bWVyc2VjcmV0Jng9OWE-&output=json&lat=36.198843&lon=135.942756&dist=50");
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
	    		//�e�e�L�X�g�̐錾
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
	    		
	    		//�e�L�X�g�ւ̑��
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
