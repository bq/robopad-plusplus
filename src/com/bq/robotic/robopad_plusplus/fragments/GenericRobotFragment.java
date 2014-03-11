/*
* This file is part of the GamePad
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

package com.bq.robotic.robopad_plusplus.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.bq.robotic.robopad_plusplus.R;
import com.bq.robotic.robopad_plusplus.RoboPadConstants;
import com.bq.robotic.robopad_plusplus.RoboPadConstants.robotType;


/**
 * Fragment of the game pad controller for a generic robot.
 * 
 * @author Estefanía Sarasola Elvira
 *
 */

public class GenericRobotFragment extends RobotFragment {

	// Debugging
	private static final String LOG_TAG = "GenericRobotFragment";


	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.fragment_generic_robot, container, false);

		if(listener != null) {
			listener.onSetFragmentTitle(R.string.generic_robot);
		}

		setUiListeners(layout);

		return layout;

	}


	/**
	 * Set the listeners to the views that need them. It must be done here in the fragment in order
	 * to get the callback here and not in the FragmentActivity, that would be a mess with all the 
	 * callbacks of all the possible fragments
	 * 
	 * @param The view used as the main container for this fragment
	 */
	@Override
	protected void setUiListeners(View containerLayout) {

		ImageButton stopButton = (ImageButton) containerLayout.findViewById(R.id.stop_button);
		stopButton.setOnClickListener(onButtonClick);
		
		Button scheduleButton = (Button) containerLayout.findViewById(R.id.schedule_button);
		scheduleButton.setOnClickListener(onButtonClick);

		ImageButton upButton = (ImageButton) containerLayout.findViewById(R.id.up_button);
		upButton.setOnTouchListener(buttonOnTouchListener);

		ImageButton downButton = (ImageButton) containerLayout.findViewById(R.id.down_button);
		downButton.setOnTouchListener(buttonOnTouchListener);

		ImageButton leftButton = (ImageButton) containerLayout.findViewById(R.id.left_button);
		leftButton.setOnTouchListener(buttonOnTouchListener);

		ImageButton rightButton = (ImageButton) containerLayout.findViewById(R.id.right_button);
		rightButton.setOnTouchListener(buttonOnTouchListener);

		Button commandButton1 = (Button) containerLayout.findViewById(R.id.command_button_1);
		commandButton1.setOnClickListener(onButtonClick);

		Button commandButton2 = (Button) containerLayout.findViewById(R.id.command_button_2);
		commandButton2.setOnClickListener(onButtonClick);

		Button commandButton3 = (Button) containerLayout.findViewById(R.id.command_button_3);
		commandButton3.setOnClickListener(onButtonClick);

		Button commandButton4 = (Button) containerLayout.findViewById(R.id.command_button_4);
		commandButton4.setOnClickListener(onButtonClick);

		Button commandButton5 = (Button) containerLayout.findViewById(R.id.command_button_5);
		commandButton5.setOnClickListener(onButtonClick);

		Button commandButton6 = (Button) containerLayout.findViewById(R.id.command_button_6);
		commandButton6.setOnClickListener(onButtonClick);
	}


	/**
	 * Send the message to the Arduino board depending on the button pressed
	 * 
	 * @param viewId The id of the view pressed
	 */
	@Override
	public void controlButtonActionDown(int viewId) {

		if(listener == null) {
			Log.e(LOG_TAG, "RobotListener is null");
			return;
		}

		switch(viewId) { 	

			case R.id.up_button:
				listener.onSendMessage(RoboPadConstants.UP_COMMAND);
				//	    			Log.e(LOG_TAG, "up command send");
				break;
	
			case R.id.down_button:
				listener.onSendMessage(RoboPadConstants.DOWN_COMMAND);
				//	    			Log.e(LOG_TAG, "down command send");
				break;
	
			case R.id.left_button:
				listener.onSendMessage(RoboPadConstants.LEFT_COMMAND);	
				//	    			Log.e(LOG_TAG, "left command send");
				break;
	
			case R.id.right_button:
				listener.onSendMessage(RoboPadConstants.RIGHT_COMMAND);
				//	    			Log.e(LOG_TAG, "right command send");
				break;

		}
	}


	/**
	 * Listeners for the views that manage only clicks
	 */
	protected OnClickListener onButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if(listener == null) {
				Log.e(LOG_TAG, "RobotListener is null");
				return;
			}

			switch(v.getId()) { 

				case R.id.stop_button:
					listener.onSendMessage(RoboPadConstants.STOP_COMMAND);    				
					break;
					
				case R.id.schedule_button:
					listener.onScheduleButtonClicked(robotType.GENERIC_ROBOT);    				
					break;
	
				case R.id.command_button_1: 
					listener.onSendMessage(RoboPadConstants.COMMAND_1); 
					break;
	
				case R.id.command_button_2: 
					listener.onSendMessage(RoboPadConstants.COMMAND_2); 
					break;
	
				case R.id.command_button_3: 
					listener.onSendMessage(RoboPadConstants.COMMAND_3); 
					break;
	
				case R.id.command_button_4: 
					listener.onSendMessage(RoboPadConstants.COMMAND_4); 
					break;
	
				case R.id.command_button_5: 
					listener.onSendMessage(RoboPadConstants.COMMAND_5); 
					break;
	
				case R.id.command_button_6: 
					listener.onSendMessage(RoboPadConstants.COMMAND_6); 
					break;

			}

		}
	};


}
