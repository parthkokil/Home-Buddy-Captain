package com.example.home_buddy_captain.initial_connection;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

public class LocationConnection {
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final ActivityResultLauncher<String> permissionLauncher;
    private final ActivityResultLauncher<IntentSenderRequest> gpsLauncher;
    private LocationListener locationListener;

    public LocationConnection(Context context,
                              ActivityResultLauncher<String> permissionLauncher,
                              ActivityResultLauncher<IntentSenderRequest> gpsLauncher) {
        this.context = context;
        this.permissionLauncher = permissionLauncher;
        this.gpsLauncher = gpsLauncher;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    // Call this method every time the app starts
    public void requestLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            checkGPSStatus();
        }
    }

    // Check if GPS is enabled
    private void checkGPSStatus() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> getUserLocation());

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(resolvable.getResolution()).build();
                    gpsLauncher.launch(intentSenderRequest);
                } catch (Exception sendEx) {
                    Toast.makeText(context, "Failed to enable GPS", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "GPS is not available on this device", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Get the user's current location
    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Location permission required", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        getAddressFromLocation(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(context, "Unable to retrieve location", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // Convert latitude & longitude to address & get the PIN code
    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String postalCode = address.getPostalCode();
                String areaName = capitalizeWords(address.getSubLocality());
                String cityName = capitalizeWords(address.getLocality());
                if (locationListener != null) {
                    locationListener.onLocationReceived(postalCode, areaName, cityName);
                }
            } else {
                Toast.makeText(context, "Unable to get address", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Geocoder failed", Toast.LENGTH_SHORT).show();
        }
    }

    private String capitalizeWords(String input) {
        if (input == null || input.trim().isEmpty()) return "";
        StringBuilder result = new StringBuilder();
        for (String word : input.trim().split(" ")) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return result.toString().trim();
    }

    public void setLocationListener(LocationListener listener) {
        this.locationListener = listener;
    }

    public interface LocationListener {
        void onLocationReceived(String pinCode, String areaName, String cityName);
    }

    /** Callback interface for async sub-locality results */
    public interface SubLocalityCallback {
        void onResult(String[] subLocalities);
    }

    /**
     * Fetches sub-localities for a given city ASYNCHRONOUSLY on a background thread.
     *
     * OLD code called this synchronously (returned String[]) and ran 121 Geocoder
     * network calls on the main thread — causing a 25-second freeze and ANR.
     *
     * NEW: runs on a background Thread, delivers results via SubLocalityCallback
     * on the main thread.
     */
    public void getSubLocalitiesForCity(String cityName, SubLocalityCallback callback) {
        if (cityName == null || cityName.trim().isEmpty()) {
            Log.e("LocationConnection", "City name is null or empty!");
            callback.onResult(new String[0]);
            return;
        }

        new Thread(() -> {
            Set<String> subLocalitiesSet = new HashSet<>();
            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

            try {
                List<Address> addresses = geocoder.getFromLocationName(cityName, 1);
                if (addresses == null || addresses.isEmpty()) {
                    Log.e("LocationConnection", "City not found!");
                    postToMain(callback, new String[0]);
                    return;
                }

                Address cityAddress = addresses.get(0);
                double baseLat = cityAddress.getLatitude();
                double baseLng = cityAddress.getLongitude();

                // 121 Geocoder calls — safe now because we're on a background thread
                for (double lat = baseLat - 0.05; lat <= baseLat + 0.05; lat += 0.01) {
                    for (double lng = baseLng - 0.05; lng <= baseLng + 0.05; lng += 0.01) {
                        List<Address> nearbyAddresses = geocoder.getFromLocation(lat, lng, 1);
                        if (nearbyAddresses != null && !nearbyAddresses.isEmpty()) {
                            String subLocality = nearbyAddresses.get(0).getSubLocality();
                            if (subLocality != null && !subLocality.isEmpty()) {
                                subLocalitiesSet.add(capitalizeWords(subLocality.trim().toLowerCase(Locale.ENGLISH)));
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            postToMain(callback, subLocalitiesSet.toArray(new String[0]));
        }).start();
    }

    /** Deliver callback results on the main thread */
    private void postToMain(SubLocalityCallback callback, String[] result) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onResult(result));
    }
}
