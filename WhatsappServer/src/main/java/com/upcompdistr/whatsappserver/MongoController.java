package com.upcompdistr.whatsappserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

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

    public static void insertUser(int user_id, String name, String phone_num, String profile_pic) {
        try {
            MongoDatabase database = mongoClient.getDatabase("WhatsUP");
            MongoCollection<Document> usersCollection = database.getCollection("Users");

            Document userDocument = new Document("user_id", user_id)
                    .append("name", name)
                    .append("phone_num", phone_num)
                    .append("profile_pic", profile_pic);

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

    public static int check_userId_exists(String name, String num) {
        try {
            MongoDatabase database = mongoClient.getDatabase("WhatsUP");
            MongoCollection<Document> usersCollection = database.getCollection("Users");
            Document result = usersCollection.find(Filters.eq("phone_num", num)).first();
    
            if (result != null) {
                // User_id exists in the collection
                String userName = result.getString("name");
                int user_id = result.getInteger("user_id");
                if (userName.equals(name)) {
                    // Name matches the associated user_id
                    return user_id;
                } else {
                    // Name does not match
                    return -1;
                }
            } else {
                // User_id does not exist in the collection
                Document maxUserIdDoc = usersCollection.find()
                        .sort(Sorts.descending("user_id"))
                        .limit(1)
                        .first();
    
                int nextUserId = (maxUserIdDoc != null) ? maxUserIdDoc.getInteger("user_id") + 1 : 1;
                insertUser(nextUserId, name, num, randomPP());
                return nextUserId; //case phone wasn't registered
            }
        } catch (MongoException e) {
            e.printStackTrace();
            System.out.println("MongoDB operation failed: " + e.getMessage());
            return -2;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unexpected error occurred: " + e.getMessage());
            return -2;
        }
    }

    public static List<ChatsModel> getAllChatsFrom(int user_id) {
        try {
            MongoDatabase database = mongoClient.getDatabase("WhatsUP");
            MongoCollection<Document> chatsCollection = database.getCollection("Chats");
            FindIterable<Document> results = chatsCollection.find(
                Filters.or(Filters.eq("user_id", user_id), Filters.eq("destination_user_id", user_id))
            );
            List<ChatsModel> chats = new ArrayList<>();

            if (!results.iterator().hasNext()) {
                return null;
            }        

            for (Document result : results) {
                int user_id2 = result.getInteger("user_id");
                int destination_user_id = result.getInteger("destination_user_id");
                Document last_message_doc = (Document) result.get("last_message");
                Message last_message = new Message(
                    last_message_doc.getString("text"),
                    last_message_doc.getString("time"),
                    last_message_doc.getInteger("message_id"),
                    last_message_doc.getInteger("sender_id")
                );
                List<Message> messages = new ArrayList<>(); // Initialize messages as an empty list
                //messages is empty for this method since Im not gonna use it
                chats.add(new ChatsModel(user_id2, destination_user_id, last_message, messages));
            }
            return chats;
        } catch (MongoException e) {
            e.printStackTrace();
            System.out.println("MongoDB operation failed: " + e.getMessage());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unexpected error occurred: " + e.getMessage());
            return null;
        }
    }

    public static UsersModel getUserById(int user_id) {
        try {
            MongoDatabase database = mongoClient.getDatabase("WhatsUP");
            MongoCollection<Document> usersCollection = database.getCollection("Users");
            Document result = usersCollection.find(Filters.eq("user_id", user_id)).first();

            if (result != null) {
                // User_id exists in the collection
                String name = result.getString("name");
                String phone_num = result.getString("phone_num");
                String profile_pic = result.getString("profile_pic");

                return new UsersModel(user_id, name, phone_num, profile_pic);
            } else {
                // User_id does not exist in the collection
                return null;
            }
        } catch (MongoException e) {
            e.printStackTrace();
            System.out.println("MongoDB operation failed: " + e.getMessage());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unexpected error occurred: " + e.getMessage());
            return null;
        }
    }
    

    private static String randomPP() {
        Random random = new Random();
        int rand = random.nextInt(4) + 1;
        int rand_sex = random.nextInt(2);
        String sex = (rand_sex==1) ? "man" : "woman";
        return sex+rand+".png";
    }
}

