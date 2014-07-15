package com.wafone.customlibrary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class AlarmClock extends View {

	private int						lineColor		= Color.parseColor("#009de0");
	private int						textColor		= Color.parseColor("#009de0");
	private int						centerX, centerY, redius, viewHeight, viewWidth;
	private Paint					mDayPaint, mNightPaint, mTextPaint, mEventPaint, mLinePaint, mBorderPaint, mCenterPaint;
	private ArrayList<TimeEntity>	dateList;
	private onAlarmTrigger			iAlarmTrigger;
	private Calendar				calendar;
	private float					clockAngle;
	private RectF					ovalRectf, innerRectf;
	private String					dateString[]	= {};
	private int						textSize		= 20;
	private int						barStrock		= 3;
	private float					screeRatio;
	private Paint					mRadioBasePaint, mRadioUpperPaint;

	private Handler					timeHandler		= new Handler() {
														@Override
														public void handleMessage(Message msg) {

															dateString = DateFormat.getTimeFormat(getContext()).format(new Date()).split("\\s");
															calendar = Calendar.getInstance();
															int hr = calendar.get(Calendar.HOUR_OF_DAY);
															int min = calendar.get(Calendar.MINUTE);

															int totalMin = (hr * 60) + min;
															clockAngle = totalMin * 0.25f;

															for (TimeEntity entity : dateList) {
																if (hr == entity.getHOUR() && min == entity.getMINUTE()) {
																	iAlarmTrigger.alarmTriggered(entity);
																}
															}
															invalidate();
															timeHandler.sendEmptyMessageDelayed(0, 60000);
														}
													};

	public AlarmClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularLoader);

		lineColor = typedArray.getColor(R.styleable.AlarmClock_lineColor, lineColor);
		textColor = typedArray.getColor(R.styleable.AlarmClock_fontColor, textColor);
		initPaint();
		dateList = new ArrayList<TimeEntity>();

		screeRatio = (1 / getResources().getDisplayMetrics().density);
		textSize = (int) (textSize / screeRatio);

		barStrock = (int) (barStrock / screeRatio);
	}

	private void initPaint() {
		mDayPaint = new Paint();

		mDayPaint.setColor(Color.WHITE);
		mDayPaint.setAntiAlias(true);
		mDayPaint.setTextSize(textSize);

		mNightPaint = new Paint();
		mNightPaint.setColor(Color.GRAY);
		mNightPaint.setAntiAlias(true);
		mNightPaint.setTextSize(textSize);

		mCenterPaint = new Paint();
		mCenterPaint.setColor(Color.WHITE);
		mCenterPaint.setAntiAlias(true);

		mTextPaint = new Paint();
		mTextPaint.setColor(textColor);
		mTextPaint.setStyle(Style.FILL);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(textSize);

		mLinePaint = new Paint();
		mLinePaint.setColor(lineColor);
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStyle(Paint.Style.STROKE);
		mLinePaint.setStrokeWidth(barStrock);

		mBorderPaint = new Paint();
		mBorderPaint.setColor(Color.LTGRAY);
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setStrokeWidth(1);

		mRadioBasePaint = new Paint();
		mRadioBasePaint.setColor(textColor);
		mRadioBasePaint.setAntiAlias(true);

		mRadioUpperPaint = new Paint();

		mRadioUpperPaint.setColor(Color.parseColor("#64009de0"));
		mRadioUpperPaint.setAntiAlias(true);

	}

	private double[] getPosition(int HOUR, int MIN, double d) {
		double[] data = new double[2];
		float angle = (((HOUR * 60) + MIN) * 0.25f) - 180;
		data[0] = d * Math.cos(angle * (Math.PI / 180)) + centerX;
		data[1] = d * Math.sin(angle * (Math.PI / 180)) + centerY;
		return data;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawCircle(centerX, centerY, redius, mBorderPaint);
		canvas.drawArc(ovalRectf, 90, 180, false, mNightPaint);
		canvas.drawArc(ovalRectf, 270, 180, false, mDayPaint);

		clockAngle -= 180;
		double xx = redius * Math.cos(clockAngle * (Math.PI / 180)) + centerX;
		double yy = redius * Math.sin(clockAngle * (Math.PI / 180)) + centerY;

		canvas.drawLine(centerX, centerY, (float) xx, (float) yy, mLinePaint);
		canvas.drawCircle(centerX, centerY, redius / 3, mCenterPaint);

		int offsetNum = 0;
		for (String s : dateString) {
			float offset = mTextPaint.measureText(s) / 2;
			canvas.drawText(s, centerX - offset, centerY + (textSize * (offsetNum)) - ((dateString.length - 1) * (textSize / 2)), mTextPaint);
			offsetNum++;
		}

		for (int i = 1; i <= 24; i++) {
			double[] dot1 = getPosition(i + 1, 0, redius * 0.99);
			Paint p = i >= 6 && i <= 18 ? mNightPaint : mDayPaint;
			canvas.drawCircle((float) dot1[0], (float) dot1[1], 1 / screeRatio, p);
			Rect f = new Rect();
			p.getTextBounds("12pm", 0, 4, f);
			if (i == 11)
				canvas.drawText("12pm", (float) dot1[0] - f.width(), (float) dot1[1], p);
			if (i == 23)
				canvas.drawText("12am", (float) dot1[0], (float) dot1[1], p);
		}

		for (TimeEntity te : dateList) {
			double[] dot1 = getPosition(te.getHOUR(), te.getMINUTE(), redius * 0.6);
			canvas.drawCircle((float) dot1[0], (float) dot1[1], 5 / screeRatio, mRadioBasePaint);
			canvas.drawCircle((float) dot1[0], (float) dot1[1], 15 / screeRatio, mRadioUpperPaint);
		}

		super.onDraw(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		viewHeight = h;
		viewWidth = w;
		centerX = w / 2;
		centerY = h / 2;
		int sideLen = Math.min(viewHeight, viewWidth) - 20;
		int paddingX = Math.max(this.getPaddingLeft(), this.getPaddingRight());
		int paddingY = Math.max(this.getPaddingTop(), this.getPaddingBottom());

		redius = (sideLen / 2) - Math.min(paddingX, paddingY);

		ovalRectf = new RectF(centerX - redius, centerY - redius, viewWidth - (centerX - redius), viewHeight - (centerY - redius));
		timeHandler.sendEmptyMessage(0);
	}

	public interface onAlarmTrigger {
		public void alarmTriggered(TimeEntity entity);
	}

	public void setOnAlarmTriger(onAlarmTrigger alarmTrigger) {
		this.iAlarmTrigger = alarmTrigger;
	}

	public boolean addTimeEntity(TimeEntity entity) {
		return dateList.add(entity);
	}

	public boolean removeTimeEntity(TimeEntity entity) {
		return dateList.remove(entity);
	}

	public void removeAll() {
		dateList.clear();
	}

	public void pause() {

	}

	public void resume() {

	}

}
