package com.example.home_buddy_captain.fragment_page.recent_view_frag_page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.home_buddy_captain.R;
import com.example.home_buddy_captain.model.Work_Request_Model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class Completed_Request_Fragment extends Fragment {

    View view ;

    FirebaseUser firebaseUser;
    RecyclerView recyclerView;
    CompletedRequestAdapter completedRequestAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_completed__request_, container, false);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.completed_recycler_view);

        if (firebaseUser != null) {
            FirebaseRecyclerOptions<Work_Request_Model> options =
                    new FirebaseRecyclerOptions.Builder<Work_Request_Model>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("service_requests").child("completed").child(firebaseUser.getUid()), Work_Request_Model.class)
                            .build();
            completedRequestAdapter = new CompletedRequestAdapter(options);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            // Set the layout manager to the RecyclerView
            recyclerView.setLayoutManager(linearLayoutManager);
            // Set the adapter to the RecyclerView
            recyclerView.setAdapter(completedRequestAdapter);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (completedRequestAdapter != null) {
            completedRequestAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (completedRequestAdapter != null) {
            completedRequestAdapter.stopListening();
        }
    }
}