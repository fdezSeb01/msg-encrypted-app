package com.upcripto.whatsappseguro;

import java.util.Random;

public class EncryptionsController {
    
    private static String abc = "QWERTYU-IOP123&45_67%890A?SD FGHJKLÑZX¿CVB!N¡M.qwerty$u/iop,asdfg;hjklñzxcv:bnm";
    private static int myPrivKey=-1;
    
    public static void setMyPrivKey(String key,String user_id){
        myPrivKey = Integer.parseInt(decryptSimetric(key,user_id));
    }

    public static String getMyPrivKey(){
        return String.valueOf(myPrivKey);
    }

    public static String generateRndKey(){
        Random random = new Random();
        int randomNumber = random.nextInt(abc.length());
        return String.valueOf(randomNumber);
    }

    public static String generatePrivKey(String pubKey, int user_id){
        int privKey = abc.length() - Integer.parseInt(pubKey);
        return SimpleSust(String.valueOf(privKey), String.valueOf(user_id));
        //cada user tiene una llave provada encriptada con su numero de usuario
    }

    public static String getHash(String text) {
        int hash=0;
        for(int i=0;i<text.length();i++){
            hash += (int)text.charAt(i);
        }
        return String.valueOf(hash);
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
        key = abc.length()-(key%abc.length());
        String temp="";
        for(int i=0;i<text.length();i++){
            int index = abc.indexOf(text.charAt(i));
            index = (index + key)%abc.length();
            temp += abc.charAt(index);
        }
        return temp;
    }
}
