module com.example.passwordgenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;
    requires java.desktop;


    opens com.example.passwordgenerator to javafx.fxml;
    exports com.example.passwordgenerator;
}