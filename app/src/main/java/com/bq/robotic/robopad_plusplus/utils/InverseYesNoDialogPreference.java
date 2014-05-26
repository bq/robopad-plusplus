package com.bq.robotic.robopad_plusplus.utils;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * The InverseYesNoDialogPreference will display a dialog, and will persist the
 * <code>true</code> when pressing the positive button and <code>false</code>
 * otherwise. It will persist to the android:key specified in xml-preference.
 */
public class InverseYesNoDialogPreference extends DialogPreference {

    public InverseYesNoDialogPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public InverseYesNoDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        // Save the inverse state, if user press the positive button, store false, and viceversa
        persistBoolean(!positiveResult);
    }
}
