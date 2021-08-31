package com.example.socialmedia.notifications;

public class Data {
    private String user;
    private String title;
    private String body;
    private String sent;
    private Integer icon;

    public Data() {
    }

    public Data(String user, String title, String body, String sent, Integer icon) {
        this.user = user;
        this.title = title;
        this.body = body;
        this.sent = sent;
        this.icon = icon;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Data{" +
                "user='" + user + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", sent='" + sent + '\'' +
                ", icon=" + icon +
                '}';
    }
}
