package com.example.abc.presentervideoplayer.sandyapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abc.presentervideoplayer.R;
import com.example.abc.presentervideoplayer.sandyapp.tabs.FindFriendsActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.zip.Inflater;

public class Main3ActivitySandy extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager myViewPager;
    TabLayout myTabLayout;
    TabsAccessorAdapter tabsAccessorAdapter;

    FirebaseAuth mAuth;
    private DatabaseReference mRootReference;
    String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3_sandy);


        mAuth = FirebaseAuth.getInstance();

        mRootReference = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Buddy Chat");

        myViewPager = findViewById(R.id.main_tab_pager);
        tabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(tabsAccessorAdapter);

        myTabLayout = findViewById(R.id.main_tab);
        myTabLayout.setupWithViewPager(myViewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLoginActivity();
        } else {
            updateUserStatus("online");
            verifyUserExistance();


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUserStatus("offline");


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUserStatus("offline");


        }
    }

    private void verifyUserExistance() {

        String currentUserID = mAuth.getCurrentUser().getUid();
        mRootReference.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()) {
                    Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();

                } else {
                    sendToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_find_friends_option) {
            sendUserToFindFriendsActivity();
        }
        if (item.getItemId() == R.id.main_setting_option) {
            sendToSettingsActivity();

        }
        if (item.getItemId() == R.id.main_logout_option) {

            updateUserStatus("offline");
            mAuth.signOut();
            sendToLoginActivity();

        }
        if (item.getItemId() == R.id.main_create_groups_option) {
            requestNewGroup();

        }
        if (item.getItemId() == R.id.main_invite_friends) {
            inviteFriends();
        }
        return true;
    }


    private void requestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Main3ActivitySandy.this, R.style.AlertDialog);
        builder.setTitle("Enter Group name:");
        final EditText groupNameField = new EditText(Main3ActivitySandy.this);
        groupNameField.setHint("e.g havier days");
        builder.setView(groupNameField);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();
                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(getApplicationContext(), "Please enter group name", Toast.LENGTH_SHORT).show();

                } else {
                    createNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        builder.show();

    }

    private void createNewGroup(final String groupName) {
        mRootReference.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), groupName + " is Created", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void sendToLoginActivity() {

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendToSettingsActivity() {

        Intent intentSetting = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intentSetting);

    }

    private void sendUserToFindFriendsActivity() {

        Intent intentFindFriends = new Intent(getApplicationContext(), FindFriendsActivity.class);
        startActivity(intentFindFriends);
    }


    private void updateUserStatus(String state) {
        String saveCurrentDate, saveCurrentTime;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());


        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStatusMap = new HashMap<>();
        onlineStatusMap.put("time", saveCurrentTime);
        onlineStatusMap.put("date", saveCurrentDate);
        onlineStatusMap.put("state", state);

        currentUserID = mAuth.getCurrentUser().getUid();
        mRootReference.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStatusMap);


    }

    private void inviteFriends() {

    }
}
