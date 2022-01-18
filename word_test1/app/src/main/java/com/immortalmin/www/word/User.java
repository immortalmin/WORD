package com.immortalmin.www.word;

import java.io.Serializable;

/**
 * user information
 */
public class User implements Serializable{
    private String uid,open_id,username,password,profile_photo,motto=null,email,telephone,access_token=null,expires_in=null;
    private int recite_num,recite_scope,status=0,login_mode,ignore_version;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
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

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public int getLogin_mode() {
        return login_mode;
    }

    public void setLogin_mode(int login_mode) {
        this.login_mode = login_mode;
    }

    public int getIgnore_version() {
        return ignore_version;
    }

    public void setIgnore_version(int ignore_version) {
        this.ignore_version = ignore_version;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", open_id='" + open_id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", motto='" + motto + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", access_token='" + access_token + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", recite_num=" + recite_num +
                ", recite_scope=" + recite_scope +
                ", status=" + status +
                ", login_mode=" + login_mode +
                ", ignore_version=" + ignore_version +
                ", last_login=" + last_login +
                '}';
    }
}
