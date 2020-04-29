package com.example.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PageReviews {

    @SerializedName("page")
    private
    int page;

    @SerializedName("id")
    private
    int movieId;

    @SerializedName("results")
    private
    List<Review> reviewList;

    public PageReviews(int page, int movieId, List<Review> reviewList) {
        this.page = page;
        this.movieId = movieId;
        this.reviewList = reviewList;
    }

    public int getPage() {
        return page;
    }

    public int getMovieId() {
        return movieId;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }
}
