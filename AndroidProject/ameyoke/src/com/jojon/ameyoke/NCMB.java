package com.jojon.ameyoke;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.view.Display;
import android.widget.Toast;

import com.nifty.cloud.mb.NCMBException;
import com.nifty.cloud.mb.NCMBPush;
import com.nifty.cloud.mb.SendCallback;

public class NCMB{
	String channel=null;
	Context context=new Context() {		
		@Override
		public void unregisterReceiver(BroadcastReceiver receiver) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void unbindService(ServiceConnection conn) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public boolean stopService(Intent service) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return false;
		}
		
		@Override
		public ComponentName startService(Intent service) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public void startIntentSender(IntentSender intent, Intent fillInIntent,
				int flagsMask, int flagsValues, int extraFlags, Bundle options)
				throws SendIntentException {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void startIntentSender(IntentSender intent, Intent fillInIntent,
				int flagsMask, int flagsValues, int extraFlags)
				throws SendIntentException {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public boolean startInstrumentation(ComponentName className,
				String profileFile, Bundle arguments) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return false;
		}
		
		@Override
		public void startActivity(Intent intent, Bundle options) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void startActivity(Intent intent) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void startActivities(Intent[] intents, Bundle options) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void startActivities(Intent[] intents) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		@Deprecated
		public void setWallpaper(InputStream data) throws IOException {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		@Deprecated
		public void setWallpaper(Bitmap bitmap) throws IOException {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void setTheme(int resid) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void sendStickyOrderedBroadcast(Intent intent,
				BroadcastReceiver resultReceiver, Handler scheduler,
				int initialCode, String initialData, Bundle initialExtras) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void sendStickyBroadcast(Intent intent) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void sendOrderedBroadcast(Intent intent, String receiverPermission,
				BroadcastReceiver resultReceiver, Handler scheduler,
				int initialCode, String initialData, Bundle initialExtras) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void sendBroadcast(Intent intent, String receiverPermission) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void sendBroadcast(Intent intent) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void revokeUriPermission(Uri uri, int modeFlags) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void removeStickyBroadcast(Intent intent) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public Intent registerReceiver(BroadcastReceiver receiver,
				IntentFilter filter, String broadcastPermission, Handler scheduler) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public Intent registerReceiver(BroadcastReceiver receiver,
				IntentFilter filter) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		@Deprecated
		public Drawable peekWallpaper() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public SQLiteDatabase openOrCreateDatabase(String name, int mode,
				CursorFactory factory, DatabaseErrorHandler errorHandler) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public SQLiteDatabase openOrCreateDatabase(String name, int mode,
				CursorFactory factory) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public FileOutputStream openFileOutput(String name, int mode)
				throws FileNotFoundException {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public FileInputStream openFileInput(String name)
				throws FileNotFoundException {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		@Deprecated
		public int getWallpaperDesiredMinimumWidth() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return 0;
		}
		
		@Override
		@Deprecated
		public int getWallpaperDesiredMinimumHeight() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return 0;
		}
		
		@Override
		@Deprecated
		public Drawable getWallpaper() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public Theme getTheme() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public Object getSystemService(String name) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public SharedPreferences getSharedPreferences(String name, int mode) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public Resources getResources() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public String getPackageResourcePath() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public String getPackageName() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public PackageManager getPackageManager() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public String getPackageCodePath() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public File getObbDir() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public Looper getMainLooper() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public File getFilesDir() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public File getFileStreamPath(String name) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public File getExternalFilesDir(String type) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public File getExternalCacheDir() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public File getDir(String name, int mode) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public File getDatabasePath(String name) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public ContentResolver getContentResolver() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public ClassLoader getClassLoader() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public File getCacheDir() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public AssetManager getAssets() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public ApplicationInfo getApplicationInfo() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public Context getApplicationContext() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public String[] fileList() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public void enforceUriPermission(Uri uri, String readPermission,
				String writePermission, int pid, int uid, int modeFlags,
				String message) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags,
				String message) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void enforcePermission(String permission, int pid, int uid,
				String message) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void enforceCallingUriPermission(Uri uri, int modeFlags,
				String message) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void enforceCallingPermission(String permission, String message) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags,
				String message) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public void enforceCallingOrSelfPermission(String permission, String message) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public boolean deleteFile(String name) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return false;
		}
		
		@Override
		public boolean deleteDatabase(String name) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return false;
		}
		
		@Override
		public String[] databaseList() {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		public Context createPackageContext(String packageName, int flags)
				throws NameNotFoundException {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
		
		@Override
		@Deprecated
		public void clearWallpaper() throws IOException {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
		@Override
		public int checkUriPermission(Uri uri, String readPermission,
				String writePermission, int pid, int uid, int modeFlags) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return 0;
		}
		
		@Override
		public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return 0;
		}
		
		@Override
		public int checkPermission(String permission, int pid, int uid) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return 0;
		}
		
		@Override
		public int checkCallingUriPermission(Uri uri, int modeFlags) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return 0;
		}
		
		@Override
		public int checkCallingPermission(String permission) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return 0;
		}
		
		@Override
		public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return 0;
		}
		
		@Override
		public int checkCallingOrSelfPermission(String permission) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return 0;
		}
		
		@Override
		public boolean bindService(Intent service, ServiceConnection conn, int flags) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return false;
		}

		@Override
		public void sendBroadcastAsUser(Intent intent, UserHandle user) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}

		@Override
		public void sendBroadcastAsUser(Intent intent, UserHandle user,
				String receiverPermission) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}

		@Override
		public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user,
				String receiverPermission, BroadcastReceiver resultReceiver,
				Handler scheduler, int initialCode, String initialData,
				Bundle initialExtras) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}

		@Override
		public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}

		@Override
		public void sendStickyOrderedBroadcastAsUser(Intent intent,
				UserHandle user, BroadcastReceiver resultReceiver,
				Handler scheduler, int initialCode, String initialData,
				Bundle initialExtras) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}

		@Override
		public void removeStickyBroadcastAsUser(Intent intent, UserHandle user) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}

		@Override
		public Context createConfigurationContext(
				Configuration overrideConfiguration) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}

		@Override
		public Context createDisplayContext(Display display) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			return null;
		}
	};
	/** �`�����l����ݒ� */
	private void subscribe() {
		WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo=wifiManager.getConnectionInfo();
		channel=String.valueOf(wifiInfo.getMacAddress());
		if(channel==null){
			channel="Sonic";
		}
		NCMBPush.subscribe(context, channel, MainActivity.class);
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
		if(minutes==null){
			minutes="null";
		}
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
	public void SetContext(Context mContext){
    	context=mContext;
    }
	private void dispMessage(String message){
//		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
}