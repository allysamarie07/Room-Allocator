/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package VIEW;

import MODEL.AccountDAO;
import VIEW.AddUpdateDeanFaculty;
import VIEWMODEL.AccountViewModel;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Allysa Marie Datahan
 */
public class DeanFacultyTable extends javax.swing.JFrame {
    private DefaultTableModel tableModel;
    private final AccountViewModel viewModel = new AccountViewModel(); // ✅ Use ViewModel

    public DeanFacultyTable() {
        initComponents();
        setupTableModel();
        loadAccounts();
    }

    private void setupTableModel() {
        tableModel = new DefaultTableModel(new Object[]{"ACCOUNT TYPE", "HONORIFICS", "ACCOUNT NAME", "RFID", "TITLE", "USERNAME", "PASSWORD", "OPTIONS"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };
        ADFTable.setModel(tableModel);

        ADFTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        ADFTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor());
        ADFTable.setRowHeight(60);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < 7; i++) {
            ADFTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void loadAccounts() {
        tableModel.setRowCount(0);
        ArrayList<Object[]> accounts = viewModel.getAllAccountsToTable(); // ✅ ViewModel usage

        for (Object[] account : accounts) {
            tableModel.addRow(new Object[]{account[0], account[1], account[2], account[3], account[4], account[5], account[6], "Options"});
        }
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            add(new JButton("UPDATE"));
            add(new JButton("DELETE"));
            return this;
        }
    }

    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel optionsPanel;
        private int selectedRow;

        public ButtonEditor() {
            optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

            JButton updateButton = new JButton("UPDATE");
            updateButton.addActionListener(e -> {
                int row = ADFTable.convertRowIndexToModel(selectedRow);
                String accountType = tableModel.getValueAt(row, 0).toString();
                String honorifics = tableModel.getValueAt(row, 1).toString();
                String accountName = tableModel.getValueAt(row, 2).toString();
                String rfidNumber = tableModel.getValueAt(row, 3).toString();
                String title = tableModel.getValueAt(row, 4).toString();
                String username = tableModel.getValueAt(row, 5).toString();
                String password = tableModel.getValueAt(row, 6).toString();

                int id = viewModel.getAccountId(accountType, honorifics, accountName, title, username, password, rfidNumber); // ✅ ViewModel

                if (id != -1) {
                    AddUpdateDeanFaculty call = new AddUpdateDeanFaculty('U', id);
                    call.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Error: Account ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                fireEditingStopped();
            });

            JButton deleteButton = new JButton("DELETE");
            deleteButton.addActionListener(e -> {
                int row = ADFTable.convertRowIndexToModel(selectedRow);
                String accountType = tableModel.getValueAt(row, 0).toString();
                String honorifics = tableModel.getValueAt(row, 1).toString();
                String accountName = tableModel.getValueAt(row, 2).toString();
                String rfidNumber = tableModel.getValueAt(row, 3).toString();
                String title = tableModel.getValueAt(row, 4).toString();
                String username = tableModel.getValueAt(row, 5).toString();
                String password = tableModel.getValueAt(row, 6).toString();

                int id = viewModel.getAccountId(accountType, honorifics, accountName, title, username, password, rfidNumber); // ✅ ViewModel

                if (id != -1) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this account?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (viewModel.deleteAccount(id)) { // ✅ ViewModel
                            tableModel.removeRow(row);
                            JOptionPane.showMessageDialog(null, "Account deleted successfully!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Error deleting account.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Error: Account ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                fireEditingStopped();
            });

            optionsPanel.add(updateButton);
            optionsPanel.add(deleteButton);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            selectedRow = row;
            return optionsPanel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ADFTable = new javax.swing.JTable();
        ADFTBackButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(960, 540));
        setUndecorated(true);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ADFTable.setBackground(new java.awt.Color(255, 255, 255));
        ADFTable.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ADFTable.setForeground(new java.awt.Color(0, 0, 0));
        ADFTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ACCOUNT TYPE", "HONORIFICS", "ACCOUNT NAME", "TITLE", "USERNAME", "PASSWORD", "OPTIONS"
            }
        ));
        jScrollPane1.setViewportView(ADFTable);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, 780, 440));

        ADFTBackButton.setBackground(new java.awt.Color(102, 0, 0));
        ADFTBackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/arrow.png"))); // NOI18N
        ADFTBackButton.setBorder(null);
        ADFTBackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ADFTBackButtonActionPerformed(evt);
            }
        });
        jPanel1.add(ADFTBackButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 10, -1, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddDeanFAcultyTable.png"))); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 960, 540));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void ADFTBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ADFTBackButtonActionPerformed
        dispose();
    }//GEN-LAST:event_ADFTBackButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DeanFacultyTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DeanFacultyTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DeanFacultyTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DeanFacultyTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DeanFacultyTable().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ADFTBackButton;
    private javax.swing.JTable ADFTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
