module com.example.rsaencryptionapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens io.zhekai.rsaencryptionapp to javafx.fxml;
    exports io.zhekai.rsaencryptionapp;
}