package com.example.home_buddy_captain.model;

public class NewUserModel {

    String email, password, role, username, mobile;

    public NewUserModel(String email, String password, String role, String username, String mobile) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.username = username;
        this.mobile = mobile;
    }
    public NewUserModel(String email, String password, String role, String username) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.username = username;
    }
    public NewUserModel(){

    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
