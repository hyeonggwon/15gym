package com.example.hyunil.a15gym;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MenuActivity extends AppCompatActivity {
    private ImageButton goalButton;
    private ImageButton chatButton;
    private ImageButton noticeButton;
    private ImageButton timerButton;
    private ImageButton profileButton;
    private ImageButton iljungButton;
    private ImageButton qrButton;

    private static int SIGN_IN_REQUEST_CODE=1;

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener((task)-> {
                Toast.makeText(getApplicationContext(),"You have been signed out.", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu2,menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            Toast.makeText(getApplicationContext(),"Successfully signed in. Welcome!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),"We couldn't sign you in. Please try again later", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean isSecond = false;  // 두번째 클릭인지 체크

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        goalButton = (ImageButton) findViewById(R.id.goal_button);
        chatButton = (ImageButton) findViewById(R.id.chat_button);
        noticeButton = (ImageButton) findViewById(R.id.notice_button);
        timerButton = (ImageButton) findViewById(R.id.timer_button);
        profileButton = (ImageButton) findViewById(R.id.profile_button);
        iljungButton = (ImageButton) findViewById(R.id.iljung_button);
        qrButton = (ImageButton) findViewById(R.id.qr_button);

        goalButton.setOnClickListener((view) -> {
            startActivity(new Intent(getApplicationContext(), GoalActivity.class));
        });

        chatButton.setOnClickListener((view) -> {
            startActivity(new Intent(getApplicationContext(), MainActivity2.class));
        });

        noticeButton.setOnClickListener((view)-> {
            checkAdmin();
            startActivity(new Intent(getApplicationContext(), NoticeActivity.class));
        });

        timerButton.setOnClickListener(view->{
            startActivity(new Intent(getApplicationContext(), TimerActivity.class));
        });
        profileButton.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        });
        iljungButton.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), PlannerActivity.class));
        });
        qrButton.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), QRcodeActivity.class));
        });
        /*
         * @ firebase에 현재 사용중인 유저가 null이라면 로그인을 안 한 것이므로 로그 인을 하도록 한다.
         * @ 만약 아니라면 이미 로그 인을 한 상태이므로 로그 인 했다는 Snackbar를 띄운다.
         */
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        }
    }

    // 관리자 아이디로 접속했는지 확인
    public void checkAdmin() {

        DatabaseReference adminReference = FirebaseDatabase.getInstance().getReference().child("admin");
        adminReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GenericTypeIndicator<HashMap<String, String>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, String>>() { };
                HashMap<String, String> value = dataSnapshot.getValue(genericTypeIndicator);

                String adminId = value.get("id");

                if(adminId == null || FirebaseAuth.getInstance().getCurrentUser() == null) {
                    NoticeActivity.isAdminMode = false;
                    return;
                }

                Log.d("NoticeActivity_Log", "adminId : " + adminId);
                Log.d("NoticeActivity_Log", "userId : "
                        + FirebaseAuth.getInstance().getCurrentUser().getEmail());

                if (adminId.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    NoticeActivity.isAdminMode = true;
                    Log.d("NoticeActivity_Log", "isAdminMode : true");
                    return;
                }
                else {
                    NoticeActivity.isAdminMode = false;
                    Log.d("NoticeActivity_Log", "isAdminMode : false");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // back 키 이벤트
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            if(isSecond == false) { // 첫번째인 경우

                Toast.makeText(this, "뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_LONG).show();

                isSecond = true;

                //Back키가 2초내에 두번 눌렸는지 감지

                TimerTask second = new TimerTask() {
                    @Override
                    public void run() {
                        timer.cancel();
                        timer = null;
                        isSecond = false;
                    }
                };

                if(timer != null){
                    timer.cancel();
                    timer = null;
                }

                timer = new Timer();
                timer.schedule(second, 2000);
            } else{
                moveTaskToBack(true);
                finish();
            }
        }
        return true;
    }
}
