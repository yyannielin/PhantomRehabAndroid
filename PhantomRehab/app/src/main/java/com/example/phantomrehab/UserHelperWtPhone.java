package com.example.phantomrehab;

public class UserHelperWtPhone {

    String user, email, pw;

    public UserHelperWtPhone() {}

    public UserHelperWtPhone(String user, String email, String pw) {
        this.user = user;
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
}

