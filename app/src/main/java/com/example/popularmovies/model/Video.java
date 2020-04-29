package com.example.popularmovies.model;

import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("key")
    private
    String key;

    @SerializedName("name")
    private
    String name;

    @SerializedName("site")
    private
    String site;

    @SerializedName("type")
    private
    String type;

    public Video(String key, String name, String site, String type) {
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getType() {
        return type;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void setType(String type) {
        this.type = type;
    }
}
