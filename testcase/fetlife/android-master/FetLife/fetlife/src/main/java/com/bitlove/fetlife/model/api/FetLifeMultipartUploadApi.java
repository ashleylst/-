package com.bitlove.fetlife.model.api;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;

public interface FetLifeMultipartUploadApi {

    @Multipart
    @POST("/api/v2/me/pictures")
    Call<ResponseBody> uploadPicture(@Header("Authorization") String authHeader, @Part("picture\"; filename=\"android_app.png\" ") RequestBody picture, @Part("is_avatar") RequestBody isAvatar, @Part("only_friends") RequestBody friendsOnly, @Part("caption") RequestBody caption, @Part("is_of_or_by_user") RequestBody isFromUser);
    //TODO: solve dynamic file name
    //https://github.com/square/retrofit/issues/1063

    @Multipart
    @PUT("/api/v2/me/videos/uploads/{video_upload_id}")
    Call<ResponseBody> uploadVideoPart(@Header("Authorization") String authHeader, @Path("video_upload_id") String videoUploadId, @Part("file\"; filename=\"android_app.part\" ") RequestBody video, @Part("number") RequestBody number);
    //TODO: solve dynamic file name
    //https://github.com/square/retrofit/issues/1063

}
