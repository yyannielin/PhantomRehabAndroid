package com.example.phantomrehab;

public class UserHelper {

    String user, email, pw, phone;

    public UserHelper() {}

    public UserHelper(String user, String email, String pw, String phone) {
        this.user = user;
        this.phone = phone;
        this.email = email;
        this.pw = pw;
    }

    public String getUser() {
        return user;
    }

    public String getEmail() {
        return email;
    }

    public String getPw() {
        return pw;
    }

    public String getPhone() {
        return phone;
    }
}
