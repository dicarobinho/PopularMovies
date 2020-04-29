package com.example.popularmovies.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "movies")
public class Movie implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    @NonNull
    private String id;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    private String originalTitle;

    @ColumnInfo(name = "poster_path")
    @SerializedName("poster_path")
    private String thumbnail;

    @ColumnInfo(name = "vote_average")
    @SerializedName("vote_average")
    private float voteAverage;

    @ColumnInfo(name = "overview")
    @SerializedName("overview")
    private String overview;

    @ColumnInfo(name = "release_date")
    @SerializedName("release_date")
    private String date;

    @ColumnInfo(name = "popularity")
    @SerializedName("popularity")
    private float popularity;

    @Ignore
    public Movie() {
    }

    public Movie(@NonNull String id, String originalTitle, String thumbnail, float voteAverage, String overview, String date, float popularity) {
        this.originalTitle = originalTitle;
        this.thumbnail = thumbnail;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.date = date;
        this.popularity = popularity;
        this.id = id;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setOriginalTitle(@NonNull String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public float getPopularity() {
        return popularity;
    }

    @NonNull
    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public String getDate() {
        return date;
    }
}
