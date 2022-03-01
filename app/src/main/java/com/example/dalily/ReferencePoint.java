package com.example.dalily;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class ReferencePoint{

    private static final LatLng ASSIUT_LATLNG = new LatLng(31.165289599999998, 27.1805965);
    //private static final LatLng LUXOR_LATLNG = new LatLng(32.7351822, 25.773687499999998);


    public static double calcDistFromRefPoint(LatLng Service_LatLng)
    {
        Location a = new Location("point a");
        a.setLatitude(Service_LatLng.latitude);
        a.setLongitude(Service_LatLng.longitude);

        Location b = new Location("point b");
        b.setLatitude(ASSIUT_LATLNG.latitude);
        b.setLongitude(ASSIUT_LATLNG.longitude);
        double result = a.distanceTo(b);
        return result;
        //float[] results = null;
        //return (Location.distanceBetween(Service_LatLng.latitude, Service_LatLng.longitude, LUXOR_LATLNG.latitude, LUXOR_LATLNG.longitude, results));

        //return SphericalUtil.computeDistanceBetween(Service_LatLng, LUXOR_LATLNG);
    }
}
