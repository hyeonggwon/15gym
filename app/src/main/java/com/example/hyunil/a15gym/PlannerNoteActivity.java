package com.example.hyunil.a15gym;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class PlannerNoteActivity extends AppCompatActivity {

    DatePicker datePicker;
    EditText editText;
    Button btn;
    Button btn2;
    //삭제버튼 만들기

    //파일이름을 저장할 변수
    String fileName;

    //(2)데이터베이스 클래스인 MyDBHelper 생성
    MyDBHelper helper = new MyDBHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner_note);

        //(1)
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        editText = (EditText) findViewById(R.id.editText);
        btn = (Button) findViewById(R.id.btn);
        btn2 = (Button) findViewById(R.id.btn2);

        //(2)캘린더를 이용하여 연월일 가지고오기
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        //(3)현재날짜 가지고오기
        //   실행했을 때 현재날짜부터 시작하도록 자동 설정
        SQLiteDatabase db = helper.getWritableDatabase();
        Toast.makeText(PlannerNoteActivity.this, Integer.toString(year)+"년"+Integer.toString(month+1)+"월"+Integer.toString(day)+"일", Toast.LENGTH_SHORT).show();
        //(4)int형 날짜를 String형으로 변환하여 파일이름에 저장하기
        fileName = Integer.toString(year) + "년 " + Integer.toString(month + 1) + "월 " + Integer.toString(day) + "일";

        //(5)에디트텍스트에 그 날짜에 해당하는 일기내용 가지고오기
        //   일기내용의 유무에 따라 버튼이 변하도록 하는 것은 readDiary 메서드를 새로 만들어 설정
        String str = readDiary(fileName);
        editText.setText(str);
        btn.setEnabled(true);

        //(7)날짜가 변동되었을 때 새로운 날짜를 fileName에 또 저장을 하고, 해당내용을 에디트텍스트에 불러옴
        datePicker.init(year, month, day, (view, year2, monthOfYear, dayOfMonth)-> {
            Toast.makeText(PlannerNoteActivity.this, Integer.toString(year2)+"년"+Integer.toString(monthOfYear+1)+"월"+Integer.toString(dayOfMonth)+"일", Toast.LENGTH_SHORT).show();
            fileName = Integer.toString(year2) + "년 " + Integer.toString(monthOfYear+1)+ "월 " + Integer.toString(dayOfMonth) + "일";
            String str2 = readDiary(fileName);
            editText.setText(str2);
            btn.setEnabled(true);
        });

        //(8)버튼이벤트 + 내용 입력부분(getWritableDatabase())
        btn.setOnClickListener(view -> {
            if(btn.getText().toString().equals("새로저장")){
                SQLiteDatabase db2 = helper.getWritableDatabase();
                String sql = "insert into myDiary values('"+fileName+"', '"+editText.getText().toString()+"');";
                db2.execSQL(sql);
                db2.close();
                Toast.makeText(PlannerNoteActivity.this,"DB에 저장",Toast.LENGTH_SHORT).show();
            }else{
                SQLiteDatabase db2 = helper.getWritableDatabase();
                String sql = "update myDiary set content='"+editText.getText().toString()+"' where diaryDate='"+fileName+"';";
                db2.execSQL(sql);
                db2.close();
                Toast.makeText(PlannerNoteActivity.this,"DB가 수정됨",Toast.LENGTH_SHORT).show();
            }
        });
        btn2.setOnClickListener(view ->{
            SQLiteDatabase db3 = helper.getWritableDatabase();
            String sql = "delete from myDiary where diaryDate='"+fileName+"';";
            db3.execSQL(sql);
            db3.close();
            Toast.makeText(PlannerNoteActivity.this,"DB가 삭제됨",Toast.LENGTH_SHORT).show();
        });


    }

    /*(6)readDiary 메서드 만들기 -> 내용출력 부분
          조건을 걸어 일기가 있으면 내용을 읽어와 에디트텍스트에 보여주고,
          일기가 없다면 없다는 문구와 함께 새로 저장할 수 있도록 버튼의 모양을 바꾼다

          String 타입으로 메서드를 만들면 return 값을 주어야 에러가 사라짐
    */
    String readDiary(String fName) {
        //일기내용을 담을 수 있는 스트링타입의 변수설정
        String strDiary = null;

        //데이터베이스내용 출력하기(getReadableDatabase())
        SQLiteDatabase db = helper.getReadableDatabase();

        //날짜에 해당하는 모든 내용을 가지고 오라는 명령
        String sql = "select * from myDiary where diaryDate='" + fName + "';";

        //검색기능(select)를 사용할 때에는 sql 문을 읽어오는 Cursor 가 필요하며 마지막에 close 해 주어야 함
        Cursor cursor = db.rawQuery(sql, null);
        //내용 유무에 따라 버튼설정을 다르게 처리
        if (cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                strDiary = cursor.getString(1);
            }
            btn.setText("수정하기");
            btn2.setEnabled(true);
        } else {
            editText.setHint("일기가 없습니다");
            btn.setText("새로저장");
            btn2.setEnabled(false);
        }
        cursor.close();
        db.close();
        return strDiary;
    }
}

