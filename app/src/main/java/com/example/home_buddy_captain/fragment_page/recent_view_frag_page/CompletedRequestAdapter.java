package com.example.home_buddy_captain.fragment_page.recent_view_frag_page;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.home_buddy_captain.R;
import com.example.home_buddy_captain.model.Work_Request_Model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CompletedRequestAdapter extends FirebaseRecyclerAdapter<Work_Request_Model, CompletedRequestAdapter.ViewHolder> {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private String serviceUID;

    public CompletedRequestAdapter(@NonNull FirebaseRecyclerOptions<Work_Request_Model> options) {
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


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_req_view,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_value, service_value, status_value, mobile_value;
        CardView work_request_cardview;

        public ViewHolder(View view) {
            super(view);

            name_value = itemView.findViewById(R.id.completed_name_value);
            service_value = itemView.findViewById(R.id.completed_service_value);
            status_value = itemView.findViewById(R.id.completed_status_value);
            mobile_value = itemView.findViewById(R.id.completed_mob_value);
            work_request_cardview = itemView.findViewById(R.id.completed_request_cardview);

        }
    }
}
