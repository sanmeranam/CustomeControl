package com.wafone.customlibrary;

import java.util.ArrayList;

import com.wafone.snooze.PatternSnooze;
import com.wafone.snooze.PatternSnooze.OnDrawFinish;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;

public class TestActivity extends Activity {

	private PatternSnooze	snooze;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		initControls();

	}

	private void initControls() {
//		com.wafone.customlibrary.TimeProgress progress=(com.wafone.customlibrary.TimeProgress)findViewById(R.id.timeProgress1);
//		progress.setTime(22, 30);
//		progress.setTextColor(Color.RED);
//		snooze = (PatternSnooze) findViewById(R.id.patternSnooze1);
//		Button n = (Button) findViewById(R.id.button1);
//		n.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				snooze.clear();
//
//			}
//		});
//
//		snooze.setOnDrawFinish(new OnDrawFinish() {
//
//			@Override
//			public void onFinish(ArrayList<Integer> result) {
//				for (Integer i : result) {
//					Log.d("result", i + "");
//				}
//			}
//		});
	}

}
