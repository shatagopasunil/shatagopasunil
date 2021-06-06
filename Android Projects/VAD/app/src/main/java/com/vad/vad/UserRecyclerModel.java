package com.vad.vad;

public class UserRecyclerModel {
    private String licence, name, status;
    private long id;

    public UserRecyclerModel() {
    }

    public UserRecyclerModel(String licence, String name, long id, String status) {
        this.licence = licence;
        this.name = name;
        this.id = id;
        this.status = status;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
