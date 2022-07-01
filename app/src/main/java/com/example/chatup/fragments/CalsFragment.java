package com.example.chatup.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatup.Adapter.CallAdapter;
import com.example.chatup.Models.Users;
import com.example.chatup.Models.UsersAdapter;
import com.example.chatup.databinding.FragmentCalsBinding;
import com.example.chatup.databinding.FragmentChatFragmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CalsFragment extends Fragment {
    FragmentCalsBinding binding;//sabhi k views le liye
    ArrayList<Users> list = new ArrayList<>();
    FirebaseDatabase database;
    String sender_contact;
    public CalsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCalsBinding.inflate(inflater, container, false);//sabhi ko find view by id kr liya
        //format for fragment data binding where extra attributes are container and attach to parent
        database=FirebaseDatabase.getInstance();//obj of database from where we fetch data
        CallAdapter adapter = new CallAdapter(list, getContext(),sender_contact);//list given to adapter))))))))))))))))))))))))
        binding.chatRecyclerView.setAdapter(adapter);//adding adapter with recycle view
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());//layout manager with recycle view
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        //otherwise fetching of songs done here in musicplayer
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {  //fetching of data from database
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){  // fetching of all accounts
                    Users users=dataSnapshot.getValue(Users.class);
                    users.setUserID(dataSnapshot.getKey());  //getkey()=key value of source location
                    if(!users.getUserID().equals(FirebaseAuth.getInstance().getUid())) {
                        list.add(users);  //adding of account to list one by one// list created))))))))))))))))))))))))
                    }
                    else{
                        sender_contact= users.getContact();
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return binding.getRoot();
    }
}
