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

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bq.robotic.robopad_plusplus.R;
import com.bq.robotic.robopad_plusplus.RoboPadConstants;
import com.bq.robotic.robopad_plusplus.RoboPadConstants.robotType;
import com.bq.robotic.robopad_plusplus.drag_drop_grid.DeleteDropZoneView;
import com.bq.robotic.robopad_plusplus.drag_drop_grid.DraggableGridView;
import com.bq.robotic.robopad_plusplus.drag_drop_grid.OnRearrangeListener;
import com.bq.robotic.robopad_plusplus.listeners.ScheduleRobotMovementsListener;


/**
 * Fragment for programming actions to the robots.
 * 
 * @author Estefanía Sarasola Elvira
 *
 */

public class ScheduleRobotMovementsFragment extends Fragment {

	// Debugging
	private static final String LOG_TAG = "ScheduleRobotActionsFragment";
	
	private ScheduleRobotMovementsListener listener;
	private DraggableGridView gridView;
	private robotType botType;
	private ArrayList<String> scheduledControls = new ArrayList<String>();
	private ScheduledThreadPoolExecutor exec;
	private int currentControlIndex = -1;
	private boolean sendStopCommand = false;
	

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.fragment_schedule_robot, container, false);
		
		gridView = ((DraggableGridView) layout.findViewById(R.id.grid_view));
		gridView.setDeleteZone((DeleteDropZoneView) layout.findViewById(R.id.delete_view));

		setUiListeners(layout);

		return layout;

	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle bundle = this.getArguments();
		if(bundle != null){
		    int robotTypeIndex = bundle.getInt(RoboPadConstants.ROBOT_TYPE_KEY, robotType.POLLYWOG.ordinal());
		    String botTypeString = "";
		    
		    if(robotTypeIndex == robotType.POLLYWOG.ordinal()) {
		    	botType = robotType.POLLYWOG;
		    	botTypeString = getString(R.string.pollywog);
		    
		    } else if (robotTypeIndex == robotType.BEETLE.ordinal()) {
		    	botType = robotType.BEETLE;
		    	botTypeString = getString(R.string.beetle);
		    	
		    	setBeetleUIComponents();
		    	
		    } else if (robotTypeIndex == robotType.RHINO.ordinal()) {
		    	botType = robotType.RHINO;
		    	botTypeString = getString(R.string.rhino);
		    	
		    	setRhinoUIComponents();
		    
		    } else if (robotTypeIndex == robotType.GENERIC_ROBOT.ordinal()) {
		    	botType = robotType.GENERIC_ROBOT;
		    	botTypeString = getString(R.string.generic_robot);
		    	
		    	setGenericRobotUIComponents();
		    }
		    
			if(listener != null) {
				listener.onSetFragmentTitle(getString(R.string.schedule_title) + botTypeString);
			}
		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Retain this fragment across configuration changes.
		setRetainInstance(true);
	}
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Check the listener is the correct one: the fragment activity container
		// implements that listener
		if (activity instanceof ScheduleRobotMovementsListener) {
			this.listener = (ScheduleRobotMovementsListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement SelectBotListener");
		}
	}
	
	
//	public void onBluetoothConnected() {
//		
//	}
	
	
	public void onBluetoothDisconnected() {
		enableControllerButtons();
		
	}


	/**
	 * Set the listeners to the views that need them. It must be done here in the fragment in order
	 * to get the callback here and not in the FragmentActivity, that would be a mess with all the 
	 * callbacks of all the possible fragments
	 * 
	 * @param The view used as the main container for this fragment
	 */
	protected void setUiListeners(View containerLayout) {
		
    	gridView.setOnRearrangeListener(new OnRearrangeListener() {
			public void onRearrange(int oldIndex, int newIndex) {
				if(scheduledControls.isEmpty()) {
					return;
				}
				
				String scheduledControl = scheduledControls.remove(oldIndex);
				if (oldIndex < newIndex)
					scheduledControls.add(newIndex, scheduledControl);
				else
					scheduledControls.add(newIndex, scheduledControl);
			}
			
			public void onRearrange(boolean isDraggedDeleted, int draggedDeletedIndex) {
				if(scheduledControls.isEmpty()) {
					return;
				}
				
				if(isDraggedDeleted) {
					scheduledControls.remove(draggedDeletedIndex);
				}
			}
		});
    	
//    	gridView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				gridView.removeViewAt(arg2);
//				scheduledControls.remove(arg2);
//			}
//		});
    	
    	//FIXME
    	gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				gridView.onClick(arg1);
				return true;
			}
		});

    	ImageButton stopButton = (ImageButton) containerLayout.findViewById(R.id.stop_button);
		stopButton.setOnClickListener(onButtonClick);

		ImageButton upButton = (ImageButton) containerLayout.findViewById(R.id.up_button);
		upButton.setOnClickListener(onButtonClick);

		ImageButton downButton = (ImageButton) containerLayout.findViewById(R.id.down_button);
		downButton.setOnClickListener(onButtonClick);

		ImageButton leftButton = (ImageButton) containerLayout.findViewById(R.id.left_button);
		leftButton.setOnClickListener(onButtonClick);

		ImageButton rightButton = (ImageButton) containerLayout.findViewById(R.id.right_button);
		rightButton.setOnClickListener(onButtonClick);
		
		ImageButton playCommandsButton = (ImageButton) containerLayout.findViewById(R.id.play_command_button);
		playCommandsButton.setOnClickListener(onButtonClick);
		
		ImageButton removeAllButton = (ImageButton) containerLayout.findViewById(R.id.remove_all_button);
		removeAllButton.setOnClickListener(onButtonClick);
	}
	
	
	private void setBeetleUIComponents() {
		LinearLayout controlsLayout = (LinearLayout) getActivity().findViewById(R.id.controls_layout);
		controlsLayout.addView(LinearLayout.inflate(getActivity().getBaseContext(), R.layout.scheduler_beetle_component, null));
		
		ImageButton mFullOpenClawButton = (ImageButton) getActivity().findViewById(R.id.full_open_claw_button);
		mFullOpenClawButton.setOnClickListener(onClawButtonClick);

		ImageButton mOpenStepClawButton = (ImageButton) getActivity().findViewById(R.id.open_claw_button);
		mOpenStepClawButton.setOnClickListener(onClawButtonClick);

		ImageButton mCloseStepClawButton = (ImageButton) getActivity().findViewById(R.id.close_claw_button);
		mCloseStepClawButton.setOnClickListener(onClawButtonClick);
	}
	
	
	private void setRhinoUIComponents() {
		LinearLayout controlsLayout = (LinearLayout) getActivity().findViewById(R.id.controls_layout);
		controlsLayout.addView(LinearLayout.inflate(getActivity().getBaseContext(), R.layout.scheduler_rhino_component, null));
		
		ImageButton mChargeButton = (ImageButton) getActivity().findViewById(R.id.charge_button);
		mChargeButton.setOnClickListener(onChargeButtonClick);
	}
	
	
	private void setGenericRobotUIComponents() {
		LinearLayout controlsLayout = (LinearLayout) getActivity().findViewById(R.id.controls_layout);
		controlsLayout.addView(LinearLayout.inflate(getActivity().getBaseContext(), R.layout.scheduler_generic_robot_component, null));
		
		ImageButton commandButton1 = (ImageButton) getActivity().findViewById(R.id.command_button_1);
		commandButton1.setOnClickListener(onGenericCommandButtonClick);
		
		ImageButton commandButton2 = (ImageButton) getActivity().findViewById(R.id.command_button_2);
		commandButton2.setOnClickListener(onGenericCommandButtonClick);
		
		ImageButton commandButton3 = (ImageButton) getActivity().findViewById(R.id.command_button_3);
		commandButton3.setOnClickListener(onGenericCommandButtonClick);
		
		ImageButton commandButton4 = (ImageButton) getActivity().findViewById(R.id.command_button_4);
		commandButton4.setOnClickListener(onGenericCommandButtonClick);
		
		ImageButton commandButton5 = (ImageButton) getActivity().findViewById(R.id.command_button_5);
		commandButton5.setOnClickListener(onGenericCommandButtonClick);
		
		ImageButton commandButton6 = (ImageButton) getActivity().findViewById(R.id.command_button_6);
		commandButton6.setOnClickListener(onGenericCommandButtonClick);
	}
	
	
    /**
     * Set the fragmentActivity listener. Right now it is not necessary because the 
     * fragment activity that contains the fragments is the one that implements the listener
     * so it is done in the onAttach of RobotFragment. But with this method can be another class 
     * witch implements the listener not the container fragment activity.
     * 
     * @param listener The ScheduleRobotActionsListener
     */
    public void setScheduleRobotActionsListener(ScheduleRobotMovementsListener listener) {
    	this.listener = listener;
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
			
			ImageView view = (ImageView) ImageView.inflate(getActivity().getBaseContext(), R.layout.grid_view_item_layout, null);

			switch(v.getId()) { 

				case R.id.stop_button:
					view.setImageResource(R.drawable.stop_button);
					gridView.addView(view);
					scheduledControls.add(RoboPadConstants.STOP_COMMAND);				
					break;
					
				case R.id.up_button:
					view.setImageResource(R.drawable.up_button);
					gridView.addView(view);
					scheduledControls.add(RoboPadConstants.UP_COMMAND);				
					break;
					
				case R.id.down_button:
					view.setImageResource(R.drawable.down_button);
					gridView.addView(view);
					scheduledControls.add(RoboPadConstants.DOWN_COMMAND);				
					break;
					
				case R.id.left_button:
					view.setImageResource(R.drawable.left_button);
					gridView.addView(view);
					scheduledControls.add(RoboPadConstants.LEFT_COMMAND);				
					break;
				
				case R.id.right_button:
					view.setImageResource(R.drawable.right_button);
					gridView.addView(view);
					scheduledControls.add(RoboPadConstants.RIGHT_COMMAND);				
					break;
					
				case R.id.remove_all_button:
					scheduledControls.clear();
					gridView.removeAll();
					break;
					
				case R.id.play_command_button:
					
					if(!listener.onCheckIsConnected() || scheduledControls.isEmpty()) {
						break;
					}
					
					disableControllerButtons();
					
					exec = new ScheduledThreadPoolExecutor(1);
					//the delay between the termination of one execution and the commencement of the next
					long delay = RoboPadConstants.DELAY_BETWEEN_SCHEDULED_COMMANDS; 
					currentControlIndex = 0;
					exec.scheduleWithFixedDelay(new MySendControlsToArduinoTask(), 0, delay, TimeUnit.MILLISECONDS);
					break;
			}

		}
	};


	protected OnClickListener onClawButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if(listener == null) {
				Log.e(LOG_TAG, "RobotListener is null");
				return;
			}
			
			ImageView view = (ImageView) ImageView.inflate(getActivity().getBaseContext(), R.layout.grid_view_item_layout, null);

			switch(v.getId()) { 
			
				case R.id.full_open_claw_button: 
					view.setImageResource(R.drawable.full_open_claw_button);
					gridView.addView(view);
					scheduledControls.add(RoboPadConstants.FULL_OPEN_STEP_COMMAND);	
					break;
	
				case R.id.open_claw_button: 
					view.setImageResource(R.drawable.open_claw_button);
					gridView.addView(view);
					scheduledControls.add(RoboPadConstants.OPEN_STEP_COMMAND);
					break;
	
				case R.id.close_claw_button:
					view.setImageResource(R.drawable.close_claw_button);
					gridView.addView(view);
					scheduledControls.add(RoboPadConstants.CLOSE_STEP_COMMAND);
					break;	
			}
		}
	};
	
	
	protected OnClickListener onChargeButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if(listener == null) {
				Log.e(LOG_TAG, "RobotListener is null");
				return;
			}
			
			ImageView view = (ImageView) ImageView.inflate(getActivity().getBaseContext(), R.layout.grid_view_item_layout, null);

			switch(v.getId()) { 
			
				case R.id.charge_button: 
					view.setImageResource(R.drawable.charge_button);
					gridView.addView(view);
					scheduledControls.add(RoboPadConstants.CHARGE_COMMAND);	
					break;
	
			}
		}
	};
	
	
	protected OnClickListener onGenericCommandButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if(listener == null) {
				Log.e(LOG_TAG, "RobotListener is null");
				return;
			}
			
			ImageView view = (ImageView) ImageView.inflate(getActivity().getBaseContext(), R.layout.grid_view_item_layout, null);

			switch(v.getId()) { 
			
				case R.id.command_button_1: 
					view.setImageResource(R.drawable.comand_button_1);
					gridView.addView(view);
					scheduledControls.add(RoboPadConstants.COMMAND_1);	
					break;
					
				case R.id.command_button_2: 
					view.setImageResource(R.drawable.comand_button_2);
					gridView.addView(view);
					scheduledControls.add(RoboPadConstants.COMMAND_2);	
					break;
					
				case R.id.command_button_3: 
					view.setImageResource(R.drawable.comand_button_3);
					gridView.addView(view);
					scheduledControls.add(RoboPadConstants.COMMAND_3);	
					break;
					
				case R.id.command_button_4: 
					view.setImageResource(R.drawable.comand_button_4);
					gridView.addView(view);
					scheduledControls.add(RoboPadConstants.COMMAND_4);	
					break;
					
				case R.id.command_button_5: 
					view.setImageResource(R.drawable.comand_button_5);
					gridView.addView(view);
					scheduledControls.add(RoboPadConstants.COMMAND_5);	
					break;
					
				case R.id.command_button_6: 
					view.setImageResource(R.drawable.comand_button_6);
					gridView.addView(view);
					scheduledControls.add(RoboPadConstants.COMMAND_6);	
					break;

			}
		}
	};


	public robotType getBotType() {
		return botType;
	}


	public void setBotType(robotType botType) {
		this.botType = botType;
	}
	
	
	private void enableControllerButtons() {
		LinearLayout controlsViews = (LinearLayout) getActivity().findViewById(R.id.controls_layout);
		for (int i = 0; i < controlsViews.getChildCount(); i++) {
			controlsViews.getChildAt(i).setEnabled(true);
		}

		((ImageButton) getActivity().findViewById(R.id.play_command_button)).setEnabled(true);
		((ImageButton) getActivity().findViewById(R.id.remove_all_button)).setEnabled(true);
		((ProgressBar) getActivity().findViewById(R.id.progress_bar)).setVisibility(View.GONE);
		
		if(botType == robotType.BEETLE) {
			LinearLayout beetleControlsViews = (LinearLayout) getActivity().findViewById(R.id.beetle_controls_layout);
			for (int i = 0; i < beetleControlsViews.getChildCount(); i++) {
				beetleControlsViews.getChildAt(i).setEnabled(true);
			}
		
		} else if(botType == robotType.GENERIC_ROBOT) {
			LinearLayout genericRobotControlsViews = (LinearLayout) getActivity().findViewById(R.id.generic_robot_controls_layout);
			for (int i = 0; i < genericRobotControlsViews.getChildCount(); i++) {
				genericRobotControlsViews.getChildAt(i).setEnabled(true);
			}
		}
		
		
		
		gridView.setEnabled(true);
	}
	
	
	private void disableControllerButtons() {
		LinearLayout controlsViews = (LinearLayout) getActivity().findViewById(R.id.controls_layout);
		for (int i = 0; i < controlsViews.getChildCount(); i++) {
			controlsViews.getChildAt(i).setEnabled(false);
		}
		
		((ImageButton) getActivity().findViewById(R.id.play_command_button)).setEnabled(false);
		((ImageButton) getActivity().findViewById(R.id.remove_all_button)).setEnabled(false);
		((ProgressBar) getActivity().findViewById(R.id.progress_bar)).setVisibility(View.VISIBLE);
		
		if(botType == robotType.BEETLE) {
			LinearLayout beetleControlsViews = (LinearLayout) getActivity().findViewById(R.id.beetle_controls_layout);
			for (int i = 0; i < beetleControlsViews.getChildCount(); i++) {
				beetleControlsViews.getChildAt(i).setEnabled(false);
			}
		
		} else if(botType == robotType.GENERIC_ROBOT) {
			LinearLayout genericRobotControlsViews = (LinearLayout) getActivity().findViewById(R.id.generic_robot_controls_layout);
			for (int i = 0; i < genericRobotControlsViews.getChildCount(); i++) {
				genericRobotControlsViews.getChildAt(i).setEnabled(false);
			}
		}
		
		gridView.setEnabled(false);
	}
		

	
	/**************************************************************************************
	 **************************   MySendControlsToArduinoTask   ***************************
	 **************************************************************************************/
	
	class MySendControlsToArduinoTask implements Runnable {

		@Override
		public void run() {
			Log.d(LOG_TAG, "currentControlIndex: " + currentControlIndex);  

			if (sendStopCommand) {
				listener.onSendMessage(RoboPadConstants.STOP_COMMAND);
				sendStopCommand = false;
				
			} else if (currentControlIndex == scheduledControls.size()) { 
				Log.i(LOG_TAG, "all movements were sent");
				exec.shutdown();
				currentControlIndex = -1;

				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						enableControllerButtons();
					}
				});

			} else {
				Log.e(LOG_TAG, "new movement: " + scheduledControls.get(currentControlIndex));
				listener.onSendMessage(scheduledControls.get(currentControlIndex));
				
				sendStopCommand = true;
				currentControlIndex++;
			}


		} 
	}
	
}
