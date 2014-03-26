package com.bq.robotic.robopad_plusplus.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.bq.robotic.robopad_plusplus.RoboPadConstants;
import com.bq.robotic.robopad_plusplus.listeners.SchedulerMenuDialogListener;

public class SchedulerMenuDialogFragment extends DialogFragment {
	
	private SharedPreferences preferences;
	
	private SchedulerMenuDialogListener listener;
    private String title;
    private String[] list;

    public SchedulerMenuDialogFragment(SchedulerMenuDialogListener listener, List<String> list, String title) {
        this.listener = listener;
        this.list = (String[]) list.toArray();
        this.title = title;
        
//        preferences = getSharedPreferences(RoboPadConstants.SAVED_SCHEDULES_TABLE, MODE_PRIVATE);
    }
    

    public SchedulerMenuDialogFragment() {}


	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setCancelable(false)
                .setItems(list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                    	
                    	loadScheduler(item);
                        
                        getDialog().dismiss(); 
                        SchedulerMenuDialogFragment.this.dismiss();

                    }
                }).create();

    }
	
	
	private void loadScheduler(int item) {
		
	}
	
	
	private void saveScheduler() {
		
		try
		{
		    File ruta_sd = getActivity().getFilesDir();
		 
		    File f = new File(ruta_sd.getAbsolutePath(), "prueba_sd.txt");
		 
		    BufferedReader fin =
		        new BufferedReader(
		            new InputStreamReader(
		                new FileInputStream(f)));
		 
		    String texto = fin.readLine();
		    fin.close();
		}
		catch (Exception ex)
		{
		    Log.e("Ficheros", "Error al leer fichero desde tarjeta SD");
		}
		
	}

}
