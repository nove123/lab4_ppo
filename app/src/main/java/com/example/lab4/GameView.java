package com.example.lab4;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GameView extends View {
    private Paint mPaint;
    Rect platform;
    int platformWidth;
    float platformX;
    float platformSpeed;

    public void setPlatformX(float platformX) {
        if (platformX < getWidth() - platformWidth / 2 && platformX > platformWidth / 2) {
            this.platformX = platformX;
        }
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        initPlatform();
        super.onLayout(changed, left, top, right, bottom);
    }

    void initPlatform() {
        platform = new Rect();
        platformWidth = 200;
        platformX = getWidth() / 2;
        platformSpeed = 6;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        platform.set(Math.round(platformX - platformWidth / 2), getHeight() - 100,
                Math.round(platformX + platformWidth / 2), getHeight() - 50);
        canvas.drawRect(platform, mPaint);
        postInvalidateOnAnimation();
    }

    public void movePlatform(float value) {
        setPlatformX(platformX + value * platformSpeed);
    }
}

