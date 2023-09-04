package com.upcompdistr.whatsappserver;

import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoController {

    static String connectionString = "mongodb+srv://root:root@cluster0.2bdva87.mongodb.net/?retryWrites=true&w=majority";
    static MongoClient mongoClient;

    // Initialize the MongoClient in a static block or constructor.
    static {
        mongoClient = MongoClients.create(connectionString);
    }

    public static void Connect() throws Exception {
        try {
            mongoClient.listDatabaseNames(); // This line will trigger a connection attempt
            System.out.println("Successfully connected to MongoDB");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cannot connect to MongoDB");
        }
    }

    public static void insertUser() {
        try {
            MongoDatabase database = mongoClient.getDatabase("WhatsUP");
            MongoCollection<Document> usersCollection = database.getCollection("Users");

            Document userDocument = new Document("user_id", 4)
                    .append("name", "Juana")
                    .append("phone_num", "3333")
                    .append("profile_pic", "woman3.png");

            usersCollection.insertOne(userDocument);
            System.out.println("Usuario creado e insertado a mongo");
        } catch (MongoException e) {
            e.printStackTrace();
            System.out.println("MongoDB operation failed: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}

