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
        int user_id = ValidateLogin(nameInput.getText(), numInput.getText());
        if(user_id==-1) return;
        ContactosController.setUserId(user_id);
        App.setRoot("contacts");
    }

    private int ValidateLogin(String name, String num){
        //send name and num to server, validate it and return user_id (ObjectID)
        //should call method in ConnectionsController ValidateLogin that returns user id
        return 1;
    }
}
