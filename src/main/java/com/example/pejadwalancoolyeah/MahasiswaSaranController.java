package com.example.pejadwalancoolyeah;

import com.example.pejadwalancoolyeah.Database.dbConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;

public class MahasiswaSaranController implements Initializable {

    @FXML private TextArea txtSaran;
    @FXML private Button btnKirim;
    @FXML private Button btnKembali;
    @FXML private Label lblInfo;
    
    private String namaMahasiswa;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("MahasiswaSaranController initialized!");
        txtSaran.setPromptText("Tulis saran atau masukan Anda di sini...");
        btnKirim.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnKembali.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
    }
    
    public void setMahasiswa(String namaMahasiswa) {
        this.namaMahasiswa = namaMahasiswa;
        lblInfo.setText("Kirim saran sebagai: " + namaMahasiswa);
    }
    
    @FXML
    private void kirimSaran(ActionEvent event) {
        String saran = txtSaran.getText().trim();
        
        if (saran.isEmpty()) {
            showAlert("Error", "Saran tidak boleh kosong!");
            return;
        }
        
        if (saran.length() < 10) {
            showAlert("Error", "Saran terlalu pendek! Minimal 10 karakter.");
            return;
        }
        
        try (Connection conn = dbConnection.getConnection()) {
            String sql = "INSERT INTO saran (pengirim, role, saran) VALUES (?, 'MAHASISWA', ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, namaMahasiswa);
            pst.setString(2, saran);
            
            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                showSuccess("Sukses", "Saran berhasil dikirim ke admin!");
                txtSaran.clear();
            } else {
                showAlert("Error", "Gagal mengirim saran!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal mengirim saran: " + e.getMessage());
        }
    }
    
    @FXML
    private void kembali(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("Mahasiswa.fxml"));
            javafx.scene.Parent root = loader.load();
            MahasiswaController mahasiswaController = loader.getController();
            mahasiswaController.setMahasiswaLogin(namaMahasiswa);
            
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node)event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal kembali ke halaman mahasiswa");
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
