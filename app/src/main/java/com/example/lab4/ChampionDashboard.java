package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ChampionDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_dashboard);

        ArrayList<String> players = new ArrayList<>();
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        Cursor query = db.rawQuery("SELECT * FROM players ORDER BY result DESC;", null);
        int i = 0;
        while(query.moveToNext()){
            String name = query.getString(0);
            String result = query.getString(1);
            players.add(++i + ") " + result + "\t,by\t:" + name);
        }
        ListView listView = findViewById(R.id.list);

        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, players);

        listView.setAdapter(adapter);
    }

    public void onDashboardClosed(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}