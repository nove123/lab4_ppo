package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS players (name TEXT, result TEXT)");

        Cursor query = db.rawQuery("SELECT COUNT(*) FROM players;", null);
        query.moveToNext();
        if (query.getInt(0) == 0) {
            db.execSQL("INSERT INTO players (name, result) VALUES ('NOOB', '00:00:10')");
            db.execSQL("INSERT INTO players (name, result) VALUES ('MASTER', '00:00:20')");
            db.execSQL("INSERT INTO players (name, result) VALUES ('GOD', '00:00:30')");
        }
    }

    public void onPlayClicked(View view) {
        String name = ((TextView)findViewById(R.id.textInputEditText)).getText().toString();
        if (name.isEmpty())
            return;
        Cursor query = db.rawQuery("SELECT name FROM players WHERE name='"+name+"'", null);
        query.moveToNext();
        if (query.getCount() > 0) {
            Toast toast = Toast.makeText(getApplicationContext(), "Player already exists", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
        }
    }

    public void onDashboardClicked(View view) {
        Intent intent = new Intent(this, ChampionDashboard.class);
        startActivity(intent);
    }
}