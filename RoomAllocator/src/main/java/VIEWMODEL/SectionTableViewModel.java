/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VIEWMODEL;

import MODEL.SectionDAO;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author talai
 */
public class SectionTableViewModel {
    private DefaultTableModel tableModel;

    public SectionTableViewModel() {
        tableModel = new DefaultTableModel(new Object[]{"SECTION", "POPULATION", "OPTIONS"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public void loadSections() {
        tableModel.setRowCount(0);
        ArrayList<Object[]> sections = SectionDAO.getAllSectionsToTable();

        if (sections != null) {
            for (Object[] section : sections) {
                tableModel.addRow(new Object[]{section[0], section[1], "OPTIONS"});
            }
        }
    }

    public int getSectionId(String sectionName, String population) {
        return SectionDAO.getSectionId(sectionName, population);
    }
    
    public boolean deleteSection(int id, String section) {
        if (SectionDAO.hasSubjectsInSection(section)) {
            JOptionPane.showMessageDialog(null, "Cannot delete section: there are subjects assigned to it. Please update or delete them first.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return SectionDAO.deleteSection(id);
    }
}

