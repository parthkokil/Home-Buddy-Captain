package com.example.home_buddy_captain.fragment_page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.home_buddy_captain.MyProfile_Activities.Add_Service_Areas_Activity;
import com.example.home_buddy_captain.MyProfile_Activities.Change_Password_Activity;
import com.example.home_buddy_captain.MyProfile_Activities.Edit_Profile_Activity;
import com.example.home_buddy_captain.MyProfile_Activities.Subscription_Activity;
import com.example.home_buddy_captain.R;
import com.example.home_buddy_captain.SignInSignUpActivities.GetStartedActivity;
import com.example.home_buddy_captain.initial_connection.LocationConnection;
import com.example.home_buddy_captain.initial_connection.LocationHelper;
import com.example.home_buddy_captain.model.NewServiceManModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private Context context;
    private CircleImageView imageButton;
    private ImageView imageViewUploadButton;
    private String base64Image;
    private FirebaseAuth auth;
    private FirebaseUser firebaseuser;

    private CardView editProfile, changePassword, subscription, addServiceAreas;
    private Button logOut;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private TextView locationView, phoneView, userName;
    private String name, phone, serviceCat;

    public ProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment_profile = inflater.inflate(R.layout.fragment_profile, container, false);

        imageButton = fragment_profile.findViewById(R.id.imgProfile);
        imageViewUploadButton = fragment_profile.findViewById(R.id.upload_img_imageView);

        auth = FirebaseAuth.getInstance();
        firebaseuser = auth.getCurrentUser();
        userName = fragment_profile.findViewById(R.id.txtName);
        phoneView = fragment_profile.findViewById(R.id.txtPhone);

        editProfile = fragment_profile.findViewById(R.id.cardView_EditProfile);
        changePassword = fragment_profile.findViewById(R.id.cardView_ChangePassword);
        subscription = fragment_profile.findViewById(R.id.cardView_Subscription);
        addServiceAreas = fragment_profile.findViewById(R.id.cardView_add_service_areas);
        logOut = fragment_profile.findViewById(R.id.logOutbtn);
        locationView = fragment_profile.findViewById(R.id.txtLocation);

        initializeImagePickerLauncher();
        fetchUserDetails(firebaseuser);

        imageViewUploadButton.setOnClickListener(v -> openFileChooser());
        imageButton.setOnClickListener(v -> uploadImageToRealTimeDatabase());

        editProfile.setOnClickListener(v -> startActivity(new Intent(context, Edit_Profile_Activity.class)));
        changePassword.setOnClickListener(v -> startActivity(new Intent(context, Change_Password_Activity.class)));
        addServiceAreas.setOnClickListener(v -> startActivity(new Intent(context, Add_Service_Areas_Activity.class)));
        subscription.setOnClickListener(v -> startActivity(new Intent(context, Subscription_Activity.class)));

        logOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getActivity(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), GetStartedActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

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

        return fragment_profile;
    }

    private void fetchUserDetails(FirebaseUser firebaseUser) {
        if (firebaseUser == null) {
            return;
        }
        DatabaseReference shortReference = FirebaseDatabase.getInstance().getReference("Registered ServiceMan User").child(firebaseUser.getUid());
        shortReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    NewServiceManModel readUserDetails = snapshot.getValue(NewServiceManModel.class);
                    if (readUserDetails != null && readUserDetails.getServiceCat() != null) {
                        serviceCat = readUserDetails.getServiceCat();
                        displayUsername(firebaseUser);
                    } else {
                        Toast.makeText(context, "Service-Man not found in database!", Toast.LENGTH_SHORT).show();
                    }
                } else {
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Service Man");
        reference.child(serviceCat).child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    NewServiceManModel readUserDetails = snapshot.getValue(NewServiceManModel.class);
                    if (readUserDetails != null && readUserDetails.getName() != null) {
                        name = readUserDetails.getName();
                        userName.setText(name);
                        phone = readUserDetails.getMobile();
                        phoneView.setText("+91-" + phone);
                        loadProfileImage(); //  Move this here so serviceCat is ready
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


    private void initializeImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        convertImageToBase64(imageUri);
                    }
                }
        );
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(intent);
    }

    private void convertImageToBase64(Uri imageUri) {
        try {
            InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
            imageButton.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToRealTimeDatabase() {
        if (base64Image == null) {
            Toast.makeText(getContext(), "No image selected!", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Registered Service Man")
                .child(serviceCat)
                .child(firebaseuser.getUid());

        Map<String, Object> updates = new HashMap<>();
        updates.put("profileImage", base64Image);

        userRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Profile image updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to upload image!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfileImage() {
        if (firebaseuser == null || serviceCat == null) {
            return;
        }
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Registered Service Man")
                .child(serviceCat)
                .child(firebaseuser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("profileImage")) {
                    String imageString = snapshot.child("profileImage").getValue(String.class);
                    if (imageString != null && !imageString.isEmpty()) {
                        byte[] decodedBytes = Base64.decode(imageString, Base64.DEFAULT);
                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        imageButton.setImageBitmap(decodedBitmap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load profile image.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
