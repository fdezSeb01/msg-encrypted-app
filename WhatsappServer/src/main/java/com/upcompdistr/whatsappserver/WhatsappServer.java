package com.upcompdistr.whatsappserver;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class loginValidation{
    String action;
    int user_id;
    public loginValidation(int user_id) {
        this.action = "loginResponse";
        this.user_id = user_id;
    }
}

class Contact{
    String profile_pic;
    String last_message_text;
    String last_message_time;
    int destination_user_id;
    String name;
    public Contact(String profile_pic, String last_message_text, String last_message_time, int destination_user_id, String name) {
        this.profile_pic = profile_pic;
        this.last_message_text = last_message_text;
        this.last_message_time = last_message_time;
        this.destination_user_id = destination_user_id;
        this.name = name;
    }    
}

class Contacts{
    String action;
    List<Contact> contacts;
    public Contacts(List<Contact> contacts) {
        this.action = "ContactsRecieved";
        this.contacts = contacts;
    }   
}

class numberCheck{
    String action;
    int user_id;
    String name;
    public numberCheck(int user_id, String name) {
        this.action = "numberCheck";
        this.user_id = user_id;
        this.name = name;
    }
}

class ChatWrapper{
    String action;
    ChatsModel chat;
    public ChatWrapper(ChatsModel chat) {
        this.action = "ChatAttached";
        this.chat = chat;
    }
}

class Chat_idWrapper{
    String action;
    String id;
    public Chat_idWrapper(String id) {
        this.action = "chat_idAttached";
        this.id = id;
    }
    
}

class RefresherContactos{
    String action;
    public RefresherContactos() {
        this.action = "refreshContactos";
    }
    
}

public class WhatsappServer {
    private static final int PORT = 5109;
    public static Map<String, Socket> connectedClients = new HashMap<>();
    public static Map<Integer, Socket> mapUserId_Socket = new HashMap<>();

    public static void main(String[] args) throws Exception{
        MongoController.Connect();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientId = UUID.randomUUID().toString();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress()+", "+clientId);

                connectedClients.put(clientId, clientSocket);

                Thread clientThread = new ClientHandler(clientId, clientSocket);
                
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("Error initializing server");
            //e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private String clientId;
    private Socket clientSocket;

    public ClientHandler(String clientId, Socket socket) {
        this.clientId = clientId;
        this.clientSocket = socket;
    }

    public void run() {
        try (
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client " + clientId + ": " + inputLine);

                Gson gson = new Gson();
                JsonElement jsonElement = gson.fromJson(inputLine, JsonElement.class);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String action = jsonObject.get("action").getAsString();

                switch (action) {
                    case "newMsg":
                        handleNewMessage(jsonObject);
                        break;
                    case "validateLogin":
                        handleLoginValidation(jsonObject,out);
                        break;
                    case "chatsRequest":
                        hanldeGettingAllChatsFrom(jsonObject.get("user_id").getAsInt(),out);
                        break;
                    case "checkUser":
                        handleCeckingUserExists(jsonObject.get("num").getAsString(),out);
                        break;
                    case "checkChat":
                        handleCheckingChatExists(jsonObject,out);
                        break;
                    case "requestMessages":
                        hanldeMessagesRequest(jsonObject.get("id").getAsString(),out);
                        break;
                    case "addUser":
                        handleAddingUser2Map(jsonObject.get("user_id").getAsInt());
                        break;
                    default:
                        handleUnsupportedAction(action);
                        break;
                }            
            }
        } catch (IOException e) {
            // Handle the exception gracefully, e.g., log it or take appropriate action
            System.err.println("IOException in ClientHandler for client " + clientId + ": " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected: " + clientId);
                // Remove the client from the map when the thread exits (e.g., client disconnects)
                WhatsappServer.connectedClients.remove(clientId);
            } catch (IOException e) {
                // Handle the exception gracefully, e.g., log it or take appropriate action
                System.err.println("Error closing client socket for client " + clientId + ": " + e.getMessage());
            }
        }
    }

    // Inside the ClientHandler class

    private void handleNewMessage(JsonObject obj){
        int user_id = obj.get("user_id").getAsInt();
        int destination_id = obj.get("destination_id").getAsInt();
        String msg = obj.get("msg").getAsString();
        String time = obj.get("time").getAsString();

        // Insert logic to process the received message

        /*// Retrieve the socket of the destination client
        Socket destinationSocket = WhatsappServer.connectedClients.get(destination_id);

        if (destinationSocket != null) {
            try {
                // Send the message to the destination client
                PrintWriter destinationOut = new PrintWriter(destinationSocket.getOutputStream(), true);
                destinationOut.println(msg);
            } catch (IOException e) {
                // Handle the exception gracefully, e.g., log it or take appropriate action
                System.err.println("IOException while sending message to destination client: " + e.getMessage());
            }
        } else {
            // Handle the case when the destination client is not connected
            System.out.println("Destination client is not connected");
        }*/
    }

    private void handleCeckingUserExists(String num, PrintWriter out){
        int response = MongoController.check_userId_exists(num);
        if (response == -2) return;
        String name;
        name = MongoController.getNameFromUser(response);
        numberCheck nc = new numberCheck(response, name);
        sendObj2Client(nc, out);
    }

    private void handleLoginValidation(JsonObject user, PrintWriter out){
        //return options (-2 mogno error,-1 name doesnt mathc ,user_id,new_user_id)
        String name = user.get("name").getAsString();
        String num = user.get("phone_num").getAsString();

        int response = MongoController.check_userId_exists(name,num);
        loginValidation lv = new loginValidation(response);
        sendObj2Client(lv, out);
    }

    private void handleUnsupportedAction(String action){
        
        System.out.println("Error managing action: "+action);
    }

    private static <T> void sendObj2Client(T obj,PrintWriter out){
        Gson gson = new Gson();
        String objString = gson.toJson(obj);
        out.println(objString);
    }

    private void hanldeGettingAllChatsFrom(int user_id, PrintWriter out){
        //returns array of Contact
        List<ChatsModel> cm_arr = MongoController.getAllChatsFrom(user_id);
        
        if(cm_arr == null) 
            return;
        
        List<Contact> contacts = new ArrayList<>();
        
        for(ChatsModel chat : cm_arr){
            UsersModel user;
            if (chat.getUser_id() == user_id)
                user = MongoController.getUserById(chat.getDestination_user_id());
            else
                user = MongoController.getUserById(chat.getUser_id());
            
            contacts.add(
                new Contact(
                    user.getProfile_pic(),
                    chat.getLast_message().getText(),
                    chat.getLast_message().getTime(),
                    user.getUser_id(),
                    user.getName()
                )
            );
        }
        Contacts cts = new Contacts(contacts);
        sendObj2Client(cts, out);
    }

    private void handleCheckingChatExists(JsonObject obj, PrintWriter out){
        int user_id = obj.get("user_id").getAsInt();
        int destination_id = obj.get("destination_id").getAsInt();

        String id = MongoController.checkIfChatExistsAddIfNot(user_id, destination_id);
        Chat_idWrapper ciw = new Chat_idWrapper(id);
        sendObj2Client(ciw, out);
        RefresherContactos r = new RefresherContactos();
        sendObj2SpecificClient(destination_id, r);

    }

    private <T> void sendObj2SpecificClient(int user_id, T obj){
        
        Socket destinationSocket = WhatsappServer.mapUserId_Socket.get(user_id);

        if (destinationSocket != null) {
            try {
                // Send the message to the destination client
                PrintWriter destinationOut = new PrintWriter(destinationSocket.getOutputStream(), true);
                Gson gson = new Gson();
                String objString = gson.toJson(obj);
                destinationOut.println(objString);
            } catch (IOException e) {
                // Handle the exception gracefully, e.g., log it or take appropriate action
                System.err.println("IOException while sending message to destination client: " + e.getMessage());
            }
        } else {
            // Handle the case when the destination client is not connected
            System.out.println("Destination client is not connected");
        }
    }

    private void hanldeMessagesRequest(String id, PrintWriter out) {

        ChatsModel cm = MongoController.getChatById(id);
        ChatWrapper cw = new ChatWrapper(cm);

        sendObj2Client(cw, out);

    }

    private void handleAddingUser2Map(int user_id){
        WhatsappServer.mapUserId_Socket.put(user_id, clientSocket);
    }
}

