package com.example.popularmovies.data;

import android.content.Context;

import com.example.popularmovies.model.Movie;

public class DataBaseOperations {

    public static void saveMovieToDataBase(final Movie movie, Context context) {
        final AppDataBase mDb = AppDataBase.getInstance(context);
        AppExecutors.getInstance().diskIO().execute(() -> mDb.movieDao().insertMovie(movie));
    }

    public static void deleteMovie(final Movie movie, Context context) {
        final AppDataBase mDb = AppDataBase.getInstance(context);
        AppExecutors.getInstance().diskIO().execute(() -> mDb.movieDao().deleteMovie(movie));
    }
}
