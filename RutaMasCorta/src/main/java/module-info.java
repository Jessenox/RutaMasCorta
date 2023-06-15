module com.ola.rutamascorta {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.ola.rutamascorta to javafx.fxml;
    exports com.ola.rutamascorta;
}
