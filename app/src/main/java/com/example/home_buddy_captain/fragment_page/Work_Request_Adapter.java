package com.example.home_buddy_captain.fragment_page;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.home_buddy_captain.MessageNote;
import com.example.home_buddy_captain.R;
import com.example.home_buddy_captain.initial_connection.Firebase_Connection;
import com.example.home_buddy_captain.model.NewServiceManModel;
import com.example.home_buddy_captain.model.NewUserModel;
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

import java.util.HashMap;
import java.util.Map;

public class Work_Request_Adapter extends FirebaseRecyclerAdapter<Work_Request_Model, Work_Request_Adapter.myviewholder> {

    Context context;
    Button btnDecline, btnAccept;
    TextView etDescription;

    public Work_Request_Adapter(FirebaseRecyclerOptions<Work_Request_Model> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull Work_Request_Model workRequestModel) {
        holder.service_value.setText(workRequestModel.getserviceRequested()); // Fix: Correct method name
        holder.status_value.setText(workRequestModel.getStatus());
        holder.name_value.setText(workRequestModel.getuser_name());
        holder.mobile_value.setText(workRequestModel.getUser_mobile());
        Log.d("DATA : ", "" + workRequestModel.getuser_name() + " " + workRequestModel.getserviceProviderUID() + " " + workRequestModel.getserviceRequested() + " " + workRequestModel.getStatus());

        holder.viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showServiceRequestDialog(workRequestModel, view);
            }
        });
    }

    private void showServiceRequestDialog(Work_Request_Model workRequestModel, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context); // or requireContext() in Fragment
        View dialogView = LayoutInflater.from(context).inflate(R.layout.work_req_detail, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        etDescription = dialogView.findViewById(R.id.etDescription);
        etDescription.setText(workRequestModel.getDescription());
        btnDecline = dialogView.findViewById(R.id.btnCancel);
        btnAccept = dialogView.findViewById(R.id.btnRequest);


        btnDecline.setOnClickListener(v -> {

            updateStatusAndLoadData(workRequestModel, "Decline", view);

            dialog.dismiss();
        });

        btnAccept.setOnClickListener(v -> {

            updateStatusAndLoadData(workRequestModel, "Accept", view);

            dialog.dismiss();
        });
    }


    private void updateStatusAndLoadData(Work_Request_Model workRequestModel, String status, View view) {
//      database reference of captain app
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//      database reference of user app
        DatabaseReference main_reference = Firebase_Connection.get2ndReference(view.getContext());

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("user_name", workRequestModel.getuser_name());
        requestMap.put("user_mobile", workRequestModel.getUser_mobile());
        requestMap.put("user_uid", workRequestModel.getUser_uid());
        requestMap.put("serviceProviderUID", workRequestModel.getserviceProviderUID());
        requestMap.put("serviceRequested", workRequestModel.getserviceRequested());
        requestMap.put("status", status);

//        adding the data into new child respective the status(accept, waiting, decline).
        if (status.equals("Accept")) {

            reference.child("service_requests").child("active").child(workRequestModel.getserviceProviderUID()).child(workRequestModel.getUser_uid())
                    .updateChildren(requestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                System.out.println("Status Changed.");
                            }
                        }
                    });

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                return;
            }
            Map<String, Object> userMap = new HashMap<>();
            reference.child("Registered Service Man").child(workRequestModel.getserviceRequested())
                    .child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                NewServiceManModel model = snapshot.getValue(NewServiceManModel.class);
                                if (model != null) {
                                    userMap.put("user_name", model.getName());
                                    userMap.put("user_mobile", model.getMobile());
                                    userMap.put("serviceProviderUID", workRequestModel.getserviceProviderUID());
                                    userMap.put("serviceRequested", workRequestModel.getserviceRequested());
                                    userMap.put("status", status);

                                    main_reference.child("service_requests").child("active").child(workRequestModel.getUser_uid())
                                            .child(workRequestModel.getserviceProviderUID())
                                            .updateChildren(userMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {  // ✅ Add <Void>
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {  // ✅ Add <Void> here
                                                    if (task.isSuccessful()) {

                                                        System.out.println("ADDED: Data is added to user DB");
                                                        // deleting the request data from pending
                                                        reference.child("service_requests").child("pending").child(workRequestModel.getserviceProviderUID())
                                                                .child(workRequestModel.getUser_uid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            System.out.println("DELETED : DATA IS DELETED SUCCESSFULLY.");
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        System.out.println("NO ADD: Data is not added to user DB.");
                                                    }
                                                }
                                            });

                                } else {
                                    Toast.makeText(view.getContext(), "DATA is not FOUND.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                System.out.println("Snapshot does not exist");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            reference.child("service_requests").child("pending").child(workRequestModel.getserviceProviderUID()).child(workRequestModel.getUser_uid())
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Map<String, Object> updateUserStatus = new HashMap<>();
                                updateUserStatus.put("status", status);
                                main_reference.child("service_requests").child("active").child(workRequestModel.getUser_uid())
                                        .child(workRequestModel.getserviceProviderUID())
                                        .updateChildren(updateUserStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    System.out.println("Status Changed In User");
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }


    }


    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.work_request, parent, false);

        return new Work_Request_Adapter.myviewholder(view);
    }


    public class myviewholder extends RecyclerView.ViewHolder {

        TextView name_value, service_value, status_value, mobile_value;
        TextView viewMore;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            name_value = itemView.findViewById(R.id.name_value);
            service_value = itemView.findViewById(R.id.service_value);
            status_value = itemView.findViewById(R.id.status_value);
            mobile_value = itemView.findViewById(R.id.mob_value);

            viewMore = itemView.findViewById(R.id.viewMore);

        }

    }
}
