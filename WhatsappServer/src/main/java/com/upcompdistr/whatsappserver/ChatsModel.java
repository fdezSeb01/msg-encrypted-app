package com.upcompdistr.whatsappserver;

import java.util.List;

class Message {
    private String text;
    private String time;
    private int message_id;
    private int sender_id;
    private String hash;
    private String encRndKey;
    private int msgType;

    // Constructors, getters, and setters
    public Message() {}
    
    public Message(String text, String time, int message_id, int sender_id, String hash, String encRndKey,
            int msgType) {
        this.text = text;
        this.time = time;
        this.message_id = message_id;
        this.sender_id = sender_id;
        this.hash = hash;
        this.encRndKey = encRndKey;
        this.msgType = msgType;
    }


    public String getHash() {
        return hash;
    }


    public void setHash(String hash) {
        this.hash = hash;
    }


    public String getEncRndKey() {
        return encRndKey;
    }


    public void setEncRndKey(String encRndKey) {
        this.encRndKey = encRndKey;
    }


    public int getMsgType() {
        return msgType;
    }


    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }
}

public class ChatsModel {
    private int user_id;
    private int destination_user_id;
    private Message last_message;
    private List<Message> messages;

    // Constructors, getters, and setters
    public ChatsModel() {}

    public ChatsModel(int user_id, int destination_user_id, Message last_message, List<Message> messages) {
        this.user_id = user_id;
        this.destination_user_id = destination_user_id;
        this.last_message = last_message;
        this.messages = messages;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getDestination_user_id() {
        return destination_user_id;
    }

    public void setDestination_user_id(int destination_user_id) {
        this.destination_user_id = destination_user_id;
    }

    public Message getLast_message() {
        return last_message;
    }

    public void setLast_message(Message last_message) {
        this.last_message = last_message;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}

