package com.example.home_buddy_captain.fragment_page;

import android.content.Context;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.home_buddy_captain.R;
import com.example.home_buddy_captain.initial_connection.LocationConnection;
import com.example.home_buddy_captain.initial_connection.LocationHelper;
import com.example.home_buddy_captain.model.NewServiceManModel;
import com.example.home_buddy_captain.model.NewUserModel;
import com.example.home_buddy_captain.model.Work_Request_Model;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment {


    private Context context;
    private RecyclerView workRequestRecyclerView;
    private ShimmerFrameLayout workRequestShimmer;

    private Work_Request_Adapter workRequestAdapter;

    private TextView greetingText, displayUsername;
    private String displayName;
    private String serviceCat;

    TextView locationView;
    private LottieAnimationView noDataAnimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View homeFrag = inflater.inflate(R.layout.fragment_home, container, false);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        fetchUserDetails(firebaseUser);

        displayUsername = homeFrag.findViewById(R.id.display_name);
        greetingText = homeFrag.findViewById(R.id.greeting_text);
        noDataAnimation = homeFrag.findViewById(R.id.no_data_animation); // ADDED CODE
        greetingUserFuntion();



        workRequestRecyclerView = homeFrag.findViewById(R.id.work_request_recyclerView);
        workRequestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // firebase options method
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference requestsRef = firebaseDatabase.child("service_requests").child("pending").child(firebaseUser.getUid());

        FirebaseRecyclerOptions<Work_Request_Model> options =
                new FirebaseRecyclerOptions.Builder<Work_Request_Model>()
                        .setQuery(requestsRef, Work_Request_Model.class)
                        .build();

        System.out.println("UID : " + firebaseUser.getUid());



        workRequestAdapter = new Work_Request_Adapter(options, getContext());
        workRequestRecyclerView.setAdapter(workRequestAdapter);

        Log.d("OPTIONS : " , " " + options);

        workRequestShimmer = homeFrag.findViewById(R.id.shimmer_work_request);
        workRequestShimmer.startShimmer();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            workRequestRecyclerView.setVisibility(View.VISIBLE);
            workRequestShimmer.stopShimmer();
            workRequestShimmer.setVisibility(View.GONE);
        }, 1500);
//  CHECKING IF THERE IS ANY DATA OR NOT IF NOT THEN SHOWING "NO_DATA_FOUND" JSON FILE IF FOUND THEN RECYCLERVIEW PERFORM.
    firebaseDatabase.child("service_requests").child("pending").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() { // ADDED CODE
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.d("DATA : ", "DATA EXIST");
                    workRequestRecyclerView.setVisibility(View.GONE);
                    noDataAnimation.setVisibility(View.VISIBLE);
                    noDataAnimation.playAnimation();
                } else {
                    Log.d("DATA NOT: ", "DATA DOESNOT EXIST");
                    workRequestRecyclerView.setVisibility(View.VISIBLE);
                    noDataAnimation.setVisibility(View.GONE);
                    workRequestAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Database Error: " + error.getMessage());
            }
        });
        locationView = homeFrag.findViewById(R.id.loc_textview);
        LocationHelper.getLocation(this, location -> {
            if (location == null || locationView == null) return;
            String pinCode = (location.length > 0 && location[0] != null) ? location[0].trim() : "";
            String area    = (location.length > 1 && location[1] != null) ? location[1].trim() : "";
            String city    = (location.length > 2 && location[2] != null) ? location[2].trim() : "";

            String place = !area.isEmpty() ? area : city;
            if (!place.isEmpty() && !pinCode.isEmpty()) {
                locationView.setText(place + " - " + pinCode);
            } else if (!place.isEmpty()) {
                locationView.setText(place);
            } else if (!pinCode.isEmpty()) {
                locationView.setText(pinCode);
            } else {
                locationView.setText("Location unavailable");
            }
        });


        return homeFrag;
    }
    private void fetchUserDetails(FirebaseUser firebaseUser){
        if (firebaseUser == null) {
            return;
        }
//        fetching the role of service man from another db -> "Registered ServiceMan User"
        DatabaseReference shortReference = FirebaseDatabase.getInstance().getReference("Registered ServiceMan User").child(firebaseUser.getUid());
        shortReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    NewServiceManModel readUserDetails = snapshot.getValue(NewServiceManModel.class);
                    if (readUserDetails != null && readUserDetails.getServiceCat() != null) {
                        serviceCat = readUserDetails.getServiceCat();
                        System.out.println(serviceCat + " Service Category");
//                        passing the service category of service man to displayUserName function for further operations.
                        displayUsername(firebaseUser);
                    } else {
                        Toast.makeText(context, "Service-Man not found in database!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "Service Man doesn't exist.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Something went wrong in getting data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void displayUsername(FirebaseUser firebaseUser) {
        if (firebaseUser == null) {
            Toast.makeText(getActivity(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Service Man");
        reference.child(serviceCat).child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    NewServiceManModel readUserDetails = snapshot.getValue(NewServiceManModel.class);
                    if (readUserDetails != null && readUserDetails.getName() != null) {
                        displayName = readUserDetails.getName();
                        displayUsername.setText(displayName);
                    } else {
                        Toast.makeText(context, "Username not found in database!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "User data not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to fetch username: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void greetingUserFuntion() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting =(hour < 12) ? "☀️ Hey! Good morning" :
                (hour < 17) ? "🌤️ Hey! Good afternoon" :
                        (hour < 21) ? "🌆 Hey! Good evening" :
                                "🌙 Hey! Good night";

        greetingText.setText(greeting);

    }


    @Override
    public void onStart() {
        super.onStart();
        if (workRequestAdapter != null) {
            workRequestAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        workRequestAdapter.stopListening();
    }




}