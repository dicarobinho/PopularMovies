package com.example.popularmovies.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.popularmovies.model.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> loadAllMovies();

    @Insert
    void insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("SELECT * FROM movies WHERE id = :id")
    LiveData<Movie> loadMovieById(String id);
}
