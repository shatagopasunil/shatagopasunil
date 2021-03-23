package com.vnrvjiet.tsrakshaadmin.Models;

public class VideoModel {
    String title, url, date;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public VideoModel(String title, String url, String date) {
        this.title = title;
        this.url = url;
        this.date = date;
    }

    public VideoModel() {
    }
}
