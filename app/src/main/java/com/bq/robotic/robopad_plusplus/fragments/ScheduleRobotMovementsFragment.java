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
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.util.DisplayMetrics;
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
import android.widget.Toast;

import com.bq.robotic.drag_drop_grid.DeleteDropZoneView;
import com.bq.robotic.drag_drop_grid.DraggableGridView;
import com.bq.robotic.drag_drop_grid.OnRearrangeListener;
import com.bq.robotic.robopad_plusplus.R;
import com.bq.robotic.robopad_plusplus.listeners.ScheduleRobotMovementsListener;
import com.bq.robotic.robopad_plusplus.listeners.ScheduledMovementsFileManagementListener;
import com.bq.robotic.robopad_plusplus.listeners.TipsManagerListener;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants.robotType;
import com.bq.robotic.robopad_plusplus.utils.ScheduledMovementsFileManagement;
import com.bq.robotic.robopad_plusplus.utils.TipsFactory;
import com.bq.robotic.robopad_plusplus.utils.TipsManager;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * Fragment for programming movements to the robots, which will execute them one by one.
 * 
 */

public class ScheduleRobotMovementsFragment extends Fragment implements ScheduledMovementsFileManagementListener, TipsManagerListener {

	// Debugging
	private static final String LOG_TAG = "ScheduleRobotActionsFragment";
	
	private ScheduleRobotMovementsListener listener;
	private DraggableGridView gridView;
	private robotType mBotType;
	private ArrayList<String> scheduledControls = new ArrayList<String>();
	private int currentControlIndex = -1;
	private boolean sendStopCommand = false;
    private MySendControlsToArduinoTask mSendControlsToArduinoTask;

    private boolean waitsBetweenMovementsEnabledInPref = true;

    private Handler sendMovementsHandler;
    private ScheduledMovementsFileManagement scheduledMovementsFileManagement;

    // Tips
    protected ToolTipRelativeLayout mToolTipFrameLayout;
    protected TipsManager tipsManager;
    private tips currentTip;
    private enum tips {ADD_COMMANDS, REMOVE_ALL, SEND, LOAD, SAVE, DELETE_FILE, GRID}
	

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.fragment_scheduler_robot, container, false);
		
		gridView = ((DraggableGridView) layout.findViewById(R.id.grid_view));
        DeleteDropZoneView deleteZone = (DeleteDropZoneView) layout.findViewById(R.id.delete_view);
        deleteZone.setDeleteDrawable(R.drawable.ic_trash);
        deleteZone.setHighlightDeleteDrawable(R.drawable.red_circle_default_button);
		gridView.setDeleteZone(deleteZone);

		return layout;
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle bundle = this.getArguments();
		if(bundle != null){
		    int robotTypeIndex = bundle.getInt(RoboPadConstants.ROBOT_TYPE_KEY, robotType.POLLYWOG.ordinal());

		    if(robotTypeIndex == robotType.POLLYWOG.ordinal()) {
		    	mBotType = robotType.POLLYWOG;

		    } else if (robotTypeIndex == robotType.BEETLE.ordinal()) {
		    	mBotType = robotType.BEETLE;
		    	setBeetleUIComponents();

            } else if (robotTypeIndex == robotType.EVOLUTION.ordinal()) {
                mBotType = robotType.EVOLUTION;

		    } else if (robotTypeIndex == robotType.RHINO.ordinal()) {
                mBotType = robotType.RHINO;
                setRhinoUIComponents();

            } else if (robotTypeIndex == robotType.CRAB.ordinal()) {
                mBotType = robotType.CRAB;

		    } else if (robotTypeIndex == robotType.GENERIC_ROBOT.ordinal()) {
		    	mBotType = robotType.GENERIC_ROBOT;
		    	setGenericRobotUIComponents();
		    }

            setUiListeners();


            // Fix the width and height of the icons that will go into the grid. This allows to have
            // the same icons as the real time manual control screen and not duplicate the icons.
            Resources r = getActivity().getResources();

            int size = -1;

            int smallestScreenWithDpCompat = getSmallestScreenWidthDp(getActivity());

            if (smallestScreenWithDpCompat >= 720) {
                size = 120;

            } else if (smallestScreenWithDpCompat >= 600) {
                size = 95;

            }else if (smallestScreenWithDpCompat >= 500) {
                size = 75;

            } else {
                size = 60;
            }

            if(size > 0) {
                gridView.setFixedChildrenHeight(size);
                gridView.setFixedChildrenWidth(size);
            }

            sendMovementsHandler = new Handler();
		}

        // Set the class that manage the sequences of movements storage
        scheduledMovementsFileManagement = new ScheduledMovementsFileManagement(getActivity(), this, mBotType);

        mToolTipFrameLayout = (ToolTipRelativeLayout) getActivity().findViewById(R.id.activity_main_tooltipframelayout);

        tipsManager = new TipsManager(getActivity(), mToolTipFrameLayout, this);
        tipsManager.initTips();

	}


    public static int getSmallestScreenWidthDp(Context context) {
        Resources resources = context.getResources();
        try {
            Field field = Configuration.class.getDeclaredField("smallestScreenWidthDp");
            return (Integer) field.get(resources.getConfiguration());
        } catch (Exception e) {
            // not perfect because reported screen size might not include status and button bars
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            int smallestScreenWidthPixels = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
            return Math.round(smallestScreenWidthPixels / displayMetrics.density);
        }
    }


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Retain this fragment across configuration changes.
		setRetainInstance(true);
	}


    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        waitsBetweenMovementsEnabledInPref = sharedPref.getBoolean(RoboPadConstants.ENABLE_WAITS_BETWEEN_MOVEMENTS_KEY, true);
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mSendControlsToArduinoTask != null) {
            sendMovementsHandler.removeCallbacks(mSendControlsToArduinoTask);
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


        // Resize the GridLayout depending on the number of icons (depending on the current robot
        // selected). In order not to get too much number of columns in little screen, it is limited
        // to two and scroll the  screen if there are more icons. If scroll is not needed the icons
        // are centered in the layout
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


    /**
     * Set the additional buttons if the current robot is the beetle
     */
	private void setBeetleUIComponents() {
		GridLayout controlsLayout = (GridLayout) getActivity().findViewById(R.id.type_of_movements_container);
		getActivity().getLayoutInflater().inflate(R.layout.scheduler_beetle_component, controlsLayout, true);
	}


    /**
     * Set the additional buttons if the current robot is the rhino
     */
	private void setRhinoUIComponents() {
        GridLayout controlsLayout = (GridLayout) getActivity().findViewById(R.id.type_of_movements_container);
        getActivity().getLayoutInflater().inflate(R.layout.scheduler_rhino_component, controlsLayout, true);

	}


    /**
     * Set the additional buttons if the current robot is the generic robot
     */
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


    /**
     * When the user press a button of a movement, it is added to the grid and to the list with all
     * the commands of the movements to send to the microcontrolller board.
     * @param drawableId the id of the drawable of the button clicked
     * @param movement the movement associated to that button
     */
    private void addButtonPressedToGrid(int drawableId, String movement) {
        ImageView view = (ImageView) ImageView.inflate(getActivity().getBaseContext(), R.layout.grid_view_item_layout, null);
        view.setImageResource(drawableId);
        gridView.addView(view);
        scheduledControls.add(movement);

    }


    /**
     * Method to add to te grid and to the list of movements, but when they are loaded from a file
     * not when the user press a button
     * @param movement the movement obtained from the stored file
     */
    private void addViewToTheGrid(String movement) {

        Integer iconId = null;

        if (movement.equals(RoboPadConstants.STOP_COMMAND)) {
            iconId = R.drawable.ic_stop_button;

        } else if (movement.equals(RoboPadConstants.UP_COMMAND)) {
            iconId = R.drawable.ic_up_button;

        } else if (movement.equals(RoboPadConstants.DOWN_COMMAND)) {
            iconId = R.drawable.ic_down_button;

        } else if (movement.equals(RoboPadConstants.LEFT_COMMAND)) {
            iconId = R.drawable.ic_left_button;

        } else if (movement.equals(RoboPadConstants.RIGHT_COMMAND)) {
            iconId = R.drawable.ic_right_button;

        } else if (movement.equals(RoboPadConstants.FULL_OPEN_STEP_COMMAND)) {
            iconId = R.drawable.ic_scheduler_full_open_claw;

        } else if (movement.equals(RoboPadConstants.OPEN_STEP_COMMAND)) {
            iconId = R.drawable.ic_scheduler_open_claw;

        } else if (movement.equals(RoboPadConstants.CLOSE_STEP_COMMAND)) {
            iconId = R.drawable.ic_scheduler_close_claw;

        } else if (movement.equals(RoboPadConstants.CHARGE_COMMAND)) {
            iconId = R.drawable.charge_button;

        } else if (movement.equals(RoboPadConstants.COMMAND_1)) {
            iconId = R.drawable.comand_button_1;

        } else if (movement.equals(RoboPadConstants.COMMAND_2)) {
            iconId = R.drawable.comand_button_2;

        } else if (movement.equals(RoboPadConstants.COMMAND_3)) {
            iconId = R.drawable.comand_button_3;

        } else if (movement.equals(RoboPadConstants.COMMAND_4)) {
            iconId = R.drawable.comand_button_4;

        } else if (movement.equals(RoboPadConstants.COMMAND_5)) {
            iconId = R.drawable.comand_button_5;

        } else if (movement.equals(RoboPadConstants.COMMAND_6)) {
            iconId = R.drawable.comand_button_6;

        }

        if(iconId != null) {
            ImageView view = (ImageView) ImageView.inflate(getActivity().getBaseContext(), R.layout.grid_view_item_layout, null);
            view.setImageResource(iconId);
            gridView.addView(view);

            scheduledControls.add(movement);
        }
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

            if(mBotType == robotType.BEETLE) {
                switch (v.getId()) {
                    case R.id.full_open_claw_button:
                        addButtonPressedToGrid(R.drawable.ic_scheduler_full_open_claw, RoboPadConstants.FULL_OPEN_STEP_COMMAND);
                        break;

                    case R.id.open_claw_button:
                        addButtonPressedToGrid(R.drawable.ic_scheduler_open_claw, RoboPadConstants.OPEN_STEP_COMMAND);
                        break;

                    case R.id.close_claw_button:
                        addButtonPressedToGrid(R.drawable.ic_scheduler_close_claw, RoboPadConstants.CLOSE_STEP_COMMAND);
                        break;
                }
            }

            if(mBotType == robotType.RHINO) {
                switch(v.getId()) {

                    case R.id.charge_button:
                        addButtonPressedToGrid(R.drawable.charge_button, RoboPadConstants.CHARGE_COMMAND);
                        break;

                }
            }

            if(mBotType == robotType.GENERIC_ROBOT) {
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
     * Listeners for the menu buttons
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
                    mSendControlsToArduinoTask = new MySendControlsToArduinoTask();
                    mSendControlsToArduinoTask.run();
                    break;

                case R.id.load_movements_button:
                    scheduledMovementsFileManagement.loadScheduledMovements();
                    break;

                case R.id.save_movements_button:
                    scheduledMovementsFileManagement.saveScheduledMovements(scheduledControls, false);
                    break;

                case R.id.remove_stored_movements_button:
                    scheduledMovementsFileManagement.deleteScheduledMovements();
                    break;

            }
        }
    };


    /**
     * Enable again the commands and menu buttons after sending all the movements to the robot
     */
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


    /**
     * Disable the commands and menu buttons when the app is sending the movements to the robot
     */
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

            mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.bluetooth_tip_text),
                    getActivity().findViewById(R.id.connect_button)).setOnToolTipViewClickedListener(onToolTipClicked);

            mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.scheduler_add_commands_text),
                    getActivity().findViewById(R.id.right_button)).setOnToolTipViewClickedListener(onToolTipClicked);

            currentTip = tips.ADD_COMMANDS;

        } else if (currentTip.equals(tips.ADD_COMMANDS)) {
            mToolTipFrameLayout.removeAllViews();

            mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.scheduler_grid_text),
                    getActivity().findViewById(R.id.tip_grid_view)).setOnToolTipViewClickedListener(onToolTipClicked);
//                    getActivity().findViewById(R.id.grid_view)).setOnToolTipViewClickedListener(onToolTipClicked);

            currentTip = tips.GRID;

        } else if (currentTip.equals(tips.GRID)) {
            mToolTipFrameLayout.removeAllViews();

            mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.scheduler_remove_all_text),
                    getActivity().findViewById(R.id.remove_all_button)).setOnToolTipViewClickedListener(onToolTipClicked);

            currentTip = tips.REMOVE_ALL;

        } else if (currentTip.equals(tips.REMOVE_ALL)) {
            mToolTipFrameLayout.removeAllViews();

            mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.scheduler_send_text),
                    getActivity().findViewById(R.id.send_movements_button)).setOnToolTipViewClickedListener(onToolTipClicked);

            currentTip = tips.SEND;

        } else if (currentTip.equals(tips.SEND)) {
            mToolTipFrameLayout.removeAllViews();

            mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.scheduler_load_text),
                    getActivity().findViewById(R.id.load_movements_button)).setOnToolTipViewClickedListener(onToolTipClicked);

            currentTip = tips.LOAD;

        } else if (currentTip.equals(tips.LOAD)) {
            mToolTipFrameLayout.removeAllViews();

            mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.scheduler_save_text),
                    getActivity().findViewById(R.id.save_movements_button)).setOnToolTipViewClickedListener(onToolTipClicked);

            currentTip = tips.SAVE;

        } else if (currentTip.equals(tips.SAVE)) {
            mToolTipFrameLayout.removeAllViews();

            mToolTipFrameLayout.showToolTipForView(TipsFactory.getTip(getActivity(), R.string.scheduler_remove_file_text),
                    getActivity().findViewById(R.id.remove_stored_movements_button)).setOnToolTipViewClickedListener(onToolTipClicked);

            currentTip = tips.DELETE_FILE;

        } else if (currentTip.equals(tips.DELETE_FILE)) {
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


    /***********************************************************************************************
     *************************  ScheduledMovementsFileManagementListener  **************************
     **********************************************************************************************/

    /**
     * Result after having clicked the save sequence button
     * @param success if the saving was succeeded or not
     */
    @Override
    public void onScheduledMovementsSaved(boolean success) {

        if(success) {
            Toast.makeText(getActivity(), R.string.save_succeeded, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), R.string.save_not_succeeded, Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * Result after having clicked the delete sequence button
     * @param success if the removing was succeeded or not
     */
    @Override
    public void onScheduledMovementsRemoved(boolean success) {

        if(success) {
            Toast.makeText(getActivity(), R.string.delete_succeeded, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), R.string.delete_not_succeeded, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Result after having clicked the load sequence button
     * @param loadedMovementsList list with the movements stored
     */
    @Override
    public void onScheduledMovementsLoaded(List<String> loadedMovementsList) {

        if(loadedMovementsList == null) {
            Toast.makeText(getActivity(), R.string.save_not_succeeded, Toast.LENGTH_SHORT).show();
            return;
        }

        int movementsSize = loadedMovementsList.size();
        if(movementsSize > 0) {
            for (int i = 0; i < movementsSize; i++) {
                addViewToTheGrid(loadedMovementsList.get(i));
            }
        }

    }


    /**************************************************************************************
	 **************************   MySendControlsToArduinoTask   ***************************
	 **************************************************************************************/

    /**
     * Class that sends the commands to the microcontroller board, with a delayed time
     * between them and with a stop between movements to show each movement better if this
     * configuration is set.
     */
	class MySendControlsToArduinoTask implements Runnable {

        @Override
        public void run() {
            Log.d(LOG_TAG, "currentControlIndex: " + currentControlIndex);

            if (sendStopCommand) {
                Log.i(LOG_TAG, "sendStopCommand");
				listener.onSendMessage(RoboPadConstants.STOP_COMMAND);
				sendStopCommand = false;

                if (mBotType == robotType.CRAB) {
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

                // Put a color around the icon when the robot is performing that movement
                currentChild.setBackgroundResource(R.drawable.turquoise_circle_default_button);

                if(currentChild.getBottom() > gridView.getHeight()) {
                    gridView.scrollTo(0, (int) (currentChild.getTop() - currentChild.getHeight()
                            - currentChild.getPaddingTop() - currentChild.getPaddingBottom()));
                }

                // Put the icon of the movement before this one with white color around
                if (currentControlIndex > 0) {
                    gridView.getChildAt(currentControlIndex - 1).setBackgroundColor(Color.WHITE);
                }

                // Enable the waits between movements only if it is enabled in the preferences menu
                if(waitsBetweenMovementsEnabledInPref) {
                    sendStopCommand = true;
                }

                currentControlIndex++;

                if (mBotType == robotType.CRAB) {
                    sendMovementsHandler.postDelayed(this, RoboPadConstants.CRAB_DELAY_BETWEEN_SCHEDULED_COMMANDS);

                } else {
                    sendMovementsHandler.postDelayed(this, RoboPadConstants.DELAY_BETWEEN_SCHEDULED_COMMANDS);
                }
			}

        }



	}
	
}
