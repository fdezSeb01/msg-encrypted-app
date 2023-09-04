package com.upcripto.whatsappseguro;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginController {
    
    @FXML
    TextField nameInput;

    @FXML
    TextField numInput;

    @FXML
    private void LoginRegister(ActionEvent event) throws IOException{
        ConnectionsController.ValidateLogin(nameInput.getText(), numInput.getText());
    }

    public static void ValidationGotten(int response) throws IOException{
        int user_id = response;
        if(user_id==-1){
            System.out.println("Phone already registered and name doesn't match");
            return;
        }
        ContactosController.setUserId(user_id);
        System.out.println("Sesion iniciada para user id "+user_id);
        App.setRoot("contacts");

    }
}
