package com.example.hyunil.a15gym;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameText;
    private TextView heightText;
    private TextView weightText;
    private TextView commentText;
    private Button sujungButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameText = (TextView) findViewById(R.id.profiletext2);
        heightText = (TextView) findViewById(R.id.profiletext4);
        weightText = (TextView) findViewById(R.id.profiletext6);
        commentText = (TextView) findViewById(R.id.profiletext8);
        sujungButton = (Button) findViewById(R.id.sujungbutton);

        Intent intent = getIntent();
        nameText.setText(intent.getStringExtra("name"));
        heightText.setText(intent.getStringExtra("height"));
        weightText.setText(intent.getStringExtra("weight"));
        commentText.setText(intent.getStringExtra("comment"));

        sujungButton.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), SujungActivity.class));
        });
    }
}
