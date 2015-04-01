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

package com.bq.robotic.robopad_plusplus.utils;


public class RoboPadConstants {
	
    // Debugging
    public static final boolean D = true;
    
    public static final long CLICK_SLEEP_TIME = 150;
    public static final long DELAY_BETWEEN_SCHEDULED_COMMANDS = 800;
    public static final long CRAB_DELAY_BETWEEN_SCHEDULED_COMMANDS = 4000;

    public static enum robotType {POLLYWOG, BEETLE, EVOLUTION, RHINO, CRAB, GENERIC_ROBOT}
    public static enum robotState {MANUAL_CONTROL, LINE_FOLLOWER, LIGHT_AVOIDER, OBSTACLES_AVOIDER}

    
    /**
     * Splash screen
     */
    public static final int SPLASH_DISPLAY_TIME = 2000; /* 2 seconds */


    /**
     * Shared preferences
     */
    public static final String WAS_ENABLING_BLUETOOTH_ALLOWED_KEY = "pref_automatic_bluetooth_allowed";
    public static final String ENABLE_WAITS_BETWEEN_MOVEMENTS_KEY = "pref_scheduler_wait_between_movements";
    public static final String ENABLE_BLUETOOTH_KEY = "pref_bluetooth_enable";
    public static final String SHOW_TIPS_KEY = "help_options";
    public static final String POLLYWOG_FIRST_TIME_TIPS_KEY = "pollywog_first_time_tips_key";
    public static final String BEETLE_FIRST_TIME_TIPS_KEY = "beetle_first_time_tips_key";
    public static final String RHINO_FIRST_TIME_TIPS_KEY = "rhino_first_time_tips_key";
    public static final String CRAB_FIRST_TIME_TIPS_KEY = "crab_first_time_tips_key";
    public static final String GENERIC_ROBOT_FIRST_TIME_TIPS_KEY = "generic_robot_first_time_tips_key";
    public static final String SCHEDULER_MOVEMENTS_FIRST_TIME_TIPS_KEY = "scheduler_movements_first_time_tips_key";
    public static final String CURRENT_TIP_KEY = "current_tip_key";
    public static enum showTipsValues {NEVER, FIRST_TIME, ALWAYS}


    /**
     * Select robot
     */
    public static final String ROBOT_SELECTED_KEY = "robot_selected";


    /**
     * Beetle robot
     */
    public static enum Claw_next_state {OPEN_STEP, CLOSE_STEP, FULL_OPEN};
    public static final int MIN_CLOSE_CLAW_POS = 55;
    public static final int MAX_OPEN_CLAW_POS = 10;
    public static final int INIT_CLAW_POS = 30;
    public static final int CLAW_STEP = 3;
    //    public static final int CLAW_STEP = 5;
    public static final String CLAW_COMMAND = "C";
    public static final String FULL_OPEN_STEP_COMMAND = "F";
    public static final String OPEN_STEP_COMMAND = "O";
    public static final String CLOSE_STEP_COMMAND = "T";
  
    
    /**
     * Commands to send to the Arduino
     */  
    //FIXME: Put in comments the correct pins
    public static final String UP_COMMAND = "U"; // servo digital port both wheels, left[pin 6, value = 0], right[pin 9, value = 180]
    public static final String DOWN_COMMAND = "D"; // servo digital port both wheels, left[pin 6, value = 180], right[pin 9, value = 0]
    public static final String LEFT_COMMAND = "L"; // servo digital port both wheels, left[pin 6, value = 90], right[pin 9, value = 0] //with 90 it is stop
    public static final String RIGHT_COMMAND = "R"; // servo digital port both wheels, left[pin 6, value = 0], right[pin 9, value = 90]
    public static final String STOP_COMMAND = "S"; //servo digital port both, stop both


    /**
     * Default state modes of the robots
     */
    public static final String MANUAL_CONTROL_MODE_COMMAND = "M";
    public static final String LINE_FOLLOWER_MODE_COMMAND = "I";
    public static final String LIGHT_AVOIDER_MODE_COMMAND = "G";
    public static final String OBSTACLES_FOLLOWER_MODE_COMMAND = "B";

    
    /**
     * Rhino Robot
     */
    public static final String CHARGE_COMMAND = "C";


    /**
     * Crab Robot
     */
    public static final int MIN_AMPLITUDE = 0;
    public static final int MAX_AMPLITUDE = 40;
    public static final int DEFAULT_AMPLITUDE = 20;
    public static final int MIN_PERIOD = 1000;
    public static final int MAX_PERIOD = 8000;
    public static final int DEFAULT_PERIOD = 2000;
    public static final int MIN_PHASE = -90;
    public static final int MAX_PHASE = 90;
    public static final int DEFAULT_PHASE = -90;
    public static final String LEFT_AMPLITUDE_COMMAND = "AL";
    public static final String RIGHT_AMPLITUDE_COMMAND = "AR";
    public static final String PERIOD_COMMAND = "T";
    public static final String PHASE_COMMAND = "F";
    public static final String RESET_COMMAND = "I";

    
    /**
     * Generic Robot
     */
    public static final String COMMAND_1 = "1";
    public static final String COMMAND_2 = "2";
    public static final String COMMAND_3 = "3";
    public static final String COMMAND_4 = "4";
    public static final String COMMAND_5 = "5";
    public static final String COMMAND_6 = "6";
    
    
    /**
     * Schedule robot movements
     */
    public static final String ROBOT_TYPE_KEY = "robot_type_key";


    /**
     * Save back stack key
     */
    public static final String CURRENT_ROBOT_BACK_STACK_KEY = "current_robot_back_stack_key";
}
