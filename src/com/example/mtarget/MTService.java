package com.example.mtarget;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MTService extends Service {
    private static final String TAG = "MTService";
    public static final String RING_ACTION = "com.example.mttarget.callring";
    public static final String INCOMMING_NUM = "incomming_number";
    public static final String ACTION_SERVICE = "com.example.mtarget.service";
    private static final int NOTIFY_ID = 0;
    
    NotificationCompat.Builder notifyBuilder;
    private NotificationManager notificationManager;
	private MTReceiver mtReceiver;
    
	private BroadcastReceiver serviceReceiver = new BroadcastReceiver(){
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
	        if (action.equals(RING_ACTION)) {
	        	String bro_incoming_num = intent.getStringExtra(INCOMMING_NUM);
	        	//if(pref_incoming_num == bro_incoming_num){
	        	    endMyCall();
	        	    Log.i(TAG, "endMyCall over.");	        	    
	        	    startMyCall( bro_incoming_num);
	        	    Log.i(TAG, "startMyCall over.");
	        	//}else{
	        		//log anonymous number stay online
	        	//}
	        }
		};
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.e(TAG, "onCreate~~~");
		super.onCreate();
		setNotification();
		setReceiver();

	}
	
	@Override  
    public void onDestroy() {  
        Log.e(TAG, "onDestroy~~~");
        super.onDestroy();
        cancelReceiver();
        cancelNotification();
	}
	
	
	public void endMyCall() { //�Ҷϵ绰
	    TelephonyManager telMag = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
	    Class<TelephonyManager> c = TelephonyManager.class;
	    Method mthEndCall = null;
	    try {
	        mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
	        mthEndCall.setAccessible(true);
	        ITelephony iTel = (ITelephony) mthEndCall.invoke(telMag,
	                (Object[]) null);
	        iTel.endCall();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void startMyCall(String incoming_number) {
    	Log.i(TAG,"callback~~~"+incoming_number);
    	try {
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ incoming_number));
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.i(TAG,"callback sleep begin");
            Thread.sleep(3000);
            Log.i(TAG,"callback sleep end");
            getApplicationContext().startActivity(callIntent);
            Log.i(TAG,"callback~~~end"+incoming_number);
        } catch (Exception e) {
        	Log.i(TAG, "endMyCall exception ~~~",e);
            e.printStackTrace();  
        }

	}
	
	private void setNotification(){
		notifyBuilder = new NotificationCompat.Builder(this)
		  .setContentTitle(getString(R.string.app_name))
		  .setContentText(getString(R.string.mtarget_running))		
		  .setSmallIcon(R.drawable.ic_launcher)
		  .setWhen(System.currentTimeMillis())
		  .setOngoing(true);
		
		Intent intent = new Intent(this, MTActivity.class);
	    PendingIntent contextIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
	    notifyBuilder.setContentIntent(contextIntent);
		
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFY_ID, notifyBuilder.build());
	}

	private void cancelNotification() {
	    notificationManager.cancel(NOTIFY_ID);
	}
	
	private void setReceiver() {
		IntentFilter intentFilter = new IntentFilter(RING_ACTION);
        registerReceiver( serviceReceiver , intentFilter); 
        Log.i(TAG, "serviceReceiver registed");
        
		mtReceiver = new MTReceiver();
		IntentFilter mtFilter = new IntentFilter();
        mtFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        mtFilter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(mtReceiver, mtFilter);
        Log.i(TAG, "mtReceiver registed");
	}

	private void cancelReceiver() {
		if(mtReceiver != null){
		    unregisterReceiver(mtReceiver);
		    Log.i(TAG, "mtReceiver is not null ,cancel.");
		}else{
			Log.i(TAG, "mtReceiver is null not cancel.");
		}
		if(serviceReceiver != null){
			unregisterReceiver(serviceReceiver);
			Log.i(TAG, "mainReceiver is not null ,cancel.");
		}else {
			Log.i(TAG, "mainReceiver is null not cancel.");
		}
		
	}

}
