package com.jlib.mmsis;

import retrofit2.Retrofit;

public class ApiUtil {
    public static ApiRequest getRequest(String url){
        return build(url).create(ApiRequest.class);
    }

    private static Retrofit build(String url){
        return new Retrofit.Builder().baseUrl(url).build();
    }
}
