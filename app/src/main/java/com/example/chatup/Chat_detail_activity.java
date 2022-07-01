package com.example.chatup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

//import com.android.volley.AuthFailureError;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatup.Adapter.ChatAdapter;
import com.example.chatup.Models.MessagesModel;
import com.example.chatup.Models.Users;
import com.example.chatup.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Chat_detail_activity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    ActivityChatDetailBinding binding;//import binding for all
    FirebaseDatabase database; // import database
    FirebaseStorage storage;
    FirebaseAuth auth;//import firebase authorization
    String sender_contact;
    String recieveID;
    Date date = new Date();
    ProgressDialog progressDialog;
    String RecieverRoom;
    String SenderRoom;
    String senderID;
    String recieveName;
    String profilePic;
    String phone;
    String about;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());//all view found
        setContentView(binding.getRoot());//set content
        getSupportActionBar().hide();

        //creating database instance
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();//instanse of authorisation
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading image");
        progressDialog.setCancelable(false);

        senderID = auth.getUid();// id of one who sign in      final-global
        recieveID = getIntent().getStringExtra("userID");// id of one whose chat is open
        // these ids are taken so that we can save the chat data between the users

        //now getting the values for the toolbar
        recieveName = getIntent().getStringExtra("userName");
        profilePic = getIntent().getStringExtra("profilePic");
        phone = getIntent().getStringExtra("phone");
        about = getIntent().getStringExtra("about");
        sender_contact = getIntent().getStringExtra("sender_contact");
        token = getIntent().getStringExtra("token");

        //setting values
        binding.userName.setText(recieveName);
        Picasso.get().load(profilePic).placeholder(R.drawable.person).into(binding.profileImage);
        //loading from intent..........//if no image.................// else this image

        binding.backArrowChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to main activity
                finish();
            }
        });


        database.getReference().child("presence").child(recieveID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String online = snapshot.getValue(String.class);
                    if (!online.isEmpty()) {
                        binding.online.setText(online);
                        binding.online.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.online.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //making arraylist of messages
        final ArrayList<MessagesModel> messagesModel = new ArrayList<>();
        final ChatAdapter add = new ChatAdapter(messagesModel, this, recieveID);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setAdapter(add);//setting adapter
        layoutManager.setStackFromEnd(true);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        RecieverRoom = recieveID + senderID;//child of chat with messages of other person
        SenderRoom = senderID + recieveID;   //child of chats with messages of itself

        //on clicking on send -=======================- adding to database
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.sendMessage.getText().toString().isEmpty()) {
                    binding.sendMessage.setError("Please write message!");
                    return;
                }
                //new message model created = one message data

                String message = binding.sendMessage.getText().toString();
                MessagesModel model = new MessagesModel(senderID, message);

                Format f = new SimpleDateFormat("HH:mm a");
                String strResult = f.format(new Date());
                model.setTimestamp(strResult);//setting time for sender


                //clear message edit text
                binding.sendMessage.setText("");
                database.getReference().child("chats")//when sent stored in sender side database
                        .child(SenderRoom).push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {//when reached stored on reciever side database
                                database.getReference().child("chats").child(RecieverRoom).push()
                                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
//
                                                sendNotification(recieveName,model.getMessage(),token);
                                            }
                                        });
                            }
                        });
            }
        });

        binding.clip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallary1 = new Intent();
                gallary1.setAction(Intent.ACTION_GET_CONTENT);
                gallary1.setType("image/*");
                startActivityForResult(gallary1, 35);

            }
        });
        binding.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Chat_detail_activity.this, NewActivity.class);
                intent.putExtra("userName", recieveName);
                intent.putExtra("profilePic", profilePic);
                intent.putExtra("phone", phone);
                intent.putExtra("about", about);
                startActivity(intent);
            }
        });


        // dataget from firebase in the form of arraylist==========
        database.getReference().child("chats").child(SenderRoom).addValueEventListener(
                new ValueEventListener() {   // sender room used because aapko aapke msg ayege
                    // even sent or recieved
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesModel.clear(); // every time one message add
                        //since many messages so loop one by one deal all
                        for (DataSnapshot snap1 : snapshot.getChildren()) {
                            MessagesModel model = snap1.getValue(MessagesModel.class);
                            model.setMainmsgID(snap1.getKey());
                            messagesModel.add(model);
                        }
                        add.notifyDataSetChanged(); // nal d nal update hoje sent message without back press
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.chatRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom <= oldBottom) {
                    binding.chatRecyclerView
                            .postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    binding.chatRecyclerView
                                            .smoothScrollToPosition(bottom);
                                }
                            }, 100);
                }
            }
        });
        // popup menu
        binding.popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop(v);
            }
        });
//        handler to handle typing
        final Handler handler = new Handler();
        //typing status changed
        binding.sendMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                database.getReference().child("presence").child(senderID).setValue("typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStopedType, 1000);
            }

            Runnable userStopedType = new Runnable() {  //this runnable ek ek second bad set value to online
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderID).setValue("online");
                }
            };
        });
        //for making call clicking on call button
        binding.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sender_contact == null) { //if your contact is null
                    AlertDialog.Builder builder = new AlertDialog.Builder(Chat_detail_activity.this);
                    builder.setTitle("Can't call!");
                    builder.setMessage("Your are not registered with calling, please enter your number");
                    final EditText input = new EditText(Chat_detail_activity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    input.setLayoutParams(lp);
                    builder.setView(input);
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    database.getReference().child("users").child(auth.getUid()).child("contact").setValue(input.getText().toString());
                                    sender_contact = input.getText().toString();
//                                    Log.d("cout",sender_contact[0])

                                }
                            });

                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                    // when data changed in

                } else if (phone == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Chat_detail_activity.this);
                    builder.setTitle("Can't call!");
                    builder.setMessage("Person you are trying to reach has not registerd their mobile number");
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();
                } else {
                    phn_call_method(phone);
                }
            }
        });


    }

    //getting started with notifications
    void sendNotification(String name, String message, String token) {
        try {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://fcm.googleapis.com/fcm/send";
            JSONObject data = new JSONObject();
            data.put("title", name);
            data.put("body", message);
            JSONObject notificationsData = new JSONObject();
            notificationsData.put("notification", data);
            notificationsData.put("to", token);
            JsonObjectRequest request = new JsonObjectRequest(url, notificationsData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(Chat_detail_activity.this, "success", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Chat_detail_activity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String,String>map=new HashMap<>();
                    String key="Key=AAAA7FJ5-FY:APA91bGXJ_aagHVohDeGOx2A7Vl848vBGvH6K2kRqHaDoirLbcrN2OcCg_XfHO6PNvDrsxXiohpwQ3QEC4l3IUP2G73tX6Ep7HZ7cdWJkYrvguxCmsKMbReRcc6CRryEgOVJvNXD5qeU";
                    map.put("Authorization",key);
                    map.put("Content-Type","application/json");
                    return map;

                }
            };
            queue.add(request);
        }

catch(Exception e){

        }
    }




       // phone calling code
            public void phn_call_method (String phone) {
                String number = phone;
                if (number.length() < 10) {
                    Toast.makeText(Chat_detail_activity.this, number, Toast.LENGTH_SHORT).show();
                } else {
                    String s = "tel:" + number;
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(s));
//            to check if the permission was granted previously or not
                    if (ContextCompat.checkSelfPermission(Chat_detail_activity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(Chat_detail_activity.this,
                                new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);

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

            //showing pop up menu when clicked on 3 dots
            public void showPop(View v){
                PopupMenu popupMenu= new PopupMenu(Chat_detail_activity.this,v);
                MenuInflater menuInflater=popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.activity,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.clear_chat:
                                new android.app.AlertDialog.Builder(Chat_detail_activity.this)
                                        .setTitle("Clear chat")
                                        .setMessage("Are you sure you want to clear the chat?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //go to firebase
                                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                String senderRoom = FirebaseAuth.getInstance().getUid() + recieveID;
                                                database.getReference().child("chats").child(senderRoom).setValue(null);
                                            }
                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                                break;
                            default:
                                break;

                        }
                        return false;
                    }

                });
                popupMenu.show();
            }
    // when image choosed to send
        @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==35) {
            if (data.getData() != null) {//user selected an image
                Uri sfilei = data.getData();
                final StorageReference reference1 = storage.getReference().child("uploads")
                        .child(date.getTime() + "aa");//storing to firebase storage
                progressDialog.show();
                //if want to store all images use push
                reference1.putFile(sfilei).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            reference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {//when upload success download url of the image
                                @Override
                                public void onSuccess(Uri uri) {
                                    String filepath= uri.toString();
                                    //when url downloaded put it in firebase database as profile pic
//                                    database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("profilePicture").setValue(uri.toString());
//                                    Toast.makeText(SettingsActivity.this, "image upload", Toast.LENGTH_SHORT).show();
                                    String message = binding.sendMessage.getText().toString();
                                    MessagesModel model = new MessagesModel(senderID, message);

                                    Format f = new SimpleDateFormat("HH:mm a");
                                    String strResult = f.format(new Date());
                                    model.setTimestamp(strResult);//setting time for sender
                                    model.setImageURL(filepath);
                                    model.setMessage("photo");

                                    //clear message edit text
                                    binding.sendMessage.setText("");
                                    database.getReference().child("chats")//when sent stored in sender side database
                                            .child(SenderRoom).push()
                                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {//when reached stored on reciever side database
                                                    database.getReference().child("chats").child(RecieverRoom).push()
                                                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }
                    }
                });

            }
        }
    }
    //online status when entered the chat section
    @Override
    public void onResume() {

        String currentID=FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentID).setValue("Online");
        super.onResume();
    }
    //online status when home pressed
    @Override
    public void onPause() {
        super.onPause();
        String currentID=FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentID).setValue(null);
    }


        }
