package com.example.hyunil.a15gym;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;

public class PlannerActivity extends AppCompatActivity {
    CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
    }


    public void onIljungButtonClicked(View v) {
        Intent myIntent = new Intent(getApplicationContext(), PlannerNoteActivity.class);
        startActivity(myIntent);
    }

}