package com.example.socialmedia.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers({
            "Content-Type: application/json",
            "Authorization: key=AAAA8TyWNHA:APA91bFrQxpBuw4XcoOzGFePf76CCge88iKUtpGQ34ibm1I4_QRz75EQlO2-gkKPMpzs3hAF71g70Ea8x4y6-0zqTj2NDki-c2El26eaMchX9qb2kFPm8KRYD3gst81LtywDZiBKYMN1"
    })
    @POST("/fcm/send")
    Call<Response> postData(@Body Sender sender);

}
