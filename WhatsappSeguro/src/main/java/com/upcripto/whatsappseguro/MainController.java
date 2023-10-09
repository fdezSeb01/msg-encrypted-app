package com.upcripto.whatsappseguro;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class MainController {

    private static int chatIdentifier= -1;
    private static int userID=-1;
    private static String chat_id = "";
    private static String simKey="0";
    private static String pubDestKey="-1";

    private static String userName="Wasap";
    @FXML
    private Label msg2;

    @FXML
    private Label headerLabel;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField txt2send;

    @FXML
    private ImageView GoBackbtn;

    @FXML
    private MenuButton menu;

    @FXML
    private Button okBtn;

    @FXML
    private Button closeBtn;

    @FXML
    private Pane SimetricoPopUp;

    @FXML
    private Pane FirmaPopUp;

    @FXML
    private Pane DecifrarSimPopUp;

    @FXML
    private TextField keyInput;

    @FXML
    private TextField simKeyInput;

    @FXML
    private Label firma_label;

    @FXML
    private Label hash_label;

    @FXML
    private Label msg_label;

    private int msgType=1;
    private String SimEncMsg="";

    private static MainController instance;

    public void initialize() {
        instance = this;

        GoBackbtn.setOnMouseEntered(event -> {
            GoBackbtn.setCursor(Cursor.HAND);
        });

        GoBackbtn.setOnMouseExited(event -> {
            GoBackbtn.setCursor(Cursor.DEFAULT);
        });
        
        //Wasap -> nombre contacto
        headerLabel.setText(userName);
        //si ya existe el chat -> nada        //si no existe el chat -> crear empty chat (last_message con todo en null)
        ConnectionsController.checkIfChatExistsAddIfNot(userID, chatIdentifier); //if any is -1 abort
        ConnectionsController.requestDestinationPubKey(chatIdentifier);
        ConnectionsController.requestPrivKey_method(userID);
        
    }

    public static void setChat_id(String id){
        chat_id = id;
        //cargar mesnajes
        ConnectionsController.requestLoadMessages(chat_id);
    }

    public static void setPubDestKey(String key){
        pubDestKey = key;
    }

    public static void recieveMessages(String[] messages, String[] senders, String[] times,String[] hashes, String[] encRndKeys, int[] types){
        if(instance != null) {
            // Use Platform.runLater to ensure UI updates happen on the JavaFX Application Thread
            javafx.application.Platform.runLater(() -> {
                try {
                    instance.generateMessages(messages,senders,times,hashes,encRndKeys,types);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });       
        }
    }

    private void generateMessages(String[] messages, String[] senders, String[] times, String[] hashes, String[] encRndKeys, int[] types) throws IOException{
        int n = messages.length;
        for(int i=0;i<n;i++){
            boolean myMsg = Integer.parseInt(senders[i]) == userID;
            printMessage(messages[i], myMsg, times[i],i,hashes[i],encRndKeys[i],types[i]);
        }
    }

    @FXML
    private void printMessage(String msg, boolean MyMsg, String time, int i, String hash, String encRndKey, int type){
        if(msg.isEmpty()) return;
        String decoded_msg  = decodeMsg(msg,type,encRndKey);
        msg = msg+"   "+time;
        Label lastText = (Label)mainPane.getChildren().get(mainPane.getChildren().size() - 1);
        Label newText = new Label(msg);
        newText.getStyleClass().add("message");
        newText.setFont(Font.font("Monospaced"));
        String hash_decoded="";
        try {
            hash_decoded = decodeHash(hash, type, encRndKey,decoded_msg);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        newText.setId(hash_decoded);
        newText.getStyleClass().add(MyMsg ? "left" : getColor(type,hash_decoded,decoded_msg));
        newText.setLayoutX(14f);
        Double Yheight = lastText.layoutYProperty().getValue()+lastText.getHeight();
        newText.setLayoutY((i==0) ? Yheight : Yheight+37);


        switch(type){
            case 1:
                newText.setOnMouseClicked(event ->{
                    see_signature(event);
                });
                break;
            case 2:
                newText.setOnMouseClicked(event ->{
                    see_signature(event);
                });
                break;
            case 3:
                newText.setOnMouseClicked(event ->{
                    see_signature(event);
                });
                break;
            case 4:
                newText.setOnMouseClicked(event ->{
                    see_decifrar_pop_up(event);
                });
                break;
            case 5:
                newText.setOnMouseClicked(event ->{
                    see_signature(event);
                });
                break;
            default:
                break;
        }


        mainPane.getChildren().add(newText);
        adjustScrollPaneHeight(newText);
    }

    @FXML
    private void printSingleMessageFromServer(String msg, boolean MyMsg, String time, int i,String hash, String encRndKey, int type){
        //tratar hash, encrndkey y msgtype
        if(msg.isEmpty()) return;
        String decoded_msg  = decodeMsg(msg,type,encRndKey);
        msg = decoded_msg+"   "+time;
        Label lastText = (Label)mainPane.getChildren().get(mainPane.getChildren().size() - 1);
        Label newText = new Label(msg);
        newText.getStyleClass().add("message");
        newText.setFont(Font.font("Monospaced"));
        String hash_decoded="";
        try {
            hash_decoded = decodeHash(hash, type, encRndKey,decoded_msg);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        newText.setId(hash_decoded);
        newText.getStyleClass().add(MyMsg ? "left" : getColor(type,hash_decoded,decoded_msg));

        newText.setLayoutX(14f);
        Double Yheight = lastText.layoutYProperty().getValue()+lastText.getHeight();
        newText.setLayoutY((i==0) ? Yheight : Yheight+7);


        switch(type){
            case 1:
                newText.setOnMouseClicked(event ->{
                    see_signature(event);
                });
                break;
            case 2:
                newText.setOnMouseClicked(event ->{
                    see_signature(event);
                });
                break;
            case 3:
                newText.setOnMouseClicked(event ->{
                    see_signature(event);
                });
                break;
            case 4:
                newText.setOnMouseClicked(event ->{
                    see_decifrar_pop_up(event);
                });
                break;
            case 5:
                newText.setOnMouseClicked(event ->{
                    see_signature(event);
                });
                break;
            default:
                break;
        }



        mainPane.getChildren().add(newText);
        adjustScrollPaneHeight(newText);
    }

    @FXML
    private void newMsg(ActionEvent event) throws IOException {
        if(txt2send.getText().isEmpty()) return;
        LocalTime currentTime = LocalTime.now();
        int hour = currentTime.getHour();
        int minute = currentTime.getMinute();
        String time = String.format("%02d:%02d", hour, minute);
        String msg = txt2send.getText()+"   "+time;

        String msgForServer=txt2send.getText();
        String hash="";
        String encRndKey="";
        switch(msgType){
            case 1: //texto plano
                break;
            case 2: //msj firmado
                hash = EncryptionsController.getHash(msgForServer);
                hash = EncryptionsController.SimpleSust(hash,pubDestKey);
                break;
            case 3: //sobre digital
                String rndKey = EncryptionsController.generateRndKey();
                hash = EncryptionsController.getHash(msgForServer);
                hash = EncryptionsController.SimpleSust(hash,rndKey);
                msgForServer=EncryptionsController.SimpleSust(msgForServer,rndKey);
                encRndKey = EncryptionsController.SimpleSust(rndKey, pubDestKey);
                break;
            case 4: //simetrico
                msgForServer=EncryptionsController.SimpleSust(msgForServer, simKey);
                break;
            case 5: //asimetrico
                msgForServer=EncryptionsController.SimpleSust(msgForServer, pubDestKey);
                break;
            default:
                break;
        }

        ConnectionsController.sendMsg2Server(chat_id,userID, msgForServer, time,chatIdentifier,hash,encRndKey,msgType);
        Label lastText = (Label)mainPane.getChildren().get(mainPane.getChildren().size() - 1);
        Label newText = new Label(msg);
        newText.getStyleClass().add("message");
        newText.setFont(Font.font("Monospaced"));
        newText.getStyleClass().add("left");
        newText.setLayoutX(14f);
        Double Yheight = lastText.layoutYProperty().getValue()+lastText.getHeight();
        newText.setLayoutY(Yheight+7);
        newText.setId(EncryptionsController.getHash(msgForServer));
        switch(msgType){
            case 1:
                newText.setOnMouseClicked(ev ->{
                    see_signature(ev);
                });
                break;
            case 2:
                newText.setOnMouseClicked(ev ->{
                    see_signature(ev);
                });
                break;
            case 3:
                newText.setOnMouseClicked(ev ->{
                    see_signature(ev);
                });
                break;
            case 4:
                newText.setOnMouseClicked(ev ->{
                    see_decifrar_pop_up(ev);
                });
                break;
            case 5:
                newText.setOnMouseClicked(ev ->{
                    see_signature(ev);
                });
                break;
            default:
                break;
        }

        mainPane.getChildren().add(newText);
        adjustScrollPaneHeight(newText);
        txt2send.setText(null);
        
    }

    private void adjustScrollPaneHeight(Label newText){
        double lastChildBottom = mainPane.getChildren().get(mainPane.getChildren().size() - 1).getBoundsInParent().getMaxY();
        double viewportBottom = scrollPane.getViewportBounds().getMaxY();

        if (lastChildBottom+80 > viewportBottom) {
            // Increment the height of the ScrollPane to show the new child
            double newScrollPaneHeight = lastChildBottom + 80; // Add a margin
            mainPane.setPrefHeight(newScrollPaneHeight);
            scrollPane.setVvalue(1.0);
        }
    }

    @FXML
    private void handleEnter(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            newMsg(new ActionEvent("sendButton", null));
            event.consume(); // Prevent further handling of Enter key event
        }
    }

    public static void setArgs(int user_id, int chat_num, String name){
        chatIdentifier = chat_num;
        userID=user_id;
        userName = name;

        System.out.println("chatting "+ userID + " con "+chatIdentifier +" " + userName);
    }

    @FXML
    private void go_home() throws IOException{
        App.setRoot("contacts");
    }

    public static void recieveMsg(String msg, String time, String id, String hash, String encRndKey, int type){
        if (!chat_id.equals(id)) return;
        if(instance != null) {
            // Use Platform.runLater to ensure UI updates happen on the JavaFX Application Thread
            javafx.application.Platform.runLater(() -> {
                try {
                    instance.printSingleMessageFromServer(msg, false, time, 1,hash,encRndKey,type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });       
        }
    }

    //------------------------------------------------------------------------------------------------
    // Menu controller
    @FXML
    private void handleTextoPlano() {
        set_invisible_sim_popup_pane();
        txt2send.promptTextProperty().set("Texto Plano");
        msgType=1;
    }

    @FXML
    private void handleFirmarMensaje() {
        set_invisible_sim_popup_pane();
        txt2send.promptTextProperty().set("Mensaje Firmado");
        msgType=2;
    }

    @FXML
    private void handleSobreDigital() {
        set_invisible_sim_popup_pane();
        txt2send.promptTextProperty().set("Sobre Digital");
        msgType=3;
    }

    @FXML
    private void handleEncriptarSimetrico() {
        txt2send.promptTextProperty().set("Encriptado Simetrico");
        msgType=4;
        SimetricoPopUp.toFront();
        SimetricoPopUp.setVisible(true);
        if(simKey!="0"){
            keyInput.setText(simKey);
        }
        
    }

    @FXML
    private void handleEncriptarAsimetrico() {
        set_invisible_sim_popup_pane();
        txt2send.promptTextProperty().set("Encriptado Asimetrico");
        msgType=5;
    }

    @FXML
    private void set_invisible_sim_popup_pane(){
        SimetricoPopUp.setVisible(false);
    }

    @FXML
    private void showFirmaPopUp(String hashRecieved, String msg, boolean simetrico){
        if(simetrico){
            //si es simetrico debe dar una opcion de poner la llave
            return;
        }
        //tratar que pasa cuando se enseña la firma
    }

    @FXML
    private void set_sim_key(){
        simKey = keyInput.getText();
        SimetricoPopUp.setVisible(false);
    } 

    private static String getColor(int msgType, String hash, String msg){
        String gen_hash="";
        gen_hash = EncryptionsController.getHash(msg);
        if(!gen_hash.equals(hash)){
            msgType =-1;
        }
        switch(msgType){
            case 1:
                return "plain";
            case 2:
                return "signed";
            case 3:
                return "digital-envelope";
            case 4:
                return "symmetric";
            case 5:
                return "asymetric";
            default:
                return "bad";
        }
    }

    private static String decodeMsg(String msg, int msgType, String encRndKey){
        switch(msgType){
            case 1:
                return msg;
            case 2:
                return msg;
            case 3:
                String rndKey = EncryptionsController.SimpleSust(encRndKey);
                return EncryptionsController.decryptSimetric(msg,rndKey);
            case 4:
                return msg;
            case 5:
                return EncryptionsController.SimpleSust(msg);
            default:
                return "Error decifrando mensaje :(";
        }

    }

    private static String decodeHash(String hash, int msgType, String encRndKey, String msg) throws NoSuchAlgorithmException{
        switch(msgType){
            case 1:
                return EncryptionsController.getHash(msg);
            case 2:
                return EncryptionsController.SimpleSust(hash);
            case 3:
                String rndKey = EncryptionsController.SimpleSust(encRndKey);
                return EncryptionsController.decryptSimetric(hash,rndKey);
            case 4:
                return EncryptionsController.getHash(msg);
            case 5:
                return EncryptionsController.getHash(msg);
            default:
                return "Error decifrando hash :(";
        }

    }
    
    @FXML
    private void close_firma_pop_up(){
        FirmaPopUp.setVisible(false);
    }

    @FXML
    private void close_decifrar_pop_up(){
        DecifrarSimPopUp.setVisible(false);
    }

    @FXML
    private void decifrar_texto(){
        String key = simKeyInput.getText();
        //poner texto decifrado en el pop up
        String decoded_msg = EncryptionsController.decryptSimetric(SimEncMsg, key);
        msg_label.setText(decoded_msg);
    }

    @FXML
    private void see_signature(MouseEvent event){
        String hash_recieved = ((Label) event.getSource()).getId();
        String msg = ((Label) event.getSource()).getText().split("   ")[0];
        String hash_generated="";
        hash_generated = EncryptionsController.getHash(msg);
        FirmaPopUp.setVisible(true);
        firma_label.setText(hash_recieved);
        hash_label.setText(hash_generated);
    }

    @FXML
    private void see_decifrar_pop_up(MouseEvent event){
        SimEncMsg = ((Label) event.getSource()).getText().split("   ")[0];
        //poner texto en el pop up
        DecifrarSimPopUp.setVisible(true);
        msg_label.setText(SimEncMsg);
    }
}
