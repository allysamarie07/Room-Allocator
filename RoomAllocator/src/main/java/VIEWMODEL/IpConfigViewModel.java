/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VIEWMODEL;

import MODEL.ConfigManager;

/**
 *
 * @author talai
 */
public class IpConfigViewModel {
    private final ConfigManager config = new ConfigManager();

    public boolean loadConfig() {
        return config.load();
    }

    public boolean saveConfig(String ip, String port, String db, String user, String pass) {
        return config.save(ip, port, db, user, pass);
    }

    public String getIp()       { return config.getIpAddress(); }
    public String getPort()     { return config.getIpPort(); }
    public String getDbName()   { return config.getDbName(); }
    public String getUsername() { return config.getDbUsername(); }
    public String getPassword() { return config.getDbPassword(); }

    public boolean isLoaded() {
        return config.getIpAddress() != null;
    }
}

