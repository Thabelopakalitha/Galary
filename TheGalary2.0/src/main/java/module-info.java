module com.example.thegalary2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.thegalary2 to javafx.fxml;
    exports com.example.thegalary2;
}