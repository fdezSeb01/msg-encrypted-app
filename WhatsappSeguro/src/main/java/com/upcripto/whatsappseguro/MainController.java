package com.upcripto.whatsappseguro;

import java.io.IOException;
import java.time.LocalTime;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

public class MainController {

    private static int chatIdentifier= -1;
    private static int userID=-1;
    private static String chat_id = "";

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
        
    }

    public static void setChat_id(String id){
        chat_id = id;
        //cargar mesnajes
        ConnectionsController.requestLoadMessages(chat_id);
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
        ConnectionsController.sendMsg2Server(chat_id,userID, txt2send.getText(),time,chatIdentifier);
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
        //No necesita nada extra
        //gris - verde
    }

    @FXML
    private void handleFirmarMensaje() {
        txt2send.promptTextProperty().set("Mensaje Firmado");
        // 1. Generar llave publica y privada
        // 2. Generar resumen con la función hash
        // 3. Encriptar resumen con llave privada
        // 4. Mandar mensaje normal junto con hash cifrado
        // 5. El que lo recibe ve el mensaje normal de color azul
        // 6. Si el resumen generado de mensaje recibido es igual al resumen desencriptado -> all good

        // Implementacion al mandar
        // 1. Sale hash generado sin cifrar
        // 2. Sale una opcion para escoger llave privada
        // 3. Se actualize el hash encriptado

        // Implementacion al recibir
        // 1. Al picarle al mensaje le sale un pop up donde viene un campo chiquito para llave, hash encriptado y label con hash generado del mensaje recibido
    }

    @FXML
    private void handleSobreDigital() {
        txt2send.promptTextProperty().set("Sobre Digital");
        // 1. Se genera una llave simetrica aleatoria "rndKey"
        // 2. Se tiene la llave publica del receptro "recKey"
        // 3. Con la "rndKey" se encripta el mensaje y con la "recKey" se encripta la "rndKey" -> "rndEncKey"
        // 4. Se manda el mensaje cifrado (con "rndKey") junto con la llave "rndEncKey"

        // 5. El que lo recibe tiene el mensaje encriptado con la "rndKey" y la llave "rndEncKey"
        // 6. El receptor desencripta la llave "rndEncKey" con su llave privada -> "rndKey"
        // 7. Finalemnte como "rndKey" es simetrica se usa esta misma para desencriptar el mensaje

        // Implementacion al mandar
        // 1. Se genera la llave aleatoria
        // 2. Hay un campo para meter la llave publica del recipient
        
        // Implementacion al recibir
        // 1. Hay un campo para introducir la llave privada
        // 2. bóton para decifrar
    }

    @FXML
    private void handleEncriptarMensaje() {
        txt2send.promptTextProperty().set("Mensaje Encriptado");
        // 1. Hay un toggle para si es simetrico
        // 2. un campo para meter llave (simetrica o privada dependiendo el caso)
    }
}
