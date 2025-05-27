/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MODEL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author talai
 */
public class ConfigManager {
    private static final String CONFIG_FILE = "src/main/java/MODEL/ipconfig.txt";
    
    private String ipAddress;
    private String ipPort;
    private String dbName;
    private String dbUsername;
    private String dbPassword;

    public ConfigManager() {
        load(); // Optionally load on init
    }

    public boolean load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
            ipAddress = reader.readLine();
            ipPort = reader.readLine();
            dbName = reader.readLine();
            dbUsername = reader.readLine();
            dbPassword = reader.readLine();
            
            System.out.println("Success reading configuration file.");
            return true;
        } catch (IOException e) {
            System.out.println("Error reading configuration file.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean save(String ip, String port, String dbName, String dbUsername, String dbPassword) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
            writer.write(ip);
            writer.newLine();
            writer.write(port);
            writer.newLine();
            writer.write(dbName);
            writer.newLine();
            writer.write(dbUsername);
            writer.newLine();
            writer.write(dbPassword);
            writer.newLine();

            // Update internal state
            this.ipAddress = ip;
            this.ipPort = port;
            this.dbName = dbName;
            this.dbUsername = dbUsername;
            this.dbPassword = dbPassword;

            System.out.println("Configuration saved successfully!");
            return true;
        } catch (IOException e) {
            System.out.println("Error writing to configuration file.");
            e.printStackTrace();
            return false;
        }
    }

    public String getIpAddress()  { return ipAddress; }
    public String getIpPort()     { return ipPort; }
    public String getDbName()     { return dbName; }
    public String getDbUsername() { return dbUsername; }
    public String getDbPassword() { return dbPassword; }

    public boolean isLoaded() {
        return ipAddress != null && !ipAddress.isEmpty();
    }
}
