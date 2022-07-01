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
import com.example.chatup.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity {
    private static final int RC_GET_AUTH_CODE = 45;
    ActivitySignInBinding binding;//importing binding
    ProgressDialog dlg;//importing dialog
    FirebaseAuth auth;//auth import
    int RC_SIGN_IN =65;//any number <100
    GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignInBinding.inflate(getLayoutInflater());//find view by id of all
        setContentView(binding.getRoot());//setting root
        getSupportActionBar().hide();//hiding the action bar

        //object of dialog
        dlg=new ProgressDialog(SignIn.this);
        dlg.setTitle("Signing In");
        dlg.setMessage("You will be signed in soon");
        auth=FirebaseAuth.getInstance();//obj of auth
        database=FirebaseDatabase.getInstance();//obj of database

        //signing up with google
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("1014996006998-veli2lbuf4rjt3m5qhguevo1ei1vut2h.apps.googleusercontent.com")
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);

        //sign in with googlw when button google pressed
        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        binding.tvForeget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this,Forgot_Password.class);
                startActivity(intent);
            }
        });

        binding.btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if empty edit text in sign in
                if(binding.etMail.getText().toString().isEmpty()){
                    binding.etMail.setError("Please enter your email!");
                    return;
                }
                if(binding.etPassword.getText().toString().isEmpty()){
                    binding.etPassword.setError("Please enter your Password!");
                    return;
                }
                dlg.show();
                auth.signInWithEmailAndPassword(binding.etMail.getText().toString(),binding.etPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dlg.dismiss();
                                if(task.isSuccessful()){//if right go to main activity
                                    Intent intent = new Intent(SignIn.this,MainActivity.class);
                                    startActivity(intent);
                                }
                                else{//if wrong user or password
                                    Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        if(auth.getCurrentUser()!=null){//if user already sign in
            Intent intent = new Intent(SignIn.this,MainActivity.class);//take me directly to main page
            startActivity(intent);

        }

        //working on Sign up text in sign in activity
        binding.tvUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this,Signup.class);//take me directly to main page
                startActivity(intent);
            }
        });
    }
    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent(); //intent aagya
        startActivityForResult(signInIntent, RC_SIGN_IN);// click krti
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);//go to google
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);//if we get result intent
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());//fetching data function called
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    // [START auth_with_google]
    public void firebaseAuthWithGoogle(String idToken) {
        //signing in with chosen mail
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //if signing in (authorisation) successful
                            Log.d("TAG", "signInWithCredential:success");
                            //this is authentication wala user
                            FirebaseUser user = auth.getCurrentUser();
                            //new user created and set all values to it
                            Users users = new Users();
                            users.setUserName(user.getDisplayName());
                            users.setUserID(user.getUid());
                            users.setProfilePicture(user.getPhotoUrl().toString());
                            //saving data to database
                            database.getReference().child("users").child(user.getUid()).setValue(users);
                            Intent intent = new Intent(SignIn.this,MainActivity.class);//take me directly to main page
                            startActivity(intent);
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
//                            updateUI(null);
                        }
                    }
                });
    }

}