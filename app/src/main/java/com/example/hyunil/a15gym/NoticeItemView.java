package com.example.hyunil.a15gym;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 공지사항 화면에서 출력되는 ListView의 item 하나의 화면에 출력될 View를 담당하는 클래스
 * titleText : 공지사항의 제목이 출력될 TextView
 * dateText : 공지사항의 작성 시간이 출력될 TextView
 * iconImage : 공지사항의 아이콘 출력될 ImageView
 */
class NoticeItemView extends LinearLayout{

    TextView titleText;
    TextView dateText;
    ImageView iconImage;

    public NoticeItemView(Context context) {
        super(context);
        init(context);
    }

    public NoticeItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init (Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.notice_item, this, true);

        titleText = (TextView) findViewById(R.id.titleText);
        dateText = (TextView) findViewById(R.id.dateText);
        iconImage = (ImageView) findViewById(R.id.iconImage);
    }

    void setTitle(String title) { titleText.setText(title); }

    void setDate(String date) { dateText.setText(date); }

    void setImage(int resId) { iconImage.setImageResource(resId); }

}
