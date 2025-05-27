/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MODEL;

import MODEL.AccountSG.Account;
import MODEL.Database;
import VIEWMODEL.LoginViewModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author talai
 */
public class AccountDAO {
    public static LoginViewModel.LoginResult findAccount(String username, String password) {
        LoginViewModel.LoginResult result = new LoginViewModel.LoginResult();

        try (Connection con = Database.getConnection()) {
            if (con == null) {
                result.success = false;
                result.message = "Failed to connect to database.";
                return result;
            }

            String query = "SELECT AccountType, AccountName FROM accounts WHERE BINARY Username = ? AND BINARY Password = ?";
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, username);
                pst.setString(2, password);

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        result.success = true;
                        result.accountType = rs.getString("AccountType");
                        result.accountName = rs.getString("AccountName");
                        result.message = "Login Successful";
                    } else {
                        result.success = false;
                        result.message = "Invalid username or password.";
                    }
                }
            }
        } catch (SQLException ex) {
            result.success = false;
            result.message = "Database error: " + ex.getMessage();
            ex.printStackTrace();
        }

        return result;
    }
    
    public static LoginViewModel.LoginResult findAccountByRFID(String rfid) {
        LoginViewModel.LoginResult result = new LoginViewModel.LoginResult();

        try (Connection con = Database.getConnection()) {
            if (con == null) {
                result.success = false;
                result.message = "Failed to connect to database.";
                return result;
            }

            String query = "SELECT AccountType, AccountName FROM accounts WHERE rfidNumber = ?";
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, rfid);

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        result.success = true;
                        result.accountType = rs.getString("AccountType");
                        result.accountName = rs.getString("AccountName");
                        result.message = "Login Successful via RFID.";
                    } else {
                        result.success = false;
                        result.message = "RFID not found.";
                    }
                }
            }
        } catch (SQLException ex) {
            result.success = false;
            result.message = "Database error: " + ex.getMessage();
            ex.printStackTrace();
        }

        return result;
    }

    // Method to retrieve all accounts in the format "Honorifics AccountName, Title"
    public static List<String> getAllAccounts(String currentUser) {
        List<String> accounts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Database.getConnection();

            // Step 1: Check the accountType of the current user
            String checkAccountTypeSql = "SELECT accountType FROM accounts WHERE AccountName = ?";
            pstmt = conn.prepareStatement(checkAccountTypeSql);
            pstmt.setString(1, currentUser);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String accountType = rs.getString("accountType");
                if (!"Dean".equalsIgnoreCase(accountType)) {
                    return accounts; // Return an empty list if not authorized
                }
            } else {
                return accounts; // Return an empty list if the user is not found
            }

            closeResources(null, pstmt, rs); // Close the resources for the first query

            // Step 2: Get all accounts excluding the current user
            String sql = "SELECT AccountName FROM accounts WHERE AccountName != ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, currentUser);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String accountName = rs.getString("AccountName");
                accounts.add(accountName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return accounts;
    }

    public static ArrayList<Object[]> getAllAccountsToTable() {
        ArrayList<Object[]> accounts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Database.getConnection();
            String sql = "SELECT AccountType, Honorifics, AccountName, rfidNumber, Title, Username, Password FROM accounts";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String accountType = rs.getString("AccountType");
                String honorifics = rs.getString("Honorifics");
                String accountName = rs.getString("AccountName");
                String rfidNumber = rs.getString("rfidNumber");
                String title = rs.getString("Title");
                String username = rs.getString("Username");
                String password = rs.getString("Password");

                accounts.add(new Object[]{accountType, honorifics, accountName, rfidNumber, title, username, password});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading accounts.", "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return accounts;
    }
    
    public static int getAccountId(String accountType, String honorifics, String accountName, String title, String username, String password, String rfid) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int id = -1; // Default to -1 if not found

        try {
            conn = Database.getConnection();
            String sql = "SELECT ID FROM accounts WHERE AccountType = ? AND Honorifics = ? AND AccountName = ? AND rfidNumber = ? AND Title = ? AND Username = ? AND Password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, accountType);
            pstmt.setString(2, honorifics);
            pstmt.setString(3, accountName);
            pstmt.setString(4, rfid);
            pstmt.setString(5, title);
            pstmt.setString(6, username);
            pstmt.setString(7, password);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving account ID.", "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return id;
    }
    
    public static String getAccountType(String accountName) {
        String accountType = "";
        // Query the database to get the account type of the given accountName
        String query = "SELECT accountType FROM accounts WHERE accountName = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, accountName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                accountType = rs.getString("accountType");  // Assuming accountType is a column in your accounts table
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return accountType;
    }

    public boolean addAccount(String accountType, String honorifics, String accountName, String username, String title, String password, String rfid) {
        String sql = "INSERT INTO accounts (AccountType, Honorifics, AccountName, rfidNumber, Username, Title, Password) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = Database.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, accountType);
            pstmt.setString(2, honorifics);
            pstmt.setString(3, accountName);
            pstmt.setString(4, rfid);
            pstmt.setString(5, username);
            pstmt.setString(6, title);
            pstmt.setString(7, password);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    public static boolean deleteAccount(int id) {
        String sql = "DELETE FROM accounts WHERE ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = Database.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    public Account getAccountById(int id) {
        Account account = null;
        String sql = "SELECT AccountType, Honorifics, AccountName, rfidNumber, Title, Username, Password FROM accounts WHERE ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Database.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                account = new AccountSG.Account(); // Static inner class instantiation
                account.setId(id);
                account.setAccountType(rs.getString("AccountType"));
                account.setHonorifics(rs.getString("Honorifics"));
                account.setAccountName(rs.getString("AccountName"));
                account.setRfid(rs.getString("rfidNumber"));
                account.setTitle(rs.getString("Title"));
                account.setUsername(rs.getString("Username"));
                account.setPassword(rs.getString("Password"));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving account by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return account;
    }
    
    public boolean updateAccount(int id, String accountType, String honorifics, String accountName, String username, String title, String password, String rfid) {
        String sql = "UPDATE accounts SET AccountType = ?, Honorifics = ?, AccountName = ?, rfidNumber = ?," +
                     "Username = ?, Title = ?, Password = ? WHERE ID = ?";

        try (Connection conn = Database.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountType);
            stmt.setString(2, honorifics);
            stmt.setString(3, accountName);
            stmt.setString(4, rfid);
            stmt.setString(5, username);
            stmt.setString(6, title);
            stmt.setString(7, password);
            stmt.setInt(8, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if at least one row was updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurs
        }
    }

    // Utility method to close database resources
    private static void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
