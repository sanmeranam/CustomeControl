package com.wafone.snooze;

import java.util.ArrayList;

import com.wafone.datetime.DateTimePicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

public final class PatternSnooze extends SurfaceView {

	private Box[][]				_boxes		= new Box[3][3];
	private Paint				mOuterCircleSelect, mOuterCircle, mTextPaint, mCenterCircle, mComBorder;
	private Point				global;
	private ArrayList<Point>	pList;
	private boolean				isOnGoing	= true;
	private ArrayList<Integer>	result;
	private OnDrawFinish		onDrawFinish;
	private Context				context;

	public PatternSnooze(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public PatternSnooze(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public PatternSnooze(Context context) {
		super(context);
		this.context = context;
		init();
	}

	private void init() {
		mOuterCircleSelect = new Paint();
		mOuterCircleSelect.setAntiAlias(true);
		mOuterCircleSelect.setColor(Color.parseColor("#009de0"));
		mOuterCircleSelect.setStyle(Paint.Style.STROKE);
		mOuterCircleSelect.setStrokeWidth(getSizeInPixel(4));

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(getSizeInPixel(13));

		mOuterCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
		mOuterCircle.setColor(Color.WHITE);
		mOuterCircle.setAntiAlias(true);
		mOuterCircle.setStrokeWidth(getSizeInPixel(4));
		mOuterCircle.setStyle(Paint.Style.STROKE);

		mCenterCircle = new Paint(mOuterCircle);
		mCenterCircle.setStyle(Paint.Style.FILL);

		mComBorder = new Paint(mCenterCircle);
		mComBorder.setStrokeWidth(6);
		pList = new ArrayList<PatternSnooze.Point>();
		result = new ArrayList<Integer>();
	}

	private class Box {
		RectF	rect;
		int		radius;
		int		status	= 0;
		Point	c;
	}

	private int getSizeInPixel(int size) {
		return (int) (size * getResources().getDisplayMetrics().density);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		int wlen = _boxes.length;
		int hlen = _boxes[0].length;

		for (int i = 0; i < wlen; i++) {
			for (int j = 0; j < hlen; j++) {
				Box b = _boxes[i][j];

				if (b.status == 0) {
					canvas.drawCircle(b.c.x, b.c.y, b.radius, mOuterCircle);
				} else if (b.status == 1) {
					canvas.drawCircle(b.c.x, b.c.y, b.radius, mOuterCircleSelect);
				}
				canvas.drawCircle(b.c.x, b.c.y, b.radius / 3, mCenterCircle);
			}
		}
		Point preP = null;
		for (Point p : pList) {
			if (preP != null) {
				canvas.drawLine(preP.x, preP.y, p.x, p.y, mOuterCircle);
			}
			preP = p;
		}

		if (!pList.isEmpty() && global != null && isOnGoing) {
			Point p = pList.get(pList.size() - 1);
			canvas.drawLine(p.x, p.y, global.x, global.y, mOuterCircle);
		}
	}

	public void clear() {
		pList.clear();
		isOnGoing = true;
		global = null;
		result = new ArrayList<Integer>();
		int wlen = _boxes.length;
		int hlen = _boxes[0].length;

		for (int i = 0; i < wlen; i++) {
			for (int j = 0; j < hlen; j++) {
				_boxes[i][j].status = 0;
			}
		}
		invalidate();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMinimumHeight(getSizeInPixel(80 * 3));
		setMinimumWidth(getSizeInPixel(80 * 3));
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		int wlen = _boxes.length;
		int hlen = _boxes[0].length;
		int spac = getSizeInPixel(80);
		int padding = (spac / 6);

		for (int i = 0; i < wlen; i++) {
			for (int j = 0; j < hlen; j++) {
				_boxes[i][j] = new Box();

				int left = (i * spac) + padding;
				int top = (j * spac) + padding;
				int right = ((i * spac) + spac) - padding;
				int bottom = ((j * spac) + spac) - padding;
				RectF tr = new RectF(left, top, right, bottom);
				int cr = (spac / 2) - padding;

				_boxes[i][j].radius = cr;
				_boxes[i][j].c = new Point(tr.left + (tr.width() / 2), tr.top + (tr.height() / 2));
				_boxes[i][j].rect = new RectF(left + cr / 3, top + cr / 3, right - cr / 3, bottom - cr / 3);

			}
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		Point p = new Point(event.getX(), event.getY());

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchDetect(1, p);
				invalidate();
				global = p;
			break;
			case MotionEvent.ACTION_MOVE:
				touchDetect(1, p);
				global = p;
				invalidate();
			break;
			case MotionEvent.ACTION_UP:
				touchDetect(1, p);
				invalidate();
				global = null;

				if (onDrawFinish != null && isOnGoing && !isAllow()) {
					onDrawFinish.onFinish(result);
				}

				isOnGoing = false;
				if (isAllow())
					clear();

			break;
			default:
			break;
		}
		return true;
	}

	private boolean isAllow() {
		// TODO check given pattern.

		return pList.size() < 4;
	}

	public ArrayList<Integer> getResult() {
		return this.result;
	}

	private class Point {
		float	x;
		float	y;

		public Point(float x, float y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Point) {
				Point p = (Point) o;
				return (p.x == this.x && p.y == this.y);
			}
			return false;
		}
	}

	private void touchDetect(int status, Point p) {

		int wlen = _boxes.length;
		int hlen = _boxes[0].length;

		for (int i = 0; i < wlen; i++) {
			for (int j = 0; j < hlen; j++) {
				if (_boxes[i][j].rect.contains(p.x, p.y)) {

					if (isOnGoing) {
						_boxes[i][j].status = status;
						secureAdd(_boxes[i][j].c, i, j);

					}

				}
			}
		}
	}

	private void secureAdd(Point p, int i, int j) {
		boolean avail = false;
		for (Point x : pList) {
			if (x.equals(p)) {
				avail = true;
				break;
			}
		}
		if (!avail) {
			pList.add(p);
			result.add(seqByIndex(i, j));
			vibrate(50);
		}
	}

	private static int seqByIndex(int i, int j) {
		switch (j) {
			case 0:
				return i + j + 1;
			case 1:
				return i + j + 3;
			case 2:
				return i + j + 5;
			default:
				return 0;
		}
	}

	private void vibrate(int duration) {
		Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		vb.vibrate(duration);

	}

	public void setOnDrawFinish(OnDrawFinish drawFinish) {
		this.onDrawFinish = drawFinish;
	}

	public static interface OnDrawFinish {
		public void onFinish(ArrayList<Integer> result);
	}

}
