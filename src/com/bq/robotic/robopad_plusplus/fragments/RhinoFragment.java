///*
//* This file is part of the GamePad
//*
//* Copyright (C) 2013 Mundo Reader S.L.
//* 
//* Date: February 2014
//* Author: Estefanía Sarasola Elvira <estefania.sarasola@bq.com>
//*
//* This program is free software: you can redistribute it and/or modify
//* it under the terms of the GNU General Public License as published by
//* the Free Software Foundation, either version 2 of the License, or
//* (at your option) any later version.
//*
//* This program is distributed in the hope that it will be useful,
//* but WITHOUT ANY WARRANTY; without even the implied warranty of
//* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//* GNU General Public License for more details.
//*
//* You should have received a copy of the GNU General Public License
//* along with this program. If not, see <http://www.gnu.org/licenses/>.
//*
//*/
//
//package com.bq.robotic.robopad_plusplus.fragments;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.SeekBar;
//import android.widget.SeekBar.OnSeekBarChangeListener;
//
//import com.bq.robotic.robopad_plusplus.R;
//import com.bq.robotic.robopad_plusplus.RoboPadConstants;
//
//
///**
// * Fragment of the game pad controller for the rhino robot.
// * 
// * @author Estefanía Sarasola Elvira
// *
// */
//
//public class RhinoFragment extends RobotFragment {
//
//	// Debugging
//	private static final String LOG_TAG = "RhinoFragment";
//	
//	private SliderView mLeftSlider;
//	private SliderView mRightSlider;
//
//
//	@Override
//	public View onCreateView(LayoutInflater inflater,
//			ViewGroup container,
//			Bundle savedInstanceState) {
//
//		View layout = inflater.inflate(R.layout.activity_rhino, container, false);
//
//		if(listener != null) {
//			listener.onSetFragmentTitle(R.string.rhino);
//		}
//
//		setUiListeners(layout);
//
//		return layout;
//
//	}
//	
//	
//	/**
//	 * Stop the rhino robot when exiting this fragment
//	 */
//	@Override
//	public void onDestroy() {	
//		mLeftSlider.setProgress(1);
//		mRightSlider.setProgress(1);
//		
//		super.onDestroy();
//	}
//	
//	
//
//	/**
//	 * Stop the rhino robot when the app is paused 
//	 */
//	@Override
//	public void onPause() {	
//		mLeftSlider.setProgress(1);
//		mRightSlider.setProgress(1);
//		
//		super.onPause();
//	}
//
//	
//	/**
//	 * Set the listeners to the views that need them. It must be done here in the fragment in order
//	 * to get the callback here and not in the FragmentActivity, that would be a mess with all the 
//	 * callbacks of all the possible fragments
//	 * 
//	 * @param The view used as the main container for this fragment
//	 */
//	@Override
//	protected void setUiListeners(View containerLayout) {
//
//		Button connectButton = (Button) containerLayout.findViewById(R.id.connect_button);
//		connectButton.setOnClickListener(onButtonClick);
//
//		Button disconnectButton = (Button) containerLayout.findViewById(R.id.disconnect_button);
//		disconnectButton.setOnClickListener(onButtonClick);
//
//		ImageButton stopButton = (ImageButton) containerLayout.findViewById(R.id.stop_button);
//		stopButton.setOnClickListener(onButtonClick);
//		
//		Button chargeButton = (Button) containerLayout.findViewById(R.id.charge_button);
//		chargeButton.setOnClickListener(onButtonClick);
//
//		mLeftSlider = (SliderView) containerLayout.findViewById(R.id.left_slider);
//		mLeftSlider.setOnSeekBarChangeListener(sliderListener);
//		mLeftSlider.setEnabled(false);
//
//		mRightSlider = (SliderView) containerLayout.findViewById(R.id.right_slider);
//		mRightSlider.setOnSeekBarChangeListener(sliderListener);
//		mRightSlider.setEnabled(false);
//
//	}
//
//
//	@Override
//	public void onBluetoothConnected() {
//		super.onBluetoothConnected();
//			
//		mLeftSlider.setProgress(1);
//		mRightSlider.setProgress(1);
//		
//		mLeftSlider.setEnabled(true);
//		mRightSlider.setEnabled(true);
//	}
//
//
//	@Override
//	public void onBluetoothDisconnected() {
//		super.onBluetoothDisconnected();
//		
//		mLeftSlider.setEnabled(false);
//		mRightSlider.setEnabled(false);
//	}
//
//
//	/**
//	 * Listeners for the views that manage only clicks
//	 */
//	protected OnClickListener onButtonClick = new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//
//			if(listener == null) {
//				Log.e(LOG_TAG, "RobotListener is null");
//				return;
//			}
//
//			switch(v.getId()) { 
//
//			case R.id.connect_button:
//				listener.onConnectRobot();    				
//				break;
//
//			case R.id.disconnect_button:
//				mLeftSlider.setProgress(1);
//				mRightSlider.setProgress(1);
//				
//				listener.onDisconnectRobot();    				
//				break;
//
//			case R.id.stop_button:
//				listener.onSendMessage(RoboPadConstants.STOP_COMMAND);
//				mLeftSlider.setProgress(1);
//				mRightSlider.setProgress(1);
//				break;
//			
//			case R.id.charge_button:
//				listener.onSendMessage(RoboPadConstants.CHARGE_COMMAND);
//				mLeftSlider.setProgress(1);
//				mRightSlider.setProgress(1);
//				break;
//			}
//		}
//	};
//
//
//	/**
//	 * Listener for the sliders
//	 */
//	protected OnSeekBarChangeListener sliderListener = new OnSeekBarChangeListener() {
//
//		@Override
//		public void onProgressChanged(SeekBar seekBar, int progress,
//				boolean fromUser) {
//
//			if(listener != null && listener.onCheckIsConnectedWithoutToast()) {
//				
//				String valueToSend = null;
//				if(progress == 1) {
//					valueToSend = "S";
//				} else if (progress == 2) {
//					valueToSend = "U";
//				} else if (progress == 0) {
//					valueToSend = "D";
//				}
//				
//				if(valueToSend == null) {
//					Log.e(LOG_TAG, "progress was not a valid number: " + progress);
//					return;
//				}
//				
//				switch (seekBar.getId()) {
//				
//					case R.id.left_slider:					
//						listener.onSendMessage(RoboPadConstants.LEFT_COMMAND + valueToSend);
//						break;
//	
//					case R.id.right_slider:
//						listener.onSendMessage(RoboPadConstants.RIGHT_COMMAND + valueToSend);
//						break;
//				}
//			}
//				
//		}
//
//		@Override
//		public void onStartTrackingTouch(SeekBar seekBar) {
//			// nothing to be done
//		}
//
//		@Override
//		public void onStopTrackingTouch(SeekBar seekBar) {
//			// nothing to be done
//		}
//
//	};
//
//
//	@Override
//	protected void controlButtonActionDown(int viewId) {
//		// None button controlled by MotionEvent 
//	}
//
//
//
//}
