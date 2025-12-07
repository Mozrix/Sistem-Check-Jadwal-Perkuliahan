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

public class MahasiswaController implements Initializable {

    @FXML private TableView<Jadwal> jadwalTable;
    @FXML private Button btnLogout;
    @FXML private Button btnSaran;
    @FXML private Label lblWelcome;
    @FXML private Label lblInfo;
    
    private String namaMahasiswaLogin;
    private String jurusan;
    private String semester;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("MahasiswaController initialized!");
        
        // Setup kolom tabel
        setupTableColumns();
        
        lblWelcome.setText("Selamat datang");
        lblInfo.setText("Memuat data...");
    }
    
    public void setMahasiswaLogin(String namaMahasiswa) {
        this.namaMahasiswaLogin = namaMahasiswa;
        System.out.println("Mahasiswa login: " + namaMahasiswa);
        
        lblWelcome.setText("Selamat datang, " + namaMahasiswa);
        lblInfo.setText("Memuat data...");
        
        // Ambil data mahasiswa dan tampilkan jadwal
        loadJadwalMahasiswa();
    }
    
    private void setupTableColumns() {
        // Hapus kolom yang ada
        jadwalTable.getColumns().clear();
        
        // Buat kolom baru
        TableColumn<Jadwal, String> colHari = new TableColumn<>("Hari");
        colHari.setCellValueFactory(new PropertyValueFactory<>("hari"));
        colHari.setPrefWidth(100);
        
        TableColumn<Jadwal, String> colJam = new TableColumn<>("Jam");
        colJam.setCellValueFactory(new PropertyValueFactory<>("jam"));
        colJam.setPrefWidth(100);
        
        TableColumn<Jadwal, String> colMatkul = new TableColumn<>("Mata Kuliah");
        colMatkul.setCellValueFactory(new PropertyValueFactory<>("mataKuliah"));
        colMatkul.setPrefWidth(150);
        
        TableColumn<Jadwal, String> colDosen = new TableColumn<>("Dosen");
        colDosen.setCellValueFactory(new PropertyValueFactory<>("dosen"));
        colDosen.setPrefWidth(150);
        
        TableColumn<Jadwal, String> colRuangan = new TableColumn<>("Ruangan");
        colRuangan.setCellValueFactory(new PropertyValueFactory<>("ruangan"));
        colRuangan.setPrefWidth(100);
        
        // Tambahkan kolom ke tabel
        jadwalTable.getColumns().addAll(colHari, colJam, colMatkul, colDosen, colRuangan);
    }
    
    private void loadJadwalMahasiswa() {
        if (namaMahasiswaLogin == null || namaMahasiswaLogin.isEmpty()) {
            showAlert("Error", "Data mahasiswa tidak ditemukan");
            return;
        }
        
        try (Connection conn = dbConnection.getConnection()) {
            // 1. Ambil jurusan dan semester dari tabel mahasiswa
            String sqlMahasiswa = "SELECT jurusan, semester FROM mahasiswa WHERE nama = ?";
            PreparedStatement pstMahasiswa = conn.prepareStatement(sqlMahasiswa);
            pstMahasiswa.setString(1, namaMahasiswaLogin);
            ResultSet rsMahasiswa = pstMahasiswa.executeQuery();
            
            if (rsMahasiswa.next()) {
                jurusan = rsMahasiswa.getString("jurusan");
                semester = rsMahasiswa.getString("semester");
                lblInfo.setText("Jurusan: " + jurusan + " | Semester: " + semester);
                
                // 2. Ambil jadwal berdasarkan jurusan
                loadJadwalByJurusan(jurusan, semester);
            } else {
                showAlert("Error", "Data mahasiswa tidak ditemukan di database");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal mengambil data mahasiswa: " + e.getMessage());
        }
    }
    
    private void loadJadwalByJurusan(String jurusan, String semester) {
        ObservableList<Jadwal> data = FXCollections.observableArrayList();

        try (Connection conn = dbConnection.getConnection()) {
            // Asumsi tabel jadwal memiliki kolom jurusan dan semester
            // Jika tidak ada kolom semester di jadwal, hapus bagian semester dari query
            String sql = "SELECT j.*, d.nama as nama_dosen FROM jadwal j " +
                        "LEFT JOIN dosen d ON j.id = d.id " +
                        "WHERE j.jurusan = ? ";
                        
            sql += "ORDER BY j.hari, j.jam";
            
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, jurusan);
            
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Jadwal jadwal = new Jadwal(
                    rs.getString("hari"),
                    formatJam(rs.getString("jam")),
                    rs.getString("matkul"),
                    rs.getString("nama_dosen"),
                    rs.getString("ruangan")
                );
                data.add(jadwal);
            }

            jadwalTable.setItems(data);
            
            if (data.isEmpty()) {
                showInfo("Info", "Belum ada jadwal untuk jurusan " + jurusan);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal memuat jadwal: " + e.getMessage());
        }
    }
    
    @FXML
    private void kirimSaran(ActionEvent event) {
        try {
            // Load halaman kirim saran mahasiswa
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MahasiswaSaran.fxml"));
            Parent root = loader.load();
            
            // Kirim nama mahasiswa ke controller saran
            MahasiswaSaranController saranController = loader.getController();
            saranController.setMahasiswa(namaMahasiswaLogin);
            
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka halaman kirim saran");
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
    
    private String formatJam(String jam) {
        // Format jam jika perlu
        if (jam != null && jam.length() >= 5) {
            return jam.substring(0, 5);
        }
        return jam;
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
        private final String dosen;
        private final String ruangan;
        
        public Jadwal(String hari, String jam, String mataKuliah, String dosen, String ruangan) {
            this.hari = hari;
            this.jam = jam;
            this.mataKuliah = mataKuliah;
            this.dosen = dosen;
            this.ruangan = ruangan;
        }
        
        public String getHari() { return hari; }
        public String getJam() { return jam; }
        public String getMataKuliah() { return mataKuliah; }
        public String getDosen() { return dosen; }
        public String getRuangan() { return ruangan; }
    }
}