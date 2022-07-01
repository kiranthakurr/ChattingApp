package com.example.chatup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.chatup.Adapter.ChatAdapter;
import com.example.chatup.Models.MessagesModel;
import com.example.chatup.databinding.ActivityGroupChatBinding;
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

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GroupChat extends AppCompatActivity {
ActivityGroupChatBinding binding;
 FirebaseDatabase database;
 FirebaseStorage storage= FirebaseStorage.getInstance();
 Date date= new Date();
  String senderID;
ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();//hinding action bar

        //creating database instance
         database= FirebaseDatabase.getInstance();
        final ArrayList<MessagesModel> messagesModel = new ArrayList<>();//arraylist
        final ChatAdapter ada = new ChatAdapter(messagesModel,this);//adapter
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);//layout manager
        layoutManager.setStackFromEnd(true);

        senderID= FirebaseAuth.getInstance().getUid();//getting sender id
        binding.userName.setText("F.R.I.E.N.D.S.");//setting name of group
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Uploading image");
        progressDialog.setCancelable(false);

        binding.chatRecyclerView.setAdapter(ada);//set adapter
        binding.chatRecyclerView.setLayoutManager(layoutManager);//setting lm

        //back arrow
        binding.backArrowChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to main activity
                Intent intent = new Intent(GroupChat.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //click on send
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.sendMessage.getText().toString().isEmpty()){
                    binding.sendMessage.setError("Please write message!");
                    return;
                }
                final String message = binding.sendMessage.getText().toString();

                //new message model created = one message data
               final MessagesModel model = new MessagesModel(senderID, message);
                Format f = new SimpleDateFormat("HH:mm a");
                String strResult = f.format(new Date());
                model.setTimestamp(strResult);//setting time for sender

                //clear message edit text
                binding.sendMessage.setText("");
                database.getReference().child("Group chat")//when sent stored in sender side database
                        .push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
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

        // dataget from firebase in the form of arraylist==========
        database.getReference().child("Group chat").addValueEventListener(
                new ValueEventListener() {   // sender room used because aapko aapke msg ayege
                    // even sent or recieved
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesModel.clear(); // every time one message add
                        //since many messages so loop one by one deal all
                        for (DataSnapshot snap1 : snapshot.getChildren()) {
                            MessagesModel model = snap1.getValue(MessagesModel.class);
                            messagesModel.add(model);
                        }
                        ada.notifyDataSetChanged(); // nal d nal update hoje sent message without back press
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 35) {
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
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            reference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {//when upload success download url of the image
                                @Override
                                public void onSuccess(Uri uri) {
                                    String filepath = uri.toString();
                                    final String message = binding.sendMessage.getText().toString();

                                    //new message model created = one message data
                                    final MessagesModel model = new MessagesModel(senderID, message);
                                    model.setMessage("photo");
                                    model.setImageURL(filepath);
                                    Format f = new SimpleDateFormat("HH:mm a");
                                    String strResult = f.format(new Date());
                                    model.setTimestamp(strResult);//setting time for sender

                                    //clear message edit text
                                    binding.sendMessage.setText("");
                                    database.getReference().child("Group chat")//when sent stored in sender side database
                                            .push()
                                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
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



}