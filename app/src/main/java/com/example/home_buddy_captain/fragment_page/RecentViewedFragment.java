package com.example.home_buddy_captain.fragment_page;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.home_buddy_captain.DashboardActivity;
import com.example.home_buddy_captain.DoubleBackPressExitHandler;
import com.example.home_buddy_captain.R;
import com.example.home_buddy_captain.fragment_page.recent_view_frag_page.Active_Request_Fragment;
import com.example.home_buddy_captain.fragment_page.recent_view_frag_page.Completed_Request_Fragment;


public class RecentViewedFragment extends Fragment {


    private int selectedTab = 1;
    private DoubleBackPressExitHandler doubleBackPressExitHandler;
    private LinearLayout activelayout, completedlayout;
    ImageView activeimage, completedimage;
    TextView activetxt, completedtxt;
    ;
    FragmentManager fragmentManager;
    View view;
    private AlertDialog internetDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View recentFrag = inflater.inflate(R.layout.fragment_recent_viewed, container, false);

        view = recentFrag;

        activelayout = recentFrag.findViewById(R.id.active_layout);
        completedlayout = recentFrag.findViewById(R.id.completed_layout);

        activetxt = recentFrag.findViewById(R.id.active_text);
        completedtxt = recentFrag.findViewById(R.id.completed_text);

        activeimage = recentFrag.findViewById(R.id.active_image);
        completedimage = recentFrag.findViewById(R.id.completed_image);


        doubleBackPressExitHandler = new DoubleBackPressExitHandler(getActivity());
        fragmentManager = requireActivity().getSupportFragmentManager(); // Use requireActivity() to get the activity

        fragmentManager.beginTransaction().setReorderingAllowed(true).replace(R.id.recentViewFragment, Active_Request_Fragment.class, null).commit();

        bottomNavigationBarControl();


        return recentFrag;
    }

    private void bottomNavigationBarControl() {

        activelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTab != 1) {
                    fragmentManager.beginTransaction().setReorderingAllowed(true).replace(R.id.recentViewFragment, Active_Request_Fragment.class, null).commit();

                    completedtxt.setVisibility(View.GONE);

                    completedimage.setImageResource(R.drawable.waiting);

                    completedlayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    // Select Home Tab
                    activetxt.setVisibility(View.VISIBLE);
                    activeimage.setImageResource(R.drawable.accept);
                    activelayout.setBackgroundResource(R.drawable.round_back_call);

                    // Set Animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    activelayout.startAnimation(scaleAnimation);

                    // Set first tab as selected
                    selectedTab = 1;
                }
            }
        });


        completedlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTab != 2) {
                    fragmentManager.beginTransaction().setReorderingAllowed(true).replace(R.id.recentViewFragment, Completed_Request_Fragment.class, null).commit();

                    activetxt.setVisibility(View.GONE);

                    activeimage.setImageResource(R.drawable.accept);

                    activelayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    // Select Profile Tab
                    completedtxt.setVisibility(View.VISIBLE);
                    completedimage.setImageResource(R.drawable.waiting);
                    completedlayout.setBackgroundResource(R.drawable.round_back_call);

                    // Set Animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    activelayout.startAnimation(scaleAnimation);

                    // Set first tab as selected
                    selectedTab = 2;
                }
            }
        });


        if (!isConnected(this)) {
            showInternetDialog();
        }
    }

    @SuppressLint("MissingInflatedId")
    private void showInternetDialog() {
        if (internetDialog != null && internetDialog.isShowing()) {
            return;
        }
        Context ctx = getContext();
        if (ctx == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(false);
        View dialogView = LayoutInflater.from(ctx).inflate(R.layout.no_internet_dialog, null);
        dialogView.findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected(RecentViewedFragment.this)) {
                    Toast.makeText(getContext(), "Still not connected. Please check your internet.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Reconnected Successfully", Toast.LENGTH_SHORT).show();
                    if (internetDialog != null && internetDialog.isShowing()) {
                        internetDialog.dismiss();
                    }
                }
            }
        });
        builder.setView(dialogView);

        internetDialog = builder.create();
        internetDialog.show();
    }


    private boolean isConnected(RecentViewedFragment fragment) {
        Context ctx = getContext();
        if (ctx == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());

    }

}