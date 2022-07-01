package com.example.chatup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.chatup.databinding.ActivityNewBinding;
import com.squareup.picasso.Picasso;

public class NewActivity extends AppCompatActivity {
    ActivityNewBinding binding;
    String recieveName;
    String profilePic;
    String phone;
    String about;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        recieveName = getIntent().getStringExtra("userName");
        profilePic = getIntent().getStringExtra("profilePic");
        phone = getIntent().getStringExtra("phone");
        about=getIntent().getStringExtra("about");


        binding.username.setText(recieveName);
        binding.status.setText(about);
        if(phone==null){
            binding.phone.setText("Not registered");
        }
        else {
            binding.phone.setText(phone);
        }
        Picasso.get().load(profilePic).placeholder(R.drawable.person).into(binding.picProfile);

        binding.viewchats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}