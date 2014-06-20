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


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.bq.robotic.robopad_plusplus.R;
import com.bq.robotic.robopad_plusplus.listeners.ScheduledMovementsFileManagementListener;
import com.bq.robotic.robopad_plusplus.utils.RoboPadConstants.robotType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that manage the storage of the movement sequences scheduled in the
 * ScheduleRobotMovementFragment. It allows save sequences in an file, stored in the internal space
 * of memory of the app. It also allows you to load the sequences from that files, or delete them.
 */

public class ScheduledMovementsFileManagement {

    private ScheduledMovementsFileManagementListener mListener;
    private Context mContext;
    private robotType mBotType;


    // Debugging
    private static final String LOG_TAG = "ScheduledMovementsFileManagement";

    public ScheduledMovementsFileManagement(Context context, ScheduledMovementsFileManagementListener listener, robotType robotType) {
        mContext = context;
        mListener = listener;
        mBotType = robotType;

    }


    /**
     * Shows the dialog with the available files that contains scheduled movements sequences
     * and execute the task in order to get the movements from the file selected.
     */
    public void loadScheduledMovements() {
        final String[] allFilesAvailable = mContext.fileList();

        final String [] filteredFilesAvailable = filterByBotType(allFilesAvailable);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.load_scheduled_movements_title);

        if(filteredFilesAvailable.length > 0) {
            builder.setItems(filteredFilesAvailable, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    new LoadMovementsTask().execute(mBotType + "_" + filteredFilesAvailable[item]);
                }
            });

        } else {
            builder.setMessage(R.string.no_files_stored);
        }

//        builder.setIcon(R.drawable.ic_load);
        AlertDialog alert = builder.create();
        alert.show();

    }


    /**
     * This method shows the list with the existing files with movements, and delete the selected
     * file.
     */
    public void deleteScheduledMovements() {
        final String[] allFilesAvailable = mContext.fileList();

        final String [] filteredFilesAvailable = filterByBotType(allFilesAvailable);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.delete_scheduled_movements_title);

        if(filteredFilesAvailable.length > 0) {
        builder.setItems(filteredFilesAvailable, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                new DeleteMovementsTask().execute(mBotType + "_" + filteredFilesAvailable[item]);
            }
        });

        } else {
            builder.setMessage(R.string.no_files_stored);
        }

//        builder.setIcon(R.drawable.ic_load);
        AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * This method saves a sequence of movements scheduled in a file. Ask the user for a name for
     * the file and check if that name already exists. If it already exists ask the user if it
     * should be overwritten or not. If the user decides not to overwrite the file, the dialog asking
     * for a name is showed again.
     * @param movementsToSave the sequence of movements to storage
     */
    public void saveScheduledMovements(final List<String> movementsToSave, boolean wasErrorBefore) {
        // Set an EditText view to get user input
        final EditText input = new EditText(mContext);
        input.setTextColor(Color.BLACK);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.save_scheduled_movements_title)
                .setView(input)
//                .setIcon(R.drawable.ic_save)
                .setPositiveButton(mContext.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String selectedName = input.getText().toString();

                        if (selectedName == null || selectedName.trim().equals("")) {
                            saveScheduledMovements(movementsToSave, true);

                        } else {

                            selectedName = mBotType.name() + "_" + selectedName;

                            boolean exist = checkIfFilenameAlreadyExist(selectedName);

                            if (exist) {
                                showOverwriteFileDialog(selectedName, movementsToSave);
                            } else {
                                new SaveMovementsTask(selectedName, movementsToSave).execute();
                            }
                        }
                    }
                }).setNegativeButton(mContext.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                });

        if(wasErrorBefore) {
            input.setError(mContext.getString(R.string.invalid_name));
        }

        AlertDialog alert = builder.create();
        alert.show();

    }


    /**
     * Checks if the newFilename param, the name introduced by the user, is equals to another
     * existing filename
     * @param newFilename filename introduced by the user
     * @return if the filename already exists
     */
    private boolean checkIfFilenameAlreadyExist(String newFilename) {
        final String[] existingFilenames = mContext.fileList();

        for (String existingFilename : existingFilenames) {
            if(existingFilename.equals(newFilename)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Show only the movements files of the current type of robot, not all, in order not to load
     * for example a sequence with a claw movement when the user is scheduling the pollywog.
     * It also remove the bot type from the filename for showing it to the user clearer.
     * @param allFiles in the app folder
     * @return the files filtered.
     */
    private String [] filterByBotType(String [] allFiles) {

        List<String> filteredFilesList = new ArrayList<String>();
        String botTypeSubstring = mBotType.name() + "_";

        for (String filename : allFiles) {
            if(filename.startsWith(botTypeSubstring)) {
                filteredFilesList.add(filename.substring(botTypeSubstring.length()));
            }
        }

        String [] filteredFilesArray = new String[filteredFilesList.size()];
        filteredFilesList.toArray(filteredFilesArray);

        return filteredFilesArray;

    }


    /**
     * As a file with the same name already exists ask the user if it should be overwritten. Shows
     * the dialog to enter a name for the file again if the user decide not to overwrite the file
     * @param selectedName name entered by the user
     * @param movementsToSave movement sequence to save
     */
    private void showOverwriteFileDialog(final String selectedName, final List<String> movementsToSave) {
        new AlertDialog.Builder(mContext)
                .setTitle("Filename already exists")
                .setMessage("A file with this name already exists. Do you want to overwrite it?")
                .setPositiveButton(mContext.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        new SaveMovementsTask(selectedName, movementsToSave).execute();
                    }
                }).setNegativeButton(mContext.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                saveScheduledMovements(movementsToSave, false);
            }
        }).show();
    }


//    private String removeExtension(String filename) {
//        int indexOfExtension = filename.lastIndexOf(".txt");
//        return filename.substring(0, indexOfExtension + 1);
//
//    }


    /***********************************************************************************************
     *                                                                                             *
     *                                      ASYNC TASK CLASSES                                     *
     *                                                                                             *
     **********************************************************************************************/

    /**
     * Async task for reading the file with the stored sequence of scheduled movements. When
     * finished, it send the result through the listener to the calling fragment.
     */
    class LoadMovementsTask extends AsyncTask<String, Void, List<String>> {

        protected List<String> doInBackground(String... params) {

            String fileSelected = params[0];

            if(fileSelected == null) {
                return null;
            }

            BufferedReader br = null;

            List<String> loadedMovements = new ArrayList<String>();

            try {
                br = new BufferedReader(
                        new InputStreamReader(mContext.openFileInput(fileSelected)));

                String line;

                while ((line = br.readLine()) != null) {
                    loadedMovements.add(line);
                }


            } catch (FileNotFoundException e) {
                Log.e(LOG_TAG, "Exception in onLoadClicked: " + e);
                loadedMovements = null;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Exception in onLoadClicked: " + e);
                loadedMovements = null;
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return loadedMovements;
        }

        // Notify to the listener (the calling activity) that the task was finalized
        protected void onPostExecute(List<String> loadedMovements) {
            mListener.onScheduledMovementsLoaded(loadedMovements);
        }
    }


    /**
     * Async task for reading the file with the stored sequence of scheduled movements. When
     * finished, it send the result through the listener to the calling fragment.
     */
    class DeleteMovementsTask extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... params) {

            String fileSelected = params[0];

            if(fileSelected == null) {
                return false;
            }

            Boolean success = false;

            try {

                success = mContext.deleteFile(fileSelected);

            } catch (Exception e) {
                Log.e(LOG_TAG, "Exception in DeleteMovementsTask: " + e);
            }

            return success;
        }

        // Notify to the listener (the calling activity) that the task was finalized
        protected void onPostExecute(Boolean success) {
            mListener.onScheduledMovementsRemoved(success);
        }
    }


    /**
     * Async task for saving a sequence of scheduled movements in to a file. When
     * finished, it send the result through the listener to the calling fragment.
     */
    class SaveMovementsTask extends AsyncTask<Void, Void, Boolean> {

        String nameSelected = null;
        List<String> movementsToSave;

        public SaveMovementsTask(String nameSelected, List<String> movementsToSave) {
            this.nameSelected = nameSelected;
            this.movementsToSave = movementsToSave;
        }

        protected Boolean doInBackground(Void... params) {

            if(movementsToSave == null || nameSelected == null) {
                return false;
            }

            OutputStreamWriter fout = null;
            BufferedWriter bwriter = null;
            Boolean success = false;

            try {

                fout = new OutputStreamWriter(
                        mContext.openFileOutput(nameSelected, Context.MODE_PRIVATE));
                bwriter = new BufferedWriter(fout);

                for (String movement : movementsToSave) {
                    bwriter.write(movement);
                    bwriter.newLine();
                }

                success = true;

            } catch (FileNotFoundException e) {
                Log.e(LOG_TAG, "Exception in SaveMovementsTask: " + e);
                success = false;
                //                Toast.makeText(this, "FileNotFoundException", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Exception in SaveMovementsTask: " + e);
                success = false;
                //                Toast.makeText(this, "IOException", Toast.LENGTH_LONG).show();
            } finally {
                if (fout != null) {
                    try {
                        if(bwriter != null) {
                            bwriter.close();
                        }
                        fout.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return success;
        }

        // Notify to the listener (the calling activity) that the task was finalized
        protected void onPostExecute(Boolean success) {
            mListener.onScheduledMovementsSaved(success);
        }
    }

}
