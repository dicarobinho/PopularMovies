package com.example.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PageMovies {

    @SerializedName("page")
    private
    int page;

    @SerializedName("total_results")
    private
    int totalResults;

    @SerializedName("total_pages")
    private
    int totalPages;

    @SerializedName("results")
    private
    List<Movie> moviesList;

    public PageMovies(){}

    public PageMovies(List<Movie> moviesList, int page, int totalResults, int totalPages) {
        this.page = page;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
        this.moviesList = moviesList;
    }

    public List<Movie> getMoviesList() {
        return moviesList;
    }

    public void setMoviesList(List<Movie> moviesList) {
        this.moviesList = moviesList;
    }

    public int getPage() {
        return page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
