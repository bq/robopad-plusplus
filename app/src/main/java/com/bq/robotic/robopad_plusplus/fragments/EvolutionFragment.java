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

package com.bq.robotic.robopad_plusplus.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;

import com.bq.robotic.robopad_plusplus.R;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants.robotState;
import com.bq.robotic.robopad_plusplus.utils.RobotConnectionsPopupWindow;
import com.bq.robotic.robopad_plusplus.utils.TipsFactory;
import com.nhaarman.supertooltips.ToolTipView;


/**
 * Fragment of the game pad controller for the Evolution robot.
 *
 * @author Estefanía Sarasola Elvira
 */

public class EvolutionFragment extends RobotFragment {

   // Debugging
   private static final String LOG_TAG = "EvolutionFragment";

   private ImageButton pinExplanationButton;
   private ImageButton lineFollowerButton;
   private ImageButton lightAvoiderButton;
   private ImageButton obstaclesAvoiderButton;

   // Tips
   private tips currentTip;

   private enum tips {PIN, BLUETOOTH, SCHEDULE, PAD, LINE_FOLLOWER, LIGHT_AVOIDER, OBSTACLES_AVOIDER}

   private boolean isBleSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

   @Override
   public View onCreateView(LayoutInflater inflater,
                            ViewGroup container,
                            Bundle savedInstanceState) {

      View layout = inflater.inflate(R.layout.fragment_evolution, container, false);
      ((ImageView) layout.findViewById(R.id.robot_bg)).setImageResource(R.drawable.ic_evolution_bg_off);
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

      ImageButton stopButton = containerLayout.findViewById(R.id.stop_button);
      stopButton.setOnClickListener(onButtonClick);

      ImageButton scheduleButton = containerLayout.findViewById(R.id.schedule_button);
      scheduleButton.setOnClickListener(onButtonClick);

      ImageButton upButton = containerLayout.findViewById(R.id.up_button);
      upButton.setOnTouchListener(buttonOnTouchListener);

      ImageButton downButton = containerLayout.findViewById(R.id.down_button);
      downButton.setOnTouchListener(buttonOnTouchListener);

      ImageButton leftButton = containerLayout.findViewById(R.id.left_button);
      leftButton.setOnTouchListener(buttonOnTouchListener);

      ImageButton rightButton = containerLayout.findViewById(R.id.right_button);
      rightButton.setOnTouchListener(buttonOnTouchListener);

      pinExplanationButton = containerLayout.findViewById(R.id.bot_icon);
      pinExplanationButton.setOnClickListener(onButtonClick);

      lineFollowerButton = containerLayout.findViewById(R.id.line_follower);
      lineFollowerButton.setOnClickListener(onButtonClick);

      lightAvoiderButton = containerLayout.findViewById(R.id.light_avoider);
      lightAvoiderButton.setOnClickListener(onButtonClick);

      obstaclesAvoiderButton = containerLayout.findViewById(R.id.obstacles_avoider);
      obstaclesAvoiderButton.setOnClickListener(onButtonClick);

   }


   /**
    * Send the message to the Arduino board depending on the button pressed
    *
    * @param viewId The id of the view pressed
    */
   @Override
   public void controlButtonActionDown(int viewId) {

      if (listener == null) {
         Log.e(LOG_TAG, "RobotListener is null");
         return;
      }

      switch (viewId) {

         case R.id.up_button:
            listener.onSendMessage(RoboPadConstants.UP_COMMAND);
            //	    			Log.d(LOG_TAG, "up command send");
            break;

         case R.id.down_button:
            listener.onSendMessage(RoboPadConstants.DOWN_COMMAND);
            //	    			Log.d(LOG_TAG, "down command send");
            break;

         case R.id.left_button:
            listener.onSendMessage(RoboPadConstants.LEFT_COMMAND);
            //	    			Log.d(LOG_TAG, "left command send");
            break;

         case R.id.right_button:
            listener.onSendMessage(RoboPadConstants.RIGHT_COMMAND);
            //	    			Log.d(LOG_TAG, "right command send");
            break;

      }
   }


   /**
    * Listeners for the views that manage only clicks
    */
   protected OnClickListener onButtonClick = v -> {

      if (listener == null) {
         Log.e(LOG_TAG, "RobotListener is null");
         return;
      }

      switch (v.getId()) {

         case R.id.stop_button:

            if (state != robotState.MANUAL_CONTROL) {
               stateChanged(robotState.MANUAL_CONTROL);
            }

            listener.onSendMessage(RoboPadConstants.STOP_COMMAND);
            break;

         case R.id.bot_icon:
            if (!isBleSupported) {
               showEvolutionConnectionsPopup(false);
               break;
            }
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogWithoutBordersRounded)
               .setMessage(getResources().getString(R.string.show_printbot_connections_ask_board))
               .setCancelable(true)
               .setPositiveButton(R.string.ble_selector_view_text, (dialog, which) ->
                  showEvolutionConnectionsPopup(true))
               .setNeutralButton(R.string.bt_socket_selector_view_text, (dialog, which) ->
                  showEvolutionConnectionsPopup(false)
               )
               .create();
            alertDialog.show();
            alertDialog.getWindow().setLayout(
               getActivity().getResources().getDimensionPixelSize(R.dimen.ask_board_for_printbot_pins_width),
               ViewGroup.LayoutParams.WRAP_CONTENT);
            try {
               TextView messageView = alertDialog.findViewById(android.R.id.message);
               messageView.setGravity(Gravity.CENTER_HORIZONTAL);
            } catch (Exception e) {
               Log.w(LOG_TAG, "Error trying to center the message in the dialog");
            }
            break;

         case R.id.schedule_button:

            if (state != robotState.MANUAL_CONTROL) {
               stateChanged(robotState.MANUAL_CONTROL);
            }

            listener.onScheduleButtonClicked(RoboPadConstants.robotType.EVOLUTION);
            break;

         case R.id.line_follower:
            if (!listener.onCheckIsConnectedWithToast()) {
               return;
            }

            if (state == robotState.MANUAL_CONTROL || state == robotState.LIGHT_AVOIDER
               || state == robotState.OBSTACLES_AVOIDER) {
               stateChanged(robotState.LINE_FOLLOWER);

            } else {
               stateChanged(robotState.MANUAL_CONTROL);
            }

            break;

         case R.id.light_avoider:
            if (!listener.onCheckIsConnectedWithToast()) {
               return;
            }

            if (state == robotState.MANUAL_CONTROL || state == robotState.LINE_FOLLOWER
               || state == robotState.OBSTACLES_AVOIDER) {
               stateChanged(robotState.LIGHT_AVOIDER);

            } else {
               stateChanged(robotState.MANUAL_CONTROL);
            }

            break;

         case R.id.obstacles_avoider:
            if (!listener.onCheckIsConnectedWithToast()) {
               return;
            }

            if (state == robotState.MANUAL_CONTROL || state == robotState.LINE_FOLLOWER
               || state == robotState.LIGHT_AVOIDER) {
               stateChanged(robotState.OBSTACLES_AVOIDER);

            } else {
               stateChanged(robotState.MANUAL_CONTROL);
            }

            break;

      }

   };

   private void showEvolutionConnectionsPopup(boolean showZumCore2BoardConnections) {
      PopupWindow popupWindow = (new RobotConnectionsPopupWindow(RoboPadConstants.robotType.EVOLUTION,
         showZumCore2BoardConnections, getActivity())).getPopupWindow();

      // Workaround for getting the correct X coordinate when the device has a notch
      int[] pinExplanationButtonLocation = new int[2];
      pinExplanationButton.getLocationOnScreen(pinExplanationButtonLocation);

      popupWindow.showAtLocation(getView(), Gravity.LEFT | Gravity.CENTER_VERTICAL,
         Math.round(pinExplanationButtonLocation[0] + pinExplanationButton.getWidth()
            - getActivity().getResources().getDimensionPixelSize(R.dimen.button_press_padding)),
         0);
   }


   /**
    * The state of the robot changes. The state is the type of control the user has of the robot
    * such as manual control, or if the robot is in line follower mode
    *
    * @param nextState next state the robot is going to have
    */
   protected void stateChanged(robotState nextState) {

      switch (nextState) {

         case MANUAL_CONTROL:
            lineFollowerButton.setSelected(false);
            lightAvoiderButton.setSelected(false);
            obstaclesAvoiderButton.setSelected(false);
            state = robotState.MANUAL_CONTROL;
            listener.onSendMessage(RoboPadConstants.MANUAL_CONTROL_MODE_COMMAND);
            break;

         case LINE_FOLLOWER:
            lineFollowerButton.setSelected(true);
            lightAvoiderButton.setSelected(false);
            obstaclesAvoiderButton.setSelected(false);
            state = robotState.LINE_FOLLOWER;
            listener.onSendMessage(RoboPadConstants.LINE_FOLLOWER_MODE_COMMAND);
            break;

         case LIGHT_AVOIDER:
            lineFollowerButton.setSelected(false);
            lightAvoiderButton.setSelected(true);
            obstaclesAvoiderButton.setSelected(false);
            state = robotState.LIGHT_AVOIDER;
            listener.onSendMessage(RoboPadConstants.LIGHT_AVOIDER_MODE_COMMAND);
            break;

         case OBSTACLES_AVOIDER:
            lineFollowerButton.setSelected(false);
            lightAvoiderButton.setSelected(false);
            obstaclesAvoiderButton.setSelected(true);
            state = robotState.OBSTACLES_AVOIDER;
            listener.onSendMessage(RoboPadConstants.OBSTACLES_FOLLOWER_MODE_COMMAND);
            break;

      }

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

         mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.line_follower_text),
            getActivity().findViewById(R.id.line_follower)).setOnToolTipViewClickedListener(onToolTipClicked);

         currentTip = tips.LINE_FOLLOWER;

      } else if (currentTip.equals(tips.LINE_FOLLOWER)) {
         mToolTipFrameLayout.removeAllViews();

         mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.light_avoider_text),
            getActivity().findViewById(R.id.light_avoider)).setOnToolTipViewClickedListener(onToolTipClicked);

         currentTip = tips.LIGHT_AVOIDER;

      } else if (currentTip.equals(tips.LIGHT_AVOIDER)) {
         mToolTipFrameLayout.removeAllViews();

         mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.obstacles_avoider_text),
            getActivity().findViewById(R.id.obstacles_avoider)).setOnToolTipViewClickedListener(onToolTipClicked);

         currentTip = tips.OBSTACLES_AVOIDER;

      } else if (currentTip.equals(tips.OBSTACLES_AVOIDER)) {
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
      ((ImageView) getActivity().findViewById(R.id.bot_icon)).setImageResource(R.drawable.bot_evolution_connected);
      ((ImageView) getActivity().findViewById(R.id.robot_bg)).setImageResource(R.drawable.ic_evolution_bg_on);

      stateChanged(robotState.MANUAL_CONTROL);
   }

   @Override
   public void onBluetoothDisconnected() {
      ((ImageView) getActivity().findViewById(R.id.bot_icon)).setImageResource(R.drawable.bot_evolution_disconnected);
      ((ImageView) getActivity().findViewById(R.id.robot_bg)).setImageResource(R.drawable.ic_evolution_bg_off);
   }
}
