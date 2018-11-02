package com.example.hyunil.a15gym;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        // 액션바 보이게 하기
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xAAFF0000));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView titleText = (TextView) findViewById(R.id.titleText);
        TextView contentText = (TextView) findViewById(R.id.contentText);


        // NoticeActivity에서 온 intent로 부터 공지사항의 제목과 내용을 받아 출력
        Intent intent = getIntent();
        titleText.setText(intent.getStringExtra("TITLE"));
        contentText.setText(intent.getStringExtra("CONTENT"));
    }

    // 액션바에 메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        if(NoticeActivity.isAdminMode)
            menu.findItem(R.id.action_delete).setVisible(true);

        return super.onCreateOptionsMenu(menu);
    }

    // 메뉴를 클릭했을때의 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.action_delete: {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setTitle("이 글을 삭제 하시겠습니까?");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("예",
                        (dialog, id1) -> {
                            final DatabaseReference noReticeference =
                                    FirebaseDatabase.getInstance().getReference().child("notice");

                            Intent intent = getIntent();
                            String key = intent.getStringExtra("KEY");
                            noReticeference.child(key).removeValue();
                            finish();
                        });
                alertDialogBuilder.setNegativeButton("아니오",
                        (dialog, id1) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
