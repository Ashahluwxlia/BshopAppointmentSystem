/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

/**
 *
 * @author anacarolina
 */
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    // Method to list all tables and return their names
    public static List<String> listTables() {
        List<String> tables = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", new String[] {"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                tables.add(tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    // Method to view data from a specific table
    public static void viewTableData(String tableName) {
        String sql = "SELECT * FROM " + tableName;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Get column count
            int columnCount = rs.getMetaData().getColumnCount();

            // Get column widths
            int[] columnWidths = new int[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnWidths[i - 1] = rs.getMetaData().getColumnName(i).length();
            }

            // Print column names
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(String.format("%-" + columnWidths[i - 1] + "s", rs.getMetaData().getColumnName(i)) + " | ");
            }
            System.out.println();

            // Print separator line
            for (int width : columnWidths) {
                System.out.print("-".repeat(width) + "-+-");
            }
            System.out.println();

            // Print rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(String.format("%-" + columnWidths[i - 1] + "s", rs.getString(i)) + " | ");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Main method to list tables and view data from each table
    public static void main(String[] args) {
        // Initialize the database (create tables if not exist)
        DatabaseInitializer.initializeDatabase();

        // List all tables
        System.out.println("Listing all tables:");
        List<String> tables = listTables();
        for (String table : tables) {
            System.out.println("Table Name: " + table);
        }

        // View data from each table
        System.out.println("\nViewing data from tables:");
        for (String table : tables) {
            System.out.println("\nData from table: " + table);
            viewTableData(table);
        }
    }
}