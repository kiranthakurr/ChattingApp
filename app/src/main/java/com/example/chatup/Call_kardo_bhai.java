package com.example.chatup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chatup.databinding.ActivityCallKardoBhaiBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.ncorti.slidetoact.SlideToActView;
import com.squareup.picasso.Picasso;

public class Call_kardo_bhai extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    ActivityCallKardoBhaiBinding binding;
    String recieveName;
    String profilePic;
    String phone;
    String sender_contact;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    FirebaseAuth auth=FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivityCallKardoBhaiBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        recieveName = getIntent().getStringExtra("userName");
        profilePic = getIntent().getStringExtra("profilePic");
        phone = getIntent().getStringExtra("phone");
        sender_contact=getIntent().getStringExtra("sender_contact");

        binding.username.setText(recieveName);
        if(phone==null){
            binding.phone.setText("Not registered");
        }
        else {
            binding.phone.setText(phone);
        }
        Picasso.get().load(profilePic).placeholder(R.drawable.person).into(binding.picProfile);

        binding.example.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NonNull SlideToActView slideToActView) {
                if(sender_contact==null){ //if your contact is null
                    AlertDialog.Builder builder=new AlertDialog.Builder(Call_kardo_bhai.this);
                    builder.setTitle("Can't call!");
                    builder.setMessage("Your are not registered with calling, please enter your number");
                    final EditText input = new EditText(Call_kardo_bhai.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    input.setLayoutParams(lp);
                    builder.setView(input);
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    database.getReference().child("users").child(auth.getUid()).child("contact").setValue(input.getText().toString());
                                    sender_contact=input.getText().toString();
//                                    Log.d("cout",sender_contact[0])
                                    binding.example.resetSlider();

                                }
                            });

                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            binding.example.resetSlider();
                        }

                    });
                    builder.show();
                    // when data changed in


                }




                else if(phone==null){
                    AlertDialog.Builder builder=new AlertDialog.Builder(Call_kardo_bhai.this);
                    builder.setTitle("Can't call!");
                    builder.setMessage("Person you are trying to reach has not registerd their mobile number");
                    builder.setPositiveButton( "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    binding.example.resetSlider();
                                }
                            });
                    builder.show();
                }

                else {
                    phn_call_method(phone);
                }
            }
        });
    }
    public void phn_call_method (String phone) {
        String number = phone;
        if (number.length() < 10) {
            Toast.makeText(Call_kardo_bhai.this, number, Toast.LENGTH_SHORT).show();
        } else {
            String s = "tel:" + number;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(s));
//            to check if the permission was granted previously or not
            if (ContextCompat.checkSelfPermission(Call_kardo_bhai.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(Call_kardo_bhai.this,
                        new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                binding.example.resetSlider();
                // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            } else {
                //You already have permission
                try {
                    startActivity(intent);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}