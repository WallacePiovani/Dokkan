module com.wallace.dokkan {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.net.http;
    requires java.desktop;

    opens com.wallace.dokkan to javafx.fxml;
    exports com.wallace.dokkan;
}