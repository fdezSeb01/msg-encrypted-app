package com.upcripto.whatsappseguro;

import java.io.IOException;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class ContactosController {
    
    private static int userID=-1;

    @FXML
    Pane MainPane;

    private static ContactosController instance;

    public void initialize() {
        instance = this;
        ConnectionsController.getChatsFrom(userID);
    }

    private void generateChats(int n, int[] ids,String[] names, String[] pps, String[] messages_text, String[] messages_time) throws IOException{
        for(int i=0;i<n;i++){
            create_new_chat(ids[i],names[i], pps[i], messages_text[i], messages_time[i]);
        }
    }

    public static void ContactsGotten(int n, int[] ids,String[] names, String[] pps, String[] messages_text, String[] messages_time) throws IOException{
        if(n==0) return;
        ContactosController.CallChatsGenerator(n, ids, names, pps, messages_text, messages_time);
        System.out.println("Chats inicializados");
    }

    public static void CallChatsGenerator(int n, int[] ids,String[] names, String[] pps, String[] messages_text, String[] messages_time) throws IOException{
        if(instance != null) {
            // Use Platform.runLater to ensure UI updates happen on the JavaFX Application Thread
            javafx.application.Platform.runLater(() -> {
                try {
                    instance.generateChats(n,ids,names,pps, messages_text, messages_time);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });       
        }
    }

    @FXML
    private void create_new_chat(int id, String name, String pp, String message_text, String message_time) throws IOException{
        Pane contactPane = new Pane();
        contactPane.getStyleClass().add("contact-pane");
        contactPane.setPrefWidth(376);
        contactPane.setPrefHeight(65);
        contactPane.setId("chat_"+id);
        contactPane.setOnMouseClicked(event -> {
            try {
                click_on_chat(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        contactPane.setOnMouseExited(event -> {
            contactPane.setCursor(Cursor.DEFAULT);
        });

        contactPane.setOnMouseEntered(event -> {
            contactPane.setCursor(Cursor.HAND);
        });

        Label nameLabel = new Label(name);
        // nameLabel.getStyleClass().add("name-label");
        nameLabel.setLayoutX(62);
        nameLabel.setLayoutY(7);
        nameLabel.setFont(Font.font("Monospaced",18.0));

        String fxmlResourcePath = getClass().getResource("").toExternalForm();

        // Construct the image path relative to the FXML resources location
        String imagePath = fxmlResourcePath + new_image_path(pp);

        // Load the image using the constructed path
        Image image = new Image(imagePath);

        ImageView imageView = new ImageView(image);
        imageView.setId("contact-image");
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        imageView.setLayoutX(6);
        imageView.setLayoutY(8);

        Label messageLabel = new Label(message_text);
        // messageLabel.getStyleClass().add("name-label");
        messageLabel.setLayoutX(65);
        messageLabel.setLayoutY(34);
        messageLabel.setFont(Font.font("Monospaced",12.0));

        Label timeLabel = new Label(message_time);
        // timeLabel.getStyleClass().add("name-label");
        timeLabel.setLayoutX(308);
        timeLabel.setLayoutY(11);
        timeLabel.setFont(Font.font("Monospaced",12.0));

        contactPane.getChildren().addAll(nameLabel, imageView, messageLabel, timeLabel);

        MainPane.getChildren().add(contactPane);

        double totalHeight = 0;
        for (Node child : MainPane.getChildren()) {
            if (child instanceof Pane) {
                totalHeight += ((Pane) child).getPrefHeight();
            }
        }
        contactPane.setLayoutY(totalHeight-65);
    }

    @FXML
    private void click_on_chat(MouseEvent event)throws IOException{
        String fxid = ((Pane) event.getSource()).getId();
        MainController.setArgs(userID,Integer.parseInt(fxid.split("_")[1]));
        App.setRoot("main");
    }

    private String new_image_path(String img){
        return "images/"+img;
    }

    public static void setUserId(int num){
        userID = num;
    }
}
