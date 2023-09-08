package com.upcripto.whatsappseguro;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {
    
    @FXML
    TextField nameInput;

    @FXML
    TextField numInput;

    @FXML
    private Button LoginBtn;

    private static LoginController instance;

    public void initialize() {
        // Initialize your button and set event listeners in the initialize method
        instance = this;
        LoginBtn.setCursor(Cursor.DEFAULT);

        LoginBtn.setOnMouseExited(event -> {
            LoginBtn.setCursor(Cursor.DEFAULT);
        });

        LoginBtn.setOnMouseEntered(event -> {
            LoginBtn.setCursor(Cursor.HAND);
        });
    }

    @FXML
    Label loginError;

    @FXML
    private void LoginRegister(ActionEvent event) throws IOException{
        ConnectionsController.ValidateLogin(nameInput.getText(), numInput.getText());
    }

    public static void ValidationGotten(int response) throws IOException{
        int user_id = response;
        if(user_id==-1){
            LoginController.LoginError(); //this causes an error but can't make method non-static
            return;
        }
        ContactosController.setUserId(user_id);
        ConnectionsController.setClientIdForServer(user_id);
        App.setRoot("contacts");

    }
    
    private void setLoginerrorVisible(){
        loginError.setVisible(true);
    }

    @FXML
    public static void LoginError(){
        if(instance != null) {
            // Use Platform.runLater to ensure UI updates happen on the JavaFX Application Thread
            javafx.application.Platform.runLater(() -> instance.setLoginerrorVisible());
        }
    }
}
