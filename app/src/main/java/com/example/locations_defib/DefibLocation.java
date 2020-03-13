package com.example.locations_defib;

class DefibLocation {
    private String location;
    private String address;
    private String cityTown;
    private String county;
    private String postcode;
    private Double latitude;
    private Double longitude;

    @Override
    public String toString() {
        return "DefibLocation{" +
                ", location='" + location + '\'' +
                ", address='" + address + '\'' +
                ", cityTown='" + cityTown + '\'' +
                ", county='" + county + '\'' +
                ", postcode='" + postcode + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCityTown() {
        return cityTown;
    }

    public void setCityTown(String cityTown) {
        this.cityTown = cityTown;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
