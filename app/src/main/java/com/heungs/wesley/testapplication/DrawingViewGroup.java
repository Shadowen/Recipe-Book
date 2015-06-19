package com.heungs.wesley.testapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by wesley on 2015-06-19.
 */
public class DrawingViewGroup extends ZoomViewGroup {
    public DrawingViewGroup(Context context, AttributeSet attr) {
        super(context, attr);

        DrawingView dv = new DrawingView(context, attr);
        addView(dv);
    }
}
