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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.bq.robotic.robopad_plusplus.R;
import com.bq.robotic.robopad_plusplus.listeners.RobotListener;
import com.bq.robotic.robopad_plusplus.listeners.TipsManagerListener;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants.robotState;
import com.bq.robotic.robopad_plusplus.utils.TipsManager;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;


/**
 * Base fragment for all the robot fragments
 *
 * @author Estefanía Sarasola Elvira
 *
 */

public abstract class RobotFragment extends Fragment implements TipsManagerListener {

	// Debugging
	private static final String LOG_TAG = "RobotFragment";

	protected boolean mIsClick;
	protected boolean mIsConnected = false;

	protected RobotListener listener;

    // Tips
    protected ToolTipRelativeLayout mToolTipFrameLayout;
    protected TipsManager tipsManager;

    protected robotState state = RoboPadConstants.robotState.MANUAL_CONTROL;


	/**
	 * Set the listeners to the UI views
	 * @param containerLayout
	 */
	protected abstract void setUiListeners (View containerLayout);


	/**
	 * Send the message depending on the button pressed
	 * @param viewId the id of the view pressed
	 */
	protected abstract void controlButtonActionDown(int viewId);


    /**
     * The state of the robot changes. The state is the type of control the user has of the robot
     * such as manual control, or if the robot is in line follower mode
     * @param nextState next state the robot is going to have
     */
    protected abstract void stateChanged(robotState nextState);


	/**
	 * Callback method called from the activity when the Bluetooth change its status to connected
	 */
	public void onBluetoothConnected() {}


	/**
	 * Callback method called from the activity when the Bluetooth change its status to disconnected
	 */
	public void onBluetoothDisconnected() {}


    /**
     * Set the fragmentActivity listener. Right now it is not necessary because the
     * fragment activity that contains the fragments is the one that implements the listener
     * so it is done in the onAttach of RobotFragment. But with this method can be another class
     * witch implements the listener not the container fragment activity.
     *
     * @param listener The RobotListener
     */
    public void setRobotListener(RobotListener listener) {
        this.listener = listener;
    }


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Check the listener is the correct one: the fragment activity container
		// implements that listener
		if (activity instanceof RobotListener) {
			this.listener = (RobotListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement robotListener");
		}
	}


    /**
     * By default checks the preferences for the show tips. The onClickListener on mToolTipFrameLayout
     * is for show the tips until isLastTipToShow is set to true.
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        mToolTipFrameLayout = (ToolTipRelativeLayout) getActivity().findViewById(R.id.activity_main_tooltipframelayout);

        tipsManager = new TipsManager(getActivity(), mToolTipFrameLayout, this);
        tipsManager.initTips();

    }


	/**
	 * Listener for the touch events. When action_down, the user is pressing the button
	 * so we send the message to the arduino, and when action_up it is send a message to the arduino
	 * in order to stop it.
	 */
	protected OnTouchListener buttonOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			final View view = v;

			Thread sendActionThread;

			switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:

                if(state != RoboPadConstants.robotState.MANUAL_CONTROL) {
                    stateChanged(RoboPadConstants.robotState.MANUAL_CONTROL);
                }

				if(listener != null && !listener.onCheckIsConnected()) {
					mIsConnected = false;
					break;
				} else {
					mIsConnected = true;
				}

				mIsClick = false;
				sendActionThread = createSendActionThread(view.getId());
				sendActionThread.start();

				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:

				if(!mIsConnected) {
					break;
				}

				mIsClick = true;
				if (listener != null) {
					listener.onSendMessage(RoboPadConstants.STOP_COMMAND);
				}

				break;

			}

			return false;
		}

	};



	/**
	 * Thread to send the command but waits and send the stop command with a 130 delay
	 * in case it was only a click and the arduino app didn't process the stop command
	 * because of itself delays
	 *
	 * @param actionId the id of the view touched
	 * @return Thread The thread that send the commands when pressed the corresponding buttons
	 */
	private Thread createSendActionThread(final int actionId) {

		Thread sendActionThread = new Thread() {

			@Override
			public void run() {
				try {

					if(!mIsClick) {
						controlButtonActionDown(actionId);
					}

					sleep(RoboPadConstants.CLICK_SLEEP_TIME);

					if(mIsClick && listener != null) {
						Log.e(LOG_TAG, "stop command in thread send");
						listener.onSendMessage(RoboPadConstants.STOP_COMMAND);
					}

				} catch (InterruptedException e) {
					Log.e(LOG_TAG, "error in sendActionThread: )" + e);
				}

			}

		};

		return sendActionThread;
	}

}
