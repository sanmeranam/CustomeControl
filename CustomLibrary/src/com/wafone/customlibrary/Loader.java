package com.wafone.customlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class Loader extends View {

	private Handler	spinHandler		= new Handler() {
										@Override
										public void handleMessage(Message msg) {
											invalidate();
											flowProgress += flowIncr;
											if (flowProgress > flowLength) {
												flowProgress = 0;
											}
											spinHandler.sendEmptyMessageDelayed(0, 100);

										}
									};

	private int		baseColor		= Color.parseColor("#99000000");
	private int		foreColor		= Color.parseColor("#009de0");
	private String	type			= "FLOW";
	private Paint	mForePaint, mBackPaint;
	private int		viewWidth, viewHeight;
	private int		barStrock		= 3;
	private int		flowLength		= 100;
	private int		flowIncr		= 10;
	private int		flowProgress	= 0;

	public Loader(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Loader);
		foreColor = typedArray.getColor(R.styleable.Loader_foreColor, foreColor);

		float screeRatio = (1 / getResources().getDisplayMetrics().density);

		barStrock = (int) (barStrock / screeRatio);
		flowLength = (int) (flowLength / screeRatio);
		flowIncr = (int) (flowIncr / screeRatio);

		mForePaint = new Paint();
		mForePaint.setColor(foreColor);
		mForePaint.setStrokeWidth(barStrock);

		mBackPaint = new Paint();
		mBackPaint.setColor(baseColor);
		mBackPaint.setStrokeWidth(barStrock);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		viewHeight = this.getHeight();
		viewWidth = this.getWidth();

		int mid = (viewHeight / 2) - barStrock / 2;

		if (type.equalsIgnoreCase("flow")) {

			int gap = 5;
			int secNumber = viewWidth / flowLength;

			for (int i = -1 * flowLength; i < secNumber + 1; i++) {
				int gapp = i == 0 ? 0 : gap;

				int x1 = (i + (gapp * i) + (i * flowLength)) + flowProgress;
				int x2 = ((i + (gapp * i) + (i * flowLength)) + flowLength) + flowProgress;

				canvas.drawLine(x1, mid, x2, mid, mForePaint);
			}
		} else {
			canvas.drawLine(0, mid, viewWidth, mid, mBackPaint);
		}

		super.onDraw(canvas);
	}

	public void start() {
		flowProgress = 0;
		spinHandler.sendEmptyMessage(0);
	}

	public void stop() {
		flowProgress = 0;
		spinHandler.removeMessages(0);
	}

}
