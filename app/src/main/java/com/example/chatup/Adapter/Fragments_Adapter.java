package com.example.chatup.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chatup.fragments.CalsFragment;
import com.example.chatup.fragments.Chat_fragment;
import com.example.chatup.fragments.StatusFragment;

public class Fragments_Adapter extends FragmentPagerAdapter {
    public Fragments_Adapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new Chat_fragment();
            case 1: return new StatusFragment();
            case 2: return new CalsFragment();
            default:return new Chat_fragment();
        }
    } // the position of viewpager determine the fragment to show

    @Override
    public int getCount() {
        return 3;  //total 3 fragment so return 3
        // will create 3 tabs
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title=null;
        if(position==0){
            title="CHATS";
        }if(position==1){
            title="STATUS";
        }if(position==2){
            title="CALLS";
        }
        return title;
    }// return to the value of tab
}
