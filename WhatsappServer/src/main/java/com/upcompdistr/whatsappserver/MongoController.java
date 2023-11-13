package com.upcompdistr.whatsappserver;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bson.Document;
import org.bson.types.ObjectId;

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

    public static void insertUser(int user_id, String name, String phone_num, String profile_pic, String safety_phrase) {
        try {
            MongoDatabase database = mongoClient.getDatabase("WhatsUP");
            MongoCollection<Document> usersCollection = database.getCollection("Users2");

            Document userDocument = new Document("user_id", user_id)
                    .append("name", name)
                    .append("phone_num", phone_num)
                    .append("profile_pic", profile_pic);

            usersCollection.insertOne(userDocument);
            create_keys_for_user(user_id, name, safety_phrase);
            System.out.println("Usuario creado con id: "+user_id + " y nombre: "+name);
        } catch (MongoException e) {
            e.printStackTrace();
            System.out.println("MongoDB operation failed: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public static void create_keys_for_user(int user_id, String name, String safety_phrase){
        try {
            MongoDatabase database = mongoClient.getDatabase("WhatsUP");
            MongoCollection<Document> certificateCollection = database.getCollection("AR1");
            Random rnd = new Random();
            int ar2 = rnd.nextInt(2);
            if(ar2 == 1){
                certificateCollection = database.getCollection("AR2");
            }
            MongoCollection<Document> privKeysCollection = database.getCollection("PrivateKeys");

            String uid = UUID.randomUUID().toString();
            Boolean pubKey_duplicated = false;
            String pubKey;
            do{
                pubKey = EncryptionsController.generateRndKey();
                MongoDatabase database = mongoClient.getDatabase("WhatsUP");
                MongoCollection<Document> arCollection = database.getCollection("AR1");
                Document result = arCollection.find(Filters.eq("pubKey", pubKey)).first();
                System.out.println("Validating public key is not in AR1");
                if (result != null) {
                    //meaning the key was found in AR1
                    pubKey_duplicated = true;
                } else {
                    arCollection = database.getCollection("AR2");
                    result = arCollection.find(Filters.eq("pubKey", pubKey)).first();
                    System.out.println("Validating public key is not in AR2");
                    if(result != null){
                        //meaning the key was found in AR2
                        pubKey_duplicated=true;
                    }
                }
            }while(pubKey_duplicated)
            String valid_from = LocalDate.now().toString();
            String valid_to = LocalDate.now().plusYears(2).toString();
            DigitalCertificateModel digital_certificate = new DigitalCertificateModel(uid, user_id, name, pubKey, valid_from, valid_to);
            Document digitalCertificateDoc = new Document("uid", digital_certificate.getUid())
                    .append("user_id", digital_certificate.getUser_id())
                    .append("name", digital_certificate.getName())
                    .append("pubKey", digital_certificate.getPubKey())
                    .append("valid_from", digital_certificate.getValidFrom())
                    .append("valid_to", digital_certificate.getValidTo());
            certificateCollection.insertOne(digitalCertificateDoc);
            System.out.println("Certificado creado: " + digital_certificate.toString());

            String privKey = EncryptionsController.generatePrivKey(pubKey, safety_phrase);
            PrivKeyModel privKey_object = new PrivKeyModel(user_id, privKey);
            Document privKeyDoc = new Document("user_id", privKey_object.getUser_id())
                    .append("private_key", privKey_object.getPrivate_key());
            privKeysCollection.insertOne(privKeyDoc);
            System.out.println("Llave privada insertada: " + privKey);

        } catch (MongoException e) {
            e.printStackTrace();
            System.out.println("MongoDB operation failed: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public static int check_userId_exists(String name, String num, String safety_phrase) {
        try {
            MongoDatabase database = mongoClient.getDatabase("WhatsUP");
            MongoCollection<Document> usersCollection = database.getCollection("Users2");
            Document result = usersCollection.find(Filters.eq("phone_num", num)).first();
    
            if (result != null) {
                // User_id exists in the collection
                String userName = result.getString("name");
                int user_id = result.getInteger("user_id");
                if (userName.equals(name)) {
                    // Name matches the associated user_id
                    System.out.println("Usuario validado con id: "+ user_id +" y nombre: "+userName);
                    return user_id;
                } else {
                    // Name does not match
                    System.out.println("Usuario no validado, id no coincide con nombre registrado");
                    return -1;
                }
            } else {
                // User_id does not exist in the collection
                Document maxUserIdDoc = usersCollection.find()
                        .sort(Sorts.descending("user_id"))
                        .limit(1)
                        .first();
    
                int nextUserId = (maxUserIdDoc != null) ? maxUserIdDoc.getInteger("user_id") + 1 : 1;
                insertUser(nextUserId, name, num, randomPP(),safety_phrase);
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

    public static int check_userId_exists(String num) {
        try {
            MongoDatabase database = mongoClient.getDatabase("WhatsUP");
            MongoCollection<Document> usersCollection = database.getCollection("Users2");
            Document result = usersCollection.find(Filters.eq("phone_num", num)).first();
    
            if (result != null) {
                int user_id = result.getInteger("user_id");
                return user_id;
            } else {
                return -1;
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

    public static String getNameFromUser(int user_id){
        try {
            MongoDatabase database = mongoClient.getDatabase("WhatsUP");
            MongoCollection<Document> usersCollection = database.getCollection("Users2");
            Document result = usersCollection.find(Filters.eq("user_id", user_id)).first();
    
            if (result != null) {
                String name = result.getString("name");
                return name;
            } else {
                return "NaN";
            }
        } catch (MongoException e) {
            e.printStackTrace();
            System.out.println("MongoDB operation failed: " + e.getMessage());
            return "Mongo Exception";
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unexpected error occurred: " + e.getMessage());
            return "Exception";
        }
    }

    public static List<ChatsModel> getAllChatsFrom(int user_id) {
        try {
            MongoDatabase database = mongoClient.getDatabase("WhatsUP");
            MongoCollection<Document> chatsCollection = database.getCollection("Chats2");
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
                    last_message_doc.getInteger("sender_id"),
                    last_message_doc.getString("hash"),
                    last_message_doc.getString("encRndKey"),
                    last_message_doc.getInteger("msgType")
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
            MongoCollection<Document> usersCollection = database.getCollection("Users2");
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

    public static String checkIfChatExistsAddIfNot(int user_id, int destination_id){
        try {
            MongoDatabase database = mongoClient.getDatabase("WhatsUP");
            MongoCollection<Document> usersCollection = database.getCollection("Chats2");

            Document chatDocument = usersCollection.find(
                Filters.or(
                    Filters.and(Filters.eq("user_id", user_id), Filters.eq("destination_user_id", destination_id)),
                    Filters.and(Filters.eq("user_id", destination_id), Filters.eq("destination_user_id", user_id))
                )
            ).first();

            if (chatDocument == null) {
                // Chat does not exist, so add it
                Document newChatDocument = new Document("user_id", user_id)
                    .append("destination_user_id", destination_id)
                    .append("last_message", new Document()
                        .append("text", "")
                        .append("time", "")
                        .append("sender_id", -1)
                        .append("message_id", -1)
                        .append("hash", "")
                        .append("encRndKey", "")
                        .append("msgType", -1))
                    .append("messages", new ArrayList<>());

                usersCollection.insertOne(newChatDocument);
                System.out.println("Nuevo chat entre "+ user_id +" y "+destination_id); 
                ClientHandler.refreshContactPageOf(destination_id);
                return newChatDocument.getObjectId("_id").toString();
            }
            return chatDocument.getObjectId("_id").toString();
            

        } catch (MongoException e) {
            e.printStackTrace();
            System.out.println("MongoDB operation failed: " + e.getMessage());
            return "NaN";
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unexpected error occurred: " + e.getMessage());
            return "NaN";
        }
    }

    public static ChatsModel getChatById(String id) {
        try {
            MongoDatabase database = mongoClient.getDatabase("WhatsUP");
            MongoCollection<Document> chatsCollection = database.getCollection("Chats2");
            Document result = chatsCollection.find(Filters.eq("_id", new ObjectId(id))).first();

            if (result != null) {
                // Chat exists in the collection
                int user_id = result.getInteger("user_id");
                int destination_user_id = result.getInteger("destination_user_id");
                Document last_message_doc = (Document) result.get("last_message");
                Message last_message = new Message(
                    last_message_doc.getString("text"),
                    last_message_doc.getString("time"),
                    last_message_doc.getInteger("message_id"),
                    last_message_doc.getInteger("sender_id"),
                    last_message_doc.getString("hash"),
                    last_message_doc.getString("encRndKey"),
                    last_message_doc.getInteger("msgType")
                );
                @SuppressWarnings("unchecked")
                Document[] messagesArray = ((List<Document>) result.get("messages")).toArray(new Document[0]);
                List<Message> messages = new ArrayList<>();
                // Iterate over each message in the array
                for (Document messageDoc : messagesArray) {
                    Document message_doc = messageDoc.get("message", Document.class);
                    Message message = new Message(
                        message_doc.getString("text"),
                        message_doc.getString("time"),
                        message_doc.getInteger("message_id"),
                        message_doc.getInteger("sender_id"),
                        message_doc.getString("hash"),
                        message_doc.getString("encRndKey"),
                        message_doc.getInteger("msgType")
                    );
                    messages.add(message);
                }
                System.out.println("Chat recuperado entre "+user_id +" y " +destination_user_id);
                return new ChatsModel(user_id, destination_user_id, last_message, messages);
            } else {
                // Chat does not exist in the collection
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

    public static void addMsg(int sender_id, String chat_id, String msg, String time, String hash, String encRndKey, int msgType) {
        try {
            MongoDatabase database = mongoClient.getDatabase("WhatsUP");
            MongoCollection<Document> chatsCollection = database.getCollection("Chats2");

            // Find the document with the given chat_id
            Document chatDocument = chatsCollection.find(Filters.eq("_id", new ObjectId(chat_id))).first();

            if (chatDocument != null) {
                // Update the last_message object with new info
                Document last_message_doc = (Document) chatDocument.get("last_message");
                int messageId = last_message_doc.getInteger("message_id") + 1;
                Document last_message = new Document("text", msg)
                    .append("time", time)
                    .append("message_id", messageId)
                    .append("sender_id", sender_id)
                    .append("hash", hash)
                    .append("encRndKey", encRndKey)
                    .append("msgType", msgType);

                // Update the last_message field in the document
                chatDocument.put("last_message", last_message);

                // Add the new message to the messages array
                Document newMessage = new Document("message", new Document("text", msg)
                    .append("time", time)
                    .append("message_id", messageId)
                    .append("sender_id", sender_id)
                    .append("hash", hash)
                    .append("encRndKey", encRndKey)
                    .append("msgType", msgType));

                @SuppressWarnings("unchecked")
                List<Document> messages = (List<Document>) chatDocument.get("messages");
                messages.add(newMessage);
                // Update the document in the collection
                chatDocument.put("messages", messages);

                // Update the document in the collection
                chatsCollection.updateOne(Filters.eq("_id", new ObjectId(chat_id)), new Document("$set", chatDocument));

                System.out.println("Nuevo mesnaje de usuario " + sender_id + " añadido a la base al chat " + chat_id);
            } else {
                System.out.println("Chat not found when trying to add msg");
            }
        } catch (MongoException e) {
            e.printStackTrace();
            System.err.println("MongoDB operation failed: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public static DigitalCertificateModel getPubKeyFromDigitalCertificate(int user_id) {
        try {
            MongoDatabase database = mongoClient.getDatabase("WhatsUP");
            MongoCollection<Document> arCollection = database.getCollection("AR1");
            Document result = arCollection.find(Filters.eq("user_id", user_id)).first();
            System.out.println("Searching for digital certificate from user "+user_id+"in AR1");
            if (result != null) {
                // User_id exists in the collection
                String name = result.getString("name");
                String uid = result.getString("uid");
                String pubKey = result.getString("pubKey");
                String validFrom = result.getString("validFrom");
                String validTo = result.getString("validTo");

                return new DigitalCertificateModel(uid, user_id, name, pubKey, validFrom, validTo);
            } else {
                arCollection = database.getCollection("AR2");
                result = arCollection.find(Filters.eq("user_id", user_id)).first();
                System.out.println("Searching for digital certificate from user "+user_id+"in AR2");

                if (result != null) {
                    // User_id exists in the collection
                    String name = result.getString("name");
                    String uid = result.getString("uid");
                    String pubKey = result.getString("pubKey");
                    String validFrom = result.getString("validFrom");
                    String validTo = result.getString("validTo");

                    return new DigitalCertificateModel(uid, user_id, name, pubKey, validFrom, validTo);
                } else {
                    // User_id does not exist in the collection
                    return null;
                }
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

    public static PrivKeyModel getPrivKeyFrom(int user_id) {
        try {
            MongoDatabase database = mongoClient.getDatabase("WhatsUP");
            MongoCollection<Document> privKeysCollection = database.getCollection("PrivateKeys");
            Document result = privKeysCollection.find(Filters.eq("user_id", user_id)).first();

            if (result != null) {
                // User_id exists in the collection
                String private_key = result.getString("private_key");

                return new PrivKeyModel(user_id, private_key);
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
}

