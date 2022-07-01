package com.example.chatup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatup.Models.Users;
import com.example.chatup.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    FirebaseAuth auth;//Auth used so that only signed in user profile changed
    FirebaseDatabase database;// profile is taken from firebase
    FirebaseStorage storage;// new upload is stored here


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        // creating object of all firebases
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent ita = new Intent(SettingsActivity.this, MainActivity.class);
//                startActivity(ita);
                finish();
            }
        });
       // saving username and about in database using save button
        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=binding.etUserName.getText().toString();
                String about = binding.etAbout.getText().toString();
                HashMap<String,Object> obj= new HashMap<>();
                obj.put("userName",username);
                obj.put("status",about);
                database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).updateChildren(obj);
                Toast.makeText(SettingsActivity.this, "profile updated", Toast.LENGTH_SHORT).show();
            }
        });
//taking data from firebase and setting it to chat fragment
        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(
        new ValueEventListener() {
            @Override//when single data changed in user id i.e profile changed
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users= snapshot.getValue(Users.class); //wo user jiska snapshot aya
                Picasso.get().load(users.getProfilePicture()).placeholder(R.drawable.person).into(binding.picProfile);
                //setting username and about in app from database
                binding.etUserName.setText(users.getUserName());
                binding.etAbout.setText(users.getStatus());
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }
);
        //adding firebase storage dependency for updating profile pic
//    when click on plus to go gallery
        binding.changeprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallary = new Intent();
                gallary.setAction(Intent.ACTION_GET_CONTENT);
                gallary.setType("image/*");
                startActivityForResult(gallary, 35);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData() != null) {//user selected an image
            Uri sfile = data.getData();
            binding.picProfile.setImageURI(sfile);//updation of profile in app
            final StorageReference reference = storage.getReference().child("pictures")
                    .child(FirebaseAuth.getInstance().getUid());//storing to firebase storage
            //if want to store all images use push
reference.putFile(sfile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {//when upload success download url of the image
            @Override
            public void onSuccess(Uri uri) {
                //when url downloaded put it in firebase database as profile pic
                database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("profilePicture").setValue(uri.toString());
                Toast.makeText(SettingsActivity.this, "image upload", Toast.LENGTH_SHORT).show();
            }
        });

    }
});

        }

    }
}