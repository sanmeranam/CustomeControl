package com.wafone.customlibrary;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class AngleMeter extends SurfaceView {

	private int	vWidth, vHeight, cx, cy;
	private Paint	mOuterPaint, mLineStrok, mTextPaint, mBorderLine, mComBorder;
	private int		angValue, currentValue, comAngValue, comCurrentValue;
	private Path	drawPath;
	private int		innerRadius, outerRadius, unitRadius;
	private String	baseText;
	private boolean	isShowArrow	= false;

	public AngleMeter(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public AngleMeter(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AngleMeter(Context context) {
		super(context);
		init();
	}

	private void init() {

		mOuterPaint = new Paint();
		mOuterPaint.setAntiAlias(true);
		mOuterPaint.setColor(Color.WHITE);
		mOuterPaint.setStyle(Paint.Style.STROKE);
		mOuterPaint.setStrokeWidth(getSizeInPixel(3));

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(getSizeInPixel(13));

		mLineStrok = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLineStrok.setColor(Color.parseColor("#009de0"));
		mLineStrok.setAntiAlias(true);
		mLineStrok.setStrokeWidth(6);
		mLineStrok.setStyle(Paint.Style.FILL);

		mBorderLine = new Paint(mLineStrok);
		mBorderLine.setStyle(Paint.Style.STROKE);

		mComBorder = new Paint(mBorderLine);
		mComBorder.setStrokeWidth(6);

		drawPath = new Path();
		innerRadius = getSizeInPixel(15);
		outerRadius = getSizeInPixel(50);
		unitRadius = getSizeInPixel(60);

		baseText = "";
	}

	class Point {
		float	x;
		float	y;

		public Point(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float verts[] = new float[] { getCircleX(outerRadius, currentValue), getCircleY(outerRadius, currentValue), getCircleX(innerRadius, currentValue + 140), getCircleY(innerRadius, currentValue + 140), getCircleX(innerRadius / 2, currentValue + 180), getCircleY(innerRadius / 2, currentValue + 180), getCircleX(innerRadius, currentValue - 140), getCircleY(innerRadius, currentValue - 140) };

		canvas.drawCircle(cx, cy, unitRadius, mBorderLine);
		canvas.drawLine(cx, 0, cx, cy * 2, mOuterPaint);
		canvas.drawLine(0, cy, cx * 2, cy, mOuterPaint);

		int comSr = unitRadius + getSizeInPixel(20);
		int comEr = unitRadius + getSizeInPixel(8);

		canvas.drawCircle(cx, cy, unitRadius + getSizeInPixel(8), mComBorder);

		mTextPaint.setColor(Color.WHITE);
		if (this.isShowArrow) {
			drawPoly(canvas, Color.parseColor("#009de0"), verts);			
			drawCenterText(angleNormalization(currentValue) + "°", canvas, mTextPaint, cx, cy);
		} else {
			canvas.drawCircle(cx, cy, unitRadius - getSizeInPixel(30), mLineStrok);
			drawCenterText("No Move", canvas, mTextPaint, cx, cy);
		}

		mTextPaint.setColor(mBorderLine.getColor());
		for (int i = 0; i <= 360; i += 90) {
			canvas.drawLine(getCompassX(comSr, comAngValue + i), getCompassY(comSr, comAngValue + i), getCompassX(comEr, comAngValue + i), getCompassY(comEr, comAngValue + i), mComBorder);
			if (i == 0) {

				drawCenterText("S", canvas, mTextPaint, getCompassX(comSr + 5, comAngValue + i), getCompassY(comSr + 5, comAngValue + i));
			}
			if (i == 180) {

				drawCenterText("N", canvas, mTextPaint, getCompassX(comSr + 5, comAngValue + i), getCompassY(comSr + 5, comAngValue + i));
			}

		}

		super.onDraw(canvas);
	}

	private int angleNormalization(int angle) {
		return angle <= 360 ? angle : angle - ((int) (angle / 360)) * 360;
	}

	private void drawPoly(Canvas canvas, int color, float[] points) {

		if (points.length < 2) {
			return;
		}

		drawPath.reset();
		drawPath.moveTo(points[0], points[1]);
		int i, len;
		len = points.length;
		for (i = 0; i < len; i += 2) {
			drawPath.lineTo(points[i], points[i + 1]);
		}
		drawPath.lineTo(points[0], points[1]);
		canvas.drawPath(drawPath, mLineStrok);
	}

	private float getCircleX(int r, int index) {
		return (float) (r * Math.cos(index * (Math.PI / 180) - (Math.PI / 2)) + cx);
	}

	private float getCircleY(int r, int index) {
		return (float) (r * Math.sin(index * (Math.PI / 180) - (Math.PI / 2)) + cy);
	}

	private float getCompassX(int r, int index) {
		return (float) (r * Math.sin(index * (Math.PI / 180)) + cx);
	}

	private float getCompassY(int r, int index) {
		return (float) (r * Math.cos(index * (Math.PI / 180)) + cy);
	}

	private int getSizeInPixel(int size) {
		return (int) (size * getResources().getDisplayMetrics().density);

	}

	public void showStatus(boolean flag) {
		this.isShowArrow = flag;
	}

	public void setAngle(int value) {
		this.angValue = value;
		animHandler.sendEmptyMessage(0);
	}

	public void setCompassAngle(int value) {
		this.comAngValue = value;
		compassHandler.sendEmptyMessage(0);
	}

	private void drawCenterText(String text, Canvas canvas, Paint paint, float cx, float cy) {
		Rect r = new Rect();
		paint.getTextBounds(text, 0, text.length(), r);
		int height = r.bottom + r.height();
		int width = r.left + r.width();

		canvas.drawText(text, cx - width / 2, cy + height / 2, paint);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		this.vWidth = w;
		this.vHeight = h;
		this.cx = w / 2;
		this.cy = h / 2;

		int tpx = Math.min(w, h);

		mTextPaint.setTextSize(tpx / 10);
		mComBorder.setTextSize(tpx / 12);

		outerRadius = tpx / 3;
		innerRadius = tpx / 3;
		unitRadius = outerRadius + getSizeInPixel(8);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	Handler	animHandler		= new Handler() {
								@Override
								public void handleMessage(Message msg) {
									if (angValue > currentValue) {
										currentValue += 10;

										currentValue = currentValue > angValue ? angValue : currentValue;

										invalidate();

									} else if (angValue < currentValue) {
										currentValue -= 10;
										currentValue = currentValue < angValue ? angValue : currentValue;
										invalidate();
									}

									if (angValue == currentValue) {
										animHandler.removeMessages(0);
									} else {
										animHandler.sendEmptyMessageDelayed(0, 50);
									}
								}
							};

	Handler	compassHandler	= new Handler() {
								@Override
								public void handleMessage(Message msg) {
									if (comAngValue > comCurrentValue) {
										comCurrentValue += 5;
										comCurrentValue = comCurrentValue > comAngValue ? comAngValue : comCurrentValue;
										invalidate();

									} else if (angValue < comCurrentValue) {
										comCurrentValue -= 5;
										comCurrentValue = comCurrentValue < comAngValue ? comAngValue : comCurrentValue;
										invalidate();
									}

									if (comAngValue == comCurrentValue) {
										compassHandler.removeMessages(0);
									} else {
										compassHandler.sendEmptyMessageDelayed(0, 50);
									}
								}
							};

}
