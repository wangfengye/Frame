package com.example.simple;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by maple on 2019/8/11 11:22
 */
public class TouchTestView  extends View {
    public static final String TAG = "TouchTestView";

    public TouchTestView(Context context) {
        super(context);
    }

    public TouchTestView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: ");
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawARGB(50,255,255,0);
    }
}
