package com.example.mtarget;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MTActivity extends Activity {
	
	private String TAG = "MTActivity";
	
	private ToggleButton serviceBtn;
	private TextView serviceTv;
	
	private String STORE_NAME = "MTSetting";
	private String BTN_STATE = "btn_state";
	private Intent serviceIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate~~~");
		super.onCreate(savedInstanceState);
		serviceIntent = new Intent(MTActivity.this, MTService.class);
		
		setContentView(R.layout.activity_main);
		
		serviceTv = (TextView)findViewById(R.id.tv_service);
		
		serviceBtn = (ToggleButton)findViewById(R.id.btn_service);
		serviceBtn.setOnClickListener(new OnClickListener() {
			@Override
            public void onClick(View v) {
                if (serviceBtn.isChecked()) {
                	//start service
                	//i.putExtra("logpath", path_selected);
                	//should append some parameters later on.
                	startService(serviceIntent);
                	serviceTv.setText(getString(R.string.service_opened));
                } else {
                	//stop service
                	Log.i(TAG, "go to stop service.");
                	stopService(serviceIntent);
                	serviceTv.setText(getString(R.string.service_closed));
                }
                SharedPreferences settings = getSharedPreferences(STORE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(BTN_STATE, serviceBtn.isChecked());
        	    editor.commit();
            }
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		SharedPreferences settings = getSharedPreferences(STORE_NAME, MODE_PRIVATE);
        boolean mtswitch = settings.getBoolean(BTN_STATE, false);
        serviceBtn.setChecked(mtswitch);
        if(mtswitch){
            serviceTv.setText(getString(R.string.service_opened));
        } else {
        	serviceTv.setText(getString(R.string.service_closed));
        }
	}
	
	@Override  
	protected void onDestroy() {
		Log.v(TAG, "onDestroy~~~");
	    super.onDestroy();  
	};
}
