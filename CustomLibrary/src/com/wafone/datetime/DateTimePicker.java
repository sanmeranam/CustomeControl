package com.wafone.datetime;

import com.wafone.customlibrary.R;
import com.wafone.datetime.HourSelect.OnHourSelectedListener;
import com.wafone.datetime.MeridianSelect.SelectListener;
import com.wafone.datetime.MinuteSelect.OnMinuteSelectedListener;

import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class DateTimePicker extends Activity {
	public static final int		DATE_RESULT_CANCEL	= 0x0001;
	public static final int		DATE_RESULT_OK		= 0x0002;
	public static final String	KEY_HOUR			= "key_hour";
	public static final String	KEY_MINUTE			= "key_minute";
	public static final String	KEY_MERIDIAN		= "key_meridian";
	public static final String	KEY_STATUS			= "key_status";

	private ViewSwitcher		switcher;
	private HourSelect			hourSelect;
	private MinuteSelect		minuteSelect;
	private TextView			textHour, textMinute, textMeridian;
	private int					animDura			= 300;
	private ScaleAnimation		animHOut, animHIn, animMOut, animMIn;
	private Dialog				thisDialog;
	private int					defaultHour			= 9, defaultMinute = 0;
	private Button				btnDone;
	private boolean				isAM				= false;
	private int					resultCode			= DATE_RESULT_CANCEL;
	private MeridianSelect		selectAM, selectPM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		migrateToDialog();
		initControls();
		initAnims();
		makeTransparent(selectAM);
		makeTransparent(selectPM);
		makeTransparent(hourSelect);
		makeTransparent(minuteSelect);
		setHourText(defaultHour);
		setMinuteText(defaultMinute);
	}

	private void migrateToDialog() {
		thisDialog = new Dialog(this);
		thisDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		thisDialog.setContentView(R.layout.activity_date_time_picker);
		thisDialog.setCancelable(true);
		thisDialog.setCanceledOnTouchOutside(true);
		thisDialog.show();
		thisDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				resultCode = DATE_RESULT_CANCEL;
				DateTimePicker.this.sendResultToParent(defaultHour, defaultMinute, isAM);
			}
		});
	}

	private void initControls() {
		switcher = (ViewSwitcher) thisDialog.findViewById(R.id.viewSwitcher1);
		hourSelect = (HourSelect) thisDialog.findViewById(R.id.hourSelect1);
		minuteSelect = (MinuteSelect) thisDialog.findViewById(R.id.minuteSelect1);
		textHour = (TextView) thisDialog.findViewById(R.id.textHour);
		textMinute = (TextView) thisDialog.findViewById(R.id.textMinute);
		textMeridian = (TextView) thisDialog.findViewById(R.id.textMeridian);
		hourSelect.setCurentHour(defaultHour);
		minuteSelect.setCurentMinute(defaultMinute);
		btnDone = (Button) thisDialog.findViewById(R.id.ok_button);
		selectAM = (MeridianSelect) thisDialog.findViewById(R.id.meridianAM);
		selectPM = (MeridianSelect) thisDialog.findViewById(R.id.meridianPM);
		selectPM.setText("PM");
		selectAM.setText("AM");
		textMeridian.setText("PM");

		selectAM.setSelected(isAM);
		selectPM.setSelected(!isAM);

		selectAM.setOnSelectListener(new SelectListener() {

			@Override
			public void onSelectChange(boolean isSelected) {
				selectAM.setSelected(isSelected);
				selectPM.setSelected(!isSelected);
				isAM = isSelected;
				textMeridian.setText(isAM ? "AM" : "PM");
			}
		});

		selectPM.setOnSelectListener(new SelectListener() {

			@Override
			public void onSelectChange(boolean isSelected) {
				selectAM.setSelected(!isSelected);
				selectPM.setSelected(isSelected);
				isAM = !isSelected;
				textMeridian.setText(isAM ? "AM" : "PM");
			}
		});

		textMeridian.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isAM = !isAM;
				selectAM.setSelected(isAM);
				selectPM.setSelected(!isAM);
				textMeridian.setText(isAM ? "AM" : "PM");
			}
		});

		switchToHour();
		btnDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				resultCode = DATE_RESULT_OK;
				DateTimePicker.this.sendResultToParent(defaultHour, defaultMinute, isAM);
			}
		});

		textHour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				switchToHour();

			}
		});
		textMinute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				switchToMinute();

			}
		});
		hourSelect.setOnHourSelcted(new OnHourSelectedListener() {

			@Override
			public void hourSelected(int hour) {
				setHourText(hour);
			}

			@Override
			public void hourSelectFinish(int hour) {
				setHourText(hour);
				switchToMinute();
			}
		});

		minuteSelect.setOnMinuteSelcted(new OnMinuteSelectedListener() {

			@Override
			public void minuteSelected(int minute) {
				setMinuteText(minute);
			}
		});
	}

	private void sendResultToParent(int hour, int minute, boolean isAm) {
		Intent data = new Intent();
		
		data.putExtra(KEY_HOUR, defaultHour);
		data.putExtra(KEY_MINUTE, defaultMinute);
		data.putExtra(KEY_MERIDIAN, isAm);
		data.putExtra(KEY_STATUS, resultCode);
		getParent().setResult(Activity.RESULT_OK, data);
		setResult(Activity.RESULT_OK, data);
		finish();
	}

	@Override
	public void finish() {
		thisDialog.dismiss();
		super.finish();
	}

	private void setHourText(int hour) {
		defaultHour = hour;
		textHour.setText(String.format("%02d", hour));
	}

	private void setMinuteText(int minute) {
		defaultMinute = minute;
		textMinute.setText(String.format("%02d", minute));
	}

	private void initAnims() {
		animHIn = new ScaleAnimation(1.5f, 1.0f, 1.5f, 1.0f, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);
		animHOut = new ScaleAnimation(1.0f, 0.5f, 1.0f, 0.5f, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);
		animMIn = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);
		animMOut = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);
		animHIn.setDuration(animDura);
		animHOut.setDuration(animDura);
		animMIn.setDuration(animDura);
		animMOut.setDuration(animDura);
	}

	private Animation getTextAnimEffect() {
		ScaleAnimation timeTextAnim = new ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);
		timeTextAnim.setDuration(animDura * 2);
		timeTextAnim.setInterpolator(new BounceInterpolator());
		return timeTextAnim;
	}

	private void makeTransparent(SurfaceView view) {
		view.setZOrderOnTop(true);
		SurfaceHolder sfhTrackHolder = view.getHolder();
		sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);
	}

	private void switchToHour() {
		switcher.setInAnimation(animHIn);
		switcher.setOutAnimation(animHOut);

		textHour.setTextColor(getResources().getColor(R.color.holo_blue));
		textMinute.setTextColor(getResources().getColor(android.R.color.darker_gray));

		if (switcher.getCurrentView().getId() != R.id.hourSelect1)
			switcher.showPrevious();

		textHour.setAnimation(getTextAnimEffect());

	}

	private void switchToMinute() {
		switcher.setInAnimation(animMIn);
		switcher.setOutAnimation(animMOut);

		textHour.setTextColor(getResources().getColor(android.R.color.darker_gray));
		textMinute.setTextColor(getResources().getColor(R.color.holo_blue));

		if (switcher.getCurrentView().getId() != R.id.minuteSelect1)
			switcher.showNext();

		textMinute.setAnimation(getTextAnimEffect());
	}

//	private void vibrate(int duration) {
//		Vibrator vb = (Vibrator) DateTimePicker.this.getSystemService(Context.VIBRATOR_SERVICE);
//		vb.vibrate(duration);
//
//	}
}
