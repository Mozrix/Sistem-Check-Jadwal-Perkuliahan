module com.example.pejadwalancoolyeah {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires java.desktop;

    opens com.example.pejadwalancoolyeah to javafx.fxml;
    exports com.example.pejadwalancoolyeah;
}
