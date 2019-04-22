package com.example.abc.presentervideoplayer.sandyapp.tabs;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abc.presentervideoplayer.R;
import com.example.abc.presentervideoplayer.sandyapp.ChatActivity;
import com.example.abc.presentervideoplayer.sandyapp.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    private View privateChatView;
    private RecyclerView chatsList;
    private DatabaseReference chatRef, usersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private String retImage = "default_image";


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        privateChatView = inflater.inflate(R.layout.fragment_chat, container, false);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

        } else {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mAuth = FirebaseAuth.getInstance();
            currentUserID = mAuth.getCurrentUser().getUid();
            chatRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(uid);
            usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        }

        chatsList = privateChatView.findViewById(R.id.chat_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));


        return privateChatView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(chatRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, ChatViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull Contacts model) {
                        final String userIds = getRef(position).getKey();
                        usersRef.child(userIds).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    if (dataSnapshot.hasChild("image")) {
                                        retImage = dataSnapshot.child("image").getValue().toString();
                                        Picasso.get().load(retImage).placeholder(R.drawable.profile_picks).into(holder.profileImage);
                                    }
                                    final String retName = dataSnapshot.child("name").getValue().toString();
                                    final String retStatus = dataSnapshot.child("status").getValue().toString();
                                    holder.userName.setText(retName);


                                    if (dataSnapshot.child("userState").hasChild("state")) {
                                        String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                        String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                        String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                        if (state.equals("online")) {
                                            holder.userStatus.setText("online");
                                        } else if (state.equals("offline")) {
                                            holder.userStatus.setText("Last seen :" + date + " " + time);
                                        }

                                    } else {

                                        holder.userStatus.setText("Offline");
                                    }


                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent chatActivity = new Intent(getContext(), ChatActivity.class);
                                            chatActivity.putExtra("visit_user_id", userIds);
                                            chatActivity.putExtra("visit_username", retName);
                                            chatActivity.putExtra("visit_image", retImage);
                                            startActivity(chatActivity);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);
                        return new ChatViewHolder(view);
                    }
                };
        chatsList.setAdapter(adapter);

        if (mAuth != null) {
            adapter.startListening();
        } else {
            Toast.makeText(getContext(), "No Friends you have...", Toast.LENGTH_LONG).show();
        }


    }


    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView userName, userStatus;

        public ChatViewHolder(View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.users_profile_image);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);

        }

    }
}
