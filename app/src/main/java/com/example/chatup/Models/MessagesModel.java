package com.example.chatup.Models;

public class MessagesModel {
    //message id... whose sender or reciever from firebase
    //time
    //text
    String messageId, message,mainmsgID,imageURL;
    String timestamp;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public MessagesModel(String messageId, String message, String timestamp) {
        this.messageId = messageId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public MessagesModel(String messageId, String message) {
        this.messageId = messageId;
        this.message = message;
    }
    public MessagesModel(){}//if working with firebase

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMainmsgID() {
        return mainmsgID;
    }

    public void setMainmsgID(String mainmsgID) {
        this.mainmsgID = mainmsgID;
    }

}
