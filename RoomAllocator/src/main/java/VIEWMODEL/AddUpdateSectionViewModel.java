/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VIEWMODEL;

import MODEL.SectionDAO;
import javax.swing.JOptionPane;

public class AddUpdateSectionViewModel {

    public boolean processSection(char mode, int sectionID, String sectionNameInput, String populationInput, javax.swing.JFrame parent) {
        String sectionName = sectionNameInput.trim().toUpperCase();
        String sectionPopulation = populationInput.trim();

        // Input validation
        if (sectionName.isEmpty() || sectionPopulation.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "All fields must be filled out.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!sectionPopulation.matches("\\d+")) {
            JOptionPane.showMessageDialog(parent, "Population must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int population = Integer.parseInt(sectionPopulation);
        boolean success;

        if (Character.toUpperCase(mode) == 'A') {
            // Add mode
            SectionDAO sectionDAO = new SectionDAO();
            success = sectionDAO.addSection(sectionName, population);
            if (success) {
                JOptionPane.showMessageDialog(parent, "Section added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parent, "Failed to add section.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            // Update mode
            String validationMessage = SectionDAO.validateRoomCapacity(sectionName, population);
            if (validationMessage != null) {
                JOptionPane.showMessageDialog(parent, validationMessage, "Room Capacity Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            success = SectionDAO.updateSection(sectionID, sectionName, sectionPopulation);
            if (success) {
                JOptionPane.showMessageDialog(parent, "Section updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parent, "Failed to update section. Ensure the ID exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        return success;
    }
}

