/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package VIEW;

import VIEWMODEL.SubjectViewModel;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import MODEL.Subject;
import VIEW.IndexView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;

public class subjectFormView extends javax.swing.JFrame {
    private SubjectViewModel viewModel;
    private String accountName;
    private String accountType;
    
    public subjectFormView() {
        this("User", "", "", ""); // Call the parameterized constructor with empty strings
    }

    public subjectFormView(String accountName, String accountType, String selectedBuilding, String currentRoomNumber) {
        this.viewModel = new SubjectViewModel(selectedBuilding, currentRoomNumber, accountName, accountType);
        this.accountName = accountName;
        this.accountType = accountType;
    
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        SubjectTable.setRowHeight(30);
        BuildingNameAndNumberTitle.setText(selectedBuilding + " - " + currentRoomNumber);

        ScheduleSelector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"ALL", "M", "T", "W", "TH", "F", "S"}));
        ScheduleSelector.addActionListener(e -> loadSubjects());

        loadSubjects();
    }

    private void loadSubjects() {
        String selectedSchedule = (String) ScheduleSelector.getSelectedItem();
        List<Subject> subjects = viewModel.getFilteredSubjects(selectedSchedule);

        String[] columns;
        if ("Student".equalsIgnoreCase(accountType)) {
            columns = new String[]{"ID", "TIME START", "TIME END", "SUBJECT", "SCHEDULE", "SECTION", "INSTRUCTOR"};
            SubjectAdd.setVisible(false);
        } else {
            columns = new String[]{"ID", "TIME START", "TIME END", "SUBJECT", "SCHEDULE", "SECTION", "INSTRUCTOR", "OPTIONS"};
            SubjectAdd.setVisible(true);
        }

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Subject subject : subjects) {
            model.addRow(new Object[]{
                subject.getId(),
                subject.getTimeStart(),
                subject.getTimeEnd(),
                subject.getName(),
                subject.getSchedule(),
                subject.getSection(),
                subject.getInstructor()
            });
        }

        SubjectTable.setModel(model);
        SubjectTable.getColumnModel().getColumn(0).setMinWidth(0);
        SubjectTable.getColumnModel().getColumn(0).setMaxWidth(0);
        SubjectTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        if (!"Student".equalsIgnoreCase(accountType)) {
            SubjectTable.getColumn("OPTIONS").setCellRenderer(new OptionsRenderer());
            SubjectTable.getColumn("OPTIONS").setCellEditor(new OptionsEditor());

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            for (int i = 1; i < 7; i++) {
                SubjectTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }
    
    private Subject getSubjectFromTable(int row) {
        return new Subject(
            (int) SubjectTable.getValueAt(row, 0),
            (String) SubjectTable.getValueAt(row, 1),
            (String) SubjectTable.getValueAt(row, 2),
            (String) SubjectTable.getValueAt(row, 3),
            (String) SubjectTable.getValueAt(row, 4),
            (String) SubjectTable.getValueAt(row, 5),
            (String) SubjectTable.getValueAt(row, 6)
        );
    }

    // Custom renderer for the "Options" column
    private class OptionsRenderer extends JPanel implements TableCellRenderer {

        private final JButton editButton;
        private final JButton deleteButton;

        public OptionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editButton = new JButton("EDIT");
            deleteButton = new JButton("DELETE");
            add(editButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
    
        // Custom editor for the "Options" column
    private class OptionsEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel;
        private final JButton editButton;
        private final JButton deleteButton;
        private int row;

        public OptionsEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editButton = new JButton("EDIT");
            deleteButton = new JButton("DELETE");
            panel.add(editButton);
            panel.add(deleteButton);

            // Add action listener for the Edit button
            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    editRow(row);
                }
            });

            // Add action listener for the Delete button
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    deleteRow(row);
                }
            });
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

    // Edit the row (open AddSubject JFrame with row data)
    private void editRow(int row) {
        if ("Student".equalsIgnoreCase(accountType)) return;
        Subject subject = getSubjectFromTable(row);

        if (!viewModel.canEditSubject(subject)) {
            JOptionPane.showMessageDialog(this, "You cannot edit this subject. It is not assigned to you.", "Permission Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AddUpdateSubject updateSubjectWindow = new AddUpdateSubject(
            'U',
            subject.getId(),
            viewModel.building, 
            viewModel.roomNumber,
            accountName,
            accountType
        );

        updateSubjectWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadSubjects();
            }
        });

        updateSubjectWindow.setVisible(true);
    }

    // Delete the row
    private void deleteRow(int row) {
        if ("Student".equalsIgnoreCase(accountType)) return;
        
        Subject subject = getSubjectFromTable(row);

        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the subject: " + subject.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            if (viewModel.deleteSubject(subject.getId())) {
                loadSubjects();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete the subject.", "Error", JOptionPane.ERROR_MESSAGE);
            }
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
        SubjectTable = new javax.swing.JTable();
        CETAFA = new javax.swing.JLabel();
        UB = new javax.swing.JLabel();
        COMPESA = new javax.swing.JLabel();
        SubjectAdd = new javax.swing.JButton();
        BuildingNameAndNumberTitle = new javax.swing.JLabel();
        DaysLabel = new javax.swing.JLabel();
        back = new javax.swing.JLabel();
        ScheduleSelector = new javax.swing.JComboBox<>();
        Background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(1920, 1080));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        SubjectTable.setBackground(new java.awt.Color(255, 255, 255));
        SubjectTable.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        SubjectTable.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        SubjectTable.setForeground(new java.awt.Color(0, 0, 0));
        SubjectTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(SubjectTable);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 230, 1650, 770));

        CETAFA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/3.png"))); // NOI18N
        jPanel1.add(CETAFA, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, -1, -1));

        UB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/1.png"))); // NOI18N
        jPanel1.add(UB, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        COMPESA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/2.png"))); // NOI18N
        jPanel1.add(COMPESA, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 20, -1, -1));

        SubjectAdd.setBackground(new java.awt.Color(255, 204, 0));
        SubjectAdd.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        SubjectAdd.setForeground(new java.awt.Color(0, 0, 0));
        SubjectAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/add-button.png"))); // NOI18N
        SubjectAdd.setText("ADD SUBJECT");
        SubjectAdd.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        SubjectAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubjectAddActionPerformed(evt);
            }
        });
        jPanel1.add(SubjectAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(1630, 180, 150, 40));

        BuildingNameAndNumberTitle.setFont(new java.awt.Font("Arial", 1, 60)); // NOI18N
        BuildingNameAndNumberTitle.setForeground(new java.awt.Color(255, 255, 255));
        BuildingNameAndNumberTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        BuildingNameAndNumberTitle.setText("BUILDING NAME");
        jPanel1.add(BuildingNameAndNumberTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 70, 1710, -1));

        DaysLabel.setBackground(new java.awt.Color(255, 255, 255));
        DaysLabel.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        DaysLabel.setForeground(new java.awt.Color(255, 255, 255));
        DaysLabel.setText("DAY:");
        jPanel1.add(DaysLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 190, 50, 30));

        back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/arrow.png"))); // NOI18N
        back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backMouseClicked(evt);
            }
        });
        jPanel1.add(back, new org.netbeans.lib.awtextra.AbsoluteConstraints(1870, 10, 40, 40));

        ScheduleSelector.setBackground(new java.awt.Color(255, 255, 255));
        ScheduleSelector.setFont(new java.awt.Font("DialogInput", 0, 18)); // NOI18N
        ScheduleSelector.setForeground(new java.awt.Color(0, 0, 0));
        ScheduleSelector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "M", "T", "W", "TH", "F", "S" }));
        jPanel1.add(ScheduleSelector, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 187, 130, -1));

        Background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IndexAdminBackground.png"))); // NOI18N
        jPanel1.add(Background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
        Background.getAccessibleContext().setAccessibleName("Background");

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
    }// </editor-fold>//GEN-END:initComponents

    private void SubjectAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubjectAddActionPerformed

        // Open the AddUpdateRoom JFrame
        AddUpdateSubject addSubjectWindow = new AddUpdateSubject('A', 0, viewModel.building, viewModel.roomNumber, accountName, accountType);

        // Add a window listener to detect when the AddRoom window is closed
        addSubjectWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                // Reload the rooms for the currently selected building
                loadSubjects();
            }
        });
        
        addSubjectWindow.setVisible(true);
    }//GEN-LAST:event_SubjectAddActionPerformed

    private void backMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backMouseClicked
        IndexView setBack = new IndexView(accountName, accountType);
        setBack.setVisible(true);
        dispose();
    }//GEN-LAST:event_backMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatMacDarkLaf.setup();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new subjectFormView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Background;
    private javax.swing.JLabel BuildingNameAndNumberTitle;
    private javax.swing.JLabel CETAFA;
    private javax.swing.JLabel COMPESA;
    private javax.swing.JLabel DaysLabel;
    private javax.swing.JComboBox<String> ScheduleSelector;
    private javax.swing.JButton SubjectAdd;
    private javax.swing.JTable SubjectTable;
    private javax.swing.JLabel UB;
    private javax.swing.JLabel back;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
