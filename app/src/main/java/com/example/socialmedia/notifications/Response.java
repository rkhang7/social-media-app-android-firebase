package com.example.socialmedia.notifications;

public class Response {
    private String success;

    public Response(String success) {
        this.success = success;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "Response{" +
                "success='" + success + '\'' +
                '}';
    }
}
