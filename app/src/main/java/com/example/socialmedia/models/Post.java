package com.example.socialmedia.models;

public class Post {
    private String pId;
    private String pTitle;
    private String pDescription;
    private String pImage;
    private String pTime;
    private String uid;
    private String uName;
    private String uEmail;
    private String uAvatar;

    public Post() {
    }

    public Post(String pId, String pTitle, String pDescription, String pImage, String pTime, String uid, String uName, String uEmail, String uAvatar) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pDescription = pDescription;
        this.pImage = pImage;
        this.pTime = pTime;
        this.uid = uid;
        this.uName = uName;
        this.uEmail = uEmail;
        this.uAvatar = uAvatar;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpDescription() {
        return pDescription;
    }

    public void setpDescription(String pDescription) {
        this.pDescription = pDescription;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuAvatar() {
        return uAvatar;
    }

    public void setuAvatar(String uAvatar) {
        this.uAvatar = uAvatar;
    }

    @Override
    public String toString() {
        return "Post{" +
                "pId='" + pId + '\'' +
                ", pTitle='" + pTitle + '\'' +
                ", pDescription='" + pDescription + '\'' +
                ", pImage='" + pImage + '\'' +
                ", pTime='" + pTime + '\'' +
                ", uid='" + uid + '\'' +
                ", uName='" + uName + '\'' +
                ", uEmail='" + uEmail + '\'' +
                ", uAvatar='" + uAvatar + '\'' +
                '}';
    }
}
