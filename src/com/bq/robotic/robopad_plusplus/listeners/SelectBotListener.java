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

package com.bq.robotic.robopad_plusplus.listeners;

import com.bq.robotic.robopad_plusplus.RoboPadConstants.robotType;

/**
 * Listener for the select robot fragment to communicate with the fragment activity that manage the 
 * bluetooth connection.
 */

public interface SelectBotListener {

	/**
	 * Callback from the SelectBotFragment for changing the bottom title bar.
	 * 
	 * @param titleId The text resource id
	 */
	void onSetFragmentTitle(int titleId);
	
	
	/**
	 * Callback from the SelectBotFragment for showing the selected type of robot fragment 
	 * 
	 * @param botType The type of robot selected by the user
	 */
	void onRobotSelected(robotType botType);
}
