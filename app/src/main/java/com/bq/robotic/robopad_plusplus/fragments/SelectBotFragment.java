/*
* This file is part of the RoboPad++
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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.bq.robotic.robopad_plusplus.R;
import com.bq.robotic.robopad_plusplus.RoboPadConstants;
import com.bq.robotic.robopad_plusplus.listeners.SelectBotListener;


/**
 * Fragment for selecting a new type of robot to control it
 * 
 * @author Estefanía Sarasola Elvira
 *
 */

public class SelectBotFragment extends Fragment {

	// Debugging
	private static final String LOG_TAG = "SelectBotFragment";

	SelectBotListener listener;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.fragment_select_bot, container, false);

		if(listener != null) {
			listener.onSetFragmentTitle(R.string.select_robot);
		}

		setUiListeners(layout);

		return layout;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Retain this fragment across configuration changes.
		setRetainInstance(true);
	}
    

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Check the listener is the correct one: the fragment activity container
		// implements that listener
		if (activity instanceof SelectBotListener) {
			this.listener = (SelectBotListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement SelectBotListener");
		}
	}


	/**
	 * Send the message to the Arduino board depending on the button pressed
	 * 
	 * @param containerLayout The id of the view pressed
	 */
	protected void setUiListeners(View containerLayout) {

		Button pollywogButton = (Button) containerLayout.findViewById(R.id.pollywog_button);
		pollywogButton.setOnClickListener(onButtonClick);

		Button beetleButton = (Button) containerLayout.findViewById(R.id.beetle_button);
		beetleButton.setOnClickListener(onButtonClick);

		Button rhinoButton = (Button) containerLayout.findViewById(R.id.rhino_button);
		rhinoButton.setOnClickListener(onButtonClick);

        Button crabButton = (Button) containerLayout.findViewById(R.id.crab_button);
        crabButton.setOnClickListener(onButtonClick);

		Button genericRobotButton = (Button) containerLayout.findViewById(R.id.generic_button);
		genericRobotButton.setOnClickListener(onButtonClick);
	}


	/**
	 * Listener for the views that manage only clicks
	 */
	protected OnClickListener onButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if(listener == null) {
				Log.e(LOG_TAG, "SelectBotListener is null");
				return;
			}

			switch(v.getId()) { 

			case R.id.pollywog_button:
				listener.onRobotSelected(RoboPadConstants.robotType.POLLYWOG);    				
				break;

			case R.id.beetle_button:
				listener.onRobotSelected(RoboPadConstants.robotType.BEETLE);      				
				break;

			case R.id.rhino_button:
				listener.onRobotSelected(RoboPadConstants.robotType.RHINO);    				
				break;

            case R.id.crab_button:
                listener.onRobotSelected(RoboPadConstants.robotType.CRAB);
                break;

			case R.id.generic_button:
				listener.onRobotSelected(RoboPadConstants.robotType.GENERIC_ROBOT);      				
				break;

			}

		}
	};

}
