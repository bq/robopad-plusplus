package com.bq.robotic.robopad_plusplus.utils;


import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bq.robotic.robopad_plusplus.R;

public class CustomPreferenceCategory extends PreferenceCategory {

    public CustomPreferenceCategory(Context context) {
        super(context);
    }

    public CustomPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPreferenceCategory(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * We catch the view after its creation, and before the activity will use it, in order to make our changes
     * @param parent
     * @return
     */
    @Override
    protected View onCreateView(ViewGroup parent) {
        // And it's just a TextView!
        TextView categoryTitle =  (TextView)super.onCreateView(parent);
        categoryTitle.setTextColor(getContext().getResources().getColor(R.color.holo_turquoise));

        return categoryTitle;
    }
}