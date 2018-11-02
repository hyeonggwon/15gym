package com.example.hyunil.a15gym;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NoticeActivity extends AppCompatActivity {

    // 관리자 아이디로 접속시 true
    public static boolean isAdminMode = false;

    private boolean adminMode;

    // 공지사항 버튼들이 올라올 ListView의 어댑터
    private final NoticeAdapter adapter = new NoticeAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("NoticeActivity_Log", "onCreate() 호출됨");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        // 액션바 보이게 하기
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xAAFF0000));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ListView noticeList = (ListView) findViewById(R.id.noticeList);

        DatabaseReference noticeReference = FirebaseDatabase.getInstance().getReference();

        // notice 디렉토리에 ChildEventListener를 붙여줌
        noticeReference.child("notice").addChildEventListener(new ChildEventListener() {

           // child가 추가되었을 때 : item을 받아와 adapter에 추가 후 화면에 갱신
           @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                NoticeItem item = dataSnapshot.getValue(NoticeItem.class);

                if(item == null) {
                    return;
                }

                adapter.addItem(item);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

           // child가 삭제되었을 때 : item을 adapter에서 삭제 후 화면에 갱신
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.removeItem(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

           // 데이터를 읽는데 실패했을 때 : Toast로 알림
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),
                        "공지사항을 읽는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        noticeList.setAdapter(adapter);

        // 리스트뷰 클릭시 intent는 클릭 된 item의 정보를 가지고 ContentActivity 실행
        noticeList.setOnItemClickListener((adapterView, view, i, l) ->  {
            NoticeItem item = (NoticeItem) adapter.getItem(i);

            Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
            intent.putExtra("KEY", item.getKey());
            intent.putExtra("TITLE", item.getTitle());
            intent.putExtra("CONTENT", item.getContent());
            startActivity(intent);
        });
    }

    // 공지사항 버튼들이 올라올 ListView의 어댑터 클래스
    private class NoticeAdapter extends BaseAdapter {

        private ArrayList<NoticeItem> items = new ArrayList();

        @Override
        public int getCount() { return items.size(); }

        @Override
        public Object getItem(int position) { return items.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            NoticeItemView view = (NoticeItemView) convertView;

            if(convertView == null)
                view = new NoticeItemView(getApplicationContext());

            NoticeItem item = items.get(position);
            view.setTitle(item.getTitle());
            view.setDate(item.getDate());

            return view;
        }

        // 리스트에 item 추가
        void addItem(NoticeItem item) { items.add(item); }

        // key를 이용해 item을 찾고 리스트에서 item 삭제
        void removeItem(String key) {
            for (int i = 0; i < items.size(); i++)
                if (items.get(i).getKey().equals(key))
                    items.remove(i);
        }
    }

    // 액션바에 메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        if(isAdminMode)
            menu.findItem(R.id.action_write).setVisible(true);

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
            case R.id.action_write: {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
