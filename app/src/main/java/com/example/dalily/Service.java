package com.example.dalily;


import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class Service {

    private String photo_uri;
    private String title;
    private String service_type;
    private String description;
    private String phone1;
    private String phone2;
    private double lat;
    private double lang;
    private double distance_from_center;
    private String id;


    public void setDistance_from_center(double distance_from_center) {
        this.distance_from_center = distance_from_center;
    }

    public Service()
   {

   }


    public Service(String photo_uri, String title, String service_type, String description,
                   String phone1, String phone2, double lat, double lang, String id)
    {
        this.photo_uri = photo_uri;
        this.title = title;
        this.service_type = service_type;
        this.description = description;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.lat = lat;
        this.lang = lang;
        this.distance_from_center = ReferencePoint.calcDistFromRefPoint(new LatLng(this.lang, this.lat));
        this.id = id;
    }

    public String getPhoto_uri() {
        return photo_uri;
    }

    public String getTitle() {
        return title;
    }

    public String getService_type() {
        return service_type;
    }

    public String getDescription() {
        return description;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhoto_uri(String photo_uri) {
        this.photo_uri = photo_uri;
    }

    public double getDistance_from_center() {
        return distance_from_center;
    }

    public double getLat() {
        return lat;
    }

    public double getLang() {
        return lang;
    }

    public String getId(){
        return id;
    }





}
