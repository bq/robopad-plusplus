package com.bq.robotic.robopad_plusplus.utils;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.bq.robotic.robopad_plusplus.R;
import com.nhaarman.supertooltips.ToolTip;

/**
 * This class provide a default tip, given the text you wants to use. It uses the SuperToolTips
 * library (https://github.com/nhaarman/supertooltips).
 * It uses a custom layout defined in custom_tooltip layout
 */

public class TipsFactory {

    public static ToolTip getTip(Context context, String tipText) {

        TextView tipContent = (TextView) LayoutInflater.from(context).inflate(R.layout.custom_tooltip, null);
        tipContent.setText(tipText);

        // The animation must be null because of a bug in the library at least with the compatibility libraries
        ToolTip toolTip = new ToolTip()
                .withContentView(tipContent)
                .withColor(context.getResources().getColor(R.color.holo_blue_dark))
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);

        return toolTip;
    }


    public static ToolTip getTip(Context context, int tipTextId) {

        TextView tipContent = (TextView) LayoutInflater.from(context).inflate(R.layout.custom_tooltip, null);
        tipContent.setText(tipTextId);

        // The animation must be null because of a bug in the library at least with the compatibility libraries
        ToolTip toolTip = new ToolTip()
                .withContentView(tipContent)
                .withColor(context.getResources().getColor(R.color.holo_blue_dark))
                .withAnimationType(ToolTip.AnimationType.FROM_TOP); 

        return toolTip;
    }

}
