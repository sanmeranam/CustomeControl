package com.wafone.datetime;

import java.lang.reflect.Array;
import java.util.Arrays;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class MinuteSelect extends SurfaceView {

	private int							viewHeight, viewWidth, centerX, centerY;
	private int							ourterRadius, innerRedius;
	private float						ratio;
	private Paint						mOuterPaint, mLineStrok, mLineStrokNone, mTextPaint;
	private int							flag	= 0, textSize = 20;
	private Point						cP;
	private int							currentMinute, secAngle = 6, inCircleRound = 20;

	private OnMinuteSelectedListener	listener;

	public MinuteSelect(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		inti();
	}

	public MinuteSelect(Context context, AttributeSet attrs) {
		super(context, attrs);
		inti();
	}

	public MinuteSelect(Context context) {
		super(context);
		inti();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.drawColor(Color.parseColor("#00000000"));
		canvas.drawCircle(centerX, centerY, ourterRadius, mOuterPaint);

		for (int i = 1; i <= 60; i++) {
			float x = getCircleX(innerRedius, i);
			float y = getCircleY(innerRedius, i);

			canvas.drawCircle(centerX, centerY, 2, new Paint());
			boolean textFlag = (i % 5 == 0);

			if (textFlag) {
				Paint m = new Paint(mOuterPaint);
				m.setColor(Color.WHITE);

				canvas.drawCircle((float) x, (float) y, inCircleRound, m);
				drawCenterText(i, canvas, mTextPaint, x, y);
			}

		}

		float x = getCircleX(innerRedius, currentMinute);
		float y = getCircleY(innerRedius, currentMinute);

		canvas.drawLine(centerX, centerY, getCircleX(innerRedius - inCircleRound, currentMinute), getCircleY(innerRedius - inCircleRound, currentMinute), mLineStrok);
		canvas.drawCircle((float) x, (float) y, inCircleRound, mLineStrok);
		if (!(currentMinute % 5 == 0))
			canvas.drawCircle((float) x, (float) y, 3 * ratio, mLineStrokNone);

		super.onDraw(canvas);
	}

	private float getCircleX(int r, int index) {
		return (float) (r * Math.cos(index * (Math.PI / 30) - (Math.PI / 2)) + centerX);
	}

	private float getCircleY(int r, int index) {
		return (float) (r * Math.sin(index * (Math.PI / 30) - (Math.PI / 2)) + centerY);
	}

	private void drawCenterText(int num, Canvas canvas, Paint paint, float cx, float cy) {
		num = num == 60 ? 0 : num;
		String text = String.format("%02d", num);
		Rect r = new Rect();
		paint.getTextBounds(text, 0, text.length(), r);
		int height = r.bottom + r.height();
		int width = r.left + r.width();

		canvas.drawText(text, cx - width / 2, cy + height / 2, paint);

	}

	private void inti() {
		ratio = getResources().getDisplayMetrics().density;
		textSize = (int) (textSize * ratio);

		mOuterPaint = new Paint();
		mOuterPaint.setAntiAlias(true);
		mOuterPaint.setColor(Color.WHITE);
		mOuterPaint.setTextSize(textSize);

		mTextPaint = new Paint(mOuterPaint);
		mTextPaint.setColor(Color.LTGRAY);

		mLineStrok = new Paint(mOuterPaint);
		mLineStrok.setColor(Color.parseColor("#66009de0"));

		mLineStrokNone = new Paint(mLineStrok);
		mLineStrokNone.setColor(Color.parseColor("#009de0"));

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		ratio = getResources().getDisplayMetrics().density;
		viewHeight = h;
		viewWidth = w;
		centerX = w / 2;
		centerY = h / 2;
		int padding = 0;
		ourterRadius = Math.min(viewWidth / 2, viewHeight / 2) - padding;
		int inPadding = (int) (padding + (ratio * 20));
		innerRedius = Math.min(viewWidth / 2, viewHeight / 2) - inPadding;
		inCircleRound = (int) (inCircleRound * ratio);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		cP = new Point((int) event.getX(), (int) event.getY());

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				flag = 1;
				Point p = new Point((int) event.getX(), (int) event.getY());
				fireMinuteSelect(p);
			break;
			case MotionEvent.ACTION_MOVE:
				flag = flag == 1 ? 2 : 0;
				p = new Point((int) event.getX(), (int) event.getY());
				fireMinuteSelect(p);
			break;
			case MotionEvent.ACTION_UP:
				flag = 0;
				p = new Point((int) event.getX(), (int) event.getY());
				fireMinuteSelect(p);
			break;

		}
		return true;
	}

	private void fireMinuteSelect(Point p) {
		int sA = (int) (180 / Math.PI * Math.atan2(p.y - centerY, p.x - centerX));
		sA = sA < 0 ? 361 + sA : sA;
		int startAngle = sA / secAngle;

		int arrAngles[] = new int[] { 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };

		currentMinute = arrAngles[startAngle];

		invalidate();
		if (this.listener != null) {
			listener.minuteSelected(currentMinute == 60 ? 0 : currentMinute);
		}
	}

	public void setCurentMinute(int minute) {
		this.currentMinute = minute < 61 && minute > 0 ? minute : 0;
		invalidate();
	}

	public void setOnMinuteSelcted(OnMinuteSelectedListener listener) {
		this.listener = listener;
	}

	public static interface OnMinuteSelectedListener {
		public void minuteSelected(int minute);
	}

}
