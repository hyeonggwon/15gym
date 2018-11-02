package com.example.hyunil.a15gym;

/**
 * 공지사항 화면에서 출력되는 ListView의 item 하나에 해당되는 클래스
 * key : 데이터베이스 내에서 item이 저장된 디렉토리의 키
 * title : 공지사항의 제목
 * content : 공지사항의 내용
 * date : 공지사항 작성 시간
 * resId : 공지사항 아이콘의 resource id
 */
class NoticeItem {

    private String key;
    private String title;
    private String content;
    private String date;
    private int resId;

    NoticeItem() {}

    NoticeItem(String key, String title, String content, String date, int resId) {
        this.key = key;
        this.title = title;
        this.content = content;
        this.date = date;
        this.resId = resId;
    }

    String getKey() { return key; }

    void setKey(String key) { this.key = key; }

    String getTitle() { return title; }

    void setTitle(String title) { this.title = title; }

    String getContent() { return content; }

    void setContent(String content) { this.content = content; }

    String getDate() { return date; }

    void setDate(String date) { this.date = date; }

    int getResId() { return resId; }

    void setResId(int resId) { this.resId = resId; }
}
