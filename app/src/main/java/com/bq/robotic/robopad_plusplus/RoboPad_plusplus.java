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
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bq.robotic.droid2ino.activities.BaseBluetoothSendOnlyActivity;
import com.bq.robotic.droid2ino.utils.AndroidinoConstants;
import com.bq.robotic.droid2ino.utils.DeviceListDialogStyle;
import com.bq.robotic.robopad_plusplus.RoboPadConstants.robotType;
import com.bq.robotic.robopad_plusplus.fragments.BeetleFragment;
import com.bq.robotic.robopad_plusplus.fragments.CrabFragment;
import com.bq.robotic.robopad_plusplus.fragments.GenericRobotFragment;
import com.bq.robotic.robopad_plusplus.fragments.PollywogFragment;
import com.bq.robotic.robopad_plusplus.fragments.RhinoFragment;
import com.bq.robotic.robopad_plusplus.fragments.RobotFragment;
import com.bq.robotic.robopad_plusplus.fragments.ScheduleRobotMovementsFragment;
import com.bq.robotic.robopad_plusplus.fragments.SelectBotFragment;
import com.bq.robotic.robopad_plusplus.listeners.RobotListener;
import com.bq.robotic.robopad_plusplus.listeners.ScheduleRobotMovementsListener;
import com.bq.robotic.robopad_plusplus.listeners.SelectBotListener;


/**
 * Main activity of the app that contains the different fragments to show to the user 
 */

public class RoboPad_plusplus extends BaseBluetoothSendOnlyActivity implements RobotListener, SelectBotListener, ScheduleRobotMovementsListener {
	
	// Debugging
    private static final String LOG_TAG = "RoboPad_plusplus";
    
    private ActionBar mActionBar;   
    private ImageButton mSelectBotButton;
    private ImageButton mGamePadButton;
    private ImageView mScheduleIcon; 
    private TextView mBottomTitleBar;
    private FragmentManager mFragmentManager;
    
    private Button connectButton;
    private Button disconnectButton;
    

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_robo_pad_plusplus);
		
		mFragmentManager = getSupportFragmentManager();
		mActionBar = getSupportActionBar();
		// Hide the action bar
		mActionBar.hide();
		
		mSelectBotButton = (ImageButton) findViewById(R.id.select_bot_button);
		mGamePadButton = (ImageButton) findViewById(R.id.pad_button);
		mScheduleIcon = (ImageView) findViewById(R.id.schedule_button);
		mBottomTitleBar = (TextView) findViewById(R.id.title_view);
		connectButton = (Button) findViewById(R.id.connect_button);
		disconnectButton = (Button) findViewById(R.id.disconnect_button);
		
		// If we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
        	
        	if(mFragmentManager.findFragmentById(R.id.game_pad_container) instanceof RobotFragment) {
    			mSelectBotButton.setClickable(true);
    			
    			mGamePadButton.setVisibility(View.VISIBLE);
    			mGamePadButton.setSelected(true);
    			mGamePadButton.setClickable(false);
        	
        	} else if (mFragmentManager.findFragmentById(R.id.game_pad_container) instanceof ScheduleRobotMovementsFragment) {
        		mSelectBotButton.setClickable(true);
        		
    			mGamePadButton.setVisibility(View.VISIBLE);
    			mGamePadButton.setSelected(false);
    			mGamePadButton.setClickable(true);
    			
    			mScheduleIcon.setVisibility(View.VISIBLE);
    			mScheduleIcon.setSelected(true);
        	}
       
            return;
        }
		
        // Show the select robot fragment
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.game_pad_container, new SelectBotFragment());
		ft.commit();
		
        connectButton.setVisibility(View.GONE);
        disconnectButton.setVisibility(View.GONE);
			
	}
	
    
	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
		
		if(connectButton == null && disconnectButton == null) {
			return;
		}
		
		// Check the status for the connect / disconnect buttons
		if(isConnectedWithoutToast()) {
            connectButton.setVisibility(View.GONE);
            disconnectButton.setVisibility(View.VISIBLE);
		} else {
			connectButton.setVisibility(View.VISIBLE);
            disconnectButton.setVisibility(View.GONE);
		}
		
	}




	/**
	 * Put the text in the title bar in the bottom of the screen
	 * 
	 * @param textId the text to put in the bottom title bar
	 */
    public void setFragmentTitle(int textId) {
    	mBottomTitleBar.setText(textId);
    }
    
    
	/**
	 * Put the text in the title bar in the bottom of the screen
	 * 
	 * @param text the text to put in the bottom title bar
	 */
    public void setFragmentTitle(String text) {
    	mBottomTitleBar.setText(text);
    }
    
    
    /**
     * Callback for the changes of the bluetooth connection status
     * 
     * @param connectionState The state of the bluetooth connection
     */
    @Override
    public void onConnectionStatusUpdate(int connectionState) {
    	
      switch (connectionState) {
      
        case AndroidinoConstants.STATE_CONNECTED:
            setStatus(R.string.title_connected_to);
            if(mFragmentManager.findFragmentById(R.id.game_pad_container) instanceof RobotFragment) {
            	((RobotFragment) mFragmentManager.findFragmentById(R.id.game_pad_container)).onBluetoothConnected();
            } 
            
            connectButton.setVisibility(View.GONE);
            disconnectButton.setVisibility(View.VISIBLE);
            break;
            
        case AndroidinoConstants.STATE_CONNECTING:
            setStatus(R.string.title_connecting);
            break;
            
        case AndroidinoConstants.STATE_LISTEN:
        case AndroidinoConstants.STATE_NONE:
            setStatus(R.string.not_connected);
            if(mFragmentManager.findFragmentById(R.id.game_pad_container) instanceof RobotFragment) {
            	((RobotFragment) mFragmentManager.findFragmentById(R.id.game_pad_container)).onBluetoothDisconnected();
            
            } else if (mFragmentManager.findFragmentById(R.id.game_pad_container) instanceof ScheduleRobotMovementsFragment) {
            	((ScheduleRobotMovementsFragment) mFragmentManager.findFragmentById(R.id.game_pad_container)).onBluetoothDisconnected();
            }
            connectButton.setVisibility(View.VISIBLE);
            disconnectButton.setVisibility(View.GONE);
            break;
      }
    }

    
    /**
     * Put the status of the connection in the bottom title bar
     * 
     * @param textId The text id in the R.xml file 
     */
    private final void setStatus(int textId) {
    	mBottomTitleBar.setText(textId);
    }
    
    
    /**
     * Put the status of the connection in the bottom title bar
     * 
     * @param subTitle The text string
     */
    private final void setStatus(CharSequence subTitle) {
        if (subTitle != null) {
        	mBottomTitleBar.setText(subTitle);
        }
    }
    
    
    /**
     * Callback for the connect and disconnect buttons
     * @param v
     */
    public void onChangeConnection(View v) {
    	
    	switch (v.getId()) {
    	
			case R.id.connect_button:
				DeviceListDialogStyle deviceListDialogStyle = requestDeviceConnection();
				
				// Style the search bluetooth devices dialog			
				deviceListDialogStyle.getSearchDevicesTitleView().setTextColor(getResources().getColor(R.color.holo_green_dark));
				deviceListDialogStyle.getDevicesPairedTitleView().setBackgroundResource(R.color.holo_green_dark);
				deviceListDialogStyle.getNewDevicesTitleView().setBackgroundResource(R.color.holo_green_dark);	
				break;
	
			case R.id.disconnect_button:
				stopBluetoothConnection();
				break;
		}
    }
	
	
	/**
	 * Callback from the select_bot_button, in the custom navigation bar.
	 * As the button for showing the SelectBotListener is not clickable when the user is 
	 * in the SelectBotFragment we don't need to manage that case. If the user clicks in the 
	 * button it is because he/she is in a RobotFragment.
	 * 
	 * @param v The select_bot_button
	 */
	public void onSelectBotButtonClick(View v) {
		
		if(!isConnectedWithoutToast()) {
			showSelectBotFragment();
			return;
		}
		
		// Show a dialog to confirm that the user wants to choose a new robot type
		// and to inform that the connection with the current robot will be lost
        AlertDialog exitRobotControl = new AlertDialog.Builder(this)
        .setMessage(getResources().getString(R.string.exit_robot_control_dialog))
        .setTitle(R.string.exit_robot_control_dialog_title)
        .setCancelable(true)
        .setPositiveButton(android.R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				stopBluetoothConnection();
				
				showSelectBotFragment();
			}
		})
		.setNegativeButton(android.R.string.no, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(LOG_TAG, "Choose a new robot cancelled");
			}
		})
        .create();
        
        exitRobotControl.show();
	}
	
	
	/**
	 * Callback from the select_bot_button, in the custom navigation bar.
	 * As the button for showing the SelectBotListener is not clickable when the user is 
	 * in the SelectBotFragment we don't need to manage that case. If the user clicks in the 
	 * button it is because he/she is in a RobotFragment.
	 * 
	 * @param v The select_bot_button
	 */
	public void onGamePadButtonClick(View v) {
		
		//FIXME: When there is some schedule to save?
//		if(!isConnectedWithoutToast()) {
//			nkl l
//			return;
//		}
		
//		// Show a dialog to confirm that the user wants to choose a new robot type
//		// and to inform that the connection with the current robot will be lost
//        AlertDialog exitScheduleScreen = new AlertDialog.Builder(this)
//        .setMessage(getResources().getString(R.string.exit_robot_control_dialog))
//        .setTitle(R.string.exit_robot_control_dialog_title)
//        .setCancelable(true)
//        .setPositiveButton(android.R.string.ok, new OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				
//			}
//		})
//		.setNegativeButton(android.R.string.no, new OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				Log.i(LOG_TAG, "Choose a new robot cancelled");
//			}
//		})
//        .create();
//        
//        exitScheduleScreen.show();
		
		
        if(mFragmentManager.findFragmentById(R.id.game_pad_container) instanceof ScheduleRobotMovementsFragment) {
        	robotType botType = ((ScheduleRobotMovementsFragment) mFragmentManager.findFragmentById(R.id.game_pad_container)).getBotType();
        	
        	if(botType != null) {
        		onRobotSelected(botType);
        	}
        }
		
	}
	
	
	/**
	 * Replaces the current RobotFragment with the SelectBotFragment 
	 */
	private void showSelectBotFragment() {
		FragmentTransaction ft = mFragmentManager.beginTransaction();		
		SelectBotFragment selectBotFragment = new SelectBotFragment();
		ft.replace(R.id.game_pad_container, selectBotFragment);
		ft.commit();
		
		// the select_bot_button must be no clickable when the SelectBotFragment is showed
		// and the gamepad icon must be hidden too
		mSelectBotButton.setClickable(false);
		mGamePadButton.setVisibility(View.GONE);
		mGamePadButton.setSelected(false);
		
		mScheduleIcon.setVisibility(View.GONE);
		mScheduleIcon.setSelected(false);
		
        connectButton.setVisibility(View.GONE);
        disconnectButton.setVisibility(View.GONE);
		
		mBottomTitleBar.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0); 
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
		
		if(scheduleRobotMovementsFragment != null) {
			
			ImageButton padButton = (ImageButton) findViewById(R.id.pad_button);
			padButton.setSelected(false);
			
			ImageButton scheduleButton = (ImageButton) findViewById(R.id.pad_button);
			scheduleButton.setSelected(true);
			
			scheduleRobotMovementsFragment.setArguments(bundle);
			ft.replace(R.id.game_pad_container, scheduleRobotMovementsFragment);
			ft.commit();

			// the select_bot_button must be clickable when the RobotFragment is showed
			// and the gamepad icon must be visible too
			
    		mSelectBotButton.setClickable(true);
    		
			mGamePadButton.setVisibility(View.VISIBLE);
			mGamePadButton.setSelected(false);
			mGamePadButton.setClickable(true);
			
			mScheduleIcon.setVisibility(View.VISIBLE);
			mScheduleIcon.setSelected(true);
		}
	}
	
	
	
	/********************************************************************************************
	 ****************************   SELECTBOTLISTENER CALLBACKS   *******************************
	 ********************************************************************************************/

	/**
	 * Callback from the SelectBotFragment for showing the selected type of robot fragment.
	 * Replaces the current RobotFragment with the SelectBotFragment. 
	 * 
	 * @param botType The type of robot selected by the user
	 */
	@Override
	public void onRobotSelected(robotType botType) {

		FragmentTransaction ft = mFragmentManager.beginTransaction();
		RobotFragment robotFragment = null;

		if (botType == robotType.POLLYWOG) {
			mBottomTitleBar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bot_pollywog_action_bar_icon, 0, 0, 0);
			robotFragment = new PollywogFragment();

		} else if (botType == robotType.BEETLE) {
			mBottomTitleBar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bot_beetle_action_bar_icon, 0, 0, 0);
			robotFragment = new BeetleFragment();

		} else if (botType == robotType.RHINO) {	
			mBottomTitleBar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bot_rhino_action_bar_icon, 0, 0, 0);
			robotFragment = new RhinoFragment();

		}else if (botType == robotType.CRAB) {
			mBottomTitleBar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bot_crab_action_bar_icon, 0, 0, 0);
			robotFragment = new CrabFragment();

		} else if (botType == robotType.GENERIC_ROBOT) {
			mBottomTitleBar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bot_generic_action_bar_icon, 0, 0, 0);
			robotFragment = new GenericRobotFragment();
		}
		
		if(robotFragment != null) {
			
			ft.replace(R.id.game_pad_container, robotFragment);
			ft.commit();

			// the select_bot_button must be clickable when the RobotFragment is showed
			// and the gamepad icon must be visible too
			mSelectBotButton.setClickable(true);
			mGamePadButton.setVisibility(View.VISIBLE);
			mGamePadButton.setSelected(true);
			
			mScheduleIcon.setVisibility(View.GONE);
			mScheduleIcon.setSelected(false);
			
            connectButton.setVisibility(View.VISIBLE);
            disconnectButton.setVisibility(View.GONE);
		}

	}
	
	
	
	/********************************************************************************************
	 ****************************   BOTH LISTENER CALLBACKS   *******************************
	 ********************************************************************************************/
	
	/**
	 * Callback from the RobotFragment or SelectBotFragment for changing the bottom title bar.
	 * 
	 * @param titleId The text resource id
	 */
	@Override
	public void onSetFragmentTitle(int titleId) {
		setFragmentTitle(titleId);		
	}

	
	/**
	 * Callback from the RobotFragment or SelectBotFragment for changing the bottom title bar.
	 * 
	 * @param title The text resource id
	 */
	@Override
	public void onSetFragmentTitle(String title) {
		setFragmentTitle(title);		
	}

}
