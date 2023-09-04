package com.upcripto.whatsappseguro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;

import com.google.gson.Gson;


class CreateUser{
    String action;
    String name;
    String num;
    public CreateUser(String name, String num) {
        this.name = name;
        this.num = num;
        action = "createUser";
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
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        // Process the received message (e.g., update UI)
                        System.out.println("Received original response from server: " + inputLine);
                    }
                } catch (IOException e) {
                    // Handle exceptions as needed (e.g., log them)
                }
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
        if (out != null) {
            out.println(msgJSON);
        } else {
          System.out.println("Can't send message to server");  
        }
    }

    // private static String talk2server(String message) {
    //     try {
    //         if (socket != null && !socket.isClosed()) {
    //             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    //             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
    //             out.println(message);
    //             System.out.println("Message sent: " + message);
    
    //             String response = in.readLine();
    //             System.out.println("Response from server: " + response);
    //             return response;
    //         } else {
    //             System.out.println("Not connected to the server.");
    //             return null; // or throw an exception
    //         }
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         return null; // or throw an exception
    //     }
    // }
    

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

    public int ValidateLogin(String name, String num){
        //create a user instance and send the info
        //esto deberia regresar el numero o -1 si hay pedo
        if (out != null) {
            out.println(num);
        } else {
          System.out.println("Can't send message to server");  
        }
        return 1;
    }
}
