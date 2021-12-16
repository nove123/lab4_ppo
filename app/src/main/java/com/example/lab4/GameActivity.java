package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity implements SensorEventListener {
    SensorManager sensorManager;
    Sensor orientationSensor;

    GameSurfaceView gameSurfaceView;
    TextView notificationView;
    TextView timeView;
    ImageView pauseView;
    ImageView leaveView;

    Boolean timerActive = false;
    double time = 0;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getSupportActionBar().hide();
        name = (String)getIntent().getExtras().get("name");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        gameSurfaceView = (GameSurfaceView) ((FrameLayout) findViewById(R.id.frame_layout)).getChildAt(0);
        notificationView = findViewById(R.id.notification);
        pauseView = findViewById(R.id.pause);
        leaveView = findViewById(R.id.leave);
        timeView = findViewById(R.id.timer);

        leaveView.setOnClickListener(v -> {
            summonMenu();
        });

        notificationView.setText("Tap to PLAY");
        notificationView.setOnClickListener(v -> {
            gameSurfaceView.isActive = true;
            timerActive = true;
            pauseView.setVisibility(View.VISIBLE);
            notificationView.setVisibility(View.GONE);
        });

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (gameSurfaceView.lose) {
                        notificationView.setText("You Lose\n Your result is " + timeView.getText() + "\nTap to see champions dashboard");
                        notificationView.setVisibility(View.VISIBLE);
                        pauseView.setVisibility(View.GONE);
                        timeView.setVisibility(View.GONE);
                        timerActive = false;
                        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
                        db.execSQL("INSERT INTO players (name, result) VALUES ('" + name +"', '" + timeView.getText() + "')");

                        notificationView.setOnClickListener(v -> {
                            summonDashboard();
                        });
                        gameSurfaceView.lose = false;
                    }
                    handler.postDelayed(this, 0);
                } catch (IllegalStateException ed) {
                }
            }
        };
        handler.postDelayed(runnable, 0);

        Handler timerHandler = new Handler();
        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (timerActive) {
                        long seconds = Math.round(time);
                        long hours = seconds / 3600;
                        long minutes = (seconds % 3600) / 60;
                        seconds = seconds % 60;
                        timeView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                        time += 0.01;
                    }
                    timerHandler.postDelayed(this, 10);
                } catch (IllegalStateException ed) { }
            }
        };
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void summonMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void summonDashboard() {
        Intent intent = new Intent(this, ChampionDashboard.class);
        startActivity(intent);
    }

    public void omMenuPlayed(View view) {
        if (gameSurfaceView.isActive) {
            gameSurfaceView.isActive = false;
            timerActive = false;
            pauseView.setImageDrawable(getDrawable(R.drawable.ic_baseline_play_arrow_42));
            leaveView.setVisibility(View.VISIBLE);
        }
        else {
            gameSurfaceView.isActive = true;
            timerActive = true;
            pauseView.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_42));
            leaveView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        int ORIENTATION_UNKNOWN = -1;
        int _DATA_X = 0;
        int _DATA_Y = 1;
        int _DATA_Z = 2;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] values = event.values;
            if (gameSurfaceView != null)
                gameSurfaceView.movePlatform(values[_DATA_Y]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null)
            sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}