package com.example.hyunil.a15gym;

/**
 * Created by hyunil on 2017-11-29.
 */

public class GetUserInfo {
    private String name;
    private String height;
    private String weight;
    private String comment;

    public GetUserInfo(String name, String height, String weight, String comment) {
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.comment = comment;
    }

    public GetUserInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
