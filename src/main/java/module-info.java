module com.example.projet_javafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.projet_javafx to javafx.fxml;
    exports com.example.projet_javafx;
    exports model;
    opens model to javafx.fxml;
}