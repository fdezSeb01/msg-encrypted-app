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
    
    private int contactPaneCount = 0;
    private static int userID=-1;
    @FXML
    Pane MainPane;

    @FXML
    private void create_new_chat() throws IOException{
        Pane contactPane = new Pane();
        contactPane.getStyleClass().add("contact-pane");
        contactPane.setPrefWidth(376);
        contactPane.setPrefHeight(65);
        contactPane.setId("chat_"+contactPaneCount);
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

        Label nameLabel = new Label("Juanito");
        // nameLabel.getStyleClass().add("name-label");
        nameLabel.setLayoutX(62);
        nameLabel.setLayoutY(7);
        nameLabel.setFont(Font.font("Monospaced",18.0));

        String fxmlResourcePath = getClass().getResource("").toExternalForm();

        // Construct the image path relative to the FXML resources location
        String imagePath = fxmlResourcePath + new_image_path();

        // Load the image using the constructed path
        Image image = new Image(imagePath);

        ImageView imageView = new ImageView(image);
        imageView.setId("contact-image");
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        imageView.setLayoutX(6);
        imageView.setLayoutY(8);

        Label messageLabel = new Label("Hola, como estas wowowow");
        // messageLabel.getStyleClass().add("name-label");
        messageLabel.setLayoutX(65);
        messageLabel.setLayoutY(34);
        messageLabel.setFont(Font.font("Monospaced",12.0));

        Label timeLabel = new Label("12:39 pm");
        // timeLabel.getStyleClass().add("name-label");
        timeLabel.setLayoutX(308);
        timeLabel.setLayoutY(11);
        timeLabel.setFont(Font.font("Monospaced",12.0));

        contactPane.getChildren().addAll(nameLabel, imageView, messageLabel, timeLabel);

        MainPane.getChildren().add(contactPane);
        contactPaneCount++;

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
        System.out.println("FXID of Clicked Chat: " + fxid);
        MainController.setArgs(userID,Integer.parseInt(fxid.split("_")[1]));
        App.setRoot("main");
    }

    private String new_image_path(){
        Random random = new Random();
        int rand = random.nextInt(4) + 1;
        int rand_sex = random.nextInt(2);
        String sex = (rand_sex==1) ? "man" : "woman";
        return "images/"+sex+rand+".png";
    }

    public static void setUserId(int num){
        userID = num;
    }
}
