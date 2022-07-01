package com.example.chatup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.chatup.Models.Users;
import com.example.chatup.databinding.ActivitySignupBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class Signup extends AppCompatActivity {

ActivitySignupBinding binding; // removing findViewByID and importing class
    private FirebaseAuth auth;  // imorting firebase account creation class
    FirebaseDatabase database;// importing firebase data storing class
    ProgressDialog dlg;//dialog box that appers during signing upp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();//hiding the action bar
        auth = FirebaseAuth.getInstance();// firebase auth object created
        database = FirebaseDatabase.getInstance();//firebase database object created

        dlg = new ProgressDialog(Signup.this);//creating object of dialog box
        dlg.setTitle("Creating account");//what it contains also
        dlg.setMessage("We are creating your account!");//when to show & stop will be in clickbutton

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dilog on click
                dlg.show();
                //creating an account using auth we take email & password and t                    ask create userID
                auth.createUserWithEmailAndPassword
                                (binding.etMail.getText().toString(), binding.etPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //dismiss dlg when closed
                                dlg.dismiss();
                                if (task.isSuccessful()) {
                                    //when authentication completes it makes user in users class
                                    // with constructor of 3 param(name,mail,password)
                                    Users user = new Users(binding.etUser.getText().toString(), binding.etMail.getText().toString(), binding.etPassword.getText().toString());
                                    String id = task.getResult().getUser().getUid();//getting id that is generated after authentication
                                    //setting user info into database
                                    database.getReference().child("users").child(id).setValue(user);
                                    // users root mai, naam ka chid jo store krta hai user ki sari information
                                    Toast.makeText(Signup.this, "User created Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                //userID will be created by upper method now we have to store the data in the database

            }

        });
        //working on Sign in text in sign up activity
        binding.tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, SignIn.class);//take me directly to main page
                startActivity(intent);
            }
        });

    }


}

//     msg.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
//@Override
//public void onComplete(@NonNull Task<String> task) {
//        if (!task.isSuccessful()) {
//        Log.w("TAG", "Fetching FCM registration token failed", task.getException());
//        return;
//        }
//
//        // Get new FCM registration token
//        String token = task.getResult();
//        user.setToken(token);
//        }
//        });