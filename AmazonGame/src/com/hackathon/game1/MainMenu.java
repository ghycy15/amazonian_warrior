package com.hackathon.game1;
/*
 * The main menue that consists the entry of every function. 
 */


import com.hackathon.game1.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenu extends Activity {

	private Button accelm_btn;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
//		gps_btn = (Button) findViewById(R.id.gps_btn);
//		gps_btn.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				
//			}
//		});
	
		accelm_btn = (Button) findViewById(R.id.accelm_btn);
		accelm_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(MainMenu.this, Accelm.class);
				startActivity(i);
			}
		});
//		send_data_btn = (Button) findViewById(R.id.send_data_btn);
//		send_data_btn.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				
//			}
//		});
	}
}