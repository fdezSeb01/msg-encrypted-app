package com.upcripto.whatsappseguro;

import java.io.IOException;
import java.time.LocalTime;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

public class MainController {

    private static int chatIdentifier= -1;
    private static int userID=-1;
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

    private static MainController instance;

    private boolean person1 = false;

    public void initialize() {
        instance = this;
        //Wasap -> nombre contacto
        headerLabel.setText(userName);
        //si ya existe el chat -> nada
        ConnectionsController.checkIfChatExistsAddIfNot(userID, chatIdentifier); //if any is -1 abort
        //si no existe el chat -> crear empty chat (last_message con todo en null)


    }
    @FXML
    private void newMsg(ActionEvent event) throws IOException {
        if(txt2send.getText().isEmpty()) return;
        LocalTime currentTime = LocalTime.now();
        int hour = currentTime.getHour();
        int minute = currentTime.getMinute();
        String time = String.format("%02d:%02d", hour, minute);
        String msg = txt2send.getText()+"   "+time;
        ConnectionsController.sendMsg2Server(userID,chatIdentifier,msg,time);
        Label lastText = (Label)mainPane.getChildren().get(mainPane.getChildren().size() - 1);
        Label newText = new Label(msg);
        newText.getStyleClass().add("message");
        newText.setFont(Font.font("Monospaced"));
        newText.getStyleClass().add(person1 ? "left" : "right");
        newText.setLayoutX(14f);
        Double Yheight = lastText.layoutYProperty().getValue()+lastText.getHeight();
        newText.setLayoutY(Yheight+7);
        mainPane.getChildren().add(newText);
        adjustScrollPaneHeight(newText);
        txt2send.setText(null);
        person1 = !person1;
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
}
