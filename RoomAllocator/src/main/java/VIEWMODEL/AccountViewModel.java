package VIEWMODEL;

import MODEL.AccountDAO;
import MODEL.AccountSG;
import java.util.ArrayList;

public class AccountViewModel {
    private final AccountDAO accountDAO;

    public AccountViewModel() {
        this.accountDAO = new AccountDAO();
    }

    // Add or Update Account
    public boolean saveAccount(char mode, int id, String accountType, String honorifics, String accountName,
                               String username, String title, String password, String rfidNumber) {
        if (!validateInput(accountType, honorifics, accountName, username, title, password, rfidNumber)) {
            return false;
        }

        if (mode == 'A') {
            return accountDAO.addAccount(accountType, honorifics, accountName, username, title, password, rfidNumber);
        } else {
            return accountDAO.updateAccount(id, accountType, honorifics, accountName, username, title, password, rfidNumber);
        }
    }

    // Validation message for the View
    public String getValidationMessage(String accountType, String honorifics, String accountName,
                                       String username, String title, String password, String rfidNumber) {
        if (accountType.isEmpty() || honorifics.isEmpty() || accountName.isEmpty() ||
            username.isEmpty() || title.isEmpty() || password.isEmpty()) {
            return "All fields must be filled out.";
        }

        if (password.length() < 8 || !password.matches(".*\\d.*")) {
            return "Password must be at least 8 characters long and contain at least one number.";
        }

        return null; // No error
    }

    private boolean validateInput(String accountType, String honorifics, String accountName,
                                  String username, String title, String password, String rfidNumber) {
        return getValidationMessage(accountType, honorifics, accountName, username, title, password, rfidNumber) == null;
    }

    // Get single account by ID (for update view)
    public AccountSG.Account getAccountById(int id) {
        return accountDAO.getAccountById(id);
    }

    // Get all accounts for display in table
    public ArrayList<Object[]> getAllAccountsToTable() {
        return accountDAO.getAllAccountsToTable();
    }

    // Get account ID for update/delete
    public int getAccountId(String accountType, String honorifics, String accountName,
                            String title, String username, String password, String rfidNumber) {
        return accountDAO.getAccountId(accountType, honorifics, accountName, title, username, password, rfidNumber);
    }

    // Delete account
    public boolean deleteAccount(int id) {
        return accountDAO.deleteAccount(id);
    }
}
