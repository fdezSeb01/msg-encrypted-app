package com.upcompdistr.whatsappserver;

public class DigitalCertificateModel {
    private String uid;
    private int user_id;
    private String name;
    private String pubKey;
    private String validFrom;
    private String validTo;
    
    @Override
    public String toString() {
        return "Certificado digital uid=" + uid + ", user_id=" + user_id + ", name=" + name + ", pubKey=" + pubKey
                + ", validFrom=" + validFrom + ", validTo=" + validTo;
    }
    public DigitalCertificateModel(String uid, int user_id, String name, String pubKey, String validFrom,
            String validTo) {
        this.uid = uid;
        this.user_id = user_id;
        this.name = name;
        this.pubKey = pubKey;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
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
    public String getPubKey() {
        return pubKey;
    }
    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }
    public String getValidFrom() {
        return validFrom;
    }
    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }
    public String getValidTo() {
        return validTo;
    }
    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }
}