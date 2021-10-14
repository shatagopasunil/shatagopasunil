package com.sunil45.crimeregistration;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class ComplaintsModel implements Serializable {
    private String additional, address, category, date, time, userid, victim, pincode;
    private HashMap<String, Object> Status;

    public ComplaintsModel() {
    }

    public ComplaintsModel(String additional, String address, String category, String date, String time, String userid, String victim, HashMap<String, Object> status, String pincode) {
        this.additional = additional;
        this.address = address;
        this.category = category;
        this.date = date;
        this.time = time;
        this.userid = userid;
        this.victim = victim;
        this.pincode = pincode;
        Status = status;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
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
