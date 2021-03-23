package com.vnrvjiet.tsrakshaadmin.Models;

public class TestingCenterModel {
    String name,type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TestingCenterModel() {
    }

    public TestingCenterModel(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
