package com.example.chatup;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.chatup.databinding.ActivityTransparentBinding;
import com.squareup.picasso.Picasso;

public class TransparentActivity extends AppCompatActivity {
    ActivityTransparentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransparentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.constTransparent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //now getting the values for the window
        String recieveName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");
        String recieveStatus= getIntent().getStringExtra("userStatus");//setting values
        binding.transparentName.setText(recieveName);
        binding.transparentabout.setText(recieveStatus);
        Picasso.get().load(profilePic).placeholder(R.drawable.ic_baseline_person_24).into(binding.transparentpic);
        //loading from intent..........//if no image.................// else this image
    }
}