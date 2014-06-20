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

package com.bq.robotic.robopad_plusplus.listeners;

import java.util.List;


/**
 * Listener for the scheduler dialog
 */

public interface ScheduledMovementsFileManagementListener {

    /**
     * Result after having clicked the save sequence button
     * @param success if the saving was succeeded or not
     */
	void onScheduledMovementsSaved(boolean success);


    /**
     * Result after having clicked the delete sequence button
     * @param success if the removing was succeeded or not
     */
	void onScheduledMovementsRemoved(boolean success);


    /**
     * Result after having clicked the load sequence button
     * @param loadedMovementsList list with the movements stored
     */
	void onScheduledMovementsLoaded(List<String> loadedMovementsList);
}
