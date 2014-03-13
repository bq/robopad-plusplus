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

package com.bq.robotic.robopad_plusplus;


public class RoboPadConstants {
	
    // Debugging
    public static final boolean D = true;
    
    public static final long CLICK_SLEEP_TIME = 150;
    
    public static enum robotType {POLLYWOG, BEETLE, RHINO, GENERIC_ROBOT};
    
    public static final String COMMAND_DIVISOR = "_";
    
    
    /**
     * Splash screen
     */
    public static final int SPLASH_DISPLAY_TIME = 2000; /* 2 seconds */
    
    
    /**
     * Beetle robot
     */
    public static enum Claw_next_state {OPEN_STEP, CLOSE_STEP, FULL_OPEN};    
    public static int MIN_CLOSE_CLAW_POS = 55;
    public static int MAX_OPEN_CLAW_POS = 10;
    public static final int INIT_CLAW_POS = 30;
    public static int CLAW_STEP = 5;
    public static String CLAW_COMMAND = "_C";
  
    
    /**
     * Pins renacuajo bot! revise for each robot
     */  
    //FIXME: Put in comments the correct pins
    public static final String UP_COMMAND = "U"; // servo digital port both wheels, left[pin 4, value = 0], right[pin 7, value = 180]
    public static final String DOWN_COMMAND = "D"; // servo digital port both wheels, left[pin 4, value = 180], right[pin 7, value = 0]
    public static final String LEFT_COMMAND = "L"; // servo digital port both wheels, left[pin 4, value = 90], right[pin 7, value = 0] //with 90 it is stop
    public static final String RIGHT_COMMAND = "R"; // servo digital port both wheels, left[pin 4, value = 0], right[pin 7, value = 90]
    public static final String STOP_COMMAND = "S"; //servo digital port both, stop both
    
    
    /**
     * Rhino Robot
     */
    public static String CHARGE_COMMAND = "C";
    
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
}
