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

package com.bq.robotic.robopad_plusplus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageButton;

import com.bq.robotic.droid2ino.activities.BaseBluetoothSendOnlyActivity;
import com.bq.robotic.droid2ino.utils.Droid2InoConstants;
import com.bq.robotic.robopad_plusplus.fragments.BeetleFragment;
import com.bq.robotic.robopad_plusplus.fragments.CrabFragment;
import com.bq.robotic.robopad_plusplus.fragments.GenericRobotFragment;
import com.bq.robotic.robopad_plusplus.fragments.PollywogFragment;
import com.bq.robotic.robopad_plusplus.fragments.RhinoFragment;
import com.bq.robotic.robopad_plusplus.fragments.RobotFragment;
import com.bq.robotic.robopad_plusplus.fragments.ScheduleRobotMovementsFragment;
import com.bq.robotic.robopad_plusplus.listeners.RobotListener;
import com.bq.robotic.robopad_plusplus.listeners.ScheduleRobotMovementsListener;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants.robotType;


/**
 * Main activity of the app that contains the different fragments to show to the user
 */

public class RoboPad_plusplus extends BaseBluetoothSendOnlyActivity implements RobotListener, ScheduleRobotMovementsListener {

    // Debugging
    private static final String LOG_TAG = "RoboPad_plusplus";

    private FragmentManager mFragmentManager;

    private ImageButton connectButton;
    private ImageButton disconnectButton;

    private Animation anim;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robo_pad_plusplus);

        robotType robotTypeSelected = (robotType) getIntent().getSerializableExtra(RoboPadConstants.ROBOT_SELECTED_KEY);

        mFragmentManager = getSupportFragmentManager();

        connectButton = (ImageButton) findViewById(R.id.connect_button);
        disconnectButton = (ImageButton) findViewById(R.id.disconnect_button);
        anim = AnimationUtils.loadAnimation(this, R.anim.bluetooth_spiner);

        // Show the selected robot fragment
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        switch (robotTypeSelected) {

            case POLLYWOG:
                ft.replace(R.id.game_pad_container, new PollywogFragment());
                break;

            case BEETLE:
                ft.replace(R.id.game_pad_container, new BeetleFragment());
                break;

            case RHINO:
                ft.replace(R.id.game_pad_container, new RhinoFragment());
                break;

            case CRAB:
                ft.replace(R.id.game_pad_container, new CrabFragment());
                break;

            case GENERIC_ROBOT:
                ft.replace(R.id.game_pad_container, new GenericRobotFragment());
                break;

        }

        ft.commit();

    }


    @Override
    protected void onPause() {
        super.onPause();
        // Store values between instances here
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(RoboPadConstants.WAS_ENABLING_BLUETOOTH_ALLOWED, wasEnableBluetoothAllowed); // value to store
        // Commit to storage
        editor.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Store values between instances here
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        wasEnableBluetoothAllowed = preferences.getBoolean(RoboPadConstants.WAS_ENABLING_BLUETOOTH_ALLOWED, false);
    }


    /**
     * Callback for the changes of the bluetooth connection status
     *
     * @param connectionState The state of the bluetooth connection
     */
    @Override
    public void onConnectionStatusUpdate(int connectionState) {

        switch (connectionState) {

            case Droid2InoConstants.STATE_CONNECTED:
                ((RobotFragment) mFragmentManager.findFragmentById(R.id.game_pad_container)).onBluetoothConnected();

                // If connected is because the Bluetooth enabling was allowed
                break;

            case Droid2InoConstants.STATE_CONNECTING:
                break;

            case Droid2InoConstants.STATE_LISTEN:
            case Droid2InoConstants.STATE_NONE:
                if (mFragmentManager.findFragmentById(R.id.game_pad_container) != null) {
                    ((RobotFragment) mFragmentManager.findFragmentById(R.id.game_pad_container)).onBluetoothDisconnected();
                }
                break;
        }

        changeViewsVisibility(connectionState);
    }


    /**
     * Change the visibility of some views as the connect/disconnect button depending on the
     * bluetooth connection state The state of the bluetooth connection
     *
     * @param connectionState the state of the current bluetooth connection
     */
    private void changeViewsVisibility(int connectionState) {

        switch (connectionState) {

            case Droid2InoConstants.STATE_CONNECTED:
                findViewById(R.id.bluetooth_spinner_view).setVisibility(View.INVISIBLE);
                findViewById(R.id.bluetooth_spinner_view).clearAnimation();

                connectButton.setVisibility(View.GONE);
                disconnectButton.setVisibility(View.VISIBLE);
                break;

            case Droid2InoConstants.STATE_CONNECTING:

                if (anim != null) {
                    anim.setInterpolator(new Interpolator() {
                        private final int frameCount = 8;

                        @Override
                        public float getInterpolation(float input) {
                            return (float) Math.floor(input * frameCount) / frameCount;
                        }
                    });

                    findViewById(R.id.bluetooth_spinner_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.bluetooth_spinner_view).startAnimation(anim);
                } else {
                    Log.e(LOG_TAG, "Anim null!!!");
                }

                break;

            case Droid2InoConstants.STATE_LISTEN:
            case Droid2InoConstants.STATE_NONE:
                findViewById(R.id.bluetooth_spinner_view).setVisibility(View.INVISIBLE);
                findViewById(R.id.bluetooth_spinner_view).clearAnimation();

                connectButton.setVisibility(View.VISIBLE);
                disconnectButton.setVisibility(View.GONE);

                break;
        }
    }


    /**
     * Callback for the connect and disconnect buttons
     *
     * @param v
     */
    public void onChangeConnection(View v) {

        switch (v.getId()) {

            case R.id.connect_button:
                requestDeviceConnection();
                break;

            case R.id.disconnect_button:
                stopBluetoothConnection();
                break;
        }
    }


    /**
     * Needed to override the callback when the user clicks the back button because if the activity
     * has an active Bluetooth connection with a robot, the user must confirm that want to loose that
     * connection
     */
    @Override
    public void onBackPressed() {

        if(!isConnectedWithoutToast()) {
            super.onBackPressed();

        } else {

            // Show a dialog to confirm that the user wants to choose a new robot type
            // and to inform that the connection with the current robot will be lost
            new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.exit_robot_control_dialog))
                    .setTitle(R.string.exit_robot_control_dialog_title)
                    .setCancelable(true)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stopBluetoothConnection();

                            RoboPad_plusplus.super.onBackPressed();
                        }
                    })

                    .create()
                    .show();

        }

    }



    /**************************************************************************************
     **************************   ROBOTLISTENER CALLBACKS   *******************************
     **************************************************************************************/

    /**
     * Callback from the RobotFragment for checking if the device is connected to an Arduino
     * through the bluetooth connection.
     * If the device is not connected it warns the user of it through a Toast.
     *
     * @return true if is connected or false if not
     */
    @Override
    public boolean onCheckIsConnected() {
        return isConnected();
    }


    /**
     * Callback from the RobotFragment for checking if the device is connected to an Arduino
     * through the bluetooth connection
     * without the warning toast if is not connected.
     *
     * @return true if is connected or false if not
     */
    public boolean onCheckIsConnectedWithoutToast() {
        return isConnectedWithoutToast();
    }


    /**
     * Callback from the RobotFragment for sending a message to the Arduino through the bluetooth
     * connection.
     *
     * @param message to be send to the Arduino
     */
    @Override
    public void onSendMessage(String message) {
//		Log.e(LOG_TAG, "message to send to arduino: " + message);
        sendMessage(message);
    }


    @Override
    public void onScheduleButtonClicked(robotType botType) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ScheduleRobotMovementsFragment scheduleRobotMovementsFragment = null;
        scheduleRobotMovementsFragment = new ScheduleRobotMovementsFragment();
        Bundle bundle = new Bundle();


        if (botType == robotType.POLLYWOG) {
            bundle.putInt(RoboPadConstants.ROBOT_TYPE_KEY, robotType.POLLYWOG.ordinal());

        } else if (botType == robotType.BEETLE) {
            bundle.putInt(RoboPadConstants.ROBOT_TYPE_KEY, robotType.BEETLE.ordinal());

        } else if (botType == robotType.RHINO) {
            bundle.putInt(RoboPadConstants.ROBOT_TYPE_KEY, robotType.RHINO.ordinal());

        } else if (botType == robotType.CRAB) {
            bundle.putInt(RoboPadConstants.ROBOT_TYPE_KEY, robotType.CRAB.ordinal());

        } else if (botType == robotType.GENERIC_ROBOT) {
            bundle.putInt(RoboPadConstants.ROBOT_TYPE_KEY, robotType.GENERIC_ROBOT.ordinal());
        }

        if (scheduleRobotMovementsFragment != null) {

            ft.addToBackStack("current_robot");
            scheduleRobotMovementsFragment.setArguments(bundle);
            ft.replace(R.id.game_pad_container, scheduleRobotMovementsFragment);
            ft.commit();

            findViewById(R.id.bq_logo).setVisibility(View.GONE);
        }
    }


}
