package com.example.shareware;

public class User {
    private String username,id, firstname, lastname, email, password, mobile;
    private String avatar = null;
    private String address = null;
    public User(){

    }

    public User(String username, String id, String password, String firstname, String lastname, String mobile, String email){
        this.username = username;
        this.id = id;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.mobile = mobile;
        this.email = email;
    }

    //setters
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAddress(String address) {this.address = address;}


    //getters
    public String getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getMobile() {
        return mobile;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getAddress() { return address; }

    public String getID() { return id; }
}
