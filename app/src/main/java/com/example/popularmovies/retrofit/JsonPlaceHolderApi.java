package com.example.popularmovies.retrofit;

import com.example.popularmovies.model.PageMovies;
import com.example.popularmovies.model.PageReviews;
import com.example.popularmovies.model.PageVideos;
import com.example.popularmovies.utils.Constants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {

    @GET(Constants.MOVIES)
    Call<PageMovies> getPageMovies(@Query("page") int number, @Query("api_key") String apiKey, @Query("sort_by") String sortType);

    @GET("/3/movie/{id}/videos")
    Call<PageVideos> getPageVideos(@Path("id") String id, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/reviews")
    Call<PageReviews> getPageReviews(@Path("id") String id, @Query("page") int number, @Query("api_key") String apiKey);
}
