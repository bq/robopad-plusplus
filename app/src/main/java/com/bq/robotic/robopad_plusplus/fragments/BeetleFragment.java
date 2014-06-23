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
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bq.robotic.robopad_plusplus.R;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants.Claw_next_state;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants.robotState;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants.robotType;
import com.bq.robotic.robopad_plusplus.utils.RobotConnectionsPopupWindow;
import com.bq.robotic.robopad_plusplus.utils.TipsFactory;
import com.nhaarman.supertooltips.ToolTipView;

/**
 * Fragment of the game pad controller for the Beetle robot.
 * 
 * @author Estefanía Sarasola Elvira
 *
 */

public class BeetleFragment extends RobotFragment {

	// Debugging
	private static final String LOG_TAG = "BeetleFragment";

	private int mClawPosition; // Current position of the claw 

    private ImageButton mFullOpenClawButton;
    private ImageButton mOpenStepClawButton;
    private ImageButton mCloseStepClawButton;
    private Handler sendClawValuesHandler;
    private boolean clawButtonUp = false;
    private ImageButton pinExplanationButton;
    private ImageButton lineFollowerButton;
    private ImageButton lightFollowerButton;

    // Tips
    private tips currentTip;

    private enum tips {PIN, BLUETOOTH, SCHEDULE, PAD, CLAWS}


	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.fragment_beetle, container, false);

        ((ImageView) layout.findViewById(R.id.robot_bg)).setImageResource(R.drawable.ic_beetle_bg_off);

        // Put the servo of the claws in a initial position
        mClawPosition = RoboPadConstants.INIT_CLAW_POS; // default open 30 (values from 5 to 50)

        setUiListeners(layout);

        sendClawValuesHandler = new Handler();

		return layout;

	}

	
	/**
	 * Set the listeners to the views that need them. It must be done here in the fragment in order
	 * to get the callback here and not in the FragmentActivity, that would be a mess with all the
	 * callbacks of all the possible fragments
	 * 
	 * @param containerLayout view used as the main container for this fragment
	 */
	@Override
	protected void setUiListeners(View containerLayout) {

		ImageButton stopButton = (ImageButton) containerLayout.findViewById(R.id.stop_button);
		stopButton.setOnClickListener(onButtonClick);

        ImageButton scheduleButton = (ImageButton) containerLayout.findViewById(R.id.schedule_button);
		scheduleButton.setOnClickListener(onButtonClick);

        mFullOpenClawButton = (ImageButton) containerLayout.findViewById(R.id.full_open_claw_button);
        mFullOpenClawButton.setOnClickListener(onButtonClick);

        mOpenStepClawButton = (ImageButton) containerLayout.findViewById(R.id.open_claw_button);
        mOpenStepClawButton.setOnTouchListener(clawOnTouchListener);

        mCloseStepClawButton = (ImageButton) containerLayout.findViewById(R.id.close_claw_button);
        mCloseStepClawButton.setOnTouchListener(clawOnTouchListener);

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

        lightFollowerButton = (ImageButton) containerLayout.findViewById(R.id.light_follower);
        lightFollowerButton.setOnClickListener(onButtonClick);
	}


    // FIXME: change the background image for the beetle
    @Override
    public void onBluetoothConnected() {
        ((ImageView) getActivity().findViewById(R.id.bot_icon)).setImageResource(R.drawable.bot_beetle_connected);
        ((ImageView) getActivity().findViewById(R.id.robot_bg)).setImageResource(R.drawable.ic_beetle_bg_on);

        state = robotState.MANUAL_CONTROL;

        mClawPosition = RoboPadConstants.INIT_CLAW_POS; // default open 30 (values from 5 to 50)
        mOpenStepClawButton.setEnabled(true);
        mFullOpenClawButton.setEnabled(true);
        mCloseStepClawButton.setEnabled(true);
    }

    @Override
    public void onBluetoothDisconnected() {
        ((ImageView) getActivity().findViewById(R.id.bot_icon)).setImageResource(R.drawable.bot_beetle_disconnected);
        ((ImageView) getActivity().findViewById(R.id.robot_bg)).setImageResource(R.drawable.ic_beetle_bg_off);
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
	 * Listener for the views that manage only clicks
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

                case R.id.full_open_claw_button:
                    if(listener.onCheckIsConnected()) {

                        if(state != RoboPadConstants.robotState.MANUAL_CONTROL) {
                            stateChanged(RoboPadConstants.robotState.MANUAL_CONTROL);
                        }

                        listener.onSendMessage(RoboPadConstants.CLAW_COMMAND
                                + getNextClawPosition(Claw_next_state.FULL_OPEN));
                    }
                    break;

                case R.id.bot_icon:

                    PopupWindow popupWindow = (new RobotConnectionsPopupWindow(RoboPadConstants.robotType.POLLYWOG,
                            getActivity())).getPopupWindow();

                    int offsetY = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                            getActivity().getResources().getDisplayMetrics());

                    // Displaying the popup at the specified location, + offsets.
                    popupWindow.showAtLocation(getView(), Gravity.CENTER_VERTICAL | Gravity.LEFT,
                            pinExplanationButton.getRight() - (int)getActivity().getResources().getDimension(R.dimen.button_press_padding),
                            offsetY);

                    break;
					
				case R.id.schedule_button:

                    if(state != RoboPadConstants.robotState.MANUAL_CONTROL) {
                        stateChanged(RoboPadConstants.robotState.MANUAL_CONTROL);
                    }

					listener.onScheduleButtonClicked(robotType.BEETLE);    				
					break;

                case R.id.line_follower:
                    if(!listener.onCheckIsConnected()) {
                        return;
                    }

                    if(state == robotState.MANUAL_CONTROL || state == robotState.LIGHT_FOLLOWER) {
                        stateChanged(robotState.LINE_FOLLOWER);

                    } else {
                        stateChanged(robotState.MANUAL_CONTROL);
                    }

                    break;

                case R.id.light_follower:
                    if(!listener.onCheckIsConnected()) {
                        return;
                    }

                    if(state == robotState.MANUAL_CONTROL || state == robotState.LINE_FOLLOWER) {
                        stateChanged(robotState.LIGHT_FOLLOWER);

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
                lineFollowerButton.setPressed(false);
                lightFollowerButton.setPressed(false);
                state = robotState.MANUAL_CONTROL;
                //FIXME
                Toast.makeText(getActivity(), "Start manual control state!", Toast.LENGTH_SHORT).show();
                listener.onSendMessage(RoboPadConstants.MANUAL_CONTROL_MODE_COMMAND);
                break;

            case LINE_FOLLOWER:
                lineFollowerButton.setPressed(true);
                lightFollowerButton.setPressed(false);
                state = robotState.LINE_FOLLOWER;
                //FIXME
                Toast.makeText(getActivity(), "Start line follower state!", Toast.LENGTH_SHORT).show();
                listener.onSendMessage(RoboPadConstants.LINE_FOLLOWER_MODE_COMMAND);
                break;

            case LIGHT_FOLLOWER:
                lightFollowerButton.setPressed(true);
                lineFollowerButton.setPressed(false);
                state = robotState.LIGHT_FOLLOWER;
                //FIXME
                Toast.makeText(getActivity(), "Start light follower state!", Toast.LENGTH_SHORT).show();
                listener.onSendMessage(RoboPadConstants.LIGHT_FOLLOWER_MODE_COMMAND);
                break;

        }

    }


    /**
     * Listener for the touch events. When action_down, the user is pressing the button
     * so we send the message of the claw value to the arduino, and when action_up it stops sending it
     */
    protected View.OnTouchListener clawOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {


            if(listener == null) {
                return false;
            }

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    if(state != RoboPadConstants.robotState.MANUAL_CONTROL) {
                        stateChanged(RoboPadConstants.robotState.MANUAL_CONTROL);
                    }

                    if(listener.onCheckIsConnected()) {
                        clawButtonUp = false;
                        (new MySendClawValueToArduinoTask(v.getId())).run();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    clawButtonUp = true;
                    break;

            }

            return false;
        }

    };


    /**
     * Get the next position for the claw of the beetle robot
     *
     * @param nextState The next state depending on the button that was pressed
     * @return The message for controlling the position of the servo of the claws
     */
    private String getNextClawPosition(Claw_next_state nextState) {

        // Show buttons enabled or disabled if the claw gets to max or min position
        if(mClawPosition == RoboPadConstants.MAX_OPEN_CLAW_POS
                && nextState == Claw_next_state.CLOSE_STEP) {

            mOpenStepClawButton.setEnabled(true);
            mFullOpenClawButton.setEnabled(true);

        } else if(mClawPosition == RoboPadConstants.MIN_CLOSE_CLAW_POS
                && (nextState == Claw_next_state.OPEN_STEP
                || nextState == Claw_next_state.FULL_OPEN) ) {

            mCloseStepClawButton.setEnabled(true);
        }

        if (nextState == Claw_next_state.OPEN_STEP) {
            mClawPosition -= RoboPadConstants.CLAW_STEP;

        } else if (nextState == Claw_next_state.CLOSE_STEP) {
            mClawPosition += RoboPadConstants.CLAW_STEP;

        } else if (nextState == Claw_next_state.FULL_OPEN) {
            mClawPosition = RoboPadConstants.MAX_OPEN_CLAW_POS;

        }

        // Don't exceed the limits of the claw
        if (mClawPosition <= RoboPadConstants.MAX_OPEN_CLAW_POS) {

            mClawPosition = RoboPadConstants.MAX_OPEN_CLAW_POS;
            mOpenStepClawButton.setEnabled(false);
            mFullOpenClawButton.setEnabled(false);
            clawButtonUp = true;

        } else if (mClawPosition >= RoboPadConstants.MIN_CLOSE_CLAW_POS) {

            mClawPosition = RoboPadConstants.MIN_CLOSE_CLAW_POS;
            mCloseStepClawButton.setEnabled(false);
            clawButtonUp = true;

        }

        return String.valueOf(mClawPosition);

    }


    private ToolTipView.OnToolTipViewClickedListener onToolTipClicked = new ToolTipView.OnToolTipViewClickedListener() {

        @Override
        public void onToolTipViewClicked(ToolTipView toolTipView) {
            onShowNextTip();
        }
    };


    /**
     * Show the next tip for this robot fragment. The tips are displayed one after another when the
     * user clicks on the screen
     */
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

            ToolTipView clawsTip = mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.claws_tip_text),
                    getActivity().findViewById(R.id.full_open_claw_button));

            int margin = getResources().getDimensionPixelSize(R.dimen.claw_buttons_margin);
            clawsTip.setPadding(0,0, margin, 0);

            clawsTip.setPointerCenterX((int) clawsTip.getX() - getActivity().getResources().getDimensionPixelSize(R.dimen.button_press_padding));
            clawsTip.setOnToolTipViewClickedListener(onToolTipClicked);

            currentTip = tips.CLAWS;

        } else if (currentTip.equals(tips.CLAWS)) {
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


    /**************************************************************************************
     *************************   MySendClawValueToArduinoTask   ***************************
     **************************************************************************************/

    class MySendClawValueToArduinoTask implements Runnable {

        private int viewId;

        public MySendClawValueToArduinoTask(int viewId) {
            this.viewId = viewId;
        }

        @Override
        public void run() {

            if(clawButtonUp) {
                sendClawValuesHandler.removeCallbacks(this);

            } else {

                String message = null;

                switch (viewId) {

                    case R.id.open_claw_button:
                        message = RoboPadConstants.CLAW_COMMAND
                                + getNextClawPosition(Claw_next_state.OPEN_STEP);
                        break;

                    case R.id.close_claw_button:
                        message = RoboPadConstants.CLAW_COMMAND
                                + getNextClawPosition(Claw_next_state.CLOSE_STEP);
                        break;
                }

                if (message != null) {
                    listener.onSendMessage(message);
                    sendClawValuesHandler.postDelayed(this, RoboPadConstants.CLICK_SLEEP_TIME);
                }
            }
        }
    }


}
