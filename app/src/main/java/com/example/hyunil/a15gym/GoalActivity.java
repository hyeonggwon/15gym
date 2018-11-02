package com.example.hyunil.a15gym;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;


public class GoalActivity extends AppCompatActivity {
    private ListView listView;
    private EditText kindOfExercise;
    private EditText numberOfExercise;
    private Button deleteButton;
    private Button addButton;
    private FirebaseListAdapter<GoalText> adapter;
    private DatabaseReference ref;
    private TextView kind;
    private TextView number;
    private ArrayList<GoalText> arrayList = new ArrayList<>();
    private GoalText selectedGoal;
    private TextView progressText;
    private Animation flowAnim;
    private RadioButton radioButton;
    private ArrayList<RadioButton> radList = new ArrayList<>();
    private static int radClicked = 0;
    private static int parentRadClicked=0;
    private Handler mHandler = new Handler();

    private final String word[]=
            {
                    "오늘 걷지 않으면 내일 뛰어야 한다.",
                    "경기는 끝날 때까지 끝난것이 아니다",
                    "바짝 붙어서 오르는 일이 없는 자는, 결코 떨어지지 않는다.",
                    "옥도 닦지 않으면 그릇이 될 수 없다.",
                    "우공이 산을 옮긴다.",
                    "오늘을 열정적으로 살아가는 당신이 가장 아름답다.",
                    "나약한 태도는 성격도 나약하게 만든다.",
                    "노력은 배신하지 않는다.",
                    "제일 힘든 고통은 노력이다"
            };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int REQUEST_SENDING_GOAL = 14;
        final Intent intent = new Intent(this,MainActivity.class);
        if(item.getItemId() == R.id.goal_share) {
            FirebaseDatabase.getInstance().getReference("desire").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long lValue = dataSnapshot.getValue(Long.class);
                    String sValue = lValue.toString();
                    intent.putExtra("goal",sValue);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) { /* intentionally blanked */ }
            });
        }
        startActivityForResult(intent,REQUEST_SENDING_GOAL);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.goal_menu,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        setTitle(R.string.goal);

        deleteButton = (Button) findViewById(R.id.deleteButton);
        addButton = (Button) findViewById(R.id.addButton);
        listView = (ListView) findViewById(R.id.listView);
        kindOfExercise = (EditText) findViewById(R.id.kindOfExerciseText);
        numberOfExercise = (EditText) findViewById(R.id.numberOfExerciseText);
        progressText = (TextView) findViewById(R.id.progressText);

        ref = FirebaseDatabase.getInstance().getReference("TEXT");
        // set adapter //
        adapter = new FirebaseListAdapter<GoalText>(
                this,
                GoalText.class,
                R.layout.goal_list,
                ref
        ) {
            @Override
            protected void populateView(View v, GoalText model, int position) {
                GoalText goal = (GoalText) model;
                arrayList.add(goal);
                kind = (TextView) v.findViewById(R.id.kind);
                number = (TextView) v.findViewById(R.id.number);
                radioButton = (RadioButton) v. findViewById(R.id.radioButton);
                radList.add(radioButton);
                if(kind != null && number != null) {
                    kind.setText(goal.getKindOfExercise());
                    number.setText(goal.getNumberOfExercise());
                }
            }
        };
        listView.setAdapter(adapter);
        //  set adapter //
        listView.setItemsCanFocus(false);

        // set onClickListener on list_view aka setOnItemClickListener //
        listView.setOnItemClickListener((adapterView, view, i, l) -> selectedGoal = adapter.getItem(i));
        // setOnItemClickListener on list_view //

        // 작업 스레드 생성하여 애니메이션을 만든다.
        new Thread( ()-> { // Thread lambda 식을 이용해 구현하였다.
            mHandler.post(() -> { // lambda 식을 이용해 구현하였다.
                flowAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.flow);
                flowAnim.setAnimationListener(new Animation.AnimationListener() {
                    Random mRandom = new Random();
                    int index = mRandom.nextInt(word.length - 1);

                    @Override
                    public void onAnimationStart(Animation animation) { progressText.setText(word[index]); }

                    @Override
                    public void onAnimationEnd(Animation animation) { progressText.setText(word[index]); }

                    @Override
                    public void onAnimationRepeat(Animation animation) { /* intentionally blanked */}
                });
            });
        }).start();
        // Thread

        // setOnClickListener when add_button clicked //
        addButton.setOnClickListener((view) ->  {
            Toast.makeText(getApplicationContext(), "저장되었습니다.",Toast.LENGTH_LONG).show();
            ref.push().setValue(new GoalText(kindOfExercise.getText().toString(),
                    numberOfExercise.getText().toString()));
            kindOfExercise.setText("");
            numberOfExercise.setText("");
            progressText.startAnimation(flowAnim);
        });
        // setOnClickListener on addButton //

        // addChildEventListener on FirebaseDatabase.getInstance().getReference("TEXT") //
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { /* Intentionally blanked */}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { /* Intentionally blanked */}

            @Override
            public void onCancelled(DatabaseError databaseError) {/* Intentionally blanked */}
        });
        // addChildEventListener on ref //

        // setOnClickListener when delete_button clicked //
        deleteButton.setOnClickListener((view) ->  {
            if(selectedGoal == null) {
                Toast.makeText(getApplicationContext(), "삭제할 목표를 클릭 후 삭제 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(ref == null) {
                listView.clearDisappearingChildren();
            }
            Query deleteQuery = ref.orderByChild("kindOfExercise").equalTo(selectedGoal.getKindOfExercise());
            // addListenerForSingleValueEvent when deleteQuery equal to list_view's item //
            deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "현재 목표치를 삭제할 수 없습니다. 다시 시도해주세요.",Toast.LENGTH_SHORT).show();
                }
            });
            Toast.makeText(getApplicationContext(),"해당 목표가 삭제되었습니다.",Toast.LENGTH_SHORT).show();
            // addListenerForSingleValueEvent on deleteQuery //
            listView.setSelector(android.R.color.transparent);
        });
        // setOnClickListener on deleteButton //

    } // onCreate()

    @Override
    protected void onPause() {
        parentRadClicked=0;
        radClicked=0;
        super.onPause();
    }

    public void onRadioButtonClicked(View v) {
        parentRadClicked = radClicked++;
        Iterator<RadioButton> iterator = radList.iterator();
        while (iterator.hasNext()) {
            if((RadioButton)v == iterator.next()) {
                Toast.makeText(getApplicationContext(), "좀 더 힘내세요°˖✧◝(⁰▿⁰)◜✧˖°",Toast.LENGTH_SHORT).show();
                if(parentRadClicked != radClicked) {
                    FirebaseDatabase.getInstance().getReference("desire").setValue(radClicked);
                    FirebaseDatabase.getInstance().getReference("time").setValue(DateFormat.format("dd-MM-yyyy", new Date().getTime()));
                }
            }
        }
    }
}

