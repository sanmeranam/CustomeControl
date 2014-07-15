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

public class ProgressMeter extends SurfaceView {

	private int	vWidth, vHeight, cx, cy;
	private Paint	mOuterPaint, mLineStrok, mTextPaint, mBorderLine, mDarkText;
	private int		angValue, currentValue;
	private Path	drawPath;
	private int		radius, outerRadius, unitRadius;
	private String	baseText;

	public ProgressMeter(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ProgressMeter(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ProgressMeter(Context context) {
		super(context);
		init();
	}

	private void init() {

		mOuterPaint = new Paint();
		mOuterPaint.setAntiAlias(true);
		mOuterPaint.setColor(Color.WHITE);
		mOuterPaint.setStyle(Paint.Style.STROKE);
		mOuterPaint.setStrokeWidth(getSizeInPixel(4));

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(getSizeInPixel(13));

		mDarkText = new Paint(mTextPaint);
		mDarkText.setColor(Color.parseColor("#009de0"));
		mDarkText.setTypeface(Typeface.DEFAULT_BOLD);

		mLineStrok = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLineStrok.setColor(Color.parseColor("#009de0"));
		mLineStrok.setAntiAlias(true);
		mLineStrok.setStrokeWidth(10);
		mLineStrok.setStyle(Paint.Style.FILL);

		mBorderLine = new Paint(mOuterPaint);
		mBorderLine.setColor(Color.parseColor("#009de0"));

		drawPath = new Path();
		radius = getSizeInPixel(15);
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

		canvas.drawCircle(cx, cy, radius, mLineStrok);

		float verts[] = new float[] { getCircleX(outerRadius, currentValue), getCircleY(outerRadius, currentValue), getCircleX(radius, currentValue + 90), getCircleY(radius, currentValue + 90), getCircleX(radius, currentValue - 90), getCircleY(radius, currentValue - 90) };

		drawPoly(canvas, Color.parseColor("#009de0"), verts);

		canvas.drawCircle(cx, cy, radius + 2, mOuterPaint);

		drawCenterText(currentValue + "", canvas, mTextPaint, cx, cy);
		drawCenterText(baseText, canvas, mDarkText, cx, cy + getSizeInPixel(30));

		for (int i = 0; i <= 180; i += 10) {
			if (i % 30 == 0)
				canvas.drawCircle(getCircleX(unitRadius, i), getCircleY(unitRadius, i), getSizeInPixel(3), mLineStrok);
			else
				canvas.drawCircle(getCircleX(unitRadius, i), getCircleY(unitRadius, i), getSizeInPixel(2), mLineStrok);
		}
		super.onDraw(canvas);
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
		return (float) (r * Math.cos(index * (Math.PI / 180) - (Math.PI)) + cx);
	}

	private float getCircleY(int r, int index) {
		return (float) (r * Math.sin(index * (Math.PI / 180) - (Math.PI)) + cy);
	}

	private int getSizeInPixel(int size) {
		return (int) (size * getResources().getDisplayMetrics().density);

	}

	public void setValue(int value) {
		this.angValue = value;
		animHandler.sendEmptyMessage(0);
	}

	public void setUnitText(String text) {
		this.baseText = text;
		invalidate();
	}

	private void drawCenterText(String text, Canvas canvas, Paint paint, float cx, float cy) {
		// int textWidth=paint.measureText(text);
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
		radius = tpx / 10;

		mTextPaint.setTextSize(radius);
		mDarkText.setTextSize(tpx / 12);

		outerRadius = tpx / 3;
		unitRadius = outerRadius + getSizeInPixel(20);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	Handler	animHandler	= new Handler() {
							@Override
							public void handleMessage(Message msg) {
								Log.d("text", "ang val=" + angValue + ", C value=" + currentValue);
								if (angValue > currentValue) {
									currentValue += 5;

									currentValue = currentValue > angValue ? angValue : currentValue;

									invalidate();

								} else if (angValue < currentValue) {
									currentValue -= 5;
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

}
