package com.upcompdistr.whatsappserver;
import java.io.*;
import java.net.*;
import java.util.HashMap;
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

public class WhatsappServer {
    private static final int PORT = 5109;
    public static Map<String, Socket> connectedClients = new HashMap<>();

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
                    case "createUser":
                        handleCreateUser(jsonObject,out);
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

    private void handleNewMessage(JsonObject obj){
        int user_id = obj.get("user_id").getAsInt();
        int destination_id = obj.get("destination_id").getAsInt();
        String msg =obj.get("msg").getAsString();
        String time = obj.get("time").getAsString();

        //insert register in mongo DB

        //return "Message recieved";
    }

    private void handleCreateUser(JsonObject user, PrintWriter out){
        loginValidation loginVal = new loginValidation(1);
        Gson gson = new Gson();
        String loginJSON = gson.toJson(loginVal);
        out.println(loginJSON);
        System.out.println("Got to the point to send the login "+loginJSON);
    }

    private void handleUnsupportedAction(String action){
        
        System.out.println("Error managing action: "+action);
    }
}

