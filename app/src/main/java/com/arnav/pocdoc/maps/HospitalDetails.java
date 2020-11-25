package com.arnav.pocdoc.maps;


import java.io.Serializable;
import java.util.Arrays;

import androidx.annotation.NonNull;

class HospitalDetails implements Serializable {
    private String hospitalName;
    private String rating;
    private String openingHours;
    private String address;
    private double[] locationlatlng;

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double[] getLocationlatlng() {
        return locationlatlng;
    }

    public void setLocationlatlng(double[] latlng) {
        this.locationlatlng = latlng;
    }


    @NonNull
    @Override
    public String toString() {
        return "NearbyHospitalsDetail{" +
                ", hospitalName='" + hospitalName + '\'' +
                ", rating=" + rating +
                ", openingHours='" + openingHours + '\'' +
                ", address='" + address + '\'' +
                ", geometry=" + Arrays.toString(locationlatlng) +
                '}';
    }
}
