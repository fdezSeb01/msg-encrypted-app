package com.upcripto.whatsappseguro;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class EncryptionsController {
    
    private static String abc = "QWERTYUIOPASDFGHJKLÑZXCVBNM0123456789zxcvbnmasdfghjklñqwertyuiop";
    private static int myPrivKey=-1;
    
    public static void setMyPrivKey(String key){
        myPrivKey = Integer.parseInt(key);
    }

    public static String generateRndKey(){
        Random random = new Random();
        int randomNumber = random.nextInt(abc.length());
        return String.valueOf(randomNumber);
    }

    public static String generatePrivKey(String pubKey, int user_id){
        int privKey = abc.length() - Integer.parseInt(pubKey);
        System.out.println("La pub es "+pubKey +" y La lenght es "+abc.length()+" por lo que la priv es " + privKey);
        return SimpleSust(String.valueOf(privKey), String.valueOf(user_id));
        //cada user tiene una llave provada encriptada con su numero de usuario
    }

    public static String getHash(String Msg) throws NoSuchAlgorithmException{
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(Msg.getBytes());
        String stringHash = new String(messageDigest.digest());
        return stringHash;
    }

    public static String SimpleSust(String text, String key_str){
        int key = Integer.parseInt(key_str);
        String temp="";
        for(int i=0;i<text.length();i++){
            int index = abc.indexOf(text.charAt(i));
            index = (index + key)%abc.length();
            temp += abc.charAt(index);
        }
        return temp;
    }

    public static String SimpleSust(String text){
        String temp="";
        for(int i=0;i<text.length();i++){
            int index = abc.indexOf(text.charAt(i));
            index = (index + myPrivKey)%abc.length();
            temp += abc.charAt(index);
        }
        return temp;
    }

    public static String decryptSimetric(String text, String key_str){
        int key = Integer.parseInt(key_str);
        String temp="";
        for(int i=0;i<text.length();i++){
            int index = abc.indexOf(text.charAt(i));
            index = (index - key)%abc.length();
            temp += abc.charAt(index);
        }
        return temp;
    }
}
