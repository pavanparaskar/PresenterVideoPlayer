package com.example.abc.presentervideoplayer.sandyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abc.presentervideoplayer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updatesettingProfile;
    private EditText setUserName, setUserStatus;
    CircleImageView profileImage;
    String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootReference;
    private static final int gallaryConst = 1;
    private StorageReference userProfileStorageRef;
    private ProgressDialog progressDialog;
    private Toolbar settingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        mRootReference = FirebaseDatabase.getInstance().getReference();

        userProfileStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        initializeFields();
        setUserName.setVisibility(View.INVISIBLE);

        updatesettingProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

        retrieveUserInfo();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallaryIntent = new Intent();
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, gallaryConst);
            }
        });
    }


    private void updateSettings() {

        String setName = setUserName.getText().toString();
        String setStatus = setUserStatus.getText().toString();

        if (TextUtils.isEmpty(setName)) {

        }
        if (TextUtils.isEmpty(setStatus)) {

        } else {
            HashMap<String, Object> map = new HashMap<>();
            map.put("uid", currentUserID);
            map.put("name", setName);
            map.put("status", setStatus);
            mRootReference.child("Users").child(currentUserID).updateChildren(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendToMainActivity();
                                Toast.makeText(getApplicationContext(), "User update profile...", Toast.LENGTH_SHORT).show();
                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(getApplicationContext(), "Error..." + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }


    }

    private void retrieveUserInfo() {

        mRootReference.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && dataSnapshot.hasChild("image")) {
                            String retrivingName = dataSnapshot.child("name").getValue().toString();
                            String retrivingStatus = dataSnapshot.child("status").getValue().toString();
                            String retrivingProfileImage = dataSnapshot.child("image").getValue().toString();
                            Picasso.get().load(retrivingProfileImage).into(profileImage);
                            setUserName.setText(retrivingName);
                            setUserStatus.setText(retrivingStatus);

                        } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {
                            String retrivingName = dataSnapshot.child("name").getValue().toString();
                            String retrivingStatus = dataSnapshot.child("status").getValue().toString();
                            // String retrivingProfileImage = dataSnapshot.child("image").getValue().toString();
                            setUserName.setText(retrivingName);
                            setUserStatus.setText(retrivingStatus);
                        } else {
                            setUserName.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "please update your profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void initializeFields() {

        updatesettingProfile = findViewById(R.id.update_setting_button);
        setUserName = findViewById(R.id.set_user_name);
        setUserStatus = findViewById(R.id.set_profile_status);
        profileImage = findViewById(R.id.profile_image);
        progressDialog = new ProgressDialog(this);
        settingsToolbar = findViewById(R.id.setings_toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gallaryConst && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog.setTitle("Profile Image");
                progressDialog.setMessage("Please wait, profile is updating....");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();

                StorageReference filepath = userProfileStorageRef.child(currentUserID + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "File is uploades successfull", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            mRootReference.child("Users").child(currentUserID).child("image")
                                    .setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Images uploads", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            } else {
                                                String error = task.getException().toString();
                                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();

                                            }

                                        }
                                    });

                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }

                    }
                });
            }

        }
    }

    private void sendToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), Main3ActivitySandy.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
