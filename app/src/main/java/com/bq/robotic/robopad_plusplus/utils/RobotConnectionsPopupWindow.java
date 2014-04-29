package com.bq.robotic.robopad_plusplus.utils;


import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bq.robotic.robopad_plusplus.R;

public class RobotConnectionsPopupWindow {

    private RoboPadConstants.robotType botType;
    private Context context;


    public RobotConnectionsPopupWindow(RoboPadConstants.robotType botType, Context context) {
        this.botType = botType;
        this.context = context;
    }


    public PopupWindow getPopupWindow() {
        LayoutInflater layoutInflater
                = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_pin_connections, null);

        TextView pinExplanationText = (TextView) popupView.findViewById(R.id.pin_explanation_text);
        pinExplanationText.setText(Html.fromHtml(context.getString(R.string.pollywog_pin_explanation)));

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
//                    popupWindow.setBackgroundDrawable(new BitmapDrawable(R.drawable.pollywogPinsConnections));

//        // Displaying the popup at the specified location, + offsets.
//        popupWindow.showAtLocation(layout, Gravity.CENTER_VERTICAL | Gravity.LEFT,
//                pinExplanationButton.getRight() - pinExplanationButton.getPaddingRight(),
//                pinExplanationButton.getPaddingTop());

        return popupWindow;
    }

}
