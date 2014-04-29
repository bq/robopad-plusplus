/*
* This file is part of the RoboPad
*
* Copyright (C) 2013 Mundo Reader S.L.
* 
* Date: February 2014
* Author: Estefanía Sarasola Elvira <estefania.sarasola@bq.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/

package com.bq.robotic.robopad_plusplus.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Class for the vertical sliders, used the SeekBar as the base class
 */

public class SliderView extends SeekBar {

	public SliderView(Context context) {
		super(context);
	}


	public SliderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	public SliderView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(h, w, oldh, oldw);
	}

	@Override 
	public synchronized void setProgress(int progress) { 
		super.setProgress(progress); 
		
		// this line is needed in order to update the thumb position too
		onSizeChanged(getWidth(), getHeight(), 0, 0); 
	}


	@Override
	public void setThumbOffset(int thumbOffset) {
		super.setThumbOffset(thumbOffset);
	}


	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);

		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	
	/**
	 * Rotate the base seekbar -90 degrees to put it in vertical
	 */
	protected void onDraw(Canvas c) {
		c.rotate(-90);
		c.translate(-getHeight(), 0);

		super.onDraw(c);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!isEnabled()) {
			return false;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
			setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
			onSizeChanged(getWidth(), getHeight(), 0, 0);
			break;

		case MotionEvent.ACTION_CANCEL:
			break;
		}

		return true;
	}

}
