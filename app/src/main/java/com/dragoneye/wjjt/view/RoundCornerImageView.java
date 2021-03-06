package com.dragoneye.wjjt.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.dragoneye.wjjt.R;

/**
 * �Զ���View��ʵ��Բ�ǣ�Բ�ε�Ч��
 *
 * @author zhy
 *
 */
public class RoundCornerImageView extends ImageView
{

	/**
	 * TYPE_CIRCLE / TYPE_ROUND
	 */
	private int type;
	private static final int TYPE_CIRCLE = 0;
	private static final int TYPE_ROUND = 1;
	private boolean isShowBorderline = false;
	private static final int BORDERLINE_WIDTH = 5;

	private Paint mPaint;
	private int roundWidth = 30;
	private int roundHeight = 30;
	private Paint mPaint2;
	private Paint mPaint3;

	public RoundCornerImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public RoundCornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public RoundCornerImageView(Context context) {
		super(context);
		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {

		if(attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundAngleImageView);
			roundWidth= a.getDimensionPixelSize(R.styleable.RoundAngleImageView_roundWidth, roundWidth);
			roundHeight= a.getDimensionPixelSize(R.styleable.RoundAngleImageView_roundHeight, roundHeight);
		}else {
			float density = context.getResources().getDisplayMetrics().density;
			roundWidth = (int) (roundWidth*density);
			roundHeight = (int) (roundHeight*density);
		}

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
//		paint.setStyle(Paint.Style.STROKE);
//		paint.setStrokeWidth(BORDERLINE_WIDTH);
//		paint.setColor(Color.WHITE);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

		mPaint2 = new Paint();
		mPaint2.setXfermode(null);

		mPaint3 = new Paint();
		mPaint3.setAntiAlias(true);
		mPaint3.setColor(Color.WHITE);
		mPaint3.setStyle(Paint.Style.STROKE);
		mPaint3.setStrokeWidth(BORDERLINE_WIDTH);
	}

	@Override
	public void draw(Canvas canvas) {
		Bitmap bitmapSrc = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
		Canvas canvasSrc = new Canvas(bitmapSrc);
		super.draw(canvasSrc);
		drawLiftUp(canvasSrc);
		drawRightUp(canvasSrc);
		drawLiftDown(canvasSrc);
		drawRightDown(canvasSrc);

		canvas.drawBitmap(bitmapSrc, 0, 0, mPaint2);
		int padding = 2;
		if (isShowBorderline) {
//			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

			RectF rect = new RectF(padding, padding, getWidth() - padding , getHeight() - padding );
			canvas.drawRoundRect(rect, 25, 25, mPaint3);
		}
		bitmapSrc.recycle();

//		roundSrc.drawBitmap(bitmapSrc, 0, 0, paint2);

//		canvas.drawRoundRect(rect, roundWidth, roundWidth, paint2);
//
//
////		if(isShowBorderline) {
//////			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
////
////			RectF rect = new RectF(5, 5, getWidth(), getHeight());
////			canvas2.drawRoundRect(rect, 30, 30, paint3);
////		}
//		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//		canvas.drawBitmap(bitmapSrc, 0, 0, paint);
//		bitmapSrc.recycle();
	}

	private void drawLiftUp(Canvas canvas) {
		Path path = new Path();
		path.moveTo(0, roundHeight);
		path.lineTo(0, 0);
		path.lineTo(roundWidth, 0);
		path.arcTo(new RectF(
						0,
						0,
						roundWidth*2,
						roundHeight*2),
				-90,
				-90);
		path.close();
		canvas.drawPath(path, mPaint);
	}

	private void drawLiftDown(Canvas canvas) {
		Path path = new Path();
		path.moveTo(0, getHeight()-roundHeight);
		path.lineTo(0, getHeight());
		path.lineTo(roundWidth, getHeight());
		path.arcTo(new RectF(
						0,
						getHeight()-roundHeight*2,
						0+roundWidth*2,
						getHeight()),
				90,
				90);
		path.close();
		canvas.drawPath(path, mPaint);
	}

	private void drawRightDown(Canvas canvas) {
		Path path = new Path();
		path.moveTo(getWidth()-roundWidth, getHeight());
		path.lineTo(getWidth(), getHeight());
		path.lineTo(getWidth(), getHeight()-roundHeight);
		path.arcTo(new RectF(
				getWidth()-roundWidth*2,
				getHeight()-roundHeight*2,
				getWidth(),
				getHeight()), 0, 90);
		path.close();
		canvas.drawPath(path, mPaint);
	}

	private void drawRightUp(Canvas canvas) {
		Path path = new Path();
		path.moveTo(getWidth(), roundHeight);
		path.lineTo(getWidth(), 0);
		path.lineTo(getWidth()-roundWidth, 0);
		path.arcTo(new RectF(
						getWidth()-roundWidth*2,
						0,
						getWidth(),
						0+roundHeight*2),
				-90,
				90);
		path.close();
		canvas.drawPath(path, mPaint);
	}



//
//	/**
//	 * ���ԭͼ���Բ��
//	 *
//	 * @param source
//	 * @return
//	 */
//	private Bitmap createRoundCornerImage(Bitmap source)
//	{
//		//source = Bitmap.createScaledBitmap(source, getWidth(), getHeight(), false);
//		final Paint paint = new Paint();
//		paint.setAntiAlias(true);
//		Bitmap target = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
//		Canvas canvas = new Canvas(target);
//		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//		RectF rect = new RectF(0, 0, getWidth(), getHeight());
//		if(isShowBorderline){
//			rect = new RectF(BORDERLINE_WIDTH, BORDERLINE_WIDTH, getWidth()-BORDERLINE_WIDTH, getHeight()-BORDERLINE_WIDTH);
//		}
//		canvas.drawRoundRect(rect, mRadius, mRadius, paint);
//		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//		canvas.drawBitmap(source, null, rect, paint);
//		if(isShowBorderline){
//			paint.reset();
//			paint.setAntiAlias(true);
//			paint.setColor(Color.WHITE);
//			paint.setStyle(Paint.Style.STROKE);
//			paint.setStrokeWidth(BORDERLINE_WIDTH);
////			rect = new RectF(0, 0, getWidth(), getHeight());
//			canvas.drawRoundRect(rect, mRadius, mRadius, paint);
//		}
//
//		return target;
//	}

	public void setShowBorderline(boolean isShowBorderline){
		this.isShowBorderline = isShowBorderline;
	}
}
