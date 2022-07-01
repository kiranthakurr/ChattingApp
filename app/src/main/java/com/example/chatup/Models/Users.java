package com.example.chatup.Models;
//models is a package that contains users
//user class contains user information

public class Users {
    //image is in the form of string can be in the form of integer
    String ProfilePicture, userName, mail, password, userID, lastMessage,contact,token;
    String status="New on ChatUp!";

    public Users() {
    }//default


    public Users(String profilePicture, String userName, String mail, String password, String userID, String lastMessage, String status) {
        ProfilePicture = profilePicture;
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.userID = userID;
        this.lastMessage = lastMessage;
        this.status = status;
    }

    public Users(String profilePicture, String userName, String mail, String password, String userID, String lastMessage, String contact, String token, String status) {
        ProfilePicture = profilePicture;
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.userID = userID;
        this.lastMessage = lastMessage;
        this.contact = contact;
        this.token = token;
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePicture() {
        return ProfilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        ProfilePicture = profilePicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getUserID(){
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    //signup constructor
    public Users(String userName,  String mail,String password) {
        this.mail = mail;
        this.userName = userName;
        this.password = password;
    }
}
