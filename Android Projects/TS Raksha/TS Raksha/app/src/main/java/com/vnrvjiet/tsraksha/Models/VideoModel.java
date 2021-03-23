package com.vnrvjiet.tsraksha.Models;

public class VideoModel {
    private String title, url, date;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getDate() {
        return date;
    }

    public VideoModel(String title, String url, String date) {
        this.title = title;
        this.url = url;
        this.date = date;
    }

    public VideoModel() {
    }
}
