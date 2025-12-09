package com.example.pejadwalancoolyeah;

import java.sql.Connection;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.pejadwalancoolyeah.Database.dbConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


public class AdminListController implements Initializable{

    @FXML private Button btnList;
    @FXML private Button btnLogout;
    @FXML private Button btnSaran;
    @FXML private TableView<ObservableList<String>> list;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Load Data Dipanggil!");
        loadData();
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
        list.getColumns().clear();
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try {
            Connection con = dbConnection.getConnection();
            String sql = "SELECT pengirim,saran FROM saran";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int colIndex = i;

                TableColumn<ObservableList<String>, String> col =
                        new TableColumn<>(rs.getMetaData().getColumnName(i + 1));

                col.setCellValueFactory(param ->
                        new javafx.beans.property.SimpleStringProperty(
                                param.getValue().get(colIndex)
                        )
                );

                list.getColumns().add(col);
            }

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();

                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i)); // ambil data
                }

                data.add(row);
            }

            list.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
