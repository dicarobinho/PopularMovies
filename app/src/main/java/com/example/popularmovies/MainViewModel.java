package com.example.popularmovies;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.popularmovies.data.AppDataBase;
import com.example.popularmovies.data.Repository;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.PageMovies;
import com.example.popularmovies.utils.ResultsDisplay;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<Movie>> movies;
    private final Repository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDataBase database = AppDataBase.getInstance(this.getApplication());
        movies = database.movieDao().loadAllMovies();
        repository = Repository.getInstance(application);
    }

    LiveData<ResultsDisplay<PageMovies>> getMoviesFromServer(int page, String apiKey, String sortType) {
        return repository.getMoviesFromServer(page, apiKey, sortType);
    }

    LiveData<List<Movie>> getMovies() {
        return movies;
    }
}
