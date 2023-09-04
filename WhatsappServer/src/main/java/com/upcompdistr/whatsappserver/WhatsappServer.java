package com.upcompdistr.whatsappserver;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class WhatsappServer {
    private static final int PORT = 5109;
    public static Map<String, Socket> connectedClients = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                String clientId = UUID.randomUUID().toString();

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
                        out.println(handleNewMessage(jsonObject));
                        break;
                    case "otherAction":
                        break;
                    default:
                        handleUnsupportedAction(action);
                        break;
                }            }
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

    private String handleNewMessage(JsonObject msg){
        System.out.println("Recieved\n"+msg);
        return "Message recieved";
    }

    private void handleUnsupportedAction(String action){
        
        System.out.println("Error managing action: "+action);
    }
}

