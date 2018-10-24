package com.gmail.at.boban.talevski.bakingapp.model;

import com.google.gson.annotations.Expose;

class Step {

    @Expose
    private int id;

    @Expose
    private String shortDescription;

    @Expose
    private String description;

    @Expose
    private String videoUrl;

    @Expose
    private String thumbnailURL;

    public Step(int id, String shortDescription, String description, String videoUrl, String thumbnailURL) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnailURL = thumbnailURL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
}
