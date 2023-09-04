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

    public void initialize() {
        // Initialize your button and set event listeners in the initialize method
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
            System.out.println("Número ya esta registrado y nombre no coincide!");
            //LoginError();
            return;
        }
        ContactosController.setUserId(user_id);
        System.out.println("Sesion iniciada para user id "+user_id);
        App.setRoot("contacts");

    }
    
    @FXML
    private void LoginError(){
        loginError.setVisible(true);
    }
}
