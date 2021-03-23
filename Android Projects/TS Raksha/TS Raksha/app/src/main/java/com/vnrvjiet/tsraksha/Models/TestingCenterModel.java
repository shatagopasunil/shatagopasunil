package com.vnrvjiet.tsraksha.Models;

public class TestingCenterModel {
    private String name,type;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public TestingCenterModel(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public TestingCenterModel() {
    }
}
