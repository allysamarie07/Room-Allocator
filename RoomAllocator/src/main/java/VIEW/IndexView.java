/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package VIEW;

import VIEWMODEL.IndexViewModel;

import VIEW.LogIn;
import VIEW.IpConfig;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;


public class IndexView extends javax.swing.JFrame {
    LogIn logInInstance = null;
    
    private String accountName;
    private String accountType;
    private IndexViewModel viewModel;
    
    public IndexView(){
        this("STUDENT", "STUDENT");
    }

    public IndexView(String accountName, String accountType) {
        this.accountName = accountName;
        this.accountType = accountType;

        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        viewModel = new IndexViewModel();
        IndexAdminTable.setModel(viewModel.getTableModel());
        
        buttonsVisibility();
        setupOptionsColumn();
        populateBuildingSelector();

        BuildingSelector.addActionListener(evt -> {
            String selectedBuilding = (String) BuildingSelector.getSelectedItem();
            if (selectedBuilding != null) {
                viewModel.loadRoomsForBuilding(selectedBuilding);
                centerTableColumns();
            }
        });
        
        if (BuildingSelector.getItemCount() > 0) {
            BuildingSelector.setSelectedIndex(0);
        }
    }
    
    private void buttonsVisibility(){
        boolean isDean = "Dean".equalsIgnoreCase(accountType);
        
        DBConfig.setVisible(isDean);
        Account.setVisible(isDean);
        SectionButton.setVisible(isDean);
        RoomAdd.setVisible(isDean);
    }

    private void populateBuildingSelector() {
        BuildingSelector.removeAllItems();
        for (String building : viewModel.getBuildingNames()) {
            BuildingSelector.addItem(building);
        }
 
    }

    private void setupOptionsColumn() {
        IndexAdminTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        IndexAdminTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor());
        IndexAdminTable.setRowHeight(30);
    }

    private void centerTableColumns() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < 4; i++) {
            IndexAdminTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void deleteRoom(String buildingName, String roomNumber) {
        viewModel.deleteRoomWithConfirmation(buildingName,roomNumber,
            () -> { // onSuccess
                populateBuildingSelector();
                viewModel.loadRoomsForBuilding(buildingName);
            },
            () -> { // onFailure
                populateBuildingSelector();
                viewModel.loadRoomsForBuilding(buildingName);
            }
        );
    }

    // Custom renderer for the Options column
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            JButton selectButton = new JButton("SELECT ROOM");
            JButton updateButton = new JButton("UPDATE");
            JButton deleteButton = new JButton("DELETE");
            add(selectButton);
            
            if ("Dean".equalsIgnoreCase(accountType)){
                add(updateButton);
                add(deleteButton);
            }
            return this;
        }
    }

    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel optionsPanel;
        private String currentRoomNumber;

        public ButtonEditor() {
            optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

            JButton selectButton = new JButton("SELECT ROOM");
            selectButton.addActionListener(e -> {
                String selectedBuilding = (String) BuildingSelector.getSelectedItem();
                if (selectedBuilding != null && currentRoomNumber != null) {
                    currentRoomNumber = currentRoomNumber.split(" ")[0];
                    subjectFormView subForm = new subjectFormView(accountName, accountType, selectedBuilding, currentRoomNumber);
                    subForm.setVisible(true);
                    dispose();
                }
                fireEditingStopped();
            });

            optionsPanel.add(selectButton); // Always add select button

            // Add update and delete buttons only for "Dean"
            if ("Dean".equalsIgnoreCase(accountType)) {
                JButton updateButton = new JButton("UPDATE");
                updateButton.addActionListener(e -> {
                    if (currentRoomNumber != null) {
                        currentRoomNumber = currentRoomNumber.split(" ")[0];
                        AddUpdateRoom updateRoomForm = new AddUpdateRoom('U', currentRoomNumber);
                        updateRoomForm.setVisible(true);
                        updateRoomForm.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosed(WindowEvent e) {
                                String selectedBuilding = (String) BuildingSelector.getSelectedItem();
                                if (selectedBuilding != null) {
                                    viewModel.loadRoomsForBuilding(selectedBuilding);
                                }
                            }
                        });
                    }
                    fireEditingStopped();
                });

                JButton deleteButton = new JButton("DELETE");
                deleteButton.addActionListener(e -> {
                    String selectedBuilding = (String) BuildingSelector.getSelectedItem();
                    if (currentRoomNumber != null) {
                        currentRoomNumber = currentRoomNumber.split(" ")[0];
                    }
                    deleteRoom(selectedBuilding, currentRoomNumber);
                    fireEditingStopped();
                });

                optionsPanel.add(updateButton);
                optionsPanel.add(deleteButton);
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (table.getValueAt(row, 0) != null) {
                currentRoomNumber = table.getValueAt(row, 0).toString().split(" ")[0];
            }
            return optionsPanel;
        }

        @Override
        public Object getCellEditorValue() {
            return optionsPanel;
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
        UB = new javax.swing.JLabel();
        CETAFA = new javax.swing.JLabel();
        COMPESA = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        IndexAdminTable = new javax.swing.JTable();
        DBConfig = new javax.swing.JButton();
        Account = new javax.swing.JButton();
        SectionButton = new javax.swing.JButton();
        RoomAdd = new javax.swing.JButton();
        LogoutButton = new javax.swing.JLabel();
        BuildingSelector = new javax.swing.JComboBox<>();
        backgroundTable = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setMinimumSize(new java.awt.Dimension(1920, 1080));
        jPanel1.setPreferredSize(new java.awt.Dimension(1920, 1080));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        UB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/1.png"))); // NOI18N
        UB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                UBMouseClicked(evt);
            }
        });
        jPanel1.add(UB, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        CETAFA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/3.png"))); // NOI18N
        CETAFA.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CETAFAMouseClicked(evt);
            }
        });
        jPanel1.add(CETAFA, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, -1, -1));

        COMPESA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/2.png"))); // NOI18N
        COMPESA.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                COMPESAMouseClicked(evt);
            }
        });
        jPanel1.add(COMPESA, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 20, -1, -1));

        IndexAdminTable.setBackground(new java.awt.Color(255, 255, 255));
        IndexAdminTable.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        IndexAdminTable.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        IndexAdminTable.setForeground(new java.awt.Color(0, 0, 0));
        IndexAdminTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ROOM NUMBER", "AIRCONDITIONED", "BOARD", "CAPACITY", "OPTIONS"
            }
        ));
        jScrollPane1.setViewportView(IndexAdminTable);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 220, 1660, 780));

        DBConfig.setBackground(new java.awt.Color(255, 204, 0));
        DBConfig.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        DBConfig.setForeground(new java.awt.Color(0, 0, 0));
        DBConfig.setText("DATABASE CONFIG");
        DBConfig.setBorder(null);
        DBConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DBConfigActionPerformed(evt);
            }
        });
        jPanel1.add(DBConfig, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 150, 140, 40));

        Account.setBackground(new java.awt.Color(255, 204, 0));
        Account.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        Account.setForeground(new java.awt.Color(0, 0, 0));
        Account.setText("ACCOUNT");
        Account.setBorder(null);
        Account.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AccountActionPerformed(evt);
            }
        });
        jPanel1.add(Account, new org.netbeans.lib.awtextra.AbsoluteConstraints(1650, 60, 140, 40));

        SectionButton.setBackground(new java.awt.Color(255, 204, 0));
        SectionButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        SectionButton.setForeground(new java.awt.Color(0, 0, 0));
        SectionButton.setText("SECTION");
        SectionButton.setBorder(null);
        SectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SectionButtonActionPerformed(evt);
            }
        });
        jPanel1.add(SectionButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1650, 110, 140, 40));

        RoomAdd.setBackground(new java.awt.Color(255, 204, 0));
        RoomAdd.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        RoomAdd.setForeground(new java.awt.Color(0, 0, 0));
        RoomAdd.setText("ADD ROOM");
        RoomAdd.setBorder(null);
        RoomAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RoomAddActionPerformed(evt);
            }
        });
        jPanel1.add(RoomAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(1650, 160, 140, 40));

        LogoutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/switch.png"))); // NOI18N
        LogoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LogoutButtonMouseClicked(evt);
            }
        });
        jPanel1.add(LogoutButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1870, 10, -1, -1));

        BuildingSelector.setBackground(new java.awt.Color(255, 255, 255));
        BuildingSelector.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BuildingSelector.setForeground(new java.awt.Color(0, 0, 0));
        BuildingSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BuildingSelectorActionPerformed(evt);
            }
        });
        jPanel1.add(BuildingSelector, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 140, 690, 60));

        backgroundTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Table Only 1.png"))); // NOI18N
        jPanel1.add(backgroundTable, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1920, 1080));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void RoomAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RoomAddActionPerformed
        // Open the AddUpdateRoom JFrame
        AddUpdateRoom addRoomWindow = new AddUpdateRoom('A', "");

        // Add a window listener to detect when the AddUpdateRoom window is closed
        addRoomWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                // Reload the rooms for the currently selected 
                String selectedBuilding = (String) BuildingSelector.getSelectedItem();
                viewModel.loadRoomsForBuilding(selectedBuilding);
                populateBuildingSelector();
            }
        });
        
        addRoomWindow.setVisible(true); // Show the AddUpdateRoom window
    }//GEN-LAST:event_RoomAddActionPerformed

    private void LogoutButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LogoutButtonMouseClicked
        if ("Dean".equalsIgnoreCase(accountType) || ("Faculty".equalsIgnoreCase(accountType))){
            int response = JOptionPane.showConfirmDialog(this, 
                "You will be redirected to the Student View. Do you want to proceed?", 
                "Confirm Logout", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                IndexView setBack = new IndexView();
                setBack.setVisible(true);
                dispose();
            }
        }else{
            // Display a confirmation dialog
            int response = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to exit?", 
                "Confirm Exit", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);

            // Check the user's response
            if (response == JOptionPane.YES_OPTION) {
                System.exit(0); // Exit the application
            }
            // If NO is selected, do nothing and stay in the application
        }
    }//GEN-LAST:event_LogoutButtonMouseClicked

    private void UBMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UBMouseClicked
       if ("Student".equalsIgnoreCase(accountType)) {
            if (logInInstance == null || !logInInstance.isDisplayable()) {
                logInInstance = new LogIn(this);
                logInInstance.setVisible(true);
            } else {
                logInInstance.toFront(); // Bring to front if already open
                logInInstance.requestFocus();
            }
        }
    }//GEN-LAST:event_UBMouseClicked

    private void CETAFAMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CETAFAMouseClicked
        
    }//GEN-LAST:event_CETAFAMouseClicked

    private void SectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SectionButtonActionPerformed
        // Define the options
        String[] options = {"CREATE", "VIEW"};

        // Show the JOptionPane with options
        int choice = JOptionPane.showOptionDialog(
                this,
                "What action would you like to take?",
                "Choose Action",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // Handle the user's choice
        if (choice == 0) { // CREATE
            AddUpdateSection addSectionFrame = new AddUpdateSection('A', 0);
            addSectionFrame.setVisible(true);
        } else if (choice == 1) { // VIEW
            SectionTable sectionTableFrame = new SectionTable();
            sectionTableFrame.setVisible(true);
        }
    }//GEN-LAST:event_SectionButtonActionPerformed

    private void COMPESAMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_COMPESAMouseClicked

    }//GEN-LAST:event_COMPESAMouseClicked

    private void AccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AccountActionPerformed
        // Define the options
        String[] options = {"CREATE", "VIEW"};

        // Show the JOptionPane with options
        int choice = JOptionPane.showOptionDialog(
                this,
                "What action would you like to take?",
                "Choose Action",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // Handle the user's choice
        if (choice == 0) { // CREATE
            AddUpdateDeanFaculty add = new AddUpdateDeanFaculty('A', 0);
            add.setVisible(true);
        } else if (choice == 1) { // VIEW
            DeanFacultyTable read = new DeanFacultyTable();
            read.setVisible(true);
        }
    }//GEN-LAST:event_AccountActionPerformed

    private void DBConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DBConfigActionPerformed
        IpConfig next = new IpConfig();
        next.setVisible(true);
    }//GEN-LAST:event_DBConfigActionPerformed

    private void BuildingSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BuildingSelectorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BuildingSelectorActionPerformed

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
            java.util.logging.Logger.getLogger(IndexView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IndexView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IndexView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IndexView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IndexView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Account;
    private javax.swing.JComboBox<String> BuildingSelector;
    private javax.swing.JLabel CETAFA;
    private javax.swing.JLabel COMPESA;
    private javax.swing.JButton DBConfig;
    private javax.swing.JTable IndexAdminTable;
    private javax.swing.JLabel LogoutButton;
    private javax.swing.JButton RoomAdd;
    private javax.swing.JButton SectionButton;
    private javax.swing.JLabel UB;
    private javax.swing.JLabel backgroundTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
