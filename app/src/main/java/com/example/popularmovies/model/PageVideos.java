package com.example.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PageVideos {

    @SerializedName("id")
    private
    int idMovie;

    @SerializedName("results")
    private
    List<Video> videosList;

    public PageVideos(int idMovie, List<Video> videosList) {
        this.idMovie = idMovie;
        this.videosList = videosList;
    }

    public int getIdMovie() {
        return idMovie;
    }

    public List<Video> getVideosList() {
        return videosList;
    }

    public void setIdMovie(int idMovie) {
        this.idMovie = idMovie;
    }

    public void setVideosList(List<Video> videosList) {
        this.videosList = videosList;
    }
}
