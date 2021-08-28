package com.example.socialmedia.models;

public class User {
    private String uid;
    private String email;
    private String name;
    private String phone;
    private String image;
    private String cover;

    public User() {
    }

    public User(String uid, String email, String name, String phone, String image, String cover) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.cover = cover;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", image='" + image + '\'' +
                ", cover='" + cover + '\'' +
                '}';
    }
}
