package com.heungs.wesley.testapplication;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by wesley on 2015-06-19.
 */
public class DrawingViewGroup extends ZoomViewGroup {
    private boolean isEditingText = false;
    private static float INVALID_COORDS = -1;
    private float px = INVALID_COORDS;
    private float py = INVALID_COORDS;

    private Set<EditText> ets;

    private EditText et;
    private int etx;
    private int ety;


    private DrawingView dv;

    public DrawingViewGroup(Context context, AttributeSet attr) {
        super(context, attr);

        setClickable(true);
        setFocusableInTouchMode(true);

        ets = new HashSet<EditText>();

        et = new EditText(context, attr);
        et.setVisibility(GONE);
        et.setHint("Words~");
        et.setZ(1);
        addView(et);
        dv = new DrawingView(context, attr);
        dv.setZ(0);
        addView(dv);
    }

    public void onLayout(boolean changed, int l, int t, int r, int b) {
        if (et.getVisibility() == GONE) {
            et.layout(0, 0, 0, 0);
        } else {
            et.layout(etx, ety, etx + et.getMeasuredWidth(), ety + et.getMeasuredHeight());
        }
        if (dv.getVisibility() != GONE) {
            dv.layout(l, t, l + dv.getMeasuredWidth(), t + dv.getMeasuredHeight());
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        Rect etBB = new Rect();
        et.getHitRect(etBB);

        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (ev.getPointerCount() > 1 || etBB.contains(Math.round(x), Math.round(y))) {
                    px = INVALID_COORDS;
                    py = INVALID_COORDS;
                } else {
                    px = x;
                    py = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d("mydebug", "Distance: " + Math.sqrt(Math.pow(px - ev.getX(), 2) + Math.pow(py - ev.getY(), 2)));
                if (Math.sqrt(Math.pow(px - x, 2) + Math.pow(py - y, 2)) < 50) {
                    isEditingText = !isEditingText;
                    if (isEditingText) {
                        etx = Math.round(px);
                        ety = Math.round(py);
                        et.setVisibility(VISIBLE);
                        et.requestFocus();
                        post(new Runnable() {
                            public void run() {
                                InputMethodManager imm = (InputMethodManager) (getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                                if (imm != null) {
                                    imm.showSoftInput(et, 0);
                                }
                            }
                        });
                    } else {
                        et.clearFocus();
                        et.setVisibility(GONE);
                        InputMethodManager imm = (InputMethodManager) (getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                        }
                        // Process the text
                        et.setText("");
                    }
                    invalidate();
                    Log.d("mydebug", "isEditingText=" + isEditingText);
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }
}
