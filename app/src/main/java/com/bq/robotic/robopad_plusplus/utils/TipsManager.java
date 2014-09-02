package com.bq.robotic.robopad_plusplus.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;

import com.bq.robotic.robopad_plusplus.fragments.BeetleFragment;
import com.bq.robotic.robopad_plusplus.fragments.CrabFragment;
import com.bq.robotic.robopad_plusplus.fragments.GenericRobotFragment;
import com.bq.robotic.robopad_plusplus.fragments.PollywogFragment;
import com.bq.robotic.robopad_plusplus.fragments.RhinoFragment;
import com.bq.robotic.robopad_plusplus.fragments.ScheduleRobotMovementsFragment;
import com.bq.robotic.robopad_plusplus.listeners.TipsManagerListener;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;

public class TipsManager {

    private Context mContext;
    private ToolTipRelativeLayout mToolTipFrameLayout;
    private boolean isLastTipToShow = true;
    private TipsManagerListener listener;

    // Debugging
    private static final String LOG_TAG = "TipsManager";


    public TipsManager(Context mContext, ToolTipRelativeLayout mToolTipFrameLayout, TipsManagerListener listener) {

        this.mContext = mContext;
        this.mToolTipFrameLayout = mToolTipFrameLayout;
        this.listener = listener;

    }


    public void initTips() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        int showTipsValue = Integer.parseInt(sharedPref.getString(RoboPadConstants.SHOW_TIPS_KEY, String.valueOf(RoboPadConstants.showTipsValues.FIRST_TIME.ordinal())));

        // If never we don't do anything
        if (showTipsValue == RoboPadConstants.showTipsValues.NEVER.ordinal()) {
            return;
        }

        if(mToolTipFrameLayout == null) {
            return;
        }

        if (showTipsValue == RoboPadConstants.showTipsValues.FIRST_TIME.ordinal()) {
            checkShowTipsIfFirstTime();

        } else if (showTipsValue == RoboPadConstants.showTipsValues.ALWAYS.ordinal()) {
            enableToolTipListener();
            showTips();
        }
    }


    /**
     * When the user has selected the preference of showing the tips only the first time, must be
     * the first time for each robot screen, because each one has itself actions available. So,
     * this method checks if it is the first time the user enters in this current robot or not.
     */
    private void checkShowTipsIfFirstTime() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());

        if(listener instanceof PollywogFragment) {

            if(sharedPref.getBoolean(RoboPadConstants.POLLYWOG_FIRST_TIME_TIPS_KEY, true)) {
                writeInSharedPreferencesEditor(RoboPadConstants.POLLYWOG_FIRST_TIME_TIPS_KEY, false);
                enableToolTipListener();
                showTips();
            }

        } else if(listener instanceof BeetleFragment) {

            if(sharedPref.getBoolean(RoboPadConstants.BEETLE_FIRST_TIME_TIPS_KEY, true)) {
                writeInSharedPreferencesEditor(RoboPadConstants.BEETLE_FIRST_TIME_TIPS_KEY, false);
                enableToolTipListener();
                showTips();
            }

        } else if(listener instanceof RhinoFragment) {

            if(sharedPref.getBoolean(RoboPadConstants.RHINO_FIRST_TIME_TIPS_KEY, true)) {
                writeInSharedPreferencesEditor(RoboPadConstants.RHINO_FIRST_TIME_TIPS_KEY, false);
                enableToolTipListener();
                showTips();
            }

        } else if(listener instanceof CrabFragment) {

            if(sharedPref.getBoolean(RoboPadConstants.CRAB_FIRST_TIME_TIPS_KEY, true)) {
                writeInSharedPreferencesEditor(RoboPadConstants.CRAB_FIRST_TIME_TIPS_KEY, false);
                enableToolTipListener();
                showTips();
            }

        } else if(listener instanceof GenericRobotFragment) {

            if(sharedPref.getBoolean(RoboPadConstants.GENERIC_ROBOT_FIRST_TIME_TIPS_KEY, true)) {
                writeInSharedPreferencesEditor(RoboPadConstants.GENERIC_ROBOT_FIRST_TIME_TIPS_KEY, false);
                enableToolTipListener();
                showTips();
            }

        } else if(listener instanceof ScheduleRobotMovementsFragment) {

        if(sharedPref.getBoolean(RoboPadConstants.SCHEDULER_MOVEMENTS_FIRST_TIME_TIPS_KEY, true)) {
            writeInSharedPreferencesEditor(RoboPadConstants.SCHEDULER_MOVEMENTS_FIRST_TIME_TIPS_KEY, false);
            enableToolTipListener();
            showTips();
        }
    }
    }


    /**
     * Write in the shared preference editor
     * @param key key of the preference
     * @param value of the preference
     */
    private void writeInSharedPreferencesEditor(String key, boolean value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    /**
     * Enable the listener of the tips layout only if the fragment has to show the tips
     */
    private void enableToolTipListener() {
        mToolTipFrameLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTips();
            }
        });
    }


    /**
     * Show the tips. When the last tip is shown, the mToolTipFrameLayout must be removed in order
     * to let the user clicks in the other buttons
     */
    protected void showTips() {
        listener.onShowNextTip();

        // if mToolTipFrameLayout was null, this method is never called, so it isn't needed to check
        if (isLastTipToShow) {
            ViewGroup parent = (ViewGroup) mToolTipFrameLayout.getParent();

            if(parent != null) {
                parent.removeView(mToolTipFrameLayout);
            }
        }
    }


    public void setLastTipToShow(boolean isLastTipToShow) {
        this.isLastTipToShow = isLastTipToShow;
    }

}
