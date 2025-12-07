package com.example.pejadwalancoolyeah;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import com.example.pejadwalancoolyeah.Database.dbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javafx.scene.control.TextField;


public class AdminController {
    
    @FXML private TextField AMatkul;
    @FXML private TextField ASKS;
    @FXML private TextField AJam;
    @FXML private TextField ARuangan;
    @FXML private TextField AHari;
    @FXML private TextField AJurusan;
    @FXML private TextField ASemester;

    // UPDATE
    @FXML private TextField UMatkul;
    @FXML private TextField USKS;
    @FXML private TextField UJam;
    @FXML private TextField URuangan;
    @FXML private TextField UHari;

    // REMOVE
    @FXML private TextField RMatkul;
    
    @FXML private Button btnList;
    @FXML private Button btnLogout;
    @FXML private Button btnSaran;

    @FXML
    private void list(ActionEvent event) {
        changeScene(event, "AdminList.fxml");
    }

    @FXML
    private void logout(ActionEvent event) {
        changeScene(event, "Login.fxml");
    }

    @FXML
    private void saran(ActionEvent event) {
        changeScene(event, "AdminSaran.fxml");
    }

    private void changeScene(ActionEvent event, String fxmlName) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlName));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void addData() {
        String sql = "INSERT INTO jadwal (matkul, sks, jam, ruangan, hari, jurusan, semester) VALUES (?,?,?,?,?,?,?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, AMatkul.getText());
            ps.setInt(2, Integer.parseInt(ASKS.getText()));
            ps.setString(3, AJam.getText());
            ps.setString(4, ARuangan.getText());
            ps.setString(5, AHari.getText());
            ps.setString(6, AJurusan.getText());
            ps.setString(7, ASemester.getText());
            ps.executeUpdate();

            System.out.println("Data ditambahkan!");
            clearAddForm();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======================================================
    // UPDATE DATA
    // ======================================================
    @FXML
    private void updateData() {
        String sql = "UPDATE jadwal SET sks=?, jam=?, ruangan=?, hari=? WHERE matkul=?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(USKS.getText()));
            ps.setString(2, UJam.getText());
            ps.setString(3, URuangan.getText());
            ps.setString(4, UHari.getText());
            ps.setString(5, UMatkul.getText());
            ps.executeUpdate();

            System.out.println("Data diupdate!");
            clearUpdateForm();
            


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======================================================
    // REMOVE DATA
    // ======================================================
    @FXML
    private void removeData() {
        String sql = "DELETE FROM jadwal WHERE matkul=?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, RMatkul.getText());
            ps.executeUpdate();

            System.out.println("Data dihapus!");
            clearRemoveForm();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void clearAddForm() {
        AMatkul.clear();
        ASKS.clear();
        AJam.clear();
        ARuangan.clear();
        AHari.clear();
        AJurusan.clear();
        ASemester.clear();
    }
    private void clearUpdateForm() {
        UMatkul.clear();
        USKS.clear();
        UJam.clear();
        URuangan.clear();
        UHari.clear();
    }
    
    private void clearRemoveForm() {
        RMatkul.clear();
    }
}
