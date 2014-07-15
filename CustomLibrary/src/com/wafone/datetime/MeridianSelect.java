package com.wafone.datetime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

public class MeridianSelect extends SurfaceView {

	private boolean			isSelected;
	private String			text	= "AM";
	private int				radius, textSize = 17, centerX, centerY;
	private float			ratio;
	private Paint			mOuterPaint, mTextPaint, mLineStrok;
	private SelectListener	listener;

	public MeridianSelect(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MeridianSelect(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MeridianSelect(Context context) {
		super(context);
		init();
	}

	private void init() {
		ratio = getResources().getDisplayMetrics().density;
		textSize = (int) (textSize * ratio);

		mOuterPaint = new Paint();
		mOuterPaint.setAntiAlias(true);
		mOuterPaint.setColor(Color.WHITE);

		mTextPaint = new Paint(mOuterPaint);
		mTextPaint.setTextSize(textSize);
		mTextPaint.setColor(Color.LTGRAY);

		mLineStrok = new Paint(mOuterPaint);
		mLineStrok.setColor(Color.parseColor("#66009de0"));
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isSelected = !isSelected;
				v.invalidate();
				if (listener != null) {
					listener.onSelectChange(isSelected);
				}
			}
		});

	}

	public void setText(String text) {
		this.text = text;
		invalidate();
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean b) {
		this.isSelected = b;
		invalidate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		centerX = w / 2;
		centerY = h / 2;
		radius = Math.min(w / 2, h / 2);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawCircle(centerX, centerY, radius, mOuterPaint);
		drawCenterText(text, canvas, mTextPaint, centerX, centerY);

		if (isSelected) {
			canvas.drawCircle(centerX, centerY, radius, mLineStrok);
		}
		super.onDraw(canvas);
	}

	private void drawCenterText(String text, Canvas canvas, Paint paint, float cx, float cy) {
		Rect r = new Rect();
		paint.getTextBounds(text, 0, text.length(), r);
		int height = r.bottom + r.height();
		int width = r.left + r.width();

		canvas.drawText(text, cx - width / 2, cy + height / 2, paint);

	}

	public void setOnSelectListener(SelectListener listener) {
		this.listener = listener;
	}

	public static interface SelectListener {
		public void onSelectChange(boolean isSelected);
	}

}
