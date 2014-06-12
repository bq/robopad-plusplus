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
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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

    private static final String SAVE_FRAGMENT_STATE_KEY = "current_fragment_key";
    private FragmentManager mFragmentManager;

    private ImageButton connectButton;
    private ImageButton disconnectButton;

    private Animation anim;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robo_pad_plusplus);

        robotType currentRobotType = (robotType) getIntent().getSerializableExtra(RoboPadConstants.ROBOT_SELECTED_KEY);

        mFragmentManager = getSupportFragmentManager();

        connectButton = (ImageButton) findViewById(R.id.connect_button);
        disconnectButton = (ImageButton) findViewById(R.id.disconnect_button);
        anim = AnimationUtils.loadAnimation(this, R.anim.bluetooth_spiner);

        // If we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }

        // Show the selected robot fragment
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        switch (currentRobotType) {

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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(RoboPadConstants.WAS_ENABLING_BLUETOOTH_ALLOWED_KEY, wasEnableBluetoothAllowed); // value to store
        // Commit to storage
        editor.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Store values between instances here
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        wasEnableBluetoothAllowed = preferences.getBoolean(RoboPadConstants.WAS_ENABLING_BLUETOOTH_ALLOWED_KEY, false);
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
                Fragment currentConnectedFragment = mFragmentManager.findFragmentById(R.id.game_pad_container);
                if (currentConnectedFragment != null) {
                    if(currentConnectedFragment instanceof RobotFragment) {
                        ((RobotFragment) mFragmentManager.findFragmentById(R.id.game_pad_container)).onBluetoothConnected();

                    } else if (currentConnectedFragment instanceof ScheduleRobotMovementsFragment) {
                        ((ScheduleRobotMovementsFragment) mFragmentManager.findFragmentById(R.id.game_pad_container)).onBluetoothConnected();
                    }
                }

                // If connected is because the Bluetooth enabling was allowed
                break;

            case Droid2InoConstants.STATE_CONNECTING:
                break;

            case Droid2InoConstants.STATE_LISTEN:
            case Droid2InoConstants.STATE_NONE:
                Fragment currentFragment = mFragmentManager.findFragmentById(R.id.game_pad_container);
                if (currentFragment != null) {
                    if(currentFragment instanceof RobotFragment) {
                        ((RobotFragment) mFragmentManager.findFragmentById(R.id.game_pad_container)).onBluetoothDisconnected();

                    } else if (currentFragment instanceof ScheduleRobotMovementsFragment) {
                        ((ScheduleRobotMovementsFragment) mFragmentManager.findFragmentById(R.id.game_pad_container)).onBluetoothDisconnected();
                    }
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
     * @param v connect and disconnect buttons
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

        if((mFragmentManager.findFragmentById(R.id.game_pad_container) instanceof ScheduleRobotMovementsFragment) || !isConnectedWithoutToast()) {
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


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        mFragmentManager.putFragment(outState, SAVE_FRAGMENT_STATE_KEY, mFragmentManager.findFragmentById(R.id.game_pad_container));

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


    /**
     * The user pressed the schedule robot button, so show the schedule fragment
     * @param botType robot type
     */
    @Override
    public void onScheduleButtonClicked(robotType botType) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ScheduleRobotMovementsFragment scheduleRobotMovementsFragment = null;
        scheduleRobotMovementsFragment = new ScheduleRobotMovementsFragment();
        Bundle bundle = new Bundle();


        switch (botType) {

            case POLLYWOG:
                bundle.putInt(RoboPadConstants.ROBOT_TYPE_KEY, robotType.POLLYWOG.ordinal());
                break;

            case BEETLE:
                bundle.putInt(RoboPadConstants.ROBOT_TYPE_KEY, robotType.BEETLE.ordinal());
                break;

            case RHINO:
                bundle.putInt(RoboPadConstants.ROBOT_TYPE_KEY, robotType.RHINO.ordinal());
                break;

            case CRAB:
                bundle.putInt(RoboPadConstants.ROBOT_TYPE_KEY, robotType.CRAB.ordinal());
                break;

            case GENERIC_ROBOT:
                bundle.putInt(RoboPadConstants.ROBOT_TYPE_KEY, robotType.GENERIC_ROBOT.ordinal());
                break;

        }

        if (scheduleRobotMovementsFragment != null) {

            ft.addToBackStack(RoboPadConstants.CURRENT_ROBOT_BACK_STACK_KEY);
            scheduleRobotMovementsFragment.setArguments(bundle);
            ft.replace(R.id.game_pad_container, scheduleRobotMovementsFragment);
            ft.commit();

        }
    }


}
