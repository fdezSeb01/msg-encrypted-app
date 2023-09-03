module com.upcripto.whatsappseguro {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    // Open a specific package to com.google.gson and javafx.fxml
    opens com.upcripto.whatsappseguro to com.google.gson, javafx.fxml;

    exports com.upcripto.whatsappseguro;
}

