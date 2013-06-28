//一旦有变化就保存

package com.hackathon.game1;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A class to get accelerometer information from an Android device.
 * 
 * @author yue
 * 
 */
public class Accelm extends Activity {

	private SensorManager sm;//A sensor manager to get accelerometer data.
	private TextView XT, YT, ZT;//Three text view to show accelerometer data on screen.
	private Button stopButton;//Button to stop recording accelerometer data.
	private Button startButton;//Button to start recording accelerometer data.
	private Button saveToFileButton;//Button to save all accelerometer data in the database to a txt file.
	private Button deleteDataButton;//Button to delete all accelerometer data in the database.
	
	private List<String> labelList = new ArrayList<String>();//Contains all availiable labels.
	private Spinner labelSpinner;
	private ArrayAdapter<String> labelAdapter;

	private AccelmDatabase Accelmdb;//The database to record accelerometer data.
	
	//String acctime;//The when one piece of accelerometer data was collected.
	/*The initiated accelerometer data in tree dimensions. 
	Setting them to 100 in order to trigger the update at the beginning.
	*/
	float X_lateral = 100;
	float Y_longitudinal = 100;
	float Z_vertical = 100;

	//Use timer with handler to control the sample frequency.
	Timer timer = new Timer();
	TimerTask task;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accelm_layout);
		Accelmdb = new AccelmDatabase(Accelm.this);// Initiate the Accelerometer
													// Database
		XT = (TextView) findViewById(R.id.XT);
		YT = (TextView) findViewById(R.id.YT);
		ZT = (TextView) findViewById(R.id.ZT);

		//Set all available labels.
		labelList.add("Ambulation");
		labelList.add("Climb_Stairs");
		labelList.add("Falling_Down");
		labelList.add("Jumping");
		labelList.add("Running");
		labelList.add("Riding_Escalator");
		labelList.add("Riding_Elevator");
		labelList.add("Staying_Still");
		labelList.add("Texting_Msg");
		labelList.add("Walking");
		labelList.add("Walk_Down_Stairs");
		labelList.add("Waving_Hand");
		labelList.add("Other1");
		labelList.add("Other2");
		labelList.add("Other3");
		labelSpinner = (Spinner) findViewById(R.id.choose_label);
		labelAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, labelList);
		labelAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		labelSpinner.setAdapter(labelAdapter);

		labelSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						//Save the chosen label.
						AccelValue.label = labelAdapter.getItem(arg2);
						arg0.setVisibility(View.VISIBLE);
					}

					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						AccelValue.label = "Default";
						arg0.setVisibility(View.VISIBLE);
					}
				});

		// Create a SensorManager to get the service of sensor from Android.
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		// Choose ACCELEROMETER.
		final int sensorType = Sensor.TYPE_ACCELEROMETER;
		
		sm.registerListener(myAccelerometerListener,
				sm.getDefaultSensor(sensorType), SensorManager.SENSOR_DELAY_UI);

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 1:
					
					Accelmdb.insertAccelm(AccelValue.label, AccelValue.actTime,
							AccelValue.x, AccelValue.y, AccelValue.z);

					break;
				}
				super.handleMessage(msg);
			}
		};

	
		startButton = (Button) findViewById(R.id.start_btn);
		startButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				timer = new Timer();

				task = new TimerTask() {
					public void run() {
						AccelValue.actTime = System.currentTimeMillis() + "";

						Message message = new Message();
						message.what = 1;

						handler.sendMessage(message);
					}
				};

				timer.schedule(task, 1, 50);

				startButton.setEnabled(false);
				stopButton.setEnabled(true);
			}
		});

		stopButton = (Button) findViewById(R.id.stop_btn);
		stopButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (timer != null) {
					timer.cancel();
					timer = null;

				}

				if (task != null) {
					task.cancel();
					task = null;
				}
				stopButton.setEnabled(false);
				startButton.setEnabled(true);
			}

		});

		saveToFileButton = (Button) findViewById(R.id.manage_DB_btn);
		saveToFileButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Accelmdb.makeAccelmString();
				Toast.makeText(
						Accelm.this,
						"Data has been saved.",
						Toast.LENGTH_SHORT).show();
			}
		});
		
		deleteDataButton = (Button) findViewById(R.id.delete_btn);
		deleteDataButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Accelmdb.deleteAccelm();
				Toast.makeText(
						Accelm.this,
						"All data has been deleted.",
						Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	/*
	 * Parameter1 ：SensorEventListener: a listener. Parameter2 ：Sensor
	 * getDefaultSensor:get defult Sensor Parameter3 ：change the refresh
	 * frequency
	 */


	/*
	 * SensorEventListener接口的实现，需要实现两个方法 方法1 onSensorChanged 当数据变化的时候被触发调用 方法2
	 * onAccuracyChanged 当获得数据的精度发生变化的时候被调用，比如突然无法获得数据时
	 */
	final SensorEventListener myAccelerometerListener = new SensorEventListener() {

		// 复写onSensorChanged方法
		public void onSensorChanged(SensorEvent sensorEvent) {
			if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				if (Math.abs(sensorEvent.values[0] - X_lateral) > 0.5
						|| Math.abs(sensorEvent.values[1] - Y_longitudinal) > 0.5
						|| Math.abs(sensorEvent.values[2] - Z_vertical) > 0.5) {
					// 图解中已经解释三个值的含义
					X_lateral = sensorEvent.values[0];
					Y_longitudinal = sensorEvent.values[1];
					Z_vertical = sensorEvent.values[2];
					XT.setText("x=" + X_lateral);
					YT.setText("y=" + Y_longitudinal);
					ZT.setText("z=" + Z_vertical);
					AccelValue.x =X_lateral + "";
					AccelValue.y = Y_longitudinal + "";
					AccelValue.z = Z_vertical + "";

					// Accelmdb.insertAccelm(inputLabelText.getText().toString(),accelmtime,
					// x_axis, y_axis, z_axis,
					// unit);
				}
			}
		}

		// 复写onAccuracyChanged方法
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// ACT.setText("onAccuracyChanged被触发");
		}
	};

	public void onPause() {
		// /*
		// * 很关键的部分：注意，说明文档中提到，即使activity不可见的时候，感应器依然会继续的工作，测试的时候可以发现，没有正常的刷新频率
		// * 也会非常高，所以一定要在onPause方法中关闭触发器，否则讲耗费用户大量电量，很不负责。
		// */
		// sm.unregisterListener(myAccelerometerListener);
		// isAccelmUpdating = false;
		// stopButton.setText("Refresh");
		super.onPause();
	}

}