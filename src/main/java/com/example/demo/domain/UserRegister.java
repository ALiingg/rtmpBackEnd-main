package com.example.demo.domain;

import jakarta.persistence.*;


@Entity
public class UserRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long uid;
    private String token;
    private String uname;
    private String email;
    private int role = 0;
    private String code;
    private String passcode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return uname;
    }

    public void setUsername(String uname) {
        this.uname = uname;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    // 密码属性varchar对应String
    private String password;

    public int getRole() {
        return role;
    }
    public void setRole(int role) {
        this.role = role;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getPasscode() {
        return passcode;
    }
    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }
}