package com.example.home_buddy_captain.initial_connection;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

public class LocationHelper {

    public interface LocationResult {
        void onReceived(String[] locationData); // [pincode, area, city]
    }

    // For Fragment
    public static void getLocation(Fragment fragment, LocationResult callback) {
        registerLaunchers(fragment, fragment.requireContext(), callback);
    }

    // For Activity
    public static void getLocation(FragmentActivity activity, LocationResult callback) {
        registerLaunchers(activity, activity, callback);
    }

    // Shared logic
    private static void registerLaunchers(
            LifecycleOwner owner,
            Context context,
            LocationResult callback
    ) {
        ActivityResultRegistry registry;

        if (owner instanceof Fragment) {
            registry = ((Fragment) owner).getActivity().getActivityResultRegistry();
        } else if (owner instanceof FragmentActivity) {
            registry = ((FragmentActivity) owner).getActivityResultRegistry();
        } else {
            throw new IllegalArgumentException("Unsupported LifecycleOwner type");
        }

        // Create dummy Fragment for context or rework LocationConnection
        ActivityResultLauncher<String>[] permissionLauncher = new ActivityResultLauncher[1];
        ActivityResultLauncher<IntentSenderRequest>[] gpsLauncher = new ActivityResultLauncher[1];

        permissionLauncher[0] = ((ActivityResultCaller) owner).registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        LocationConnection locationConnection = new LocationConnection(context, permissionLauncher[0], gpsLauncher[0]);
                        requestWithCallback(locationConnection, callback);
                    } else {
                        Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        gpsLauncher[0] = ((ActivityResultCaller) owner).registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        LocationConnection locationConnection = new LocationConnection(context, permissionLauncher[0], gpsLauncher[0]);
                        requestWithCallback(locationConnection, callback);
                    } else {
                        Toast.makeText(context, "GPS is required!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        LocationConnection locationConnection = new LocationConnection(context, permissionLauncher[0], gpsLauncher[0]);
        requestWithCallback(locationConnection, callback);
    }

    private static void requestWithCallback(LocationConnection locationConnection, LocationResult callback) {
        locationConnection.setLocationListener((pinCode, areaName, cityName) -> {
            callback.onReceived(new String[]{pinCode, areaName, cityName});
        });
        locationConnection.requestLocation();
    }
}