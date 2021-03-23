package com.vnrvjiet.tsraksha.Models;

public class HelpLineModel {
    private String phone1,phone2,email;

    public HelpLineModel() {
    }

    public HelpLineModel(String phone1, String phone2) {
        this.phone1 = phone1;
        this.phone2 = phone2;
    }

    public String getPhone1() {
        return phone1;
    }


    public String getPhone2() {
        return phone2;
    }

    public String getEmail() {
        return email;
    }
}
