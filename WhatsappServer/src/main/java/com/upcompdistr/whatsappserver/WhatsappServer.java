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
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;


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

class IncomingMsg{
    String action;
    String msg;
    String time;
    String chat_id;
    public IncomingMsg(String msg, String time, String chat_id) {
        this.msg = msg;
        this.time = time;
        action = "incomingMsg";
        this.chat_id = chat_id;
    }
    
}

public class WhatsappServer {
    private static final int PORT = 5109;
    public static Map<String, Socket> connectedClients = new HashMap<>();
    public static Map<Integer, Socket> mapUserId_Socket = new HashMap<>();
    public static Map<String, Integer> mapClientId_UserId = new HashMap<>();

    public static void main(String[] args) throws Exception{
        redirectMongoLogs();
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
    
    private static void redirectMongoLogs() throws IOException{
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);

        FileHandler fileHandler = new FileHandler("mongodb.log"); // Set the log file name
        mongoLogger.addHandler(fileHandler);

        // Your MongoDB code here

        //fileHandler.close(); // Close the file handler when done
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
                //System.out.println("Received from client " + clientId + ": " + inputLine);

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
                // Remove the client from the map when the thread exits (e.g., client disconnects)
                WhatsappServer.connectedClients.remove(clientId);
                int user_id = -1;
                if(WhatsappServer.mapClientId_UserId.get(clientId) != null){
                    user_id = WhatsappServer.mapClientId_UserId.get(clientId);
                    WhatsappServer.mapClientId_UserId.remove(clientId);
                    WhatsappServer.mapUserId_Socket.remove(user_id);
                }
                System.out.println("Client disconnected: " + clientId + ", user_id: " +user_id);
            } catch (IOException e) {
                // Handle the exception gracefully, e.g., log it or take appropriate action
                System.err.println("Error closing client socket for client " + clientId + ": " + e.getMessage());
            }
        }
    }

    // Inside the ClientHandler class

    private void handleNewMessage(JsonObject obj){
        int sender_id = obj.get("sender_id").getAsInt();
        int destination_id = obj.get("destination_id").getAsInt();
        String chat_id = obj.get("chat_id").getAsString();
        String msg = obj.get("msg").getAsString();
        String time = obj.get("time").getAsString();
        
        IncomingMsg im = new IncomingMsg(msg,time,chat_id);
        sendObj2SpecificClient(destination_id, im);
        System.out.println("Mensaje mandado de usuario "+sender_id +" a "+destination_id);
        //falta agregar mensaje a messages y actualizar last_message
        MongoController.addMsg(sender_id, chat_id, msg, time);
        
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
        int[] ids_arr = new int[cm_arr.size()];
        int i=0;
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
            ids_arr[i] = user.getUser_id();
            i++;
        }
        Contacts cts = new Contacts(contacts);
        System.out.println("Recuperados todos los chats de " + user_id + " con " + Arrays.toString(ids_arr));
        sendObj2Client(cts, out);
    }

    private void handleCheckingChatExists(JsonObject obj, PrintWriter out){
        int user_id = obj.get("user_id").getAsInt();
        int destination_id = obj.get("destination_id").getAsInt();

        String id = MongoController.checkIfChatExistsAddIfNot(user_id, destination_id);
        Chat_idWrapper ciw = new Chat_idWrapper(id);
        sendObj2Client(ciw, out);

    }

    public static void refreshContactPageOf(int user_id){
        RefresherContactos r = new RefresherContactos(); //no hacer si el chat ya existia
        sendObj2SpecificClient(user_id, r);
    }

    private static <T> void sendObj2SpecificClient(int user_id, T obj){
        
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
        WhatsappServer.mapClientId_UserId.put(clientId, user_id);
        String name = MongoController.getNameFromUser(user_id);
        System.out.println("Usuario registrado a socket, user_id: "+user_id +", nombre: "+name +", clientId: "+clientId);
    }
}

