package com.example.pejadwalancoolyeah.Database;

import java.sql.*;

public class db {
    private static final String URL = "jdbc:mysql://localhost:3306/penjadwalan";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}