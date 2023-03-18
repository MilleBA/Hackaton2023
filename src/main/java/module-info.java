module spill.hackaton2023 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;

    opens spill.hackaton2023 to javafx.fxml;
    exports spill.hackaton2023;
}