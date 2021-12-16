package com.example.lab4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private MyThread mMyThread;

    private Paint mPaint;

    Rect platform;
    int platformWidth;
    float platformX;
    float platformSpeed;

    int ballX;
    int ballY;
    int ballRadius;
    double ballAngle;
    double ballSpeed;
    double maxAngleRandomModifier;
    int ballAcceleration;

    int currentAcceleration = 0;
    public void accelerateBall() {
        if (currentAcceleration++ == ballAcceleration) {
            ballSpeed += 0.1;
            currentAcceleration = 0;
        }
    }

    Boolean lose = false;
    Boolean isActive = false;

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mPaint = new Paint();
        initPlatform();
        initBall();

        mMyThread = new MyThread(getHolder(), this);
        mMyThread.setRunning(true);
        mMyThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        mMyThread.setRunning(false);

        while (retry) {
            try {
                mMyThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public void initPlatform() {
        platform = new Rect();
        platformWidth = 200;
        platformX = getWidth() / 2;
        platformSpeed = 2;
    }

    public void initBall() {
        ballX = getWidth() / 2;
        ballY = getHeight() / 2;
        ballRadius = 20;
        ballSpeed = 3;
        maxAngleRandomModifier = 6;
        ballAcceleration = 1000;

        Random r = new Random();
        double rand = -45 + (90) * r.nextDouble();
        ballAngle = 270.0 + rand;
    }

    public void setPlatformX(float platformX) {
        if (platformX >= getWidth() - platformWidth / 2) {
            this.platformX = getWidth() - platformWidth / 2;
        } else if (platformX <= platformWidth / 2) {
            this.platformX = platformWidth / 2;
        } else
            this.platformX = platformX;
    }

    public void movePlatform(float value) {
        if (isActive)
            setPlatformX(platformX + value * platformSpeed);
    }

    public void rotateBall(double angle) {
        Random r = new Random();
        double rand = -maxAngleRandomModifier + (2 * maxAngleRandomModifier) * r.nextDouble();
        ballAngle = (angle - ballAngle + rand) % 360;
    }

    public void moveBall() {
        if (isActive) {
            ballX += ballSpeed * Math.cos(Math.toRadians(ballAngle));
            ballY -= ballSpeed * Math.sin(Math.toRadians(ballAngle));
            Log.v(Integer.toString(ballY), Integer.toString(ballX));
            if (ballY >= getHeight() - 100 && ballY <= getHeight() - 99 + ballSpeed &&
                    ballX >= platformX - platformWidth / 2 && ballX <= platformX + platformWidth / 2) {
                ballAngle = 360 - ballAngle;
            } else if (ballY <= 0 && (ballX >= getWidth() || ballX <= 0)) {
                ballAngle = (ballAngle + 180) % 360.0;
            } else if (ballY <= 0) {
                rotateBall(360);
            } else if (ballX >= getWidth() || ballX <= 0) {
                rotateBall(180);
            } else if (ballY > getHeight()) {
                isActive = false;
                lose = true;
            } else return;

            ballX += ballSpeed * Math.cos(Math.toRadians(ballAngle));
            ballY -= ballSpeed * Math.sin(Math.toRadians(ballAngle));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);

            mPaint.setColor(Color.GREEN);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(ballX, ballY, ballRadius, mPaint);
            moveBall();
            accelerateBall();

            mPaint.setColor(Color.BLUE);
            platform.set(Math.round(platformX - platformWidth / 2), getHeight() - 100,
                    Math.round(platformX + platformWidth / 2), getHeight() - 50);
            canvas.drawRect(platform, mPaint);
        }
    }
}