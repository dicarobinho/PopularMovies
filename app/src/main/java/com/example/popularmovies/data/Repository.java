package com.example.popularmovies.data;

import android.app.Application;
import android.util.Log;

import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.PageMovies;
import com.example.popularmovies.model.PageReviews;
import com.example.popularmovies.model.PageVideos;
import com.example.popularmovies.retrofit.ApiManager;
import com.example.popularmovies.utils.ResultsDisplay;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private static volatile Repository sInstance;
    private final MovieDao movieDao;

    private MutableLiveData<ResultsDisplay<PageMovies>> mMoviesObservable;
    private MutableLiveData<ResultsDisplay<PageVideos>> mVideosObservable;
    private MutableLiveData<ResultsDisplay<PageReviews>> mReviewsObservable;

    private Repository(final Application application) {
        AppDataBase appDatabase = AppDataBase.getInstance(application);
        movieDao = appDatabase.movieDao();
    }

    public static Repository getInstance(final Application application) {
        if (sInstance == null) {
            synchronized (Repository.class) {
                if (sInstance == null) {
                    sInstance = new Repository(application);
                }
            }
        }
        return sInstance;
    }

    public LiveData<ResultsDisplay<PageMovies>> getMoviesFromServer(int page, String apiKey, String sortType) {
        mMoviesObservable = new MutableLiveData<>();
        mMoviesObservable.setValue(null);

        ApiManager.getInstance().getPageMovies(page, apiKey, sortType, new Callback<PageMovies>() {
            @Override
            public void onResponse(@NonNull Call<PageMovies> call, @NonNull Response<PageMovies> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        PageMovies page = response.body();
                        mMoviesObservable.setValue(ResultsDisplay.success(page));
                    }
                } else
                    mMoviesObservable.setValue(ResultsDisplay.error(String.valueOf(response.code()), null));
            }

            @Override
            public void onFailure(@NonNull Call<PageMovies> call, @NonNull Throwable t) {
                Log.v("Error", "Http Movies Error");
                mMoviesObservable.setValue(ResultsDisplay.error(t.getMessage(), null));
            }
        });

        return mMoviesObservable;
    }

    public LiveData<ResultsDisplay<PageVideos>> getVideosFromServer(String id, String apiKey) {
        mVideosObservable = new MutableLiveData<>();
        mVideosObservable.setValue(null);

        ApiManager.getInstance().getPageVideos(id, apiKey, new Callback<PageVideos>() {
            @Override
            public void onResponse(@NonNull Call<PageVideos> call, @NonNull Response<PageVideos> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        PageVideos page = response.body();
                        mVideosObservable.setValue(ResultsDisplay.success(page));
                    }
                } else
                    mVideosObservable.setValue(ResultsDisplay.error(String.valueOf(response.code()), null));
            }

            @Override
            public void onFailure(@NonNull Call<PageVideos> call, @NonNull Throwable t) {
                Log.v("Error", "Http Videos Error");
                mVideosObservable.setValue(ResultsDisplay.error(t.getMessage(), null));
            }
        });

        return mVideosObservable;
    }

    public LiveData<ResultsDisplay<PageReviews>> getReviewsFromServer(String id, int number, String apiKey) {
        mReviewsObservable = new MutableLiveData<>();
        mReviewsObservable.setValue(null);

        ApiManager.getInstance().getPageReviews(id, number, apiKey, new Callback<PageReviews>() {
            @Override
            public void onResponse(@NonNull Call<PageReviews> call, @NonNull Response<PageReviews> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        PageReviews page = response.body();
                        mReviewsObservable.setValue(ResultsDisplay.success(page));
                    }
                } else
                    mReviewsObservable.setValue(ResultsDisplay.error(String.valueOf(response.code()), null));
            }

            @Override
            public void onFailure(@NonNull Call<PageReviews> call, @NonNull Throwable t) {
                Log.v("Error", "Http Reviews Error");
                mReviewsObservable.setValue(ResultsDisplay.error(t.getMessage(), null));
            }
        });

        return mReviewsObservable;
    }

    public LiveData<Movie> loadMovieById(String id) {
        return movieDao.loadMovieById(id);
    }
}
