module observer.observergenerics {
    requires javafx.controls;
    requires javafx.fxml;


    opens observer to javafx.fxml;
    exports observer;
}