module com.wallace.dokkan {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.wallace.dokkan to javafx.fxml;
    exports com.wallace.dokkan;
}