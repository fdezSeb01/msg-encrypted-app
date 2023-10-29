package com.upcompdistr.whatsappserver;

public class PrivKeyModel {
    private int user_id;
    private String private_key;
    
    public PrivKeyModel(int user_id, String private_key) {
        this.user_id = user_id;
        this.private_key = private_key;
    }

    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public String getPrivate_key() {
        return private_key;
    }
    public void setPrivate_key(String private_key) {
        this.private_key = private_key;
    }
}
