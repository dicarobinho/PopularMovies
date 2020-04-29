package com.example.popularmovies;

import android.app.Application;
import com.example.popularmovies.data.Repository;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.PageReviews;
import com.example.popularmovies.model.PageVideos;
import com.example.popularmovies.utils.ResultsDisplay;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class DetailsActivityViewModel extends AndroidViewModel {
    private final Repository repository;

    public DetailsActivityViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    LiveData<ResultsDisplay<PageVideos>> getVideosFromServer(String id, String apiKey) {
        return repository.getVideosFromServer(id, apiKey);
    }

    LiveData<ResultsDisplay<PageReviews>> getReviewsFromServer(String id, int number, String apiKey) {
        return repository.getReviewsFromServer(id, number, apiKey);
    }

    LiveData<Movie> getMovieById(String id) {
        return repository.loadMovieById(id);
    }
}


