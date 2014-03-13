
package com.bq.robotic.robopad_plusplus.drag_drop_grid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;

import com.bq.robotic.robopad_plusplus.R;

//public class DeleteDropZoneView extends View {
//
//	private Paint textPaintStraight;
//	private Paint textPaintRed;
//	private Paint bitmapPaint;
//	private Paint bitmapPaintRed;
//	private boolean straight = true;
//
//	private Bitmap trash;
//	private Rect bounds;
//
//
//	public DeleteDropZoneView(Context context) {
//		super(context);
//
//		bounds = new Rect();
//
//		textPaintStraight = createTextPaint();
//		textPaintStraight.setColor(Color.WHITE);
//
//		textPaintRed = createTextPaint();
//		textPaintRed.setColor(Color.RED);
//
//		bitmapPaint = createBaseBitmapPaint();
//
//		bitmapPaintRed = createBaseBitmapPaint();
//		ColorFilter filter = new LightingColorFilter(Color.RED, 1);
//		bitmapPaintRed.setColorFilter(filter);
//
//		setBackgroundColor(Color.BLACK);
//		getBackground().setAlpha(200);
//		
//	}
//	
//	
//	public DeleteDropZoneView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//
//		bounds = new Rect();
//
//		textPaintStraight = createTextPaint();
//		textPaintStraight.setColor(Color.WHITE);
//
//		textPaintRed = createTextPaint();
//		textPaintRed.setColor(Color.RED);
//
//		bitmapPaint = createBaseBitmapPaint();
//
//		bitmapPaintRed = createBaseBitmapPaint();
//		ColorFilter filter = new LightingColorFilter(Color.RED, 1);
//		bitmapPaintRed.setColorFilter(filter);
//
//		setBackgroundColor(Color.BLACK);
//		getBackground().setAlpha(200);
//		
//	}
//	
//
//	private Paint createTextPaint() {
//		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		textPaint.setStyle(Style.FILL);
//		textPaint.setTextAlign(Paint.Align.CENTER);
//		textPaint.setTypeface(Typeface.DEFAULT_BOLD);
//		return textPaint;
//	}
//
//	private Paint createBaseBitmapPaint() {
//		Paint bitmapPaint = new Paint();
//		bitmapPaint.setAntiAlias(true);
//		bitmapPaint.setFilterBitmap(true);
//		bitmapPaint.setDither(true);
//		return bitmapPaint;
//	}
//
//	@Override
//	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//
//		int measuredHeight = getMeasuredHeight();
//		int measuredWidth = getMeasuredWidth();
//		String removeString = getResources().getString(R.string.remove);
//
//		initTrashIcon();
//
//		textPaintStraight.getTextBounds(removeString, 0, 6, bounds);
//
//		int proportion = 3 * measuredHeight / 4;
//		if (straight) {
//			textPaintStraight.setTextSize(proportion);
//			canvas.drawText(removeString, (measuredWidth / 2) + (trash.getWidth() / 2) + 5, measuredHeight - ((measuredHeight - bounds.height()) / 2) , textPaintStraight);
//			canvas.drawBitmap(trash, (measuredWidth / 2) - (bounds.width() / 2) - (trash.getWidth() / 2) - 10, 0, bitmapPaint);
//		} else {
//			textPaintRed.setTextSize(proportion);
//			canvas.drawText(removeString, (measuredWidth / 2) + (trash.getWidth() / 2) + 5, measuredHeight - ((measuredHeight - bounds.height()) / 2) , textPaintRed);
//			canvas.drawBitmap(trash, (measuredWidth / 2) - (bounds.width() / 2) - (trash.getWidth() / 2) - 10, 0, bitmapPaintRed);
//		}
//	}
//
//	private void initTrashIcon() {
//		if (trash == null) {
////			trash = getImage(android.R.drawable.ic_menu_delete, getMeasuredWidth(), getMeasuredHeight());
////			trash = getImage(android.R.drawable.ic_menu_delete, getWidth(), getHeight());
//			trash = getImage(android.R.drawable.ic_menu_delete);
//		}
//	}
//
//	public void highlight() {
//		straight = false;
//		invalidate();
//	}
//
//	public void smother() {
//		straight = true;
//		invalidate();
//	}
//
////	private Bitmap getImage (int id, int width, int height) {
////	    Bitmap bmp = BitmapFactory.decodeResource( getResources(), id );
////	    Bitmap img = Bitmap.createScaledBitmap(bmp, width, height, true);
////	    if (bmp != null && !isInEditMode()) {
////	        bmp.recycle();
////	    }
////	    invalidate();
////	    return img;
////	}
//	
//	private Bitmap getImage (int id) {
//	    Bitmap bmp = BitmapFactory.decodeResource( getResources(), id );
////	    if (bmp != null && !isInEditMode()) {
////	        bmp.recycle();
////	    }
////	    invalidate();
//	    return bmp;
//	}
//
//}


public class DeleteDropZoneView extends Button {

	private boolean straight = true;


	public DeleteDropZoneView(Context context) {
		super(context);

		setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_delete, 0, 0, 0);
		setCompoundDrawablePadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics()));
		setText(getResources().getString(R.string.remove));

		setTextColor(Color.WHITE);
		setBackgroundColor(Color.BLACK);
		getBackground().setAlpha(200);
		
	}
	
	
	public DeleteDropZoneView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_delete, 0, 0, 0);
		setCompoundDrawablePadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics()));
		setText(getResources().getString(R.string.remove));

		setTextColor(Color.WHITE);
		setBackgroundColor(Color.BLACK);
		getBackground().setAlpha(200);
		
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (straight) {
			setBackgroundColor(Color.BLACK);
			getBackground().setAlpha(200);
		} else {
			setBackgroundColor(Color.RED);
			getBackground().setAlpha(200);
		}
	}


	public void highlight() {
		straight = false;
		invalidate();
	}

	public void smother() {
		straight = true;
		invalidate();
	}

	
}
