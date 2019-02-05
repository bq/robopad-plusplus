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

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
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
import android.widget.Toast;

import com.bq.robotic.droid2ino.activities.BaseBluetoothSendOnlyActivity;
import com.bq.robotic.droid2ino.utils.Droid2InoConstants.ConnectionState;
import com.bq.robotic.droid2ino.views.DevicesListDialogStyle;
import com.bq.robotic.robopad_plusplus.fragments.BeetleFragment;
import com.bq.robotic.robopad_plusplus.fragments.CrabFragment;
import com.bq.robotic.robopad_plusplus.fragments.EvolutionFragment;
import com.bq.robotic.robopad_plusplus.fragments.GenericRobotFragment;
import com.bq.robotic.robopad_plusplus.fragments.PollywogFragment;
import com.bq.robotic.robopad_plusplus.fragments.RhinoFragment;
import com.bq.robotic.robopad_plusplus.fragments.RobotFragment;
import com.bq.robotic.robopad_plusplus.fragments.ScheduleRobotMovementsFragment;
import com.bq.robotic.robopad_plusplus.listeners.RobotListener;
import com.bq.robotic.robopad_plusplus.listeners.ScheduleRobotMovementsListener;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants.robotType;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Main activity of the app that contains the different fragments to show to the user
 */

public class RoboPad_plusplus extends BaseBluetoothSendOnlyActivity implements RobotListener,
   ScheduleRobotMovementsListener, EasyPermissions.PermissionCallbacks {

   // Debugging
   private static final String LOG_TAG = "RoboPad_plusplus";

   private static final String SAVE_FRAGMENT_STATE_KEY = "current_fragment_key";
   private FragmentManager mFragmentManager;

   private ImageButton connectButton;
   private ImageButton disconnectButton;

   private Animation anim;

   // Permissions
   // Location permission is now needed in order to scan for near bluetooth devices
   private static final int RC_LOCATION_PERM = 124;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // Set the orientation to landscape if the device doesn't supports user_landscape
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)
         setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

      setContentView(R.layout.activity_robo_pad_plusplus);

      robotType currentRobotType = (robotType) getIntent().getSerializableExtra(RoboPadConstants.ROBOT_SELECTED_KEY);

      mFragmentManager = getSupportFragmentManager();

      connectButton = findViewById(R.id.connect_button);
      disconnectButton = findViewById(R.id.disconnect_button);
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

         case EVOLUTION:
            ft.replace(R.id.game_pad_container, new EvolutionFragment());
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
      // Store values between instances here
      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
      SharedPreferences.Editor editor = preferences.edit();

      editor.putBoolean(RoboPadConstants.WAS_ENABLING_BLUETOOTH_ALLOWED_KEY, isEnableBluetoothAllowed()); // value to store
      // Commit to storage
      editor.commit();

      // Send here the stop command before the connection is lost
      onSendMessage(RoboPadConstants.STOP_COMMAND);

      super.onPause();
   }

   @Override
   protected void onStart() {
      super.onStart();
      // Store values between instances here
      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
      onEnableBluetoothAllowed(preferences.getBoolean(RoboPadConstants.WAS_ENABLING_BLUETOOTH_ALLOWED_KEY, false));
   }

   @Override
   protected void onConnectionStatusUpdated(ConnectionState connectionState) {
      Log.d(LOG_TAG, "onConnectionStatusUpdated = " + connectionState);
      switch (connectionState) {
         case CONNECTED_CONFIGURED:
            Fragment currentConnectedFragment = mFragmentManager.findFragmentById(R.id.game_pad_container);
            if (currentConnectedFragment != null) {
               if (currentConnectedFragment instanceof RobotFragment) {
                  ((RobotFragment) mFragmentManager.findFragmentById(R.id.game_pad_container)).onBluetoothConnected();

               } else if (currentConnectedFragment instanceof ScheduleRobotMovementsFragment) {
                  ((ScheduleRobotMovementsFragment) mFragmentManager.findFragmentById(R.id.game_pad_container)).onBluetoothConnected();
               }
            }

            // If connected is because the Bluetooth enabling was allowed
            break;

         case CONNECTING:
            break;

         case LISTENING:
         case DISCONNECTED:
            Fragment currentFragment = mFragmentManager.findFragmentById(R.id.game_pad_container);
            if (currentFragment != null) {
               if (currentFragment instanceof RobotFragment) {
                  ((RobotFragment) mFragmentManager.findFragmentById(R.id.game_pad_container)).onBluetoothDisconnected();

               } else if (currentFragment instanceof ScheduleRobotMovementsFragment) {
                  ((ScheduleRobotMovementsFragment) mFragmentManager.findFragmentById(R.id.game_pad_container)).onBluetoothDisconnected();
               }
            }
            break;
      }

      changeViewsVisibility(connectionState);
   }

   @Override
   protected void onError(String errorMessage, ConnectionState errorState, Exception e) {
      if (e != null) Log.e(LOG_TAG, "Trace: " + e);
      if (errorMessage != null) {
         Log.e(LOG_TAG, errorMessage);
         Toast.makeText(RoboPad_plusplus.this, errorMessage, Toast.LENGTH_SHORT).show();
      }

      if (errorState != null)
         changeViewsVisibility(errorState);
   }

   /**
    * Change the visibility of some views as the connect/disconnect button depending on the
    * bluetooth connection state The state of the bluetooth connection
    *
    * @param connectionState the state of the current bluetooth connection
    */
   private void changeViewsVisibility(ConnectionState connectionState) {

      switch (connectionState) {
         case CONNECTED_CONFIGURED:
         case CONNECTED_NOT_CONFIGURED:
         case ERROR_CONFIGURING:
            findViewById(R.id.bluetooth_spinner_view).setVisibility(View.INVISIBLE);
            findViewById(R.id.bluetooth_spinner_view).clearAnimation();

            connectButton.setVisibility(View.GONE);
            disconnectButton.setVisibility(View.VISIBLE);
            break;

         case CONNECTING:
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

         case LISTENING:
         case DISCONNECTED:
         case ERROR_CONNECTING:
            findViewById(R.id.bluetooth_spinner_view).setVisibility(View.INVISIBLE);
            findViewById(R.id.bluetooth_spinner_view).clearAnimation();

            connectButton.setVisibility(View.VISIBLE);
            disconnectButton.setVisibility(View.GONE);

            break;
      }
   }

   @Override
   protected void onDeviceListDialogStyleObtained(DevicesListDialogStyle deviceListDialogStyle) {
      deviceListDialogStyle.getBtSocketSelectorTab().setText(R.string.bt_socket_selector_view_text);
      if (deviceListDialogStyle.getBleSelectorTab() != null)
         deviceListDialogStyle.getBleSelectorTab().setText(R.string.ble_selector_view_text);
   }

   /**
    * Callback for the connect and disconnect buttons
    *
    * @param v connect and disconnect buttons
    */
   public void onChangeConnection(View v) {
      switch (v.getId()) {
         case R.id.connect_button:
            tryConnectToDevice();
            break;

         case R.id.disconnect_button:
            stopBluetoothConnection();
            break;
      }
   }

   /**
    * Connect to the device
    */
   @AfterPermissionGranted(RC_LOCATION_PERM)
   private void tryConnectToDevice() {
      String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION};
      if (EasyPermissions.hasPermissions(this, perms)) {
         requestDeviceConnection();
      } else {
         // Do not have permissions, request them now
         EasyPermissions.requestPermissions(this, getString(R.string.permission_location),
            RC_LOCATION_PERM, perms);
      }
   }

   /**
    * Needed to override the callback when the user clicks the back button because if the activity
    * has an active Bluetooth connection with a robot, the user must confirm that want to loose that
    * connection
    */
   @Override
   public void onBackPressed() {
      if ((mFragmentManager.findFragmentById(R.id.game_pad_container) instanceof ScheduleRobotMovementsFragment)
         || !isConnected()) {
         super.onBackPressed();

      } else {
         // Show a dialog to confirm that the user wants to choose a new robot type
         // and to inform that the connection with the current robot will be lost
         new AlertDialog.Builder(this)
            .setMessage(getResources().getString(R.string.exit_robot_control_dialog))
            .setTitle(R.string.exit_robot_control_dialog_title)
            .setCancelable(true)
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
               stopBluetoothConnection();

               RoboPad_plusplus.super.onBackPressed();
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
   public boolean onCheckIsConnectedWithToast() {
      return isConnectedWithToast();
   }


   /**
    * Callback from the RobotFragment for checking if the device is connected to an Arduino
    * through the bluetooth connection
    * without the warning toast if is not connected.
    *
    * @return true if is connected or false if not
    */
   public boolean onCheckIsConnected() {
      return isConnected();
   }


   /**
    * Callback from the RobotFragment for sending a message to the Arduino through the bluetooth
    * connection.
    *
    * @param message to be send to the Arduino
    */
   @Override
   public void onSendMessage(String message) {
//		Log.d(LOG_TAG, "message to send to arduino: " + message);
      sendMessage(message);
   }


   /**
    * The user pressed the schedule robot button, so show the schedule fragment
    *
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

   /**************************************************************************************
    ********************************   PERMISSIONS   *************************************
    **************************************************************************************/
   @Override
   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);

      // Forward results to EasyPermissions
      EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
   }

   @Override
   public void onPermissionsGranted(int requestCode, List<String> perms) {
      requestDeviceConnection();
   }

   @Override
   public void onPermissionsDenied(int requestCode, List<String> perms) {
      Log.d(LOG_TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

      // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
      // This will display a dialog warning the user that the app will be able to connect only
      // to paired devices, but show it only when the permission is not permanently denied, as the
      // app is still functional without it
      if (!EasyPermissions.permissionPermanentlyDenied(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
         new AlertDialog.Builder(this)
            .setMessage(getString(R.string.rationale_location))
            .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> requestDeviceConnection())
            .create()
            .show();
      } else if (EasyPermissions.permissionPermanentlyDenied(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
         requestDeviceConnection();
      }
   }

}
