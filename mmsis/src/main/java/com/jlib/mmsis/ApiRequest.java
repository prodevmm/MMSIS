package com.jlib.mmsis;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiRequest {

    @GET("fileDb.jsp")
    Call<ResponseBody> getResponse(@Query("code_code") String code);

    @FormUrlEncoded
    @POST("to_filedown")
    Call<ResponseBody> getFile(@Field("dirsub") String dirsub, @Field("filemask") String filemask, @Field("filename") String filename);

    @FormUrlEncoded
    @POST("contents_fileDb.jsp")
    Call<ResponseBody> loadPage(@Field("code_code") String code, @Field("pagelistscale") String page_list_scale, @Field("pg") String pg);


}
