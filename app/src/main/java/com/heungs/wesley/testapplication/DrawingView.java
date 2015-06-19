package com.heungs.wesley.testapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {
    //drawing path
    private Path drawPath;
    private Paint drawPaint;
    //canvas
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

    // Cursor
    private float cx;
    private float cy;
    private Paint cPaint;

    public DrawingView(Context context, AttributeSet attr) {
        super(context, attr);
        setupDrawing();
    }

    private void setupDrawing() {
        // Init
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(Color.parseColor("black"));
        // Path properties
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        cx = 0;
        cy = 0;
        cPaint = new Paint();
        cPaint.setColor(Color.parseColor("red"));
        cPaint.setAntiAlias(true);
        cPaint.setStrokeWidth(20);
        cPaint.setStyle(Paint.Style.FILL);
        cPaint.setStrokeJoin(Paint.Join.ROUND);
        cPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        cx = event.getX();
        cy = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(cx, cy);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(cx, cy);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBitmap, 0, 0, drawPaint);
        canvas.drawPath(drawPath, drawPaint);
        canvas.drawCircle(cx, cy, 5, cPaint);
    }
}