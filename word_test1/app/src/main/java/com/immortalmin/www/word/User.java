package com.immortalmin.www.word;

import java.io.Serializable;

/**
 * user information
 */
public class User implements Serializable{
    private String uid,username,password,profile_photo,status="0",motto,email,telephone;
    private int recite_num,recite_scope;
    private long last_login;

    public User(){}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRecite_num() {
        return recite_num;
    }

    public void setRecite_num(int recite_num) {
        this.recite_num = recite_num;
    }

    public int getRecite_scope() {
        return recite_scope;
    }

    public void setRecite_scope(int recite_scope) {
        this.recite_scope = recite_scope;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getLast_login() {
        return last_login;
    }

    public void setLast_login(long last_login) {
        this.last_login = last_login;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", status='" + status + '\'' +
                ", motto='" + motto + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", recite_num=" + recite_num +
                ", recite_scope=" + recite_scope +
                ", last_login=" + last_login +
                '}';
    }
}
