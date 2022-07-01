package com.example.chatup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chatup.Adapter.Fragments_Adapter;
import com.example.chatup.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;//import binding
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());//obj of binding
        setContentView(binding.getRoot());//root
        auth = FirebaseAuth.getInstance();//obj of auth
        database=FirebaseDatabase.getInstance();//instance of database
        binding.viewPager.setAdapter(new Fragments_Adapter(getSupportFragmentManager()));//adapter with viewpager
        binding.tabLayout.setupWithViewPager(binding.viewPager);//tabs with viewpager
        //acction bar elevation set 14 tablayout elevation set 14 so no line come in between
        getSupportActionBar().setElevation(0);

        FirebaseMessaging.getInstance()
                .getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                        HashMap<String,Object> obj=new HashMap<>();
                        obj.put("token",token);
                        database.getReference().child("users")
                                        .child(FirebaseAuth.getInstance().getUid())
                                                .updateChildren(obj);
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//populate the menu we have with the items in R.menu.menu
        MenuInflater inflater=getMenuInflater();//create a inflater
        inflater.inflate(R.menu.menu,menu);//which inflate the items
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//when we select an option in menu
        switch(item.getItemId())//get the id of selected item
        {//logout selected
            case R.id.signout:
                String currentID = FirebaseAuth.getInstance().getUid();
                database.getReference().child("presence").child(currentID).setValue(null);
                auth.signOut();//user logout
                //go back to sign in page
                Intent intent = new Intent(MainActivity.this,SignIn.class);//take me directly to main page
                startActivity(intent);
                break;

            case R.id.settings:
                Intent intent2 = new Intent(MainActivity.this,SettingsActivity.class);//take me directly to main page
                startActivity(intent2);
                break;
            case R.id.group:
                Intent intent1 = new Intent(MainActivity.this,GroupChat.class);//take me directly to group chat activity
                startActivity(intent1);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //switching of tabs

        if(binding.viewPager.getCurrentItem()==0){
            super.onBackPressed();
        }
        else{
            binding.viewPager.setCurrentItem(0);
        }
    }
    }

