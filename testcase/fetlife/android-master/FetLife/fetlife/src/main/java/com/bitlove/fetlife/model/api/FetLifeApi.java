package com.bitlove.fetlife.model.api;

import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Conversation;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.FriendRequest;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Message;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Relationship;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Status;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Video;
import com.bitlove.fetlife.model.pojos.fetlife.json.AuthBody;
import com.bitlove.fetlife.model.pojos.fetlife.json.Feed;
import com.bitlove.fetlife.model.pojos.fetlife.json.Token;
import com.bitlove.fetlife.model.pojos.fetlife.json.VideoUploadResult;
import com.squareup.okhttp.ResponseBody;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface FetLifeApi {

    @POST("/api/oauth/token")
    Call<Token> login(@Query("client_id") String clientId, @Query("client_secret") String clientSecret, @Query("redirect_uri") String redirectUrl, @Body() AuthBody authBody);

    @FormUrlEncoded
    @POST("/api/oauth/token")
    Call<Token> refreshToken(@Query("client_id") String clientId, @Field("client_secret") String clientSecret, @Field("redirect_uri") String redirectUrl, @Field("grant_type") String grantType, @Field("refresh_token") String refreshToken);

    @GET("/api/v2/me")
    Call<Member> getMe(@Header("Authorization") String authHeader);

    @GET("/api/v2/me/conversations")
    Call<List<Conversation>> getConversations(@Header("Authorization") String authHeader, @Query("order_by") String orderBy, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/me/conversations/{conversationId}")
    Call<Conversation> getConversation(@Header("Authorization") String authHeader, @Path("conversationId") String conversationId);

    @GET("/api/v2/me/friends")
    Call<List<Member>> getFriends(@Header("Authorization") String authHeader, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/me/conversations/{conversationId}/messages")
    Call<List<Message>> getMessages(@Header("Authorization") String authHeader, @Path("conversationId") String conversationId, @Query("since_id") String sinceMessageId, @Query("until_id") String untilMessageId, @Query("limit") int limit);

    @GET("/api/v2/search/members")
    Call<List<Member>> searchMembers(@Header("Authorization") String authHeader, @Query("query") String query, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/members/{memberId}")
    Call<Member> getMember(@Header("Authorization") String authHeader, @Path("memberId") String memberId);

    @GET("/api/v2/members/{memberId}/latest_activity")
    Call<Feed> getMemberFeed(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/members/{memberId}/relationships")
    Call<List<Relationship>> getMemberRelationship(@Header("Authorization") String authHeader, @Path("memberId") String memberId);

    @GET("/api/v2/members/{memberId}/pictures")
    Call<List<Picture>> getMemberPictures(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/members/{memberId}/videos")
    Call<List<Video>> getMemberVideos(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/members/{memberId}/friends")
    Call<List<Member>> getMemberFriends(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/members/{memberId}/followers")
    Call<List<Member>> getMemberFollowers(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/members/{memberId}/following ")
    Call<List<Member>> getMemberFollowees(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/members/{memberId}/statuses")
    Call<List<Status>> getMemberStatuses(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @FormUrlEncoded
    @POST("/api/v2/me/conversations/{conversationId}/messages")
    Call<Message> postMessage(@Header("Authorization") String authHeader, @Path("conversationId") String conversationId, @Field("body") String body);

    @FormUrlEncoded
    @PUT("/api/v2/me/conversations/{conversationId}/messages/read")
    Call<ResponseBody> setMessagesRead(@Header("Authorization") String authHeader, @Path("conversationId") String conversationId, @Field("ids") String[] ids);

    @FormUrlEncoded
    @POST("/api/v2/me/conversations")
    Call<Conversation> postConversation(@Header("Authorization") String authHeader, @Field("user_id") String userId, @Field("subject") String subject, @Field("body") String body);

    @GET("/api/v2/me/friendrequests")
    Call<List<FriendRequest>> getFriendRequests(@Header("Authorization") String authHeader, @Query("filter") String filter, @Query("limit") int limit, @Query("page") int page);

    @PUT("/api/v2/me/friendrequests/{friendRequestId}")
    Call<FriendRequest> acceptFriendRequests(@Header("Authorization") String authHeader, @Path("friendRequestId") String friendRequestId);

    @DELETE("/api/v2/me/friendrequests/{friendRequestId}")
    Call<FriendRequest> cancelFriendRequest(@Header("Authorization") String authHeader, @Path("friendRequestId") String friendRequestId);

    @FormUrlEncoded
    @POST("/api/v2/me/friendrequests")
    Call<FriendRequest> createFriendRequest(@Header("Authorization") String authHeader, @Field("member_id") String friendId);

    @PUT("/api/v2/members/{memberId}/follow")
    Call<ResponseBody> createFollow(@Header("Authorization") String authHeader, @Path("memberId") String memberId);

    @DELETE("/api/v2/members/{memberId}/follow")
    Call<ResponseBody> removeFollow(@Header("Authorization") String authHeader, @Path("memberId") String memberId);

    @DELETE("api/v2/me/relations/{memberId}")
    Call<ResponseBody> removeFriendship(@Header("Authorization") String authHeader, @Path("memberId") String memberId);

    @GET("/api/v2/me/feed")
    Call<Feed> getFeed(@Header("Authorization") String authHeader, @Query("limit") int limit, @Query("page") int page);

    @PUT("/api/v2/me/loves/{content_type}/{content_id}")
    Call<ResponseBody> putLove(@Header("Authorization") String authHeader, @Path("content_id") String contentId, @Path("content_type") String contentType);

    @DELETE("/api/v2/me/loves/{content_type}/{content_id}")
    Call<ResponseBody> deleteLove(@Header("Authorization") String authHeader, @Path("content_id") String contentId, @Path("content_type") String contentType);

    @FormUrlEncoded
    @POST("/api/v2/me/videos/uploads")
    Call<VideoUploadResult> uploadVideoStart(@Header("Authorization") String authHeader, @Field("title") String title, @Field("description") String description, @Field("video_file_name") String videoFileName, @Field("only_friends") boolean friendsOnly, @Field("is_of_or_by_user") boolean isFromUser);

    @POST("/api/v2/me/videos/uploads/{video_upload_id}/finish")
    Call<ResponseBody> uploadVideoFinish(@Header("Authorization") String authHeader, @Path("video_upload_id") String videoUploadId);

}
