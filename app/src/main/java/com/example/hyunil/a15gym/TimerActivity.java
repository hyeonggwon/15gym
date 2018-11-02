package com.example.hyunil.a15gym;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TimerActivity extends Activity implements View.OnFocusChangeListener{

    // 작업 Thread의 진행 여부
    private boolean running;

    Button startButton;
    Button pauseButton;
    Button continueButton;
    Button initButton;

    TextView hourInputText;
    TextView minInputText;
    TextView secInputText;
    TextView focusedText;

    LinearLayout controlButtons;
    GridLayout keyboardLayout;
    LinearLayout timeLayout;
    LinearLayout.LayoutParams timeLayoutLP;

    // 현재 focus 되어있는 TextView에서 사용자에 의해 입력된 숫자 개수
    int numberCount;

    // 타이머 시작 시 사용자가 설정한 시간이 저장되는 변수
    long initialTime;

    // 타이머 시작 또는 계속 버튼을 눌렀을 당시의 시간 정보
    long startTime;

    // 타이머가 진행 되고 있는 매 순간의 시간 정보
    long currentTime;

    // 타이머의 현재 남은 시간
    long remainingTime;

    // 타이머 시작 또는 계속 버튼을 눌렀을 당시의 남은 시간
    long totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        OnFunctionButtonClickListener onFunctionButtonClickListener =
                new OnFunctionButtonClickListener();

        OnNumberButtonClickListener onNumberButtonClickListener =
                new OnNumberButtonClickListener();

        startButton = (Button) findViewById(R.id.startButton);
        pauseButton = (Button) findViewById(R.id.pauseButton);
        continueButton = (Button) findViewById(R.id.continueButton);
        initButton = (Button) findViewById(R.id.initButton);

        hourInputText = (TextView) findViewById(R.id.hourInput);
        minInputText = (TextView) findViewById(R.id.minInput);
        secInputText = (TextView) findViewById(R.id.secInput);
        focusedText = hourInputText;

        controlButtons = (LinearLayout) findViewById(R.id.controlButtons);
        keyboardLayout = (GridLayout) findViewById(R.id.keyboardLayout);
        timeLayout = (LinearLayout) findViewById(R.id.timeLayout);
        timeLayoutLP = (LinearLayout.LayoutParams) timeLayout.getLayoutParams();

        // numnberCount 초기화
        numberCount = 0;

        // 아래에 있는 3개의 TextView만 포커스를 받을 수 있게함
        hourInputText.setFocusableInTouchMode(true);
        minInputText.setFocusableInTouchMode(true);
        secInputText.setFocusableInTouchMode(true);

        // 아래에 있는 3개의 TextView에 OnFocusChangeListener 추가
        hourInputText.setOnFocusChangeListener(this);
        minInputText.setOnFocusChangeListener(this);
        secInputText.setOnFocusChangeListener(this);

        // 기능 버튼에 OnFunctionButtonClickListener 추가
        startButton.setOnClickListener(onFunctionButtonClickListener);
        pauseButton.setOnClickListener(onFunctionButtonClickListener);
        continueButton.setOnClickListener(onFunctionButtonClickListener);
        initButton.setOnClickListener(onFunctionButtonClickListener);
        findViewById(R.id.backspaceButton).setOnClickListener(onFunctionButtonClickListener);
        findViewById(R.id.nextButton).setOnClickListener(onFunctionButtonClickListener);

        // 숫자 버튼에 OnNumberButtonClickListener 추가
        findViewById(R.id.button1).setOnClickListener(onNumberButtonClickListener);
        findViewById(R.id.button2).setOnClickListener(onNumberButtonClickListener);
        findViewById(R.id.button3).setOnClickListener(onNumberButtonClickListener);
        findViewById(R.id.button4).setOnClickListener(onNumberButtonClickListener);
        findViewById(R.id.button5).setOnClickListener(onNumberButtonClickListener);
        findViewById(R.id.button6).setOnClickListener(onNumberButtonClickListener);
        findViewById(R.id.button7).setOnClickListener(onNumberButtonClickListener);
        findViewById(R.id.button8).setOnClickListener(onNumberButtonClickListener);
        findViewById(R.id.button9).setOnClickListener(onNumberButtonClickListener);
        findViewById(R.id.button0).setOnClickListener(onNumberButtonClickListener);
    }

    // 뒤로가기 버튼을 누를 시 액티비티를 Destroy하지 않는다.
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // 10ms 마다 남은 시간을 계산 및 출력하는 Thread
    private class TimerThread implements Runnable {

        private Handler handler = new Handler();

        @Override
        public void run() {

            while(running) {

                handler.post(()-> {

                    // 현재 시간을 얻어옴
                    currentTime = System.currentTimeMillis();

                    // 남은 시간을 계산
                    remainingTime = totalTime - currentTime + startTime;

                    // 남은 시간의 초 부분을 계산
                    long sec = remainingTime / 1000 % 60;

                    // 계산된 초 부분이 출력과 다를 시 남은 시, 분, 초를 화면에 출력
                    if(sec != Long.parseLong(secInputText.getText().toString())) {
                        long hour = remainingTime / 1000 / 3600;
                        long min = remainingTime / 1000 / 60 % 60;

                        hourInputText.setText(String.format("%02d", hour));
                        minInputText.setText(String.format("%02d", min));
                        secInputText.setText(String.format("%02d", sec));

                        // 00:00:05 의 시간이 남은 이후부터 빨간색으로 표시
                        if(hour == 0 && min == 0 && sec <= 5) {
                            secInputText.setTextColor(Color.parseColor("#FFFF0000"));
                        }
                    }

                    // 남은시간이 0일 시 실행 내용
                    if(remainingTime <= 0) {
                        // 작업 Thread 중단
                        running = false;

                        // 알람을 울림
                        playAlarm();

                        // 상단바에 Notification 생성
                        makeNotification();

                        // 시작 버튼을 누르기 전으로 화면 초기화
                        initDisplay();
                    }
                });
                // 10ms 만큼 기다린다.
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    // 기능이 있는 버튼을 클릭할 시의 리스너
    private class OnFunctionButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                /** 시작 버튼을 클릭할 시
                 * 1. 타이머 진행 화면 구성으로 변경
                 * 2. 총 남은 시간 계산 후 initialTime에 저장
                 * 3. startTime 저장 후 작업 Thread 시작
                 */
                case R.id.startButton:
                    timeLayoutLP.weight = 7;

                    keyboardLayout.setVisibility(View.GONE);
                    startButton.setVisibility(View.GONE);
                    controlButtons.setVisibility(View.VISIBLE);
                    focusedText.setTextColor(Color.parseColor("#FFFFFFFF"));

                    totalTime = Integer.parseInt(hourInputText.getText().toString()) * 3600 * 1000
                            + Integer.parseInt(minInputText.getText().toString()) * 60 * 1000
                            + Integer.parseInt(secInputText.getText().toString()) * 1000;
                    initialTime = totalTime;

                    startTime = System.currentTimeMillis();
                    running = true;
                    new Thread(new TimerThread()).start();
                    break;

                /** 중지 버튼을 클릭할 시
                 * 1. 작업 Thread 중지
                 * 2. 타이머 중지 화면 구성으로 변경
                 */
                case R.id.pauseButton:
                    running = false;

                    pauseButton.setVisibility(View.GONE);
                    continueButton.setVisibility(View.VISIBLE);
                    break;

                /** 계속 버튼을 클릭할 시
                 * 1. 타이머 진행 화면 구성으로 변경
                 * 2. 현재 남은 시간 저장
                 * 3. startTime 저장 후 작업 Thread 시작
                 */
                case R.id.continueButton:
                    continueButton.setVisibility(View.GONE);
                    pauseButton.setVisibility(View.VISIBLE);

                    totalTime = remainingTime;
                    startTime = System.currentTimeMillis();
                    running = true;
                    new Thread(new TimerThread()).start();
                    break;

                /** 초기화 버튼을 클릭할 시
                 * 1. 작업 Thread 중지
                 * 2. 시작 버튼을 누르기 전으로 화면 초기화
                 */
                case  R.id.initButton:
                    running = false;
                    initDisplay();
                    break;

                // (타이머 시작 전 편집 화면에서) backspace 버튼을 클릭할 시
                case R.id.backspaceButton:
                    focusedText.setText("00");
                    numberCount = 0;
                    break;

                // (타이머 시작 전 편집 화면에서) 다음 버튼을 클릭할 시 : 포커스 오른쪽으로 이동
                case R.id.nextButton:
                    focusOnNext();
                    break;
            }
        }
    }

    // 숫자 버튼을 클릭할 시의 리스너
    private class OnNumberButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Button clickedButton = (Button) view;

            String inputStr;
            int inputNum;

            // 이전에 입력된 숫자의 개수가 0개이고 숫자 X를 입력받았을 때 : "0X" 의 형식으로 숫자 출력, numberCount++
            if(numberCount == 0) {
                inputStr = "0" + clickedButton.getText().toString();

                focusedText.setText(inputStr);
                numberCount++;
            }
            // 이전에 입력된 숫자의 개수가 0보다 크고 이전에 입력된 숫자가 X, 현재 입력된 숫자가 Y 일 때
            else {
                // "XY" 형식으로 문자열 및 숫자 생성
                inputStr = focusedText.getText().toString();
                inputStr = inputStr.charAt(inputStr.length()-1) + clickedButton.getText().toString();
                inputNum = Integer.parseInt(inputStr);

                /** if 현재 포커스 된 뷰가 secInputText이고 텍스트가 "59"로 되어있을 때 : "0Y"로 변경
                 *  else if 현재 포커스 된 뷰가 hourInputText가 아니고 inputNum이 60보다 클 때 : "59"로 변경
                 *  else 현재 포커스  된 뷰의 텍스트를 "XY"로 변경
                 */
                if(focusedText.getId() == R.id.secInput
                        && focusedText.getText().toString().equals("59"))
                    focusedText.setText("0" + clickedButton.getText().toString());
                else if(focusedText.getId() != R.id.hourInput && inputNum >= 60)
                    focusedText.setText("59");
                else
                    focusedText.setText(inputStr);

                numberCount++;

                // 포커스를 오른쪽으로 이동
                focusOnNext();
            }
        }
    }

    // 포커스가 바뀌었을 시 호출되는 함수
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // 포커스가 변한 뷰를 가져옴
        TextView textView = (TextView) v;

        /** 포커스를 받았을 때 : 텍스트 색깔을 청록색으로 바꾸고 focusedText에 포커스 받은 뷰 저장
         * 포커스를 잃었을 때 : 텍스트 색깔을 흰 색으로 바꿈
         */
        if(hasFocus) {
            textView.setTextColor(Color.parseColor("#AA00AAFF"));
            focusedText = textView;
            numberCount = 0;
        }
        else {
            textView.setTextColor(Color.parseColor("#FFFFFFFF"));
        }

    }

    // 포커스를 이동하는 함수 : hour -> min -> sec (sec 에서는 이동하지 않음)
    private void focusOnNext() {
        switch (focusedText.getId()) {
            case R.id.hourInput:
                minInputText.requestFocus();
                numberCount = 0;
                break;
            case R.id.minInput:
                secInputText.requestFocus();
                numberCount = 0;
                break;
            case R.id.secInput:
                break;
        }
    }

    // 상단바에 Notification을 생성하는 함수
    private void makeNotification() {
        NotificationManager notificationManager= (NotificationManager)
                TimerActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        Intent intent1 = new Intent(getApplicationContext(), TimerActivity.class);

        // TimerActivity를 실행하는 한 번만 실행되는 Intent
        PendingIntent pendingNotificationIntent =
                PendingIntent.getActivity(getApplicationContext(),
                        0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        // 클릭 시 TimerActivity를 실행하는 Notification을 build함
        builder.setSmallIcon(R.drawable.clock)
                .setWhen(System.currentTimeMillis())
                .setNumber(1)
                .setContentTitle("타이머가 종료되었습니다.")
                .setContentIntent(pendingNotificationIntent)
                .setAutoCancel(true)
                .setOngoing(false);

        // Notification 생성
        if (Build.VERSION.SDK_INT < 16) {
            notificationManager.notify(1, builder.getNotification());
        } else {
            notificationManager.notify(1, builder.build());
        }
    }

    // 알람을 울리는 함수
    private void playAlarm() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        switch (audioManager.getRingerMode()) {
            // 소리모드 일 때 : 강아지 짖는 소리 재생, 1초간 진동
            case AudioManager.RINGER_MODE_NORMAL:
                MediaPlayer mediaPlayer;
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.puppy);
                mediaPlayer.start();
                vibrator.vibrate(new long[]{100, 1000}, 1);
                break;
            // 진동모드 일 때 : 1초간 진동
            case AudioManager.RINGER_MODE_VIBRATE:
                vibrator.vibrate(new long[]{100, 1000}, 1);
                break;
            // 무음모드 일 때 : 아무것도 하지 않음
            case AudioManager.RINGER_MODE_SILENT:
                break;
        }
    }

    // 현재 화면을 시작 버튼 누르기 전 화면으로 초기화 하는 함수
    private void initDisplay() {
        long hour = initialTime / 1000 / 3600;
        long min = initialTime / 1000 / 60 % 60;
        long sec = initialTime / 1000 % 60;

        hourInputText.setText(String.format("%02d", hour));
        minInputText.setText(String.format("%02d", min));
        secInputText.setText(String.format("%02d", sec));

        timeLayoutLP.weight = 5;

        pauseButton.setVisibility(View.VISIBLE);
        continueButton.setVisibility(View.GONE);
        controlButtons.setVisibility(View.GONE);
        keyboardLayout.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.VISIBLE);
        secInputText.setTextColor(Color.parseColor("#FFFFFFFF"));
        focusedText.setTextColor(Color.parseColor("#AA00AAFF"));
    }

}
