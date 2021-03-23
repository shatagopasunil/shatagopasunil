package com.vnrvjiet.tsraksha.Models;

public class Hospitals {
    private String name, address, location, latitude, longitude;

    public String getName() {
        return name;
    }


    public String getAddress() {
        return address;
    }

    public String getLocation() {
        return location;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public Hospitals(String name, String address, String location, String latitude, String longitude) {
        this.name = name;
        this.address = address;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Hospitals() {
    }
}
