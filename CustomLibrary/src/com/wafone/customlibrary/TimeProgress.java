package com.wafone.customlibrary;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

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
import android.util.AttributeSet;
import android.view.View;

public class TimeProgress extends View {

	private int	hour, minute, textSize = 10;
	private int	baseColor	= Color.parseColor("#96009de0");
	private int	foreColor	= Color.parseColor("#ffffff");
	private int	textColor	= Color.parseColor("#ffffff");
	private Paint	mForePaint, mBackPaint, mTextPaint;
	private int		barStrock	= 6;
	private int		viewWidth, viewHeight;
	private int		centerX, centerY, sideLength, circleRedius;
	private RectF	ovalRectF;
	private float	clockAngle;
	private int		currentHour, currentMinute;

	public TimeProgress(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public TimeProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);

	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularLoader);
		hour = typedArray.getInteger(R.styleable.TimeProgress_hour, 0);
		minute = typedArray.getInteger(R.styleable.TimeProgress_minute, 0);
		foreColor = typedArray.getColor(R.styleable.TimeProgress_borderColor, foreColor);
		baseColor = typedArray.getColor(R.styleable.TimeProgress_baseLineColor, baseColor);
		textColor = typedArray.getColor(R.styleable.TimeProgress_txtColor, textColor);

		textSize = typedArray.getInteger(R.styleable.TimeProgress_fontSize, textSize);

		float screeRatio = (1 / getResources().getDisplayMetrics().density);

		textSize = (int) (textSize / screeRatio);

		barStrock = (int) (barStrock / screeRatio);

		mForePaint = new Paint();
		mForePaint.setColor(foreColor);
		mForePaint.setAntiAlias(true);
		mForePaint.setStrokeWidth(barStrock);
		mForePaint.setStyle(Paint.Style.STROKE);

		mBackPaint = new Paint();
		mBackPaint.setColor(baseColor);
		mBackPaint.setAntiAlias(true);
		mBackPaint.setStrokeWidth(barStrock);
		mBackPaint.setStyle(Paint.Style.STROKE);

		mTextPaint = new Paint();
		mTextPaint.setColor(textColor);
		mTextPaint.setStyle(Style.FILL);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(textSize);
	}

	public void setTextColor(int color) {
		this.textColor = color;
		mTextPaint.setColor(this.textColor);
		invalidate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		viewHeight = h;
		viewWidth = w;
		centerX = viewWidth / 2;
		centerY = viewHeight / 2;
		sideLength = Math.min(viewHeight, viewWidth);
		circleRedius = (sideLength / 2) - barStrock;
		ovalRectF = new RectF(centerX - circleRedius, centerY - circleRedius, viewWidth - (centerX - circleRedius), viewHeight - (centerY - circleRedius));

		super.onSizeChanged(w, h, oldw, oldh);
	}

	// private double[] getPosition(int HOUR,int MIN,double d){
	// double[] data=new double[2];
	// float angle=(((HOUR*60)+MIN)*0.25f)-180;
	// data[0]=d*Math.cos(angle*(Math.PI/180))+centerX;
	// data[1]=d*Math.sin(angle*(Math.PI/180))+centerY;
	// return data;
	// }

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawCircle(centerX, centerY, circleRedius, mBackPaint);

		canvas.drawArc(ovalRectF, 270 - clockAngle, clockAngle, false, mForePaint);

		int[] times = timeDiff(hour, currentHour, minute, currentMinute);

		String s = times[0] + "h " + times[1] + "m";
		Rect f = new Rect();
		mTextPaint.getTextBounds(s, 0, s.length(), f);
		float x = centerX - f.width() / 2;
		float y = centerY + f.height() / 2;

		canvas.drawText(s, x, y, mTextPaint);

		super.onDraw(canvas);
	}

	public void setTime(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
		invalidate();
		timeHandler.sendEmptyMessage(0);
	}

	public void stop() {
		timeHandler.removeMessages(0);
	}

	private int[] timeDiff(int th, int ch, int tm, int cm) {
		int targetTime = (th * 60) + tm;
		int currentTime = (ch * 60) + cm;
		int[] result = new int[2];
		
		if(targetTime<currentTime){
			int res=(int) (TimeUnit.HOURS.toMinutes(24)-(currentTime-targetTime));
			result[0]=res/60;
			result[1]=res-(result[0]*60);
		}else if(targetTime>currentTime){
			int res=(int) (targetTime-currentTime);
			result[0]=res/60;
			result[1]=res-(result[0]*60);
		}
		return result;
	}

	private Handler	timeHandler	= new Handler() {
									@Override
									public void handleMessage(Message msg) {

										Calendar calendar = Calendar.getInstance();
										currentHour = calendar.get(Calendar.HOUR_OF_DAY);
										currentMinute = calendar.get(Calendar.MINUTE);

										int[] times = timeDiff(hour, currentHour, minute, currentMinute);

										if (times[0] != 0 && times[1] != 0) {
											int totalMin = (times[0] * 60) + times[1];
											clockAngle = totalMin * 0.25f;
										}
										invalidate();
										timeHandler.sendEmptyMessageDelayed(0, 30000);
									}
								};

}
