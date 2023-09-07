package com.upcripto.whatsappseguro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;

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
    public int user_id;
    public int destination_id;
    public String msg;
    public String time;
    public newMsg(int user_id, int destination_id, String msg, String time) {
        this.user_id = user_id;
        this.destination_id = destination_id;
        this.msg = msg;
        this.time = time;
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
            e.printStackTrace();
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
                            case "messageIncoming":
                                break;
                            case "ContactsRecieved":
                                handleContactsRecieved(jsonObject);
                                break;
                            case "numberCheck":
                                handleNumberChecked(jsonObject.get("user_id").getAsInt());
                                break;
                            default:
                                handleUnsupportedAction(action);
                                break;
                        }
                        System.out.println("Received original message from server: " + inputLine);
                    }
                } catch (IOException e) {
                    // Handle exceptions as needed (e.g., log them)
                }
    }

    public static void sendMsg2Server(int user_id, int destination_id, String text, String time) {
        newMsg msg = new newMsg(user_id, destination_id, text, time);
        Gson gson = new Gson();
        String msgJSON = gson.toJson(msg);
        talk2server(msgJSON);
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

    private static void handleNumberChecked(int user_id){
        ContactosController.numberCheckedGotten(user_id);
    }
}
