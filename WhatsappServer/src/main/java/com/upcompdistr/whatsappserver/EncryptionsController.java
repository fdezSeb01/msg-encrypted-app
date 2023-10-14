package com.upcompdistr.whatsappserver;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class EncryptionsController {

    private static String abc = "QWERTYU-IOP123&45_67%890A?SD FGHJKLÑZX¿CVB!N¡M.qwerty$u/iop,asdfg;hjklñzxcv:bnm";

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
}
