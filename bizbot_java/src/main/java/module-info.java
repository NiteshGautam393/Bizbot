module com.example.bizbot {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jasperreports;


    opens com.example.bizbot to javafx.fxml;
    exports com.example.bizbot;
}