/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MODEL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author talai
 */
public class Database {
    public static Connection getConnection() {
    String ipAddress = "";
    String ipPort = "";
    String dbName = "";
    String dbUsername = "";
    String dbPassword = "";
    Connection con = null;
        try {
        // Define the path to the file (assuming it's inside the src/dbcon directory)
        String filePath = "src/main/java/MODEL/ipconfig.txt";

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                // Read each line and set the fields
                ipAddress = reader.readLine();
                ipPort = reader.readLine();
                dbName = reader.readLine();
                dbUsername = reader.readLine();
                dbPassword = reader.readLine();
            } catch (IOException e) {
                System.out.println("Error reading configuration file.");
                e.printStackTrace();
            }
            String url = "jdbc:mysql://" + ipAddress + ":" + ipPort + "/" + dbName;
            String username = dbUsername; 
            String password = dbPassword; 
            con = DriverManager.getConnection(url, username, password);
            System.out.println("Connected Successfully");
        } catch (Exception e) {
            System.out.println(e);
        }
        return con;
    }
}
