package com.example.abc.presentervideoplayer.sandyapp;

import android.graphics.Color;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Size;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abc.presentervideoplayer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;

public class GroupchatActivity extends AppCompatActivity {
    private ImageView sentMessageButton;
    private Toolbar mToolbar;
    private EditText userMessageInputs;
    private ScrollView mScrollView;
    private TextView displayUserMessage;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, groupNameRef, groupMessagekeyRef;
    private String currentGroupName, currentuserID, currentUserName, currentDate, currentTime;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);
        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(getApplicationContext(), currentGroupName, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        currentuserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        initializeFields();
        getUserInfo();

        sentMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageInfoToDatabse();
                userMessageInputs.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }


    private void initializeFields() {

        mToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        sentMessageButton = findViewById(R.id.sent_message_button);
        userMessageInputs = findViewById(R.id.input_group_messages);

        mScrollView = findViewById(R.id.my_scroll_view_layout);
        displayUserMessage = findViewById(R.id.group_chat_text_display);


    }

    @Override
    protected void onStart() {
        super.onStart();

        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    displayMessages(dataSnapshot);

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    displayMessages(dataSnapshot);

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getUserInfo() {

        userRef.child(currentuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveMessageInfoToDatabse() {

        String message = userMessageInputs.getText().toString();
        String messageKey = groupNameRef.push().getKey();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(), "Please Enter Text First...", Toast.LENGTH_SHORT).show();

        } else {
            java.util.Calendar cCalForDate = java.util.Calendar.getInstance();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy");
            currentDate = simpleDateFormat.format(cCalForDate.getTime());


            java.util.Calendar cCalForTime = java.util.Calendar.getInstance();

            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = simpleTimeFormat.format(cCalForTime.getTime());

            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            groupNameRef.updateChildren(stringObjectHashMap);

            groupMessagekeyRef = groupNameRef.child(messageKey);
            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);

            groupMessagekeyRef.updateChildren(messageInfoMap);

        }

    }

    private void displayMessages(DataSnapshot dataSnapshot) {
        SpannableStringBuilder builder = new SpannableStringBuilder();


        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()) {
            String chatDate = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot) iterator.next()).getValue();

           /* SpannableString redSpannable = new SpannableString(chatName);
            redSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, chatName.length(), 0);
            redSpannable.setSpan(new RelativeSizeSpan(1.5f), 0, chatName.length(), 0);
            builder.append(redSpannable);


            SpannableString chatMessageSpannableString = new SpannableString(chatMessage);
            chatMessageSpannableString.setSpan(new ForegroundColorSpan(Color.GRAY), 0, chatMessage.length(), 0);
            builder.append("\n\t" + chatMessageSpannableString);

            SpannableString chatTimeSpannableString = new SpannableString(chatTime);
            chatTimeSpannableString.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0, chatTime.length(), 0);
            chatTimeSpannableString.setSpan(new RelativeSizeSpan(0.8f), 0, chatTime.length(), 0);
            builder.append("\n\t" + chatTimeSpannableString);*/
            displayUserMessage.append(chatName + ":\n" + chatMessage + "\n" + chatDate + "\t" + chatTime + "\n");
           // displayUserMessage.setText(builder, TextView.BufferType.SPANNABLE);
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

        }
    }


}
