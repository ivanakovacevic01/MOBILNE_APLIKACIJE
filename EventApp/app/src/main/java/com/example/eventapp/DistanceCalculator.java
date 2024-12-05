package com.example.eventapp;

import android.location.Address;
import android.location.Geocoder;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.List;

public class DistanceCalculator {

    public static double calculateDistanceBetweenAddresses(Geocoder geocoder, String address1, String address2) {
        try {
            // Convert addresses to GeoPoints
            GeoPoint geoPoint1 = getGeoPointFromAddress(geocoder, address1);
            GeoPoint geoPoint2 = getGeoPointFromAddress(geocoder, address2);

            // Calculate distance between GeoPoints
            return calculateDistance(geoPoint1, geoPoint2);
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // Return -1 to indicate an error
        }
    }

    private static GeoPoint getGeoPointFromAddress(Geocoder geocoder, String address) throws IOException {
        List<Address> addressList = geocoder.getFromLocationName(address, 1);
        if (addressList != null && addressList.size() > 0) {
            Address location = addressList.get(0);
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            return new GeoPoint(latitude, longitude);
        } else {
            throw new IOException("Unable to find coordinates for address: " + address);
        }
    }

    private static double calculateDistance(GeoPoint startPoint, GeoPoint endPoint) {
        double earthRadius = 6371000; // Radius of the earth in meters

        // Convert latitude and longitude from degrees to radians
        double lat1 = Math.toRadians(startPoint.getLatitude());
        double lon1 = Math.toRadians(startPoint.getLongitude());
        double lat2 = Math.toRadians(endPoint.getLatitude());
        double lon2 = Math.toRadians(endPoint.getLongitude());

        // Differences in latitude and longitude
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        // Haversine formula
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        return distance;
    }
}
