package com.example.hyunil.a15gym;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity2 extends AppCompatActivity {
    /*
     * @ local : int SIGN_IN_REQUEST_CODE := 접속했다는 상수
     * @ local : FirebaseListAdapter<ChatMessage> adapter := 파이어베이스를 사용한 어뎁터
     * @ local : RelativeLayout mainLayout := main layout
     * @ local : FloatingActionButton fab := 전송 버튼
    */
    private static int SIGN_IN_REQUEST_CODE=1;
    private final int REQUEST_SENDING_GOAL = 14;
    private FirebaseListAdapter<ChatMessage> adapter;
    RelativeLayout mainLayout;
    FloatingActionButton fab;

    /*
     * @ 로그 아웃 처리를 위한 함수.
     * @ Snackbar를 사용해서 로그 아웃시 Toast와 비슷하게 띄움
     * @ 만약 로그 아웃하여 item에 menu_sign_out의 id가 전달되었다면
     * @ firebase에서 제공하는 메소드인 addOnCompleteListener를 호출
     *
     * @ onComplete := 태스크가 끝났을 시에 호출된다.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_delete) {
            FirebaseDatabase.getInstance().getReference("EMAIL").removeValue();
        }
        return true;
    }

    /*
     * @ 인플레이터 사용해서 menu 레이아웃을 불러온다.
     * @ 오른쪽 점 세 개로 나타낸다. 여기서 이벤트를 소모할 경우 true를 반환해 더 이상 이벤트를 소모하지 않도록 한다.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    /*
     * @ onActivityResult를 오버라이드 했다.
     * @ 만약 request code가 로그 인이라면 로그인 했다는 것을 Snackbar로 띄우고 입력한 채팅을 띄워준다.
     * @ 만약 아니라면 로그 인 하지 않고 그냥 Snackbar만 띄우고 finish()한다.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            Snackbar.make(mainLayout,"Successfully signed in. Welcome!", Snackbar.LENGTH_SHORT).show();
            displayChatMessage();
        } else if(requestCode == REQUEST_SENDING_GOAL) {
            Intent intent = data;
            Bundle bundle = intent.getExtras();

            FirebaseDatabase.getInstance().getReference("EMAIL").push().setValue(new ChatMessage(bundle.getString("목표치"),
                    FirebaseAuth.getInstance().getCurrentUser().getEmail()));
        } else {
            Snackbar.make(mainLayout,"We couldn't sign you in. Please try again later", Snackbar.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setTitle(R.string.trainer);

        /*
         * @ fab에 onClickListener()를 달고 만약 눌러졌으면 firebase database에 key, value값으로
         * @ EditText에 적힌 텍스트와 email의 주소를 보내고 text를 지운다.
         */
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener((view) -> {
            EditText input = (EditText)findViewById(R.id.input);
            FirebaseDatabase.getInstance().getReference("EMAIL").push().setValue(new ChatMessage(input.getText().toString(),
                    FirebaseAuth.getInstance().getCurrentUser().getEmail()));
            input.setText("");
        });

        /*
         * @ firebase에 현재 사용중인 유저가 null이라면 로그인을 안 한 것이므로 로그 인을 하도록 한다.
         * @ 만약 아니라면 이미 로그 인을 한 상태이므로 로그 인 했다는 Snackbar를 띄운다.
         */
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        } else {
            Snackbar.make(mainLayout,"로그 인: "+FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
            displayChatMessage();
        }
    }

    /*
         * @ 실제 친 메세지를 띄워주는 함수
         * @
         */
    private void displayChatMessage() {
        ListView listOfMessage = (ListView) findViewById(R.id.list_of_message);
        /*
         * @ local : adapter := FirebaseListAdapter<T>(activity, class, layout, refOfFirebaseDatabase) 의 반환 값 전달.
         * @ 이 변수는 실제로 메세지를 firebase에 전달하여 거기의 데이터베이스에서 <key,value>로 저장된 값을 가져온다.
         * @ 반면, 이 생성자에는 populateView함수가 있어 오버라이딩 해야한다.
         */
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.list_item,
                FirebaseDatabase.getInstance().getReference("EMAIL")) {
            /*
             * @ ChatMessage 클래스에 있는 변수의 값들을 채워넣는다.
             */
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText, messageTime; RelativeLayout listLayout;
                messageText = (TextView) v.findViewById(R.id.message_text);
                messageTime = (TextView) v.findViewById(R.id.message_time);
                listLayout = (RelativeLayout)v.findViewById(R.id.list_layout);

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                    if (model.getMessageUser().compareTo(FirebaseAuth.getInstance().getCurrentUser().getEmail()) == 0) {
                        String string = "나"+model.getMessageText();
                        messageText.setText(string);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageText.getLayoutParams();
                        params.addRule(RelativeLayout.ALIGN_PARENT_END);
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        messageText.setGravity(Gravity.RIGHT|Gravity.END);
                        messageText.setLayoutParams(params);

                        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
                        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) messageTime.getLayoutParams();
                        params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        params1.addRule(RelativeLayout.ALIGN_PARENT_START);
                        messageTime.setGravity(Gravity.TOP);
                        messageTime.setGravity(Gravity.CENTER);
                        messageTime.setLayoutParams(params1);

                        listLayout.setBackground(getResources().getDrawable(R.drawable.thm_chatroom_message_bubble_me_bg));
                    } else if (model.getMessageUser().compareTo(FirebaseAuth.getInstance().getCurrentUser().getEmail()) != 0) {
                        messageText.setText(model.getMessageText());
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageText.getLayoutParams();
                        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        params.addRule(RelativeLayout.ALIGN_PARENT_START);
                        messageText.setGravity(Gravity.LEFT|Gravity.START);
                        messageText.setLayoutParams(params);

                        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
                        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) messageTime.getLayoutParams();
                        params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        params1.addRule(RelativeLayout.ALIGN_PARENT_START);
                        messageTime.setGravity(Gravity.TOP);
                        messageTime.setGravity(Gravity.CENTER);
                        messageTime.setLayoutParams(params1);

                        listLayout.setBackground(getResources().getDrawable(R.drawable.thm_chatroom_message_bubble_you_bg));
                    }
                }
            }
        };
        // setAdapter는 뷰의 adapter를 설정한다. 여기서는 먼저 정의해둔 adapter 변수를 사용한다.
        listOfMessage.setAdapter(adapter);
    }
}
