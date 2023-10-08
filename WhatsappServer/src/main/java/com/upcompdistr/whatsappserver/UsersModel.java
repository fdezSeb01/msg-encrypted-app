package com.upcompdistr.whatsappserver;

public class UsersModel {
    private int user_id;
    private String name;
    private String phone_num;
    private String profile_pic;
    private String pubKey;
    private String privKey;

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }



    public String getPrivKey() {
        return privKey;
    }

    public void setPrivKey(String privKey) {
        this.privKey = privKey;
    }

    // Constructors, getters, and setters
    public UsersModel() {}


    public UsersModel(int user_id, String name, String phone_num, String profile_pic, String pubKey, String privKey) {
        this.user_id = user_id;
        this.name = name;
        this.phone_num = phone_num;
        this.profile_pic = profile_pic;
        this.pubKey = pubKey;
        this.privKey = privKey;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }
}

