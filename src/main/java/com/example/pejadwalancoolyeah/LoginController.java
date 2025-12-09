package com.example.pejadwalancoolyeah;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.example.pejadwalancoolyeah.Database.dbConnection;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField txtUname;

    @FXML
    private TextField txtPass;

    @FXML
     private void submit(ActionEvent event) {
        String username = txtUname.getText().trim();
        String password = txtPass.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username dan password wajib diisi!");
            return;
        }

        try (Connection conn = dbConnection.getConnection()) {
            LoginResult result = authenticateUser(conn, username, password);
            
            if (result != null) {
                switch (result.getRole().toUpperCase()) {
                    case "ADMIN":
                        changeScene(event, "Admin.fxml");
                        break;
                    case "DOSEN":
                        pindahKeHalamanDosen(event, result.getNama());
                        break;
                    case "MAHASISWA":
                        pindahKeHalamanMahasiswa(event, result.getNama());
                        break;
                    default:
                        showAlert("Error", "Role tidak dikenali!");
                }
            } else {
                showAlert("Login Gagal", "Username atau password salah!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Gagal koneksi ke database!");
        }
    }

    private LoginResult authenticateUser(Connection conn, String username, String password) {
        String queryMahasiswa = 
            "SELECT 'MAHASISWA' as role, nama " +
            "FROM mahasiswa m " +
            "WHERE m.npm = ? AND DATE_FORMAT(m.tanggal_lahir, '%Y%m%d') = ?";
        
        String queryDosen = 
            "SELECT 'DOSEN' as role, nama " +
            "FROM dosen d " +
            "WHERE d.nip = ? AND DATE_FORMAT(d.tanggal_lahir, '%Y%m%d') = ?";
        
        String queryAdmin = 
            "SELECT role, username as nama FROM users WHERE username = ? AND password = ? AND role = 'ADMIN'";

        try {
            try (PreparedStatement pst = conn.prepareStatement(queryMahasiswa)) {
                pst.setString(1, username);
                pst.setString(2, password);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    return new LoginResult(
                        rs.getString("role"), 
                        rs.getString("nama")
                    );
                }
            }

            try (PreparedStatement pst = conn.prepareStatement(queryDosen)) {
                pst.setString(1, username);
                pst.setString(2, password);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    return new LoginResult(
                        rs.getString("role"), 
                        rs.getString("nama")
                    );
                }
            }

            try (PreparedStatement pst = conn.prepareStatement(queryAdmin)) {
                pst.setString(1, username);
                pst.setString(2, password);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    return new LoginResult(
                        rs.getString("role"), 
                        rs.getString("nama")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private void pindahKeHalamanDosen(ActionEvent event, String namaDosen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dosen.fxml"));
            Parent root = loader.load();
            
            DosenController dosenController = loader.getController();
            dosenController.setDosenLogin(namaDosen);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka halaman dosen");
        }
    }
    
    private void pindahKeHalamanMahasiswa(ActionEvent event, String namaMahasiswa) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Mahasiswa.fxml"));
            Parent root = loader.load();
            
            MahasiswaController MahasiswaController = loader.getController();
            MahasiswaController.setMahasiswaLogin(namaMahasiswa);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka halaman mahasiswa");
        }
    }
    
    

    private void changeScene(ActionEvent event, String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka tampilan " + fxml);
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
    
    private static class LoginResult {
    private final String role;
    private final String nama;
        
    public LoginResult(String role, String nama) {
        this.role = role;
        this.nama = nama;
        }
        
    public String getRole() {
        return role;
        }
        
    public String getNama() {
        return nama;
        }
    }
}
