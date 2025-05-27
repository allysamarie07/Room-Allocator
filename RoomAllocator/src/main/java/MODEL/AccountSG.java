/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MODEL;

/**
 *
 * @author talai
 */
public class AccountSG {
    public static class Account {
        private int id;
        private String accountType;
        private String honorifics;
        private String accountName;
        private String RfidNumber;
        private String title;
        private String username;
        private String password;

        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getAccountType() { return accountType; }
        public void setAccountType(String accountType) { this.accountType = accountType; }

        public String getHonorifics() { return honorifics; }
        public void setHonorifics(String honorifics) { this.honorifics = honorifics; }

        public String getAccountName() { return accountName; }
        public void setAccountName(String accountName) { this.accountName = accountName; }
        
        public String getRfid() { return RfidNumber; }
        public void setRfid(String RfidNumber) { this.RfidNumber = RfidNumber; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
