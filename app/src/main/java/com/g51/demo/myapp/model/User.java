package com.g51.demo.myapp.model;

import java.io.Serializable;

public class User implements Serializable
{
    public String name;
    public String image;
    public String email;
    public String token;
    public String id;

    public User(String name, String image, String email, String token, String id, String status, String imageUri) {
        this.name = name;
        this.image = image;
        this.email = email;
        this.token = token;
        this.id = id;
        this.status = status;
        this.imageUri = imageUri;
    }

    public String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String imageUri;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
