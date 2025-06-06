/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package VIEW;

import VIEWMODEL.AddUpdateRoomViewModel;
import MODEL.RoomSG;
import java.util.Set;

import javax.swing.JOptionPane;

/**
 *
 * @author talai
 */
public class AddUpdateRoom extends javax.swing.JFrame {

    private final char mode; // 'A' for Add, 'U' for Update
    private final String oldRoomNumber;
    private final AddUpdateRoomViewModel viewModel;

    public AddUpdateRoom() {
        this('O', null);
    }

    public AddUpdateRoom(char mode, String oldRoomNumber) {
        initComponents();
        this.mode = mode;
        this.oldRoomNumber = oldRoomNumber;
        this.viewModel = new AddUpdateRoomViewModel();

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width / 2;
        int height = screenSize.height / 2;
        this.setSize(width, height);
        this.setLocationRelativeTo(null);

        populateExistingBuildingNames();
        initTypeListener();

        if (mode == 'U' && oldRoomNumber != null) {
            loadRoomDetails(oldRoomNumber);
        }
    }

    private void populateExistingBuildingNames() {
        Set<String> buildingNames = viewModel.getBuildingNames(mode, this);
        BuildingName.removeAll();
        for (String name : buildingNames) {
            BuildingName.addItemSuggestion(name);
        }
    }

    private void initTypeListener() {
        Type.addActionListener(e -> {
            String selectedType = (String) Type.getSelectedItem();
            if ("CLASSROOM".equalsIgnoreCase(selectedType)) {
                jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddRoomBG.png")));
            } else if ("LABORATORY".equalsIgnoreCase(selectedType)) {
                jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddRoomWLabBG.png")));
            }
        });
    }

    private void loadRoomDetails(String roomNumber) {
        AddUpdateRoomViewModel.RoomForm form = viewModel.loadRoomDetails(roomNumber);
        if (form != null) {
            BuildingName.setText(form.buildingName);
            RoomNumber.setText("LABORATORY".equalsIgnoreCase(form.type)
                    ? form.roomNumber
                    : form.roomNumber.replaceAll("[^0-9]", ""));
            Airconditioned.setSelectedItem(form.airconditioned);
            Board.setSelectedItem(form.board);
            Capacity.setText(form.capacity);
            Type.setSelectedItem(form.type);
        } else {
            JOptionPane.showMessageDialog(this, "Room not found!", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
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

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        RoomNumber = new javax.swing.JTextField();
        Type = new javax.swing.JComboBox<>();
        Board = new javax.swing.JComboBox<>();
        Capacity = new javax.swing.JTextField();
        Airconditioned = new javax.swing.JComboBox<>();
        ARAddButton = new javax.swing.JButton();
        ARBackButton = new javax.swing.JButton();
        BuildingName = new TextFieldAutoSuggestion.TextFieldSuggestion();
        jLabel2 = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMinimumSize(new java.awt.Dimension(960, 540));
        jPanel1.setPreferredSize(new java.awt.Dimension(960, 540));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        RoomNumber.setBackground(new java.awt.Color(255, 255, 255));
        RoomNumber.setFont(new java.awt.Font("DialogInput", 0, 18)); // NOI18N
        RoomNumber.setForeground(new java.awt.Color(0, 0, 0));
        RoomNumber.setBorder(null);
        jPanel1.add(RoomNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 180, 430, 50));

        Type.setBackground(new java.awt.Color(255, 255, 255));
        Type.setFont(new java.awt.Font("DialogInput", 0, 18)); // NOI18N
        Type.setForeground(new java.awt.Color(0, 0, 0));
        Type.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CLASSROOM", "LABORATORY" }));
        jPanel1.add(Type, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 345, 190, 50));

        Board.setBackground(new java.awt.Color(255, 255, 255));
        Board.setFont(new java.awt.Font("DialogInput", 0, 18)); // NOI18N
        Board.setForeground(new java.awt.Color(0, 0, 0));
        Board.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "WHITEBOARD", "BLACKBOARD" }));
        jPanel1.add(Board, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 260, 190, 50));

        Capacity.setBackground(new java.awt.Color(255, 255, 255));
        Capacity.setFont(new java.awt.Font("DialogInput", 0, 18)); // NOI18N
        Capacity.setForeground(new java.awt.Color(0, 0, 0));
        Capacity.setBorder(null);
        Capacity.setMinimumSize(new java.awt.Dimension(64, 24));
        Capacity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CapacityActionPerformed(evt);
            }
        });
        jPanel1.add(Capacity, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 345, 126, 50));

        Airconditioned.setBackground(new java.awt.Color(255, 255, 255));
        Airconditioned.setFont(new java.awt.Font("DialogInput", 0, 18)); // NOI18N
        Airconditioned.setForeground(new java.awt.Color(0, 0, 0));
        Airconditioned.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NO", "YES" }));
        Airconditioned.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AirconditionedActionPerformed(evt);
            }
        });
        jPanel1.add(Airconditioned, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 260, 130, 50));

        ARAddButton.setBackground(new java.awt.Color(255, 204, 0));
        ARAddButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        ARAddButton.setForeground(new java.awt.Color(0, 0, 0));
        ARAddButton.setText("ADD");
        ARAddButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        ARAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ARAddButtonActionPerformed(evt);
            }
        });
        jPanel1.add(ARAddButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 430, 190, 40));

        ARBackButton.setBackground(new java.awt.Color(255, 204, 0));
        ARBackButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        ARBackButton.setForeground(new java.awt.Color(0, 0, 0));
        ARBackButton.setText("BACK");
        ARBackButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        ARBackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ARBackButtonActionPerformed(evt);
            }
        });
        jPanel1.add(ARBackButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 430, 200, 40));
        jPanel1.add(BuildingName, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 110, 430, 50));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddRoomBG.png"))); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 960, 540));

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

    private void AirconditionedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AirconditionedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AirconditionedActionPerformed

    private void ARBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ARBackButtonActionPerformed
        dispose();
    }//GEN-LAST:event_ARBackButtonActionPerformed

    private void ARAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ARAddButtonActionPerformed
        AddUpdateRoomViewModel.RoomForm form = new AddUpdateRoomViewModel.RoomForm();
        form.buildingName = BuildingName.getText().trim();
        form.roomNumber = RoomNumber.getText().trim();
        form.airconditioned = (String) Airconditioned.getSelectedItem();
        form.board = (String) Board.getSelectedItem();
        form.type = (String) Type.getSelectedItem();
        form.capacity = Capacity.getText().trim();

        AddUpdateRoomViewModel.RoomResult result = viewModel.addUpdateRoom(form, mode, oldRoomNumber);

        if (result.success) {
            JOptionPane.showMessageDialog(this, result.message);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result.message, "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_ARAddButtonActionPerformed

    private void CapacityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CapacityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CapacityActionPerformed

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
            java.util.logging.Logger.getLogger(AddUpdateRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddUpdateRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddUpdateRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddUpdateRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AddUpdateRoom().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton ARAddButton;
    private javax.swing.JButton ARBackButton;
    private javax.swing.JComboBox<String> Airconditioned;
    private javax.swing.JComboBox<String> Board;
    private TextFieldAutoSuggestion.TextFieldSuggestion BuildingName;
    private javax.swing.JTextField Capacity;
    private javax.swing.JTextField RoomNumber;
    private javax.swing.JComboBox<String> Type;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
