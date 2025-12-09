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
import javafx.stage.Stage;

import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.scene.control.TableCell;
import javafx.scene.control.Alert;

public class AdminSaranController implements Initializable{

    @FXML private Button btnList;
    @FXML private TableView<Saran> saran;
    @FXML private Button btnLogout;
    @FXML private Button btnSaran;
    @FXML private TableColumn<Saran, String> colPengirim;
    @FXML private TableColumn<Saran, String> colSaran;
    @FXML private TableColumn<Saran, Void> colAksi;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Load Data Dipanggil!");
        setupTableColumns();
        loadData();
    }
    
    private void setupTableColumns() {
        colPengirim.setCellValueFactory(new PropertyValueFactory<>("pengirim"));
        colSaran.setCellValueFactory(new PropertyValueFactory<>("saran"));

        colAksi.setCellFactory(new Callback<TableColumn<Saran, Void>, TableCell<Saran, Void>>() {
            @Override
            public TableCell<Saran, Void> call(final TableColumn<Saran, Void> param) {
                return new TableCell<Saran, Void>() {
                    private final Button btnYa = new Button("Ya");
                    private final Button btnTidak = new Button("Tidak");
                    
                    {
                        btnYa.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5px 10px;");
                        btnTidak.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 5px 10px;");
                        
                        btnYa.setOnAction((ActionEvent event) -> {
                            Saran data = getTableView().getItems().get(getIndex());
                            setujuiSaran(data.getId());
                        });
                        
                        btnTidak.setOnAction((ActionEvent event) -> {
                            Saran data = getTableView().getItems().get(getIndex());
                            hapusSaran(data.getId());
                        });
                    }
                    
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Saran data = getTableView().getItems().get(getIndex());
                            if (data.getStatus() == null) {
                                setGraphic(new javafx.scene.layout.HBox(5, btnYa, btnTidak));
                            } else {
                                setGraphic(null);
                            }
                        }
                    }
                };
            }
        });
    }

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
    
    @FXML
    private void jadwal(ActionEvent event) {
        changeScene(event, "Admin.fxml");
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
    
    private void loadData() {
        ObservableList<Saran> data = FXCollections.observableArrayList();

        try {
            Connection con = dbConnection.getConnection();
            String sql = "SELECT id, pengirim, saran, status FROM saran WHERE status IS NULL ORDER BY created_at DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Saran saran = new Saran(
                    rs.getInt("id"),
                    rs.getString("pengirim"),
                    rs.getString("saran"),
                    rs.getString("status")
                );
                data.add(saran);
            }

            saran.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
    
    private void setujuiSaran(int id) {
        try {
            Connection con = dbConnection.getConnection();
            String sql = "UPDATE saran SET status = 'DISETUJUI' WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                showAlert("Sukses", "Saran telah disetujui dan tetap tersimpan di database.");
                loadData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal menyetujui saran: " + e.getMessage());
        }
    }
    
    private void hapusSaran(int id) {
        try {
            Connection con = dbConnection.getConnection();
            String sql = "DELETE FROM saran WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                showAlert("Sukses", "Saran telah ditolak dan dihapus dari database.");
                loadData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal menghapus saran: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static class Saran {
        private final int id;
        private final String pengirim;
        private final String saran;
        private final String status;
        
        public Saran(int id, String pengirim, String saran, String status) {
            this.id = id;
            this.pengirim = pengirim;
            this.saran = saran;
            this.status = status;
        }
        
        public int getId() {
            return id;
        }
        
        public String getPengirim() {
            return pengirim;
        }
        
        public String getSaran() {
            return saran;
        }
        
        public String getStatus() {
            return status;
        }
    }
}
