/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package VIEW;

import MODEL.SectionDAO;
import VIEW.AddUpdateSection;
import VIEWMODEL.SectionTableViewModel;

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
public class SectionTable extends javax.swing.JFrame {
    private SectionTableViewModel viewModel;
    /**
     * Creates new form AddDeanFacultyTable
     */
    public SectionTable() {
        viewModel = new SectionTableViewModel();
        initComponents();
        setupTable();
        viewModel.loadSections();
    }
    
   private void setupTable() {
        JTable.setModel(viewModel.getTableModel());
        JTable.setRowHeight(30);
        JTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        JTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < viewModel.getTableModel().getColumnCount() - 1; i++) {
            JTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    // ButtonRenderer stays the same
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

    // ButtonEditor delegates logic to ViewModel
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private int row;

        public ButtonEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            JButton updateButton = new JButton("UPDATE");
            JButton deleteButton = new JButton("DELETE");

            updateButton.addActionListener(e -> {
                int modelRow = JTable.convertRowIndexToModel(row);
                String section = viewModel.getTableModel().getValueAt(modelRow, 0).toString();
                String population = viewModel.getTableModel().getValueAt(modelRow, 1).toString();
                int id = viewModel.getSectionId(section, population);

                if (id != -1) {
                    new AddUpdateSection('U', id).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Error: Section ID not found.");
                }
                fireEditingStopped();
            });

            deleteButton.addActionListener(e -> {
                int modelRow = JTable.convertRowIndexToModel(row);
                String section = viewModel.getTableModel().getValueAt(modelRow, 0).toString();
                String population = viewModel.getTableModel().getValueAt(modelRow, 1).toString();
                int id = viewModel.getSectionId(section, population);
                
                String modifiedSection = section + " (" + population + " Students)";
                System.out.println(modifiedSection);
                if (id != -1) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Confirm delete?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION && viewModel.deleteSection(id, modifiedSection)) {
                        viewModel.getTableModel().removeRow(modelRow);
                        JOptionPane.showMessageDialog(null, "Section deleted.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Error deleting section.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Section ID not found.");
                }
                fireEditingStopped();
            });

            panel.add(updateButton);
            panel.add(deleteButton);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return panel;
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
        JTable = new javax.swing.JTable();
        ADFTBackButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(960, 540));
        setUndecorated(true);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        JTable.setBackground(new java.awt.Color(255, 255, 255));
        JTable.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        JTable.setForeground(new java.awt.Color(0, 0, 0));
        JTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "SECTION", "POPULATION", "OPTIONS"
            }
        ));
        jScrollPane1.setViewportView(JTable);

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
            java.util.logging.Logger.getLogger(SectionTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SectionTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SectionTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SectionTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SectionTable().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ADFTBackButton;
    private javax.swing.JTable JTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
