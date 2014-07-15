package com.wafone.customlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class CustomSwitch extends CheckBox {

	private Paint	mPaintOn, mPaintOff, mPaintBase, mTextPaint;
	private int		cx, cy;

	public CustomSwitch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mPaintOn = new Paint();
		mPaintOn.setAntiAlias(true);
		mPaintOn.setStrokeWidth(2);
		mPaintOn.setColor(Color.parseColor("#009de0"));

		mPaintOff = new Paint(mPaintOn);
		mPaintOff.setColor(Color.LTGRAY);

		mPaintBase = new Paint(mPaintOff);
		mPaintBase.setColor(Color.parseColor("#009de0"));
		mPaintBase.setStyle(Paint.Style.STROKE);
		mPaintBase.setStrokeWidth(getSizeInPixel(5));

		mTextPaint = new Paint();
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setStyle(Style.FILL);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(getSizeInPixel(15));
	}

	/**
	 * @param size
	 * @return
	 */
	private float getSizeInPixel(int size) {
		return (size * getResources().getDisplayMetrics().density);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		cx = w / 2;
		cy = h / 2;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		canvas.drawCircle(cx, cy, getSizeInPixel(20), mPaintBase);
		if (this.isChecked()) {
			canvas.drawCircle(cx, cy, getSizeInPixel(15), mPaintOn);
			drawCenterText("ON", canvas, mTextPaint, cx, cy);
		} else {
			canvas.drawCircle(cx, cy, getSizeInPixel(15), mPaintOff);
			drawCenterText("OFF", canvas, mTextPaint, cx, cy);
		}
	}

	private void drawCenterText(String text, Canvas canvas, Paint paint, float cx, float cy) {
		Rect r = new Rect();
		paint.getTextBounds(text, 0, text.length(), r);
		int height = r.bottom + r.height();
		int width = r.left + r.width();
		canvas.drawText(text, cx - width / 2, cy + height / 2, paint);

	}
}
