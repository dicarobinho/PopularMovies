package com.example.popularmovies.retrofit;

import com.example.popularmovies.model.PageMovies;
import com.example.popularmovies.model.PageReviews;
import com.example.popularmovies.model.PageVideos;
import com.example.popularmovies.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {

    private static ApiManager sApiManager;
    private static JsonPlaceHolderApi sApiInterface;

    private ApiManager() {
        Retrofit retrofitForced = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sApiInterface = retrofitForced.create(JsonPlaceHolderApi.class);
    }

    public static ApiManager getInstance() {
        if (sApiManager == null) {
            sApiManager = new ApiManager();
        }
        return sApiManager;
    }

    public void getPageMovies(int number, String apiKey, String sortType, Callback<PageMovies> callback) {
        Call<PageMovies> response = sApiInterface.getPageMovies(number, apiKey, sortType);
        response.enqueue(callback);
    }

    public void getPageVideos(String id, String apiKey, Callback<PageVideos> callback) {
        Call<PageVideos> response = sApiInterface.getPageVideos(id, apiKey);
        response.enqueue(callback);
    }

    public void getPageReviews(String id, int number, String apiKey, Callback<PageReviews> callback) {
        Call<PageReviews> response = sApiInterface.getPageReviews(id, number, apiKey);
        response.enqueue(callback);
    }
}
