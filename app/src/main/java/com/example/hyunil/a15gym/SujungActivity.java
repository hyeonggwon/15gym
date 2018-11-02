package com.example.hyunil.a15gym;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SujungActivity extends AppCompatActivity {
    private EditText name;
    private EditText height;
    private EditText weight;
    private EditText comment;
    private Button addButton;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sujung);

        name = (EditText) findViewById(R.id.sujungtext2);
        height = (EditText) findViewById(R.id.sujungtext4);
        weight = (EditText) findViewById(R.id.sujungtext6);
        comment = (EditText) findViewById(R.id.sujungtext8);

        addButton = (Button) findViewById(R.id.sujungbutton);
    }

    public void onChangeButtonClicked(View v) {
        database.push().setValue(new GetUserInfo(name.getText().toString(), height.getText().toString(),
                weight.getText().toString(), comment.getText().toString()));
        Intent intent = new Intent();

        intent.putExtra("name",name.getText().toString());
        intent.putExtra("height",height.getText().toString());
        intent.putExtra("weight",weight.getText().toString());
        intent.putExtra("comment",comment.getText().toString());

        ComponentName componentName = new ComponentName(
                "com.example.hyunil.a15gym",
                "com.example.hyunil.a15gym.ProfileActivity"
        );
        intent.setComponent(componentName);
        startActivity(intent);
    }
}
