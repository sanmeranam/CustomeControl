package com.wafone.customlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class GroupCheckText extends SurfaceView {

	private int						resNumber[];
	private String[]				groupText;
	private Paint					mText, mSelect, mLine, mLineDefualt;
	private int						textSize	= 15;
	private RectF					eachRect[], downRect;
	private OnValueChangedListener	changedListener;

	public GroupCheckText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public GroupCheckText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
     * 
     */
	private void init() {
		groupText = new String[] {};

		mText = new Paint();
		mText.setAntiAlias(true);
		mText.setColor(Color.DKGRAY);// Color.parseColor("#009de0"));
		mText.setTypeface(Typeface.DEFAULT_BOLD);
		mText.setTextSize(getSizeInPixel(textSize));

		mLine = new Paint(mText);
		mLine.setColor(Color.parseColor("#009de0"));
		mLine.setStrokeWidth(getSizeInPixel(3));
		mLine.setStrokeCap(Cap.SQUARE);

		mLineDefualt = new Paint(mLine);
		mLineDefualt.setColor(Color.LTGRAY);

		mSelect = new Paint(mText);
		mSelect.setColor(Color.parseColor("#66009de0"));

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		textSize = 12;// (int) getSizeInPixel(5);
		mText.setTextSize(getSizeInPixel(textSize));

		eachRect = new RectF[groupText.length];

		int hGap = (int) getSizeInPixel(7);
		int rw = (int) getSizeInPixel(25);

		int rh = rw, top = 0;// (int) getSizeInPixel(10);

		for (int i = 0; i < groupText.length; i++) {
			int left = i * (rw + hGap);
			int right = left + rw;
			int buttom = rh + top;
			eachRect[i] = new RectF(left, top, right, buttom);
		}
	}

	/**
	 * 
	 * @param size
	 * @return
	 */
	private float getSizeInPixel(int size) {
		return (size * getResources().getDisplayMetrics().density);
	}

	/**
	 * 
	 * @param arrText
	 */
	public void setGroupText(String[] arrText) {
		this.groupText = arrText;
		resNumber = new int[this.groupText.length];
		invalidate();
	}

	/**
	 * 
	 * @return
	 */
	public int[] getGroupSelected() {
		return resNumber;
	}

	/**
	 * 
	 * @return
	 */
	public void setGroupSelected(int[] reint) {
		this.resNumber = reint;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {

		for (int i = 0; i < groupText.length; i++) {

			int cx = (int) (eachRect[i].left + eachRect[i].width() / 2);
			int cy = (int) (eachRect[i].top + eachRect[i].height() / 2);

			drawCenterText(groupText[i], canvas, mText, cx, cy);
			Paint p = resNumber[i] == 1 ? mLine : mLineDefualt;
			canvas.drawLine(eachRect[i].left, eachRect[i].top + eachRect[i].height(), eachRect[i].left + eachRect[i].width(), eachRect[i].top + eachRect[i].height(), p);
		}
		if (downRect != null)
			canvas.drawRect(downRect, mSelect);
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				for (int i = 0; i < groupText.length; i++) {
					if (eachRect[i].contains(event.getX(), event.getY())) {
						downRect = eachRect[i];
						invalidate();
						break;
					}
				}
			break;
			case MotionEvent.ACTION_MOVE:
				downRect = null;
			break;
			case MotionEvent.ACTION_UP:
				for (int i = 0; i < groupText.length; i++) {
					if (eachRect[i].contains(event.getX(), event.getY())) {
						invert(i);
						break;
					}
				}
				downRect = null;
				invalidate();
				if (changedListener != null) {
					changedListener.valueChanged(resNumber);
				}
			break;
		}
		return true;
	}

	private void invert(int index) {
		resNumber[index] = Math.abs(resNumber[index] - 1);
	}

	private void drawCenterText(String text, Canvas canvas, Paint paint, float cx, float cy) {
		Rect r = new Rect();
		paint.getTextBounds(text, 0, text.length(), r);
		int height = r.bottom + r.height();
		int width = r.left + r.width();
		canvas.drawText(text, cx - width / 2, cy + height / 2, paint);

	}

	public void setOnValueChangedListener(OnValueChangedListener changedListener) {
		this.changedListener = changedListener;

	}

	public static interface OnValueChangedListener {
		public void valueChanged(int[] value);
	}

}
