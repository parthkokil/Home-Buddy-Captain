package com.example.home_buddy_captain.initial_connection;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Firebase_Connection extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initializeSecondDB(getApplicationContext());
    }

    public static synchronized DatabaseReference initializeSecondDB(Context context) {
        FirebaseApp secondaryApp = null;

        // Check if the secondary app is already initialized
        for (FirebaseApp app : FirebaseApp.getApps(context)) {
            if (app.getName().equals("secondary")) {
                secondaryApp = app;
                break;
            }
        }

        // If secondary app is not found, initialize it
        if (secondaryApp == null) {
//            This is calling Firebase Home-Buddy User Database.
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setProjectId("newhomieshifter")
                    .setApplicationId("1:720361896731:android:c916fd9ff12582721a31be") // from mobilesdk_app_id
                    .setApiKey("AIzaSyDPRMWrVxYD4FhlxO2wSmEskyvAuRkxMOo")               // from current_key
                    .setDatabaseUrl("https://newhomieshifter-default-rtdb.firebaseio.com/") // inferred from project_id
                    .build();

            secondaryApp = FirebaseApp.initializeApp(context, options, "secondary");
        }

        FirebaseDatabase secondaryDatabase = FirebaseDatabase.getInstance(secondaryApp);
        return secondaryDatabase.getReference();
    }


    public static DatabaseReference get2ndReference(Context context){
        return initializeSecondDB(context);
    }
}
