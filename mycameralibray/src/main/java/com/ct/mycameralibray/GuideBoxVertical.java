package com.ct.mycameralibray;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


public class GuideBoxVertical extends View {
    private Rect rectBox;
    int width, height;
    Paint paint;
    int left = 0, right = 0, top = 0, bottom = 0;

    public GuideBoxVertical(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#D5D8DC"));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStrokeWidth(5);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);

        if (width > height * CameraFragment.Ratio.getASPECT_RATIO()) {
            width = (int) (height * CameraFragment.Ratio.getASPECT_RATIO() + .5);
        } else {
            height = (int) (width / CameraFragment.Ratio.getASPECT_RATIO() + .5);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    public void onDraw(Canvas canvas) {
        rectBox = new Rect();
        left = 20;
        right = width - left;
        top = height /50;
        bottom = height - top;
        rectBox.set(left, top, right, bottom);
        canvas.drawRect(rectBox, paint);
        super.onDraw(canvas);
    }
}