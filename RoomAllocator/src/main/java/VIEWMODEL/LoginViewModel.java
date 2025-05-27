/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VIEWMODEL;

import MODEL.AccountDAO;

/**
 *
 * @author talai
 */
public class LoginViewModel {

    public static class LoginResult {
        public boolean success;
        public String message;
        public String accountType;
        public String accountName;
    }

    public LoginResult authenticate(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            LoginResult result = new LoginResult();
            result.success = false;
            result.message = "Please enter both username and password.";
            return result;
        }

        return AccountDAO.findAccount(username, password);
    }
    
    public LoginResult authenticateWithRFID(String rfid) {
        if (rfid == null || rfid.isEmpty()) {
            LoginResult result = new LoginResult();
            result.success = false;
            result.message = "RFID cannot be empty.";
            return result;
        }

        return AccountDAO.findAccountByRFID(rfid);
    }
}

