package com.g51.demo.myapp.model;

import java.util.Date;

public class Post {
    private String keyUserId;
    private String daiDien;
    private String name;
    private String staus;
    private String imgUri;
    private String idpost;

    private Date date;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String time;

    public String getIdpost() {
        return idpost;
    }

    public void setIdpost(String idpost) {
        this.idpost = idpost;
    }


    public String getKeyUserId() {
        return keyUserId;
    }

    public void setKeyUserId(String keyUserId) {
        this.keyUserId = keyUserId;
    }
    public Post() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Post(String keyUserId, String daiDien, String name, String staus, String imgUri, String time, String idpost, Date date) {
        this.keyUserId = keyUserId;
        this.daiDien = daiDien;
        this.name = name;
        this.staus = staus;
        this.imgUri = imgUri;
        this.time = time;
        this.idpost=idpost;
        this.date = date;
    }

    public String getDaiDien() {
        return daiDien;
    }

    public void setDaiDien(String daiDien) {
        this.daiDien = daiDien;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStaus() {
        return staus;
    }

    public void setStaus(String staus) {
        this.staus = staus;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }
}
