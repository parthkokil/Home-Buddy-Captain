package com.example.home_buddy_captain.fragment_page.recent_view_frag_page;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.home_buddy_captain.R;
import com.example.home_buddy_captain.initial_connection.Firebase_Connection;
import com.example.home_buddy_captain.model.Work_Request_Model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;

public class ActiveRequestAdapter  extends FirebaseRecyclerAdapter<Work_Request_Model, ActiveRequestAdapter.ViewHolder> {


    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    public ActiveRequestAdapter(@NonNull FirebaseRecyclerOptions<Work_Request_Model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Work_Request_Model model) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        holder.name_value.setText(model.getuser_name());
        holder.service_value.setText(model.getserviceRequested());
        holder.status_value.setText(model.getStatus());
        holder.mobile_value.setText(model.getUser_mobile());

        holder.finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Close Request");
                builder.setMessage("Do you really want to close the request?");

                builder.setPositiveButton(" ✅ Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ✅ Finish logic here

                        Map<String, Object> requestMap = new HashMap<>();
                        requestMap.put("user_name", model.getuser_name());
                        requestMap.put("user_mobile", model.getUser_mobile());
                        requestMap.put("user_uid", model.getUser_uid());
                        requestMap.put("description", model.getDescription());
                        requestMap.put("serviceProviderUID", model.getserviceProviderUID());
                        requestMap.put("serviceRequested", model.getserviceRequested());
                        requestMap.put("status", "Completed");

                        DatabaseReference main_reference = Firebase_Connection.get2ndReference(view.getContext());
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        reference.child("service_requests").child("completed").child(model.getserviceProviderUID())
                                .child(model.getUser_uid())
                                .updateChildren(requestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(view.getContext(), "The Request is Closed.", Toast.LENGTH_SHORT).show();

                                            reference.child("service_requests").child("active").child(model.getserviceProviderUID())
                                                    .child(model.getUser_uid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                main_reference.child("service_requests").child("active").child(model.getUser_uid())
                                                                        .child(model.getserviceProviderUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if (snapshot.exists()) {
                                                                                    Work_Request_Model readUserDetails = snapshot.getValue(Work_Request_Model.class);
                                                                                    if (readUserDetails != null && readUserDetails.getuser_name() != null) {
                                                                                        Map<String, Object> userMapForUser = new HashMap<>();
                                                                                        userMapForUser.put("user_name", readUserDetails.getuser_name());
                                                                                        userMapForUser.put("user_mobile", readUserDetails.getUser_mobile());
                                                                                        userMapForUser.put("user_uid", model.getUser_uid());
                                                                                        userMapForUser.put("description", model.getDescription());
                                                                                        userMapForUser.put("serviceProviderUID", model.getserviceProviderUID());
                                                                                        userMapForUser.put("serviceRequested", model.getserviceRequested());
                                                                                        userMapForUser.put("status", "Completed");

                                                                                        main_reference.child("service_requests").child("completed").child(model.getUser_uid())
                                                                                                .child(model.getserviceProviderUID()).setValue(userMapForUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        main_reference.child("service_requests").child("active").child(model.getUser_uid())
                                                                                                                .child(model.getserviceProviderUID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                        System.out.println("USER Active DATA DELETED");
                                                                                                                    }
                                                                                                                });
                                                                                                        System.out.println("USER DATA SAVED IN COMPLETED");
                                                                                                    }
                                                                                                });

                                                                                    } else {
                                                                                        Toast.makeText(view.getContext(), "Username not found in database!", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                } else {
                                                                                    Toast.makeText(view.getContext(), "User data not found!", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });
                                                                System.out.println("Request DELETED.");
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });


                    }
                });

                builder.setNegativeButton(" ❌ Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ❌ Cancel logic (just dismisses)
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                // Optional: Style buttons
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.active_req_view,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_value, service_value, status_value, mobile_value;
        CardView work_request_cardview;
        Button finish;
        public ViewHolder(View view) {
            super(view);

            name_value = itemView.findViewById(R.id.active_name_value);
            service_value = itemView.findViewById(R.id.active_service_value);
            status_value = itemView.findViewById(R.id.active_status_value);
            mobile_value = itemView.findViewById(R.id.active_mob_value);
            finish = itemView.findViewById(R.id.finish);
            work_request_cardview = itemView.findViewById(R.id.active_request_cardview);

        }
    }
}
