package com.bq.robotic.robopad_plusplus.utils;


import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.bq.robotic.robopad_plusplus.R;

public class RobotConnectionsPopupWindow {

    private ScaleImageView popupView;

    // Debugging
    private static final String LOG_TAG = "RobotConnectionsPopupWindow";


    public RobotConnectionsPopupWindow(RoboPadConstants.robotType botType, Context context) {

        LayoutInflater layoutInflater
                = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        popupView = (ScaleImageView) layoutInflater.inflate(R.layout.popup_pin_connections, null);

        switch (botType) {

            case POLLYWOG:
                popupView.setImageResource(R.drawable.pollywog_pins);
                break;

            case BEETLE:
                popupView.setImageResource(R.drawable.beetle_pins);
                break;

        }
    }

    public PopupWindow getPopupWindow() {

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        // Needed for dismiss the popup window when clicked outside the popup window
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        popupWindow.setAnimationStyle(R.style.popup_animation);

        // Clear the default translucent background
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        return popupWindow;
    }

}
