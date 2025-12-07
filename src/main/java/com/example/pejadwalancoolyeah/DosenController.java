package com.example.pejadwalancoolyeah;

import com.example.pejadwalancoolyeah.Database.dbConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class DosenController implements Initializable {

    @FXML private TableView<Jadwal> jadwalTable;
    @FXML private Button btnLogout;
    @FXML private Button btnSaran;
    @FXML private Label lblWelcome;
    @FXML private Label lblMatkul;
    
    private String namaDosenLogin;
    private String pjMatkul;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DosenController initialized!");
        
        // Setup kolom tabel
        setupTableColumns();
        
        lblWelcome.setText("Selamat datang");
        lblMatkul.setText("Mata Kuliah: ");
    }
    
    public void setDosenLogin(String namaDosen) {
        this.namaDosenLogin = namaDosen;
        System.out.println("Dosen login: " + namaDosen);
        
        lblWelcome.setText("Selamat datang, " + namaDosen);
        lblMatkul.setText("Memuat data...");
        
        // Ambil data dosen dan tampilkan jadwal
        loadJadwalDosen();
    }
    
    private void setupTableColumns() {
        // Hapus kolom yang ada
        jadwalTable.getColumns().clear();
        
        // Buat kolom baru secara manual
        TableColumn<Jadwal, String> colHari = new TableColumn<>("Hari");
        colHari.setCellValueFactory(new PropertyValueFactory<>("hari"));
        colHari.setPrefWidth(100);
        
        TableColumn<Jadwal, String> colJam = new TableColumn<>("Jam");
        colJam.setCellValueFactory(new PropertyValueFactory<>("jam"));
        colJam.setPrefWidth(100);
        
        TableColumn<Jadwal, String> colMatkul = new TableColumn<>("Mata Kuliah");
        colMatkul.setCellValueFactory(new PropertyValueFactory<>("mataKuliah"));
        colMatkul.setPrefWidth(150);
        
        TableColumn<Jadwal, String> colRuangan = new TableColumn<>("Ruangan");
        colRuangan.setCellValueFactory(new PropertyValueFactory<>("ruangan"));
        colRuangan.setPrefWidth(100);
        
        // Tambahkan kolom ke tabel
        jadwalTable.getColumns().addAll(colHari, colJam, colMatkul, colRuangan);
    }
    
    private void loadJadwalDosen() {
        if (namaDosenLogin == null || namaDosenLogin.isEmpty()) {
            showAlert("Error", "Data dosen tidak ditemukan");
            return;
        }
        
        try (Connection conn = dbConnection.getConnection()) {
            // 1. Ambil pj_matkul dari tabel dosen
            String sqlDosen = "SELECT pj_matkul FROM dosen WHERE nama = ?";
            PreparedStatement pstDosen = conn.prepareStatement(sqlDosen);
            pstDosen.setString(1, namaDosenLogin);
            ResultSet rsDosen = pstDosen.executeQuery();
            
            if (rsDosen.next()) {
                pjMatkul = rsDosen.getString("pj_matkul");
                lblMatkul.setText("Mata Kuliah: " + pjMatkul);
                
                // 2. Ambil jadwal dari tabel jadwal berdasarkan pj_matkul
                loadJadwalFromJadwalTable(pjMatkul);
            } else {
                showAlert("Error", "Data dosen tidak ditemukan di database");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal mengambil data dosen: " + e.getMessage());
        }
    }
    
    private void loadJadwalFromJadwalTable(String mataKuliah) {
        ObservableList<Jadwal> data = FXCollections.observableArrayList();

        try (Connection conn = dbConnection.getConnection()) {
            // Asumsi kolom di tabel jadwal: hari, jam, mata_kuliah, ruangan
            // Sesuaikan dengan struktur tabel jadwal Anda
            String sql = "SELECT hari, jam, matkul, ruangan FROM jadwal WHERE matkul = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, mataKuliah);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Jadwal jadwal = new Jadwal(
                    rs.getString("hari"),
                    rs.getString("jam"),
                    rs.getString("matkul"),
                    rs.getString("ruangan")
                );
                data.add(jadwal);
            }

            jadwalTable.setItems(data);
            
            if (data.isEmpty()) {
                showInfo("Info", "Belum ada jadwal untuk mata kuliah: " + mataKuliah);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal memuat jadwal: " + e.getMessage());
        }
    }
        
    @FXML
    private void kirimSaran(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("DosenSaran.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static class Jadwal {
        private final String hari;
        private final String jam;
        private final String mataKuliah;
        private final String ruangan;
        
        public Jadwal(String hari, String jam, String mataKuliah, String ruangan) {
            this.hari = hari;
            this.jam = jam;
            this.mataKuliah = mataKuliah;
            this.ruangan = ruangan;
        }
        
        public String getHari() { return hari; }
        public String getJam() { return jam; }
        public String getMataKuliah() { return mataKuliah; }
        public String getRuangan() { return ruangan; }
    }
}