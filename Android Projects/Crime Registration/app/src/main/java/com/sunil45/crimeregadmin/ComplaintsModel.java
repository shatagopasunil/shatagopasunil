package com.sunil45.crimeregadmin;

import java.util.HashMap;

public class ComplaintsModel {
    private String additional, address, category, date, time, userid, victim;
    private HashMap<String, Object> Status;

    public ComplaintsModel() {
    }

    public ComplaintsModel(String additional, String address, String category, String date, String time, String userid, String victim, HashMap<String, Object> status) {
        this.additional = additional;
        this.address = address;
        this.category = category;
        this.date = date;
        this.time = time;
        this.userid = userid;
        this.victim = victim;
        Status = status;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getVictim() {
        return victim;
    }

    public void setVictim(String victim) {
        this.victim = victim;
    }

    public HashMap<String, Object> getStatus() {
        return Status;
    }

    public void setStatus(HashMap<String, Object> status) {
        Status = status;
    }
}
