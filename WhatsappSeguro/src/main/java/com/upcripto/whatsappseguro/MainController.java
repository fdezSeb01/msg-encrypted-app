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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class MainController {

    private static int chatIdentifier= -1;
    private static int userID=-1;
    private static String chat_id = "";
    private static int simKey=-1;
    private static int pubDestKey=-1;

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
    private TextField keyInput;

    private int msgType=1;

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
        
    }

    public static void setChat_id(String id){
        chat_id = id;
        //cargar mesnajes
        ConnectionsController.requestLoadMessages(chat_id);
    }

    public static void setPubDestKey(String key){
        pubDestKey = Integer.parseInt(key);
    }

    public static void recieveMessages(String[] messages, String[] senders, String[] times){
        if(instance != null) {
            // Use Platform.runLater to ensure UI updates happen on the JavaFX Application Thread
            javafx.application.Platform.runLater(() -> {
                try {
                    instance.generateMessages(messages,senders,times);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });       
        }
    }

    private void generateMessages(String[] messages, String[] senders, String[] times) throws IOException{
        int n = messages.length;
        for(int i=0;i<n;i++){
            boolean myMsg = Integer.parseInt(senders[i]) == userID;
            printMessage(messages[i], myMsg, times[i],i);
        }
    }

    @FXML
    private void printMessage(String msg, boolean MyMsg, String time, int i){
        if(msg.isEmpty()) return;
        msg = msg+"   "+time;
        Label lastText = (Label)mainPane.getChildren().get(mainPane.getChildren().size() - 1);
        Label newText = new Label(msg);
        newText.getStyleClass().add("message");
        newText.setFont(Font.font("Monospaced"));
        newText.getStyleClass().add(MyMsg ? "left" : "right");
        newText.setLayoutX(14f);
        Double Yheight = lastText.layoutYProperty().getValue()+lastText.getHeight();
        newText.setLayoutY((i==0) ? Yheight : Yheight+37);
        mainPane.getChildren().add(newText);
        adjustScrollPaneHeight(newText);
    }

    @FXML
    private void printSingleMessageFromServer(String msg, boolean MyMsg, String time, int i){
        if(msg.isEmpty()) return;
        msg = msg+"   "+time;
        Label lastText = (Label)mainPane.getChildren().get(mainPane.getChildren().size() - 1);
        Label newText = new Label(msg);
        newText.getStyleClass().add("message");
        newText.setFont(Font.font("Monospaced"));
        newText.getStyleClass().add(MyMsg ? "left" : "right");
        newText.setLayoutX(14f);
        Double Yheight = lastText.layoutYProperty().getValue()+lastText.getHeight();
        newText.setLayoutY((i==0) ? Yheight : Yheight+7);
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
                try {
                    hash = EncryptionsController.getHash(msgForServer);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                break;
            case 3: //sobre digital
                String rndKey = EncryptionsController.generateRndKey();
                msgForServer=EncryptionsController.SimpleSust(msgForServer,Integer.parseInt(rndKey));
                try {
                    hash = EncryptionsController.getHash(msgForServer);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                hash = EncryptionsController.SimpleSust(hash, Integer.parseInt(rndKey));
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

    public static void recieveMsg(String msg, String time, String id){
        if (!chat_id.equals(id)) return;
        if(instance != null) {
            // Use Platform.runLater to ensure UI updates happen on the JavaFX Application Thread
            javafx.application.Platform.runLater(() -> {
                try {
                    instance.printSingleMessageFromServer(msg, false, time, 1);
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
        txt2send.promptTextProperty().set("Texto Plano");
        msgType=1;
    }

    @FXML
    private void handleFirmarMensaje() {
        txt2send.promptTextProperty().set("Mensaje Firmado");
        msgType=2;
    }

    @FXML
    private void handleSobreDigital() {
        txt2send.promptTextProperty().set("Sobre Digital");
        msgType=3;
    }

    @FXML
    private void handleEncriptarSimetrico() {
        txt2send.promptTextProperty().set("Encriptado Simetrico");
        msgType=4;
        
    }

    @FXML
    private void handleEncriptarAsimetrico() {
        txt2send.promptTextProperty().set("Encriptado Asimetrico");
        msgType=5;
        SimetricoPopUp.toFront();
        SimetricoPopUp.setVisible(true);
        if(simKey!=-1){
            keyInput.setText(String.valueOf(simKey));
        }
    }

    @FXML
    private void set_invisible_sim_popup_pane(){
        SimetricoPopUp.setVisible(false);
    }

    @FXML
    private void set_sim_key(){
        simKey = Integer.parseInt(keyInput.getText());
        SimetricoPopUp.setVisible(false);
    } 
}
