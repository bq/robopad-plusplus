/*
* This file is part of the GamePad
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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bq.robotic.robopad_plusplus.R;
import com.bq.robotic.robopad_plusplus.RoboPadConstants;
import com.bq.robotic.robopad_plusplus.RoboPadConstants.robotType;
import com.bq.robotic.robopad_plusplus.drag_drop_grid.DragDropGrid;
import com.bq.robotic.robopad_plusplus.drag_drop_grid.RobotControlsDragDropGridAdapter;
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
	private DragDropGrid gridview;
	private robotType botType;


	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.fragment_schedule_robot, container, false);

		setUiListeners(layout);
		
		gridview = (DragDropGrid) layout.findViewById(R.id.grid_view);	
		
		RobotControlsDragDropGridAdapter adapter = new RobotControlsDragDropGridAdapter(getActivity(), gridview);
		
        gridview.setAdapter(adapter);
		gridview.setOnClickListener(onGridViewClick);

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
		    	
		    } else if (robotTypeIndex == robotType.RHINO.ordinal()) {
		    	botType = robotType.RHINO;
		    	botTypeString = getString(R.string.rhino);
		    
		    } else if (robotTypeIndex == robotType.GENERIC_ROBOT.ordinal()) {
		    	botType = robotType.GENERIC_ROBOT;
		    	botTypeString = getString(R.string.generic_robot);
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


	/**
	 * Set the listeners to the views that need them. It must be done here in the fragment in order
	 * to get the callback here and not in the FragmentActivity, that would be a mess with all the 
	 * callbacks of all the possible fragments
	 * 
	 * @param The view used as the main container for this fragment
	 */
	protected void setUiListeners(View containerLayout) {

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

			switch(v.getId()) { 

				case R.id.stop_button:
//					listener.onSendMessage(RoboPadConstants.STOP_COMMAND);    				
					break;
			}

		}
	};
	
	
	/**
	 * Listeners for the views in the grid
	 */
	protected OnClickListener onGridViewClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Toast.makeText(getActivity(), "Clicked View", Toast.LENGTH_SHORT).show();
		}
	};


	public robotType getBotType() {
		return botType;
	}


	public void setBotType(robotType botType) {
		this.botType = botType;
	}
	
}
