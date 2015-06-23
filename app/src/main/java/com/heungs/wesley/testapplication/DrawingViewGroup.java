package com.heungs.wesley.testapplication;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wesley on 2015-06-19.
 */
public class DrawingViewGroup extends ZoomViewGroup {
    private boolean isEditingText = false;

    private Map<EditText, Point> ets;
    private EditText currentEdit;

    private DrawingView dv;

    public DrawingViewGroup(Context context, AttributeSet attr) {
        super(context, attr);

        setClickable(true);
        setFocusableInTouchMode(true);

        ets = new HashMap<EditText, Point>();

        dv = new DrawingView(context, attr);
        dv.setZ(0);
        addView(dv);
    }

    public void onLayout(boolean changed, int l, int t, int r, int b) {
        for (Map.Entry<EditText, Point> e : ets.entrySet()) {
            EditText k = e.getKey();
            Point v = e.getValue();
            if (k.getVisibility() == GONE) {
                k.layout(0, 0, 0, 0);
            } else {
                int x = (int) v.x;
                int y = (int) v.y;
                int mw = k.getMeasuredWidth();
                int mh = k.getMeasuredHeight();
                k.layout(x, y, x + mw, y + mh);
            }
        }
        if (dv.getVisibility() != GONE) {
            dv.layout(l, t, l + dv.getMeasuredWidth(), t + dv.getMeasuredHeight());
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                final double distance = Math.pow(mLastTouchX - x, 2) + Math.pow(mLastTouchY - y, 2);
                if (distance < 50 * 50) {
                    if (isEditingText) {
                        // Stop editing
                        // Lose focus
                        currentEdit.clearFocus();
                        // Hide keyboard
                        InputMethodManager imm = (InputMethodManager) (getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(currentEdit.getWindowToken(), 0);
                        }
                        // Destroy empty text boxes
                        if (currentEdit.getText().toString().isEmpty()) {
                            ets.remove(currentEdit);
                            removeView(currentEdit);
                            invalidate();
                        }
                        // Reset for another text box
                        currentEdit = null;
                        isEditingText = false;
                    } else {
                        // Start editing
                        // Find an existing edit text
                        for (EditText e : ets.keySet()) {
                            Rect hr = new Rect();
                            e.getHitRect(hr);
                            if (hr.contains(x, y)) {
                                currentEdit = e;
                            }
                        }
                        // Otherwise... create a new EditText
                        if (currentEdit == null) {
                            currentEdit = new EditText(getContext(), null);
                            currentEdit.setHint("Words~");
                            currentEdit.setZ(1);
                            ets.put(currentEdit, new Point((int) mLastTouchX, (int) mLastTouchY));
                            addView(currentEdit);
                        }
                        currentEdit.requestFocus();
                        post(new Runnable() {
                            public void run() {
                                InputMethodManager imm = (InputMethodManager) (getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                                if (imm != null) {
                                    imm.showSoftInput(currentEdit, 0);
                                }
                            }
                        });
                        isEditingText = true;
                    }
                    invalidate();
                    return super.onInterceptTouchEvent(ev) | true;
                }
        }

        return super.onInterceptTouchEvent(ev);
    }
}
