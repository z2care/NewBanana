package com.example.mtarget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MTReceiver extends BroadcastReceiver {
    private static final String TAG = "MTReceiver";
    private String incoming_number = "";
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
        if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
        	Log.i(TAG, "action_new_outgoing_call");//nothing to do
        } else if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            switch (tm.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:
            	Log.i(TAG, "call state ringing");
            	incoming_number= intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            	sendBroad(context, MTService.RING_ACTION, incoming_number);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
            	Log.i(TAG, "call state offhook");//nothing to do.
                break;
            case TelephonyManager.CALL_STATE_IDLE:
            	Log.i(TAG, "call state idle");//nothing to do.
                break;
            }
        }

	}
	
    private void sendBroad(Context context, String action, String incoming_num){
    	Intent intent=new Intent(action);
        intent.putExtra(MTService.INCOMMING_NUM, incoming_num);
    	context.sendBroadcast(intent);
    }

}
