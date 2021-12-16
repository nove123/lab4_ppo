package com.example.lab4;

import android.animation.ArgbEvaluator;
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;

public class MyThread extends Thread {
    private final SurfaceHolder mSurfaceHolder;
    private GameSurfaceView gameSurfaceView;

    private boolean mRunning;

    public MyThread(SurfaceHolder holder, GameSurfaceView sView) {
        mSurfaceHolder = holder;
        mRunning = false;
        gameSurfaceView = sView;
    }

    public void setRunning(boolean running) {
        mRunning = running;
    }

    @SuppressLint("WrongCall")
    @Override
    public void run() {
        Canvas canvas;

        while (mRunning) {
            canvas = null;
            try {
                canvas = mSurfaceHolder.lockCanvas(null);
                synchronized (mSurfaceHolder) {
                    gameSurfaceView.onDraw(canvas);
                }
            }
            finally {
                if (canvas != null)
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}