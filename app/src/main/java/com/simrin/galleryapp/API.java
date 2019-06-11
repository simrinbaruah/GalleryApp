package com.simrin.galleryapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {
    String BASE_URL = "https://api.flickr.com/";

    @GET("services/rest/")
    Call<PhotoList>  getPhoto(@Query("method")String method, @Query("per_page") String per_page,
                              @Query("page") String page, @Query("api_key") String api_key, @Query("format") String format,
                              @Query("nojsoncallback") String nojsoncallback, @Query("extras") String extras);
}
