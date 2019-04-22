package com.example.abc.presentervideoplayer.sandyapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.abc.presentervideoplayer.sandyapp.tabs.ChatFragment;
import com.example.abc.presentervideoplayer.sandyapp.tabs.ContactFragment;
import com.example.abc.presentervideoplayer.sandyapp.tabs.GroupFragment;
import com.example.abc.presentervideoplayer.sandyapp.tabs.RequestsFragment;

/**
 * Created by ABC on 22-09-2018.
 */

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {

            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 1:
                GroupFragment groupFragment = new GroupFragment();
                return groupFragment;
            case 2:
                ContactFragment contactFragment = new ContactFragment();
                return contactFragment;
            case 3:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;


            default:
                return null;

        }

    }

    @Override
    public int getCount() {


        return 4;
    }

    @Override
    public CharSequence getPageTitle(int i) {

        switch (i) {
            case 0:

                return "Chats";
            case 1:

                return "Group";
            case 2:

                return "Contacts";

            case 3:
                return "Requests";

            default:
                return null;
        }

    }
}
