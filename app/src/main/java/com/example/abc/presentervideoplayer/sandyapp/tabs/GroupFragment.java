package com.example.abc.presentervideoplayer.sandyapp.tabs;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.abc.presentervideoplayer.R;
import com.example.abc.presentervideoplayer.sandyapp.GroupchatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {
    private View groupFragmentView;
    private ArrayList<String> list_Of_Groups = new ArrayList<>();
    private ListView listViewGroups;
    private ArrayAdapter<String> arrayAdapterGroup;
    private DatabaseReference groupRef;

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView = inflater.inflate(R.layout.fragment_group, container, false);
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        initializeFields();
        retrieveAndDisplayGroups();
        listViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentGroupName = parent.getItemAtPosition(position).toString();
                Intent grouIntent = new Intent(getContext(), GroupchatActivity.class);
                grouIntent.putExtra("groupName", currentGroupName);
                startActivity(grouIntent);
            }
        });


        return groupFragmentView;
    }

    private void retrieveAndDisplayGroups() {
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()) {
                    set.add(((DataSnapshot) iterator.next()).getKey());
                }

                list_Of_Groups.clear();
                list_Of_Groups.addAll(set);
                arrayAdapterGroup.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initializeFields() {

        listViewGroups = groupFragmentView.findViewById(R.id.list_view_group);
        arrayAdapterGroup = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list_Of_Groups);
        listViewGroups.setAdapter(arrayAdapterGroup);
    }

}
