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

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bq.robotic.robopad_plusplus.R;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants.robotState;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants.robotType;
import com.bq.robotic.robopad_plusplus.utils.RobotConnectionsPopupWindow;
import com.bq.robotic.robopad_plusplus.utils.TipsFactory;
import com.nhaarman.supertooltips.ToolTipView;


/**
 * Fragment of the game pad controller for the Pollywog robot.
 * 
 * @author Estefanía Sarasola Elvira
 *
 */

public class PollywogFragment extends RobotFragment {

	// Debugging
	private static final String LOG_TAG = "PollywogFragment";

    private ImageButton pinExplanationButton;
    private ImageButton lineFollowerButton;


    // Tips
    private tips currentTip;
    private enum tips {PIN, BLUETOOTH, SCHEDULE, PAD, LINE_FOLLOWER}


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_pollywog, container, false);

        setUiListeners(layout);

        return layout;

    }


    /**
     * Set the listeners to the views that need them. It must be done here in the fragment in order
     * to get the callback here and not in the FragmentActivity, that would be a mess with all the
     * callbacks of all the possible fragments
     *
     * @param containerLayout The view used as the main container for this fragment
     */
	@Override
	protected void setUiListeners(View containerLayout) {

		ImageButton stopButton = (ImageButton) containerLayout.findViewById(R.id.stop_button);
		stopButton.setOnClickListener(onButtonClick);

        ImageButton scheduleButton = (ImageButton) containerLayout.findViewById(R.id.schedule_button);
        scheduleButton.setOnClickListener(onButtonClick);

		ImageButton upButton = (ImageButton) containerLayout.findViewById(R.id.up_button);
		upButton.setOnTouchListener(buttonOnTouchListener);

		ImageButton downButton = (ImageButton) containerLayout.findViewById(R.id.down_button);
		downButton.setOnTouchListener(buttonOnTouchListener);

		ImageButton leftButton = (ImageButton) containerLayout.findViewById(R.id.left_button);
		leftButton.setOnTouchListener(buttonOnTouchListener);

		ImageButton rightButton = (ImageButton) containerLayout.findViewById(R.id.right_button);
		rightButton.setOnTouchListener(buttonOnTouchListener);

        pinExplanationButton = (ImageButton) containerLayout.findViewById(R.id.bot_icon);
        pinExplanationButton.setOnClickListener(onButtonClick);

        lineFollowerButton = (ImageButton) containerLayout.findViewById(R.id.line_follower);
        lineFollowerButton.setOnClickListener(onButtonClick);
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
				break;
	
			case R.id.down_button:
				listener.onSendMessage(RoboPadConstants.DOWN_COMMAND);
				break;
	
			case R.id.left_button:
				listener.onSendMessage(RoboPadConstants.LEFT_COMMAND);	
				break;
	
			case R.id.right_button:
				listener.onSendMessage(RoboPadConstants.RIGHT_COMMAND);
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

                    if(state != RoboPadConstants.robotState.MANUAL_CONTROL) {
                        stateChanged(RoboPadConstants.robotState.MANUAL_CONTROL);
                    }
					listener.onSendMessage(RoboPadConstants.STOP_COMMAND);    				
					break;

                case R.id.bot_icon:

                    PopupWindow popupWindow = (new RobotConnectionsPopupWindow(RoboPadConstants.robotType.POLLYWOG,
                            getActivity())).getPopupWindow();

                    int offsetY = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                            getActivity().getResources().getDisplayMetrics());

                    popupWindow.showAtLocation(getView(), Gravity.CENTER_VERTICAL | Gravity.LEFT,
                            pinExplanationButton.getRight() - (int)getActivity().getResources().getDimension(R.dimen.button_press_padding),
                            offsetY);

                    break;

				case R.id.schedule_button:

                    if(state != RoboPadConstants.robotState.MANUAL_CONTROL) {
                        stateChanged(RoboPadConstants.robotState.MANUAL_CONTROL);
                    }

					listener.onScheduleButtonClicked(robotType.POLLYWOG);
					break;

                case R.id.line_follower:
                    if(!listener.onCheckIsConnected()) {
                        return;
                    }

                    if(state == robotState.MANUAL_CONTROL) {
                        stateChanged(robotState.LINE_FOLLOWER);

                    } else {
                        stateChanged(robotState.MANUAL_CONTROL);
                    }

                    break;
			}

		}
	};


    /**
     * The state of the robot changes. The state is the type of control the user has of the robot
     * such as manual control, or if the robot is in line follower mode
     * @param nextState next state the robot is going to have
     */
    protected void stateChanged(robotState nextState) {

        switch (nextState) {

            case MANUAL_CONTROL:
                lineFollowerButton.setSelected(false);
                state = robotState.MANUAL_CONTROL;
                listener.onSendMessage(RoboPadConstants.MANUAL_CONTROL_MODE_COMMAND);
                break;

            case LINE_FOLLOWER:
                lineFollowerButton.setSelected(true);
                state = robotState.LINE_FOLLOWER;
                listener.onSendMessage(RoboPadConstants.LINE_FOLLOWER_MODE_COMMAND);
                break;

        }

    }


    private ToolTipView.OnToolTipViewClickedListener onToolTipClicked = new ToolTipView.OnToolTipViewClickedListener() {

        @Override
        public void onToolTipViewClicked(ToolTipView toolTipView) {
            onShowNextTip();
        }
    };


    public void onShowNextTip() {

        if (currentTip == null) {
            setIsLastTipToShow(false);

            mToolTipFrameLayout.removeAllViews();

            mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.pin_explanation_tip_text),
                    getActivity().findViewById(R.id.bot_icon)).setOnToolTipViewClickedListener(onToolTipClicked);

            currentTip = tips.PIN;

        } else if (currentTip.equals(tips.PIN)) {
            mToolTipFrameLayout.removeAllViews();

            mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.bluetooth_tip_text),
                    getActivity().findViewById(R.id.connect_button)).setOnToolTipViewClickedListener(onToolTipClicked);

            currentTip = tips.BLUETOOTH;

        } else if (currentTip.equals(tips.BLUETOOTH)) {
            mToolTipFrameLayout.removeAllViews();

            mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.schedule_tip_text),
                    getActivity().findViewById(R.id.schedule_button)).setOnToolTipViewClickedListener(onToolTipClicked);

            currentTip = tips.SCHEDULE;

        } else if (currentTip.equals(tips.SCHEDULE)) {
            mToolTipFrameLayout.removeAllViews();

            mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.pad_tip_text),
                    getActivity().findViewById(R.id.right_button)).setOnToolTipViewClickedListener(onToolTipClicked);

            currentTip = tips.PAD;

        } else if (currentTip.equals(tips.PAD)) {
            mToolTipFrameLayout.removeAllViews();

            mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.line_follower_text),
                    getActivity().findViewById(R.id.line_follower)).setOnToolTipViewClickedListener(onToolTipClicked);

            currentTip = tips.LINE_FOLLOWER;

        } else if (currentTip.equals(tips.LINE_FOLLOWER)) {
            mToolTipFrameLayout.removeAllViews();

            currentTip = null;
            setIsLastTipToShow(true);
            mToolTipFrameLayout.setOnClickListener(null);

        }

    }


    @Override
    public void setIsLastTipToShow(boolean isLastTipToShow) {
        tipsManager.setLastTipToShow(isLastTipToShow);
    }


    @Override
    public void onBluetoothConnected() {
        ((ImageView) getActivity().findViewById(R.id.bot_icon)).setImageResource(R.drawable.ic_bot_pollywog_connected);
        ((ImageView) getActivity().findViewById(R.id.robot_bg)).setImageResource(R.drawable.pollywog_bg_on);

        stateChanged(robotState.MANUAL_CONTROL);
    }

    @Override
    public void onBluetoothDisconnected() {
        ((ImageView) getActivity().findViewById(R.id.bot_icon)).setImageResource(R.drawable.ic_bot_pollywog_disconnected);
        ((ImageView) getActivity().findViewById(R.id.robot_bg)).setImageResource(R.drawable.pollywog_bg_off);
    }

}
