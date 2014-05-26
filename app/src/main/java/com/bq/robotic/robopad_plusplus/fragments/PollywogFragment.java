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

    // Tips
    private ToolTipView pin_explanation_tip;
    private ToolTipView bluetooth_tip;
    private ToolTipView pad_tip;
    private ToolTipView schedule_tip;
    private ToolTipView currentTipView;


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
					listener.onSendMessage(RoboPadConstants.STOP_COMMAND);    				
					break;

                case R.id.bot_icon:

                    PopupWindow popupWindow = (new RobotConnectionsPopupWindow(RoboPadConstants.robotType.POLLYWOG,
                            getActivity())).getPopupWindow();

//                    // Displaying the popup at the specified location, + offsets.
//                    popupWindow.showAtLocation(getView(), Gravity.CENTER_VERTICAL | Gravity.LEFT,
//                            pinExplanationButton.getRight() - pinExplanationButton.getPaddingRight(),
//                            pinExplanationButton.getPaddingTop());

                    int offsetY = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                            getActivity().getResources().getDisplayMetrics());

                    popupWindow.showAtLocation(getView(), Gravity.CENTER_VERTICAL | Gravity.LEFT,
                            pinExplanationButton.getRight() - (int)getActivity().getResources().getDimension(R.dimen.button_press_padding),
                            offsetY);

                    break;

				case R.id.schedule_button:
					listener.onScheduleButtonClicked(robotType.POLLYWOG);
					break;
			}

		}
	};


    private ToolTipView.OnToolTipViewClickedListener onToolTipClicked = new ToolTipView.OnToolTipViewClickedListener() {

        @Override
        public void onToolTipViewClicked(ToolTipView toolTipView) {
            showNextTip();
        }
    };


    protected void showNextTip() {
        if (currentTipView == null) {
            setIsLastTipToShow(false);
            // Pin explanation tip
            pin_explanation_tip = mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.pin_explanation_tip_text),
                    getActivity().findViewById(R.id.bot_icon));

            currentTipView = pin_explanation_tip;
            currentTipView.setOnToolTipViewClickedListener(onToolTipClicked);

        } else if (currentTipView == pin_explanation_tip) {
            pin_explanation_tip.remove();
            pin_explanation_tip = null;

            bluetooth_tip = mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.bluetooth_tip_text),
                    getActivity().findViewById(R.id.connect_button));

            currentTipView = bluetooth_tip;
            currentTipView.setOnToolTipViewClickedListener(onToolTipClicked);

        } else if (currentTipView == bluetooth_tip) {
            bluetooth_tip.remove();
            bluetooth_tip = null;

            schedule_tip = mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.schedule_tip_text),
                    getActivity().findViewById(R.id.schedule_button));

            currentTipView = schedule_tip;
            currentTipView.setOnToolTipViewClickedListener(onToolTipClicked);

        } else if (currentTipView == schedule_tip) {
            schedule_tip.remove();
            schedule_tip = null;

            pad_tip = mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.pad_tip_text),
                    getActivity().findViewById(R.id.right_button));

            currentTipView = pad_tip;
            currentTipView.setOnToolTipViewClickedListener(onToolTipClicked);

        } else if (currentTipView == pad_tip) {
            pad_tip.remove();
            pad_tip = null;

            currentTipView = null;
            setIsLastTipToShow(true);
            mToolTipFrameLayout.setOnClickListener(null);
        }

    }

    @Override
    protected void setIsLastTipToShow(boolean isLastTipToShow) {
        this.isLastTipToShow = isLastTipToShow;
    }


    @Override
    public void onBluetoothConnected() {
        ((ImageView) getActivity().findViewById(R.id.bot_icon)).setImageResource(R.drawable.ic_bot_pollywog_connected);
        ((ImageView) getActivity().findViewById(R.id.robot_bg)).setImageResource(R.drawable.pollywog_bg_on);
    }

    @Override
    public void onBluetoothDisconnected() {
        ((ImageView) getActivity().findViewById(R.id.bot_icon)).setImageResource(R.drawable.ic_bot_pollywog_disconnected);
        ((ImageView) getActivity().findViewById(R.id.robot_bg)).setImageResource(R.drawable.pollywog_bg_off);
    }

}
