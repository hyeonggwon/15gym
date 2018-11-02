package com.example.hyunil.a15gym;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    EditText titleEdit;
    EditText contentEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // 액션바 보이게 하기
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xAAFF0000));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        titleEdit = (EditText) findViewById(R.id.titleEdit);
        contentEdit = (EditText) findViewById(R.id.contentEdit);
    }

    // 액션바에 메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.action_complete).setVisible(true);
        return true;
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

            // "완료" 메뉴를 눌렀을 시의 실행 내용
            case R.id.action_complete: {
                // 다이얼로그를 만들어 주는 Builder
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                // 다이얼로그 제목
                alertDialogBuilder.setTitle("작성을 완료 하시겠습니까?");

                // 취소 버튼 없앰
                alertDialogBuilder.setCancelable(false);

                // "예" 버튼을 눌렀을 시의 실행 내용
                alertDialogBuilder.setPositiveButton("예",
                        (dialog, id1) -> {// 현재 시간에 대한 정보를 받아온 다음 한국 시간에 맞춤
                            Date originDate = new Date(System.currentTimeMillis());
                            originDate.setHours(originDate.getHours() + 9);

                            // 제목, 내용, 작성시간, 아이콘의 resource id를 데이터베이스에 저장
                            String title = titleEdit.getText().toString();
                            String content = contentEdit.getText().toString();
                            String date = new SimpleDateFormat("yyyy년 MM월 dd일 hh:mm:ss a")
                                    .format(originDate);

                            writeNewNotice(title, content, date, R.drawable.bullhorn_icon);

                            // 액티비티 종료
                            finish();
                        });

                // "아니오" 버튼을 눌렀을 시의 실행 내용
                alertDialogBuilder.setNegativeButton("아니오",
                        (dialog, id1) -> dialog.cancel());

                // 다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();

                // 다이얼로그를 띄움
                alertDialog.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // 작성한 공지사항의 제목, 내용, 시간, 아이콘 resource id를 저장하는 함수
    private void writeNewNotice(String title, String content, String date, int resId) {

        // notice의 child로 key값을 가진 디렉토리 생성 후 변수에 저장 (key 값 = 디렉토리 이름)
        final DatabaseReference newReference = FirebaseDatabase.getInstance().getReference().child("notice").push();

        // 생성된 child의 키 값을 저장
        String key = newReference.getKey();

        // 공지사항 item 하나 생성
        NoticeItem noticeItem = new NoticeItem(key,
                title,
                content,
                date,
                R.drawable.bullhorn_icon);

        // 생성된 아이템을 새 child에 저장
        newReference.setValue(noticeItem);
    }
}
