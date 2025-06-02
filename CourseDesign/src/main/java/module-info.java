module org.visier.coursedesign {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires org.json;

    opens org.visier.coursedesign to javafx.fxml;
    opens org.visier.coursedesign.Controller to javafx.fxml;
    opens org.visier.coursedesign.Entity to javafx.base;
    exports org.visier.coursedesign;
    exports org.visier.coursedesign.Controller;
    exports org.visier.coursedesign.Entity;
}