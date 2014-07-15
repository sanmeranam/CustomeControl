package com.wafone.datetime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class HourSelect extends SurfaceView {

	private int						viewHeight, viewWidth, centerX, centerY;
	private int						ourterRadius, innerRedius;
	private float					ratio;
	private Paint					mOuterPaint, mLineStrok, mTextPaint;
	private int						flag	= 0, textSize = 20;
	private Point					cP;
	private int						currentHour, secAngle = 15, inCircleRound = 18;

	private OnHourSelectedListener	listener;

	public HourSelect(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public HourSelect(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HourSelect(Context context) {
		super(context);
		init();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.drawColor(Color.parseColor("#00000000"));
		canvas.drawCircle(centerX, centerY, ourterRadius, mOuterPaint);

		for (int i = 1; i <= 12; i++) {
			float x = getCircleX(innerRedius, i);
			float y = getCircleY(innerRedius, i);

			Paint m = new Paint(mOuterPaint);
			m.setColor(Color.WHITE);
			canvas.drawCircle((float) x, (float) y, inCircleRound, m);

			canvas.drawCircle(centerX, centerY, 2, new Paint());

			drawCenterText(i + "", canvas, mTextPaint, x, y);
			if (currentHour == i) {
				canvas.drawLine(centerX, centerY, getCircleX(innerRedius - inCircleRound, i), getCircleY(innerRedius - inCircleRound, i), mLineStrok);
				canvas.drawCircle((float) x, (float) y, inCircleRound, mLineStrok);
			}

		}

		super.onDraw(canvas);
	}

	private float getCircleX(int r, int index) {
		return (float) (r * Math.cos(index * (Math.PI / 6) - (Math.PI / 2)) + centerX);
	}

	private float getCircleY(int r, int index) {
		return (float) (r * Math.sin(index * (Math.PI / 6) - (Math.PI / 2)) + centerY);
	}

	private void drawCenterText(String text, Canvas canvas, Paint paint, float cx, float cy) {
		// int textWidth=paint.measureText(text);
		Rect r = new Rect();
		paint.getTextBounds(text, 0, text.length(), r);
		int height = r.bottom + r.height();
		int width = r.left + r.width();

		canvas.drawText(text, cx - width / 2, cy + height / 2, paint);

	}

	private void init() {
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

		// currentPoint = new Point((int) event.getX(), (int) event.getY());
		cP = new Point((int) event.getX(), (int) event.getY());

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				flag = 1;
				Point p = new Point((int) event.getX(), (int) event.getY());
				fireHourSelect(p);
			break;
			case MotionEvent.ACTION_MOVE:
				flag = flag == 1 ? 2 : 0;
				p = new Point((int) event.getX(), (int) event.getY());
				fireHourSelect(p);
			break;
			case MotionEvent.ACTION_UP:
				flag = 0;
				p = new Point((int) event.getX(), (int) event.getY());
				fireHourSelect(p);
				if (listener != null)
					listener.hourSelectFinish(currentHour);
			break;

		}
		return true;
	}

	private void fireHourSelect(Point p) {
		int sA = (int) (180 / Math.PI * Math.atan2(p.y - centerY, p.x - centerX));

		boolean isUpper = sA < 0;
		sA = Math.abs(sA);

		int startAngle = sA / secAngle;

		int arrAnglesU[] = new int[] { 3, 2, 2, 1, 1, 12, 12, 11, 11, 10, 10, 9, 9 };
		int arrAnglesL[] = new int[] { 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9 };

		currentHour = isUpper ? arrAnglesU[startAngle] : arrAnglesL[startAngle];
		invalidate();
		if (this.listener != null) {
			listener.hourSelected(currentHour);
		}
	}

	public void setCurentHour(int hour) {
		this.currentHour = hour < 13 && hour > 0 ? hour : 0;
		invalidate();
	}

	public void setOnHourSelcted(OnHourSelectedListener listener) {
		this.listener = listener;
	}

	public static interface OnHourSelectedListener {
		public void hourSelected(int hour);

		public void hourSelectFinish(int hour);
	}

}
