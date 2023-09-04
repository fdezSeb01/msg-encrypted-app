package com.upcompdistr.whatsappserver;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoController {
    public static void Connect() throws Exception{
        String connectionString = "mongodb+srv://root:root@cluster0.2bdva87.mongodb.net/?retryWrites=true&w=majority";

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            System.out.println("Connected to MongoDB successfully!");
            
            // You can perform database operations here
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cannot connect to MongoDB");

        }
    }
}

