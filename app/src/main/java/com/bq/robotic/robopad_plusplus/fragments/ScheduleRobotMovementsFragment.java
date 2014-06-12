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

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.bq.robotic.drag_drop_grid.DeleteDropZoneView;
import com.bq.robotic.drag_drop_grid.DraggableGridView;
import com.bq.robotic.drag_drop_grid.OnRearrangeListener;
import com.bq.robotic.robopad_plusplus.R;
import com.bq.robotic.robopad_plusplus.listeners.ScheduleRobotMovementsListener;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants.robotType;

import java.util.ArrayList;



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
	private int currentControlIndex = -1;
	private boolean sendStopCommand = false;

    private Handler sendMovementsHandler;
	

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.fragment_schedule_robot, container, false);
		
		gridView = ((DraggableGridView) layout.findViewById(R.id.grid_view));
		gridView.setDeleteZone((DeleteDropZoneView) layout.findViewById(R.id.delete_view));

		return layout;

	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle bundle = this.getArguments();
		if(bundle != null){
		    int robotTypeIndex = bundle.getInt(RoboPadConstants.ROBOT_TYPE_KEY, robotType.POLLYWOG.ordinal());

		    if(robotTypeIndex == robotType.POLLYWOG.ordinal()) {
		    	botType = robotType.POLLYWOG;

		    } else if (robotTypeIndex == robotType.BEETLE.ordinal()) {
		    	botType = robotType.BEETLE;
		    	setBeetleUIComponents();
		    	
		    } else if (robotTypeIndex == robotType.RHINO.ordinal()) {
                botType = robotType.RHINO;
                setRhinoUIComponents();

            } else if (robotTypeIndex == robotType.CRAB.ordinal()) {
                botType = robotType.CRAB;

		    } else if (robotTypeIndex == robotType.GENERIC_ROBOT.ordinal()) {
		    	botType = robotType.GENERIC_ROBOT;
		    	setGenericRobotUIComponents();
		    }

            setUiListeners();

            Resources r = getActivity().getResources();

            int size = -1;
            Configuration config = r.getConfiguration();

            if (config.smallestScreenWidthDp >= 720) {
                size = 120;

            } else if (config.smallestScreenWidthDp >= 600) {
                size = 95;

            }else if (config.screenWidthDp >= 500) {
                size = 80;

            } else {
                size = 60;
            }

            if(size > 0) {
                gridView.setFixedChildrenHeight(size);
                gridView.setFixedChildrenWidth(size);
            }

            sendMovementsHandler = new Handler();
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
	
	
	public void onBluetoothConnected() {
		// Do nothing
	}
	
	
	public void onBluetoothDisconnected() {
		enableControllerButtons();
		
	}


	/**
	 * Set the listeners to the views that need them. It must be done here in the fragment in order
	 * to get the callback here and not in the FragmentActivity, that would be a mess with all the 
	 * callbacks of all the possible fragments
	 */
	private void setUiListeners() {
		
    	gridView.setOnRearrangeListener(new OnRearrangeListener() {
            public void onRearrange(int oldIndex, int newIndex) {
                if (scheduledControls.isEmpty()) {
                    return;
                }

                String scheduledControl = scheduledControls.remove(oldIndex);
                if (oldIndex < newIndex)
                    scheduledControls.add(newIndex, scheduledControl);
                else
                    scheduledControls.add(newIndex, scheduledControl);
            }

            public void onRearrange(boolean isDraggedDeleted, int draggedDeletedIndex) {
                if (scheduledControls.isEmpty()) {
                    return;
                }

                if (isDraggedDeleted) {
                    scheduledControls.remove(draggedDeletedIndex);
                }
            }
        });

        GridLayout movementsViews = (GridLayout) getActivity().findViewById(R.id.type_of_movements_container);
        for (int i = 0; i < movementsViews.getChildCount(); i++) {
            movementsViews.getChildAt(i).setOnClickListener(onMovementsButtonClick);
        }

        LinearLayout optionsLayout = (LinearLayout) getActivity().findViewById(R.id.menu_options_container);
        for (int i = 0; i < optionsLayout.getChildCount(); i++) {
            optionsLayout.getChildAt(i).setOnClickListener(onOptionsButtonClick);
        }


        final GridLayout typeOfMovementsContainer = (GridLayout) getActivity().findViewById(R.id.type_of_movements_container);
        final ViewTreeObserver vto = typeOfMovementsContainer.getViewTreeObserver();
        final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                Rect scrollBounds = new Rect();
                ScrollView scroll = (ScrollView) getActivity().findViewById(R.id.scroll_container);
                scroll.getDrawingRect(scrollBounds);
//                int finalHeight = scrollBounds.bottom - getActivity().getResources().getDimensionPixelSize(R.dimen.scheduler_grid_margin);
                int finalHeight = scrollBounds.bottom;
                int childCount = typeOfMovementsContainer.getChildCount();

                if(childCount > 1) {
                    for (int i = 0; i < childCount; i++) {

                        if (typeOfMovementsContainer.getChildAt(i).getBottom() > finalHeight) {
                            typeOfMovementsContainer.setColumnCount(2);
                            break;
                        }
                    }
                }

                scroll.invalidate();

                final ViewTreeObserver vto = typeOfMovementsContainer.getViewTreeObserver();
                vto.removeOnPreDrawListener(this);

                return true;
            }
        };

        vto.addOnPreDrawListener(preDrawListener);

	}
	
	
	private void setBeetleUIComponents() {
		GridLayout controlsLayout = (GridLayout) getActivity().findViewById(R.id.type_of_movements_container);
		getActivity().getLayoutInflater().inflate(R.layout.scheduler_beetle_component, controlsLayout, true);
	}
	
	
	private void setRhinoUIComponents() {
        GridLayout controlsLayout = (GridLayout) getActivity().findViewById(R.id.type_of_movements_container);
        getActivity().getLayoutInflater().inflate(R.layout.scheduler_rhino_component, controlsLayout, true);

	}
	
	
	private void setGenericRobotUIComponents() {
        GridLayout controlsLayout = (GridLayout) getActivity().findViewById(R.id.type_of_movements_container);
        getActivity().getLayoutInflater().inflate(R.layout.scheduler_generic_robot_component, controlsLayout, true);

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


    private void addButtonPressedToGrid(int drawableId, String movement) {
        ImageView view = (ImageView) ImageView.inflate(getActivity().getBaseContext(), R.layout.grid_view_item_layout, null);
        view.setImageResource(drawableId);
        gridView.addView(view);
        scheduledControls.add(movement);

    }


	/**
	 * Listeners for the views that manage only clicks
	 */
	private OnClickListener onMovementsButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if(listener == null) {
				Log.e(LOG_TAG, "RobotListener is null");
				return;
			}
			
			switch(v.getId()) {

				case R.id.stop_button:
                    addButtonPressedToGrid(R.drawable.ic_stop_button, RoboPadConstants.STOP_COMMAND);
					break;
					
				case R.id.up_button:
                    addButtonPressedToGrid(R.drawable.ic_up_button, RoboPadConstants.UP_COMMAND);
					break;
					
				case R.id.down_button:
                    addButtonPressedToGrid(R.drawable.ic_down_button, RoboPadConstants.DOWN_COMMAND);
					break;
					
				case R.id.left_button:
                    addButtonPressedToGrid(R.drawable.ic_left_button, RoboPadConstants.LEFT_COMMAND);
					break;
				
				case R.id.right_button:
                    addButtonPressedToGrid(R.drawable.ic_right_button, RoboPadConstants.RIGHT_COMMAND);
					break;
			}

            if(botType == robotType.BEETLE) {
                switch (v.getId()) {
                    case R.id.full_open_claw_button:
                        addButtonPressedToGrid(R.drawable.ic_full_open_claw, RoboPadConstants.FULL_OPEN_STEP_COMMAND);
                        break;

                    case R.id.open_claw_button:
                        addButtonPressedToGrid(R.drawable.ic_open_claw, RoboPadConstants.OPEN_STEP_COMMAND);
                        break;

                    case R.id.close_claw_button:
                        addButtonPressedToGrid(R.drawable.ic_close_claw, RoboPadConstants.CLOSE_STEP_COMMAND);
                        break;
                }
            }

            if(botType == robotType.RHINO) {
                switch(v.getId()) {

                    case R.id.charge_button:
                        addButtonPressedToGrid(R.drawable.charge_button, RoboPadConstants.CHARGE_COMMAND);
                        break;

                }
            }

            if(botType == robotType.GENERIC_ROBOT) {
                switch(v.getId()) {

                    case R.id.command_button_1:
                        addButtonPressedToGrid(R.drawable.comand_button_1, RoboPadConstants.COMMAND_1);
                        break;

                    case R.id.command_button_2:
                        addButtonPressedToGrid(R.drawable.comand_button_2, RoboPadConstants.COMMAND_2);
                        break;

                    case R.id.command_button_3:
                        addButtonPressedToGrid(R.drawable.comand_button_3, RoboPadConstants.COMMAND_3);
                        break;

                    case R.id.command_button_4:
                        addButtonPressedToGrid(R.drawable.comand_button_4, RoboPadConstants.COMMAND_4);
                        break;

                    case R.id.command_button_5:
                        addButtonPressedToGrid(R.drawable.comand_button_5, RoboPadConstants.COMMAND_5);
                        break;

                    case R.id.command_button_6:
                        addButtonPressedToGrid(R.drawable.comand_button_6, RoboPadConstants.COMMAND_6);
                        break;

                }
            }

		}
	};


    /**
     * Listeners for the views that manage only clicks
     */
    private OnClickListener onOptionsButtonClick = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (listener == null) {
                Log.e(LOG_TAG, "RobotListener is null");
                return;
            }

            switch (v.getId()) {

                case R.id.remove_all_button:
                    scheduledControls.clear();
                    gridView.removeAll();
                    break;

                case R.id.send_movements_button:

                    if (!listener.onCheckIsConnected() || scheduledControls.isEmpty()) {
                        break;
                    }

                    disableControllerButtons();

                    currentControlIndex = 0;
                    (new MySendControlsToArduinoTask()).run();
                    break;

                case R.id.load_movements_button:
                    //FIXME
//		            loadScheduler();
                    break;

                case R.id.save_movements_button:
                    saveScheduler();
                    break;

                case R.id.remove_stored_movements_button:
                    //FIXME
//		            removeScheduler();
                    break;

//                case R.id.setting_button:
////				    PopupMenu popup = new PopupMenu(getActivity(), v);
////				    MenuInflater inflater = popup.getMenuInflater();
////				    inflater.inflate(R.menu.scheduler_menu, popup.getMenu());
////				    popup.setOnMenuItemClickListener(onMenuItemClickListener);
////				    popup.show();
//                    break;
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
        GridLayout controlsLayout = (GridLayout) getActivity().findViewById(R.id.type_of_movements_container);
		for (int i = 0; i < controlsLayout.getChildCount(); i++) {
            controlsLayout.getChildAt(i).setEnabled(true);
		}

        ((ProgressBar) getActivity().findViewById(R.id.progress_bar)).setVisibility(View.GONE);

        LinearLayout optionsLayout = (LinearLayout) getActivity().findViewById(R.id.menu_options_container);
        for (int i = 0; i < optionsLayout.getChildCount(); i++) {
            optionsLayout.getChildAt(i).setEnabled(true);
        }
		
		gridView.setEnabled(true);
	}
	
	
	private void disableControllerButtons() {
        GridLayout controlsViews = (GridLayout) getActivity().findViewById(R.id.type_of_movements_container);
		for (int i = 0; i < controlsViews.getChildCount(); i++) {
			controlsViews.getChildAt(i).setEnabled(false);
		}

        LinearLayout optionsLayout = (LinearLayout) getActivity().findViewById(R.id.menu_options_container);
        for (int i = 0; i < optionsLayout.getChildCount(); i++) {
            optionsLayout.getChildAt(i).setEnabled(false);
        }

		((ProgressBar) getActivity().findViewById(R.id.progress_bar)).setVisibility(View.VISIBLE);

		gridView.setEnabled(false);
	}
		

	private void saveScheduler() {
		
	}
	
	
	
	/**************************************************************************************
	 **************************   MySendControlsToArduinoTask   ***************************
	 **************************************************************************************/
	
	class MySendControlsToArduinoTask implements Runnable {

        @Override
        public void run() {
            Log.d(LOG_TAG, "currentControlIndex: " + currentControlIndex);

            if (sendStopCommand) {
                Log.i(LOG_TAG, "sendStopCommand");
				listener.onSendMessage(RoboPadConstants.STOP_COMMAND);
				sendStopCommand = false;

                if (botType == robotType.CRAB) {
                    sendMovementsHandler.postDelayed(this, RoboPadConstants.CRAB_DELAY_BETWEEN_SCHEDULED_COMMANDS);

                } else {
                    sendMovementsHandler.postDelayed(this, RoboPadConstants.DELAY_BETWEEN_SCHEDULED_COMMANDS);
                }

			} else if (currentControlIndex >= scheduledControls.size()) {
				Log.i(LOG_TAG, "all movements were sent");
                sendMovementsHandler.removeCallbacks(this);

                gridView.getChildAt(currentControlIndex - 1).setBackgroundColor(Color.WHITE);
				currentControlIndex = -1;

                gridView.scrollTo(0, 0);

                enableControllerButtons();

			} else if (currentControlIndex < scheduledControls.size()) {
                Log.d(LOG_TAG, "new movement: " + scheduledControls.get(currentControlIndex));
                listener.onSendMessage(scheduledControls.get(currentControlIndex));

                if(currentControlIndex == 0) {
                    gridView.scrollToTop();
                }

                View currentChild = gridView.getChildAt(currentControlIndex);

                currentChild.setBackgroundResource(R.drawable.turquoise_circle_default_button);

                if(currentChild.getBottom() > gridView.getHeight()) {
                    gridView.scrollTo(0, (int) (currentChild.getTop() - currentChild.getHeight()
                            - currentChild.getPaddingTop() - currentChild.getPaddingBottom()));
                }

                if (currentControlIndex > 0) {
                    gridView.getChildAt(currentControlIndex - 1).setBackgroundColor(Color.WHITE);
                }

                sendStopCommand = true;
                currentControlIndex++;

                if (botType == robotType.CRAB) {
                    sendMovementsHandler.postDelayed(this, RoboPadConstants.CRAB_DELAY_BETWEEN_SCHEDULED_COMMANDS);

                } else {
                    sendMovementsHandler.postDelayed(this, RoboPadConstants.DELAY_BETWEEN_SCHEDULED_COMMANDS);
                }
			}

        }



	}
	
}
