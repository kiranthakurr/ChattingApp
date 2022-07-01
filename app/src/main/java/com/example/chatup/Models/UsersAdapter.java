package com.example.chatup.Models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatup.Chat_detail_activity;
import com.example.chatup.R;
import com.example.chatup.TransparentActivity;
import com.example.chatup.fragments.Chat_fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{   //takes the list of users and context
    ArrayList<Users> list;
    Context context;
    String sender_contact;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public UsersAdapter(ArrayList<Users> list, Context context, String sender_contact) {
        this.list = list;
        this.context = context;
        this.sender_contact= sender_contact;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sample_chat_layout,parent,false);
        return new ViewHolder(view);   //takes the layout and inflate data in tha form of listview and return
    }

    @Override//it sets the data here
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {//layout_of_adapter is viewholder
Users users =list.get(position);
//to get image online set up picasso in gradle;
        Picasso.get().load(users.getProfilePicture()).placeholder(R.drawable.ic_baseline_person_24).into(holder.imageView);
       holder.username.setText(users.getUserName());

//getting done with last message
        FirebaseDatabase.getInstance().getReference().child("chats").
                child(FirebaseAuth.getInstance().getUid()+users.getUserID())
                .orderByChild("timestamp").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()) {
                            for (DataSnapshot sp2 : snapshot.getChildren()) {
                                holder.lastMessage.setText(sp2.child("message").getValue(String.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //done with updated contact
        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users= snapshot.getValue(Users.class);
                try {
                    sender_contact = users.getContact();
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//       holder.username.setText(users.getLastMessage());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Chat_detail_activity.class);
                //sending values from main to chat detail
                intent.putExtra("userID",users.getUserID());
                intent.putExtra("userName",users.getUserName());
                intent.putExtra("profilePic",users.getProfilePicture());
                intent.putExtra("phone",users.getContact());
                intent.putExtra("about",users.getStatus());
                intent.putExtra("sender_contact",sender_contact);
                intent.putExtra("token",users.getToken());
                context.startActivity(intent);//started the intent
            }
        });//when clicked on holder that is inflated by adapter
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenti = new Intent(context, TransparentActivity.class);
                intenti.putExtra("userStatus",users.getStatus());
                intenti.putExtra("userName",users.getUserName());
                intenti.putExtra("profilePic",users.getProfilePicture());
                context.startActivity(intenti);
                Toast.makeText(context, "image clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView lastMessage, username;  // yhis is we want to bombard

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.profile_image);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            username = itemView.findViewById(R.id.nameInLAyout);

        }
    }
}
