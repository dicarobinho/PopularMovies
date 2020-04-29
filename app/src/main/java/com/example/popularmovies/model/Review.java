package com.example.popularmovies.model;

import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("author")
    private
    String author;

    @SerializedName("content")
    private
    String content;

    @SerializedName("url")
    private
    String url;

    public Review(String author, String content, String url) {
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
