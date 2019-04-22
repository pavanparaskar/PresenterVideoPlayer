package com.example.abc.presentervideoplayer.sandyapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String recieveUserId, senderUserId, current_state;
    private CircleImageView userProfileImage;
    private TextView userProfileUserName, userProfileUserStatus;
    private Button sendMessageRequestButton, declineMessageRequestButton;
    private DatabaseReference UserRef, ChatRequestRef, ContactsRef, NotificationRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");


        userProfileImage = findViewById(R.id.visit_profile_image);
        userProfileUserName = findViewById(R.id.visit_user_name);
        userProfileUserStatus = findViewById(R.id.visit_profile_status);
        sendMessageRequestButton = findViewById(R.id.send_message_request_button);
        declineMessageRequestButton = findViewById(R.id.decline_message_request_button);

        recieveUserId = getIntent().getExtras().getString("visit_user_id").toString();
        Toast.makeText(getApplicationContext(), recieveUserId, Toast.LENGTH_SHORT).show();
        senderUserId = mAuth.getCurrentUser().getUid();
        current_state = "new";
        retrieveUserInfo();
    }

    private void retrieveUserInfo() {

        UserRef.child(recieveUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))) {
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_picks).into(userProfileImage);
                    userProfileUserName.setText(userName);
                    userProfileUserStatus.setText(userStatus);

                    ManageChatRequest();

                } else {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    userProfileUserName.setText(userName);
                    userProfileUserStatus.setText(userStatus);
                    ManageChatRequest();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void ManageChatRequest() {


        ChatRequestRef.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(recieveUserId)) {
                            String request_type = dataSnapshot.child(recieveUserId).child("request_type").getValue().toString();
                            if (request_type.equals("sent")) {
                                current_state = "request_sent";
                                sendMessageRequestButton.setText("Cancel Request Chat");
                            } else if (request_type.equals("recieved")) {
                                current_state = "request_recieved";
                                sendMessageRequestButton.setText("Accept Chat Request");
                                declineMessageRequestButton.setVisibility(View.VISIBLE);
                                declineMessageRequestButton.setEnabled(true);
                                declineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancelChatRequest();
                                    }
                                });

                            }
                        } else {
                            ContactsRef.child(senderUserId).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(recieveUserId)) {
                                                current_state = "friends";
                                                sendMessageRequestButton.setText("Remove This Contact");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    }
                            );
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        if (!senderUserId.equals(recieveUserId)) {
            sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessageRequestButton.setEnabled(false);
                    if (current_state.equals("new")) {
                        sendChatRequest();
                    }

                    if (current_state.equals("request_sent")) {
                        cancelChatRequest();

                    }
                    if (current_state.equals("request_recieved")) {
                        acceptChatRequest();

                    }
                    if (current_state.equals("friends")) {
                        removeSpecificContact();

                    }
                }
            });

        } else {
            sendMessageRequestButton.setVisibility(View.INVISIBLE);
        }


    }


    private void sendChatRequest() {


        ChatRequestRef.child(senderUserId).child(recieveUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()) {
                                                   ChatRequestRef.child(recieveUserId).child(senderUserId)
                                                           .child("request_type").setValue("recieved")
                                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<Void> task) {
                                                                   if (task.isSuccessful()) {

                                                                       HashMap<String, String> chatNotificationMap = new HashMap<>();
                                                                       chatNotificationMap.put("from", senderUserId);
                                                                       chatNotificationMap.put("type", "request");
                                                                       NotificationRef.child(recieveUserId).push()
                                                                               .setValue(chatNotificationMap)
                                                                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                   @Override
                                                                                   public void onComplete(@NonNull Task<Void> task) {
                                                                                       if (task.isSuccessful()){
                                                                                           sendMessageRequestButton.setEnabled(true);
                                                                                           current_state = "request_sent";
                                                                                           sendMessageRequestButton.setText("Cancel Request Chat");
                                                                                       }

                                                                                   }
                                                                               });



                                                                   }
                                                               }
                                                           });
                                               }

                                           }
                                       }
                );

    }

    private void cancelChatRequest() {

        ChatRequestRef.child(senderUserId).child(recieveUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ChatRequestRef.child(recieveUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                sendMessageRequestButton.setEnabled(true);
                                                current_state = "new";
                                                sendMessageRequestButton.setText("Send Message");
                                                declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                declineMessageRequestButton.setEnabled(false);
                                            }

                                        }
                                    });
                        }

                    }
                });


    }

    private void acceptChatRequest() {
        ContactsRef.child(senderUserId).child(recieveUserId)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            ContactsRef.child(recieveUserId).child(senderUserId)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                ChatRequestRef.child(senderUserId)
                                                        .child(recieveUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    ChatRequestRef.child(recieveUserId)
                                                                            .child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        sendMessageRequestButton.setEnabled(true);
                                                                                        current_state = "friends";
                                                                                        sendMessageRequestButton.setText("Remove This Contact");
                                                                                        declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                                        declineMessageRequestButton.setEnabled(false);


                                                                                    }

                                                                                }
                                                                            });
                                                                }

                                                            }
                                                        });
                                            }

                                        }
                                    });
                        }

                    }
                });
    }

    private void removeSpecificContact() {
        ContactsRef.child(senderUserId).child(recieveUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ContactsRef.child(recieveUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                sendMessageRequestButton.setEnabled(true);
                                                current_state = "new";
                                                sendMessageRequestButton.setText("Send Message");
                                                declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                declineMessageRequestButton.setEnabled(false);
                                            }

                                        }
                                    });
                        }

                    }
                });

    }


}
