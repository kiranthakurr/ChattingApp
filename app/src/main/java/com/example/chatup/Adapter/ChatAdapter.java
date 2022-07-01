package com.example.chatup.Adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatup.Models.MessagesModel;
import com.example.chatup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


// when chat send -- click on send button adapter takes the context and message info and set the layout

public class ChatAdapter extends RecyclerView.Adapter{// mostly we extend viewholder also but we have two viewholders here so
    // this work done separately

    ArrayList<MessagesModel>  messagesModel;  // takes array of messagesmodel which have attributes     // message id,time,text
    Context context;
    String recID;
    int SENDER_VIEW_TYPE=1;
    int RECIEVER_VIEW_TYPE=2;

    public ChatAdapter(ArrayList<MessagesModel> messagesModel, Context context, String recID) {
        this.messagesModel = messagesModel;
        this.context = context;
        this.recID = recID;
    }
//method created

    @Override
    public int getItemViewType(int position) {
        //condition to know which view type sender or reciever
        //condition -- device pe jis person ne login kiya hai wo sender hai,, identification
        if(messagesModel.get(position).getMessageId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }
        else{
            return RECIEVER_VIEW_TYPE;
        }
    }

    //all constructors
    public ChatAdapter(ArrayList<MessagesModel> messagesModel, Context context) {
        this.messagesModel = messagesModel;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {    //layout identified
        if(viewType==SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new senderViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciever,parent,false);
            return new recieveViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {//message identified
    MessagesModel mm= messagesModel.get(position);
//when long click on message
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete the message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//go to firebase
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String senderRoom= FirebaseAuth.getInstance().getUid()+recID;
                                database.getReference().child("chats").child(senderRoom).child(mm.getMainmsgID()).setValue(null);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                     dialog.dismiss();
                            }
                        }).show();
                return false;
            }
        });

    if(holder.getClass()==senderViewHolder.class){

        if(mm.getMessage().equals("photo")){
            ((senderViewHolder)holder).img.setVisibility(View.VISIBLE);
            ((senderViewHolder)holder).senderText.setVisibility(View.GONE);
            Glide.with(context).load(mm.getImageURL()).placeholder(R.drawable.loading).into(((senderViewHolder)holder).img);
        }

        ((senderViewHolder)holder).senderText.setText(mm.getMessage());
        ((senderViewHolder)holder).senderTime.setText(mm.getTimestamp());
    }
    else{
        if(mm.getMessage().equals("photo")){
            ((recieveViewHolder)holder).img.setVisibility(View.VISIBLE);
            ((recieveViewHolder)holder).recieverText.setVisibility(View.GONE);
        Glide.with(context).load(mm.getImageURL()).placeholder(R.drawable.loading).into(((recieveViewHolder)holder).img);
    }

        ((recieveViewHolder)holder).recieverText.setText(mm.getMessage());
        ((recieveViewHolder)holder).recieverTime.setText(mm.getTimestamp());
    }
    }

    @Override
    public int getItemCount() {
        return messagesModel.size();
    }


//    in normal adapters we do create viewholders and then on bind set text
//    but here we have to layouts so two viewholders
    public class recieveViewHolder extends RecyclerView.ViewHolder {
        TextView recieverText, recieverTime;
        ImageView img;
    public recieveViewHolder(@NonNull View itemView) {
        super(itemView);
        recieverText=itemView.findViewById(R.id.recievertext);
        recieverTime=itemView.findViewById(R.id.recieverTime);
        img=itemView.findViewById(R.id.imageView3);
    }
}
    public class senderViewHolder extends RecyclerView.ViewHolder {
        TextView senderText, senderTime;
        ImageView img;
        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderText=itemView.findViewById(R.id.senderText);
            senderTime=itemView.findViewById(R.id.senderTime);
            img=itemView.findViewById(R.id.img7);

        }
    }
}
