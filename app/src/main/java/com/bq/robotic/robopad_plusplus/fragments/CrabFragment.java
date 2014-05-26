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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bq.robotic.robopad_plusplus.R;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants.robotType;
import com.bq.robotic.robopad_plusplus.utils.TipsFactory;
import com.nhaarman.supertooltips.ToolTipView;


/**
 * Fragment of the game pad controller for the Crab robot.
 * 
 * @author Estefanía Sarasola Elvira
 *
 */

public class CrabFragment extends RobotFragment {

	// Debugging
	private static final String LOG_TAG = "CrabFragment";

    // Tips
    private ToolTipView pin_explanation_tip;
    private ToolTipView bluetooth_tip;
    private ToolTipView pad_tip;
    private ToolTipView currentTipView;


	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.fragment_crab, container, false);

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

		Button scheduleButton = (Button) containerLayout.findViewById(R.id.schedule_button);
		scheduleButton.setOnClickListener(onButtonClick);

        Button resetButton = (Button) containerLayout.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(onButtonClick);

        SeekBar leftAmplitudeView = (SeekBar) containerLayout.findViewById(R.id.left_amplitude);
        leftAmplitudeView.setOnSeekBarChangeListener(onSeekBarChangedListener);

        SeekBar rightAmplitudeView = (SeekBar) containerLayout.findViewById(R.id.right_amplitude);
        rightAmplitudeView.setOnSeekBarChangeListener(onSeekBarChangedListener);

        SeekBar periodView = (SeekBar) containerLayout.findViewById(R.id.period_bar);
        periodView.setOnSeekBarChangeListener(onSeekBarChangedListener);

        SeekBar phaseView = (SeekBar) containerLayout.findViewById(R.id.phase);
        phaseView.setOnSeekBarChangeListener(onSeekBarChangedListener);

	}


	/**
	 * Send the message to the Arduino board depending on the button pressed
	 *
	 * @param viewId The id of the view pressed
	 */
	@Override
	public void controlButtonActionDown(int viewId) {
        // Nothing to do here
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
                    listener.onSendMessage(RoboPadConstants.RESET_COMMAND);
					listener.onScheduleButtonClicked(robotType.CRAB);
					break;

                case R.id.reset_button:
                    listener.onSendMessage(RoboPadConstants.RESET_COMMAND);

                    ((TextView) getActivity().findViewById(R.id.left_amplitude_value)).setText
                            (String.valueOf(RoboPadConstants.DEFAULT_AMPLITUDE));
                    ((TextView) getActivity().findViewById(R.id.right_amplitude_value)).setText
                            (String.valueOf(RoboPadConstants.DEFAULT_AMPLITUDE));
                    ((TextView) getActivity().findViewById(R.id.period_value)).setText
                            (String.valueOf(RoboPadConstants.DEFAULT_PERIOD));
                    ((TextView) getActivity().findViewById(R.id.phase_value)).setText
                            (String.valueOf(RoboPadConstants.DEFAULT_PHASE));

                    ((SeekBar) getActivity().findViewById(R.id.left_amplitude)).setProgress
                            (RoboPadConstants.DEFAULT_AMPLITUDE);
                    ((SeekBar) getActivity().findViewById(R.id.right_amplitude)).setProgress
                            (RoboPadConstants.DEFAULT_AMPLITUDE);
                    ((SeekBar) getActivity().findViewById(R.id.period_bar)).setProgress
                            (RoboPadConstants.DEFAULT_PERIOD);
                    ((SeekBar) getActivity().findViewById(R.id.phase)).setProgress
                            (RoboPadConstants.DEFAULT_PHASE);

                    break;
			}

		}
	};


    /**
     * Listener for the seek bar that updates the TextView values and send the message to the arduino
     * with the progress value when the user stops moving the slider
     */
    protected SeekBar.OnSeekBarChangeListener onSeekBarChangedListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

                switch (seekBar.getId()) {

                    case R.id.left_amplitude:
                        ((TextView) getActivity().findViewById(R.id.left_amplitude_value)).setText
                                (String.valueOf(progress));
                        break;

                    case R.id.right_amplitude:
                        ((TextView) getActivity().findViewById(R.id.right_amplitude_value)).setText
                                (String.valueOf(progress));
                        break;

                    // Plus 1000 because the range of the period is from 0 to 8000 and the seek bar
                    // goes from 0 to 7000
                    case R.id.period_bar:
                        ((TextView) getActivity().findViewById(R.id.period_value)).setText
                                (String.valueOf(progress + 1000));
                        break;

                    // Minus 90 because the range of the period is from -90 to 90 and the seek bar
                    // goes from 0 to 180
                    case R.id.phase:
                        ((TextView) getActivity().findViewById(R.id.phase_value)).setText
                                (String.valueOf(progress - 90));
                        break;
                }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // nothing to be done
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            switch (seekBar.getId()) {

                case R.id.left_amplitude:
                    listener.onSendMessage(RoboPadConstants.LEFT_AMPLITUDE_COMMAND
                            + seekBar.getProgress());
                    break;

                case R.id.right_amplitude:
                    listener.onSendMessage(RoboPadConstants.RIGHT_AMPLITUDE_COMMAND
                            + seekBar.getProgress());
                    break;

                case R.id.period_bar:
                    listener.onSendMessage(RoboPadConstants.PERIOD_COMMAND
                            + (seekBar.getProgress() + 1000));
                    break;

                case R.id.phase:
                    listener.onSendMessage(RoboPadConstants.PHASE_COMMAND
                            + (seekBar.getProgress() - 90));
                    break;
            }
        }

    };






    // FIXME: Wrong, copied from the pollywog
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

            currentTipView = null;
            setIsLastTipToShow(true);
            mToolTipFrameLayout.setOnClickListener(null);

        }

    }

    @Override
    protected void setIsLastTipToShow(boolean isLastTipToShow) {
        this.isLastTipToShow = isLastTipToShow;
    }


}
