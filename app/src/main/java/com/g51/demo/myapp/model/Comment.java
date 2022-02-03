package com.g51.demo.myapp.model;

import java.util.Date;

public class Comment {

    private String userImg,username;
    private String usercomment;
    private String time;
    private String postid;
    private Date date;

    public Comment() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Comment(String userImg, String username, String usercomment, String time, String postid, Date date) {
        this.userImg = userImg;
        this.username = username;
        this.usercomment = usercomment;
        this.time = time;
        this.postid = postid;
        this.date = date;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsercomment() {
        return usercomment;
    }

    public void setUsercomment(String usercomment) {
        this.usercomment = usercomment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }


}
