package com.upcripto.whatsappseguro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


class User{
    String action;
    String name;
    String phone_num;
    public User(String name, String num) {
        this.name = name;
        this.phone_num = num;
        action = "validateLogin";
    }
}

class newMsg{
    public String action;
    public String chat_id;
    public int sender_id;
    public String msg;
    public String time;
    public int destination_id;
    public String hash;
    public String encRndKey;
    public int msgType;

    public newMsg(String chat_id, int sender_id, String msg, String time, int destination_id, String hash, String encRndKey, int msgType) {
        this.chat_id = chat_id;
        this.sender_id = sender_id;
        this.msg = msg;
        this.time = time;
        this.destination_id = destination_id;
        this.hash = hash;
        this.encRndKey = encRndKey;
        this.msgType=msgType;
        action = "newMsg";

    }
}

class ChatsRequest{
    public String action;
    public int user_id;
    public ChatsRequest(int user_id) {
        this.user_id = user_id;
        this.action = "chatsRequest";
    }
    
}

class CheckUser{
    public String action;
    public String num;
    public CheckUser(String num) {
        this.num = num;
        this.action = "checkUser";
    }
    
}

class checkChat{
    int user_id;
    int destination_id;
    String action;
    public checkChat(int user_id, int destination_id) {
        this.user_id = user_id;
        this.destination_id = destination_id;
        this.action = "checkChat";
    } 
}

class requestMessages{
    String id;
    String action;
    public requestMessages(String id) {
        this.id = id;
        this.action = "requestMessages";
    } 
}

class addUser{
    String action;
    int user_id;
    public addUser(int user_id) {
        this.user_id = user_id;
        this.action = "addUser";
    }
}

class requestPubKey{
    int user_id;
    String action;
    public requestPubKey(int user_id){
        this.user_id = user_id;
        this.action = "requestPubKey";
    }
}



public class ConnectionsController {
    private static Socket socket;
    private static PrintWriter out; // Add this field for sending messages

    public static void SocketConnect() {
        final String SERVER_ADDRESS = "localhost"; // Change to the server's IP address if needed
        final int SERVER_PORT = 5109;
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to server "+SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);

            // Create a dedicated thread for receiving messages
            Thread receiveThread = new Thread(() -> {
                Listener();
            });
            receiveThread.setDaemon(true);
            receiveThread.start();
        } catch (IOException e) {
            System.out.println("Could not connect to Server on  port " +SERVER_PORT);
            //e.printStackTrace();
        }
    }

    public static void Listener(){
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        // Process the received message (e.g., update UI)
                        Gson gson = new Gson();
                        JsonElement jsonElement = gson.fromJson(inputLine, JsonElement.class);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        String action = jsonObject.get("action").getAsString();
                        switch(action){
                            case "loginResponse":
                                handleLoginValidationResponse(jsonObject.get("user_id").getAsString());
                                break;
                            case "ContactsRecieved":
                                handleContactsRecieved(jsonObject);
                                break;
                            case "numberCheck":
                                handleNumberChecked(jsonObject.get("user_id").getAsInt(),jsonObject.get("name").getAsString());
                                break;
                            case "chat_idAttached":
                                handleChat_idRecieved(jsonObject.get("id").getAsString());
                                break;
                            case "ChatAttached":
                                handleMessagesRecieved(jsonObject);
                                break;
                            case "refreshContactos":
                                handleRefresh();
                                break;
                            case "incomingMsg":
                                hanldeIncomingMessage(jsonObject);
                                break;
                            case "pubKey_attached":
                                handlePubKeyRecieved(jsonObject.get("pubKey").getAsString());
                                break;
                            default:
                                handleUnsupportedAction(action);
                                break;
                        }
                        System.out.println("Received message from server: " + inputLine);
                    }
                } catch (IOException e) {
                    // Handle exceptions as needed (e.g., log them)
                }
    }

    public static void sendMsg2Server(String chat_id, int sender_id, String text, String time, int destination_id, String hash, String encRndKey, int msgType) {
        newMsg msg = new newMsg(chat_id, sender_id, text,time, destination_id, hash,encRndKey,msgType);
        talk2server(msg);
    }
    
    public static void closeSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Socket closed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ValidateLogin(String name, String num){
        User user = new User(name, num);
        talk2server(user);
    }

    public static <T> void talk2server(T obj){
        Gson gson = new Gson();
        String objJSON = gson.toJson(obj);
        if (out != null) {
            out.println(objJSON);
        } else {
          System.out.println("Can't send message to server");  
        }
    }

    public static void handleLoginValidationResponse(String response) throws IOException{
        LoginController.ValidationGotten(Integer.parseInt(response));
    }



    private static void handleContactsRecieved(JsonObject obj) throws IOException {
        JsonArray contactsArray = obj.getAsJsonArray("contacts");        
        int n = contactsArray.size();

        int[] ids = new int[n];
        String[] names = new String[n];
        String[] pps = new String[n];
        String[] messages_text = new String[n];
        String[] messages_time = new String[n];

        for (int i = 0; i < n; i++) {
            JsonObject contactObj = contactsArray.get(i).getAsJsonObject();
            ids[i] = contactObj.get("destination_user_id").getAsInt();
            names[i] = contactObj.get("name").getAsString();
            pps[i] = contactObj.get("profile_pic").getAsString();
            messages_text[i] = contactObj.get("last_message_text").getAsString();
            messages_time[i] = contactObj.get("last_message_time").getAsString();
        }

        ContactosController.ContactsGotten(n, ids, names, pps, messages_text, messages_time);
    }

    private static void handleUnsupportedAction(String action){
        
        System.out.println("Error managing action: "+action);
    }

    public static void getChatsFrom(int user_id){
        ChatsRequest cr = new ChatsRequest(user_id);
        talk2server(cr);
    }

    public static void check_user_exists(String num){
        CheckUser cu = new CheckUser(num);
        talk2server(cu);
    }

    private static void handleNumberChecked(int user_id, String name){
        ContactosController.numberCheckedGotten(user_id, name);
    }

    public static void checkIfChatExistsAddIfNot(int user_id, int destination_id){
        checkChat cc = new checkChat(user_id, destination_id);
        talk2server(cc);
    }

    public static void requestLoadMessages(String id){
        requestMessages rm  = new requestMessages(id);
        talk2server(rm);
    }

    private static void handleChat_idRecieved(String id){
        MainController.setChat_id(id);
    }

    private static void handleMessagesRecieved(JsonObject obj){
        String[] messages;
        String[] senders;
        String[] times; 
        String[] hashes;
        String[] encRndKeys;
        int[] types; 
        
        JsonObject chat = obj.get("chat").getAsJsonObject();
        JsonArray messagesArray = chat.get("messages").getAsJsonArray();
        int n = messagesArray.size();

        messages = new String[n];
        senders = new String[n];
        times = new String[n];
        hashes = new String[n];
        encRndKeys = new String[n];
        types = new int[n];

        List<JsonObject> sortedMessages = new ArrayList<>();
        for (JsonElement messageElement : messagesArray) {
            sortedMessages.add(messageElement.getAsJsonObject());
        }

        // Sort messages by message_id
        sortedMessages.sort(Comparator.comparing(m -> m.get("message_id").getAsInt()));

        for (int i = 0; i < n; i++) {
            JsonObject messageObj = sortedMessages.get(i);
            messages[i] = messageObj.get("text").getAsString();
            senders[i] = String.valueOf(messageObj.get("sender_id").getAsInt());
            times[i] = messageObj.get("time").getAsString();
            hashes[i] = messageObj.get("hash").getAsString();
            encRndKeys[i] = messageObj.get("encRndKey").getAsString();
            types[i] = messageObj.get("type").getAsInt();
        }
        
        MainController.recieveMessages(messages, senders, times,hashes,encRndKeys,types);

    }

    public static void setClientIdForServer(int user_id){
        addUser au = new addUser(user_id);
        talk2server(au);
    }

    private static void handleRefresh() throws IOException{
        ContactosController.RefreshPage();
    }

    private static void hanldeIncomingMessage(JsonObject obj){
        String msg = obj.get("msg").getAsString();
        String time = obj.get("time").getAsString();
        String id = obj.get("chat_id").getAsString();
        String hash = obj.get("hash").getAsString();
        String encRndKey = obj.get("encRndKey").getAsString();
        int type = obj.get("type").getAsInt();

        MainController.recieveMsg(msg, time,id,hash,encRndKey,type);
    }

    public static void requestDestinationPubKey(int user_id){
        requestPubKey rpk = new requestPubKey(user_id);
        talk2server(rpk);
    }

    public static void handlePubKeyRecieved(String pubKey){
        MainController.setPubDestKey(pubKey);
    }
}
