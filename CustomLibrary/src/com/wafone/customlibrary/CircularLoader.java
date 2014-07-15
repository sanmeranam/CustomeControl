package com.wafone.customlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class CircularLoader extends View {

	private int	value, textSize = 20;
	private int	baseColor	= Color.parseColor("#99000000");
	private int	foreColor	= Color.parseColor("#009de0");
	private int	textColor	= Color.parseColor("#009de0");
	private Paint	mForePaint, mBackPaint, mTextPaint;
	private int		barStrock	= 6;
	private int		viewWidth, viewHeight;
	private int		centerX, centerY, sideLength, circleRedius;
	private RectF	ovalRectF;
	private boolean	isShowText	= false;

	public CircularLoader(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularLoader);
		value = typedArray.getInteger(R.styleable.CircularLoader_value, 0);
		foreColor = typedArray.getColor(R.styleable.CircularLoader_circleColor, foreColor);
		baseColor = typedArray.getColor(R.styleable.CircularLoader_baseColor, baseColor);
		isShowText = typedArray.getBoolean(R.styleable.CircularLoader_showText, false);
		textSize = typedArray.getInteger(R.styleable.CircularLoader_textSize, textSize);

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

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawCircle(centerX, centerY, circleRedius, mBackPaint);
		canvas.drawArc(ovalRectF, -90, (float) (3.6 * value), false, mForePaint);

		if (isShowText) {
			String s = value + "%";
			Rect f = new Rect();
			mTextPaint.getTextBounds(s, 0, s.length(), f);
			float x = centerX - f.width() / 2;
			float y = centerY + f.height() / 2;

			canvas.drawText(s, x, y, mTextPaint);
		}
		super.onDraw(canvas);
	}

	public void setValue(int value) {
		value = value < 0 ? 0 : value;
		value = value > 100 ? 100 : value;
		this.value = value;

		invalidate();
	}

}
