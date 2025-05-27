/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VIEWMODEL;

import VIEW.AddUpdateRoom;
import MODEL.RoomDAO;
import MODEL.RoomSG;
import java.util.Collections;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

public class AddUpdateRoomViewModel {

    private RoomDAO roomDAO;
    private DefaultComboBoxModel<String> buildingNameModel;
    private String selectedType;

    public AddUpdateRoomViewModel() {
        roomDAO = new RoomDAO();
        buildingNameModel = new DefaultComboBoxModel<>();
    }

    public DefaultComboBoxModel<String> getBuildingNameModel() {
        return buildingNameModel;
    }
    /*
    public void loadBuildingNames() throws Exception {
        Set<String> buildingNames = roomDAO.getBuildingNames();
        buildingNameModel.removeAllElements();
        for (String name : buildingNames) {
            buildingNameModel.addElement(name);
        }
    }
    */
    public void setSelectedType(String type) {
        this.selectedType = type;
    }

    public String getBackgroundImagePath() {
        if ("CLASSROOM".equalsIgnoreCase(selectedType)) {
            return "/AddRoomBG.png";
        } else if ("LABORATORY".equalsIgnoreCase(selectedType)) {
            return "/AddRoomWLabBG.png";
        }
        return null;
    }

    // -----------------------
    // RoomForm and addRoom()
    // -----------------------

    public static class RoomForm {
        public String buildingName;
        public String roomNumber;
        public String airconditioned;
        public String board;
        public String capacity;
        public String type;
    }

    public static class RoomResult {
        public boolean success;
        public String message;

        public RoomResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    public RoomResult addUpdateRoom(RoomForm form, char mode, String oldRoomNumber) {
        if (form.buildingName == null || form.buildingName.trim().isEmpty()) {
            return new RoomResult(false, "Building Name cannot be empty.");
        }

        if (form.roomNumber == null || form.roomNumber.trim().isEmpty()) {
            return new RoomResult(false, "Room Number must not be empty.");
        }

        if ("LABORATORY".equalsIgnoreCase(form.type)) {
            if (!form.roomNumber.matches("[a-zA-Z0-9]+")) {
                return new RoomResult(false, "Room Number for LABORATORY must contain only letters and numbers.");
            }
        } else {
            if (!form.roomNumber.matches("\\d+")) {
                return new RoomResult(false, "Room Number must contain only numbers.");
            }
        }

        if (form.capacity == null || form.capacity.trim().isEmpty()) {
            return new RoomResult(false, "Capacity must not be empty.");
        }

        if (!form.capacity.matches("\\d+")) {
            return new RoomResult(false, "Capacity must contain only numbers.");
        }

        // Construct room number
        String modifiedRoomNumber;
        if ("LABORATORY".equalsIgnoreCase(form.type)) {
            modifiedRoomNumber = form.roomNumber;
        } else {
            String[] excludedWords = {"and", "of", "the", "in", "on", "at"};
            String[] words = form.buildingName.trim().split("\\s+");
            StringBuilder abbreviation = new StringBuilder();
            for (String word : words) {
                String lower = word.toLowerCase();
                boolean excluded = false;
                for (String ex : excludedWords) {
                    if (lower.equals(ex)) {
                        excluded = true;
                        break;
                    }
                }
                if (!excluded && !word.isEmpty()) {
                    abbreviation.append(word.charAt(0));
                }
            }
            modifiedRoomNumber = abbreviation.toString().toUpperCase() + form.roomNumber;
        }
        
        boolean success;
        String message;

        if (Character.toUpperCase(mode) == 'A') {
            success = roomDAO.addRoom(form.buildingName, modifiedRoomNumber, form.airconditioned, form.board, form.capacity, form.type);
            message = success ? "Room added successfully!" : "Failed to add room. Please try again.";
        } else {
            success = roomDAO.updateRoom(oldRoomNumber, form.buildingName, modifiedRoomNumber, form.airconditioned, form.board, form.capacity, form.type);
            message = success ? "Room updated successfully!" : "Failed to update room. Please try again.";
        }
        
        return new RoomResult(success, message);
    }
    
    public RoomForm loadRoomDetails(String roomNumber) {
        RoomSG room = roomDAO.getRoom(roomNumber);
        if (room == null) return null;

        RoomForm form = new RoomForm();
        form.buildingName = room.getBuilding();
        form.roomNumber = room.getRoomNumber();
        form.airconditioned = room.getAirconditioned();
        form.board = room.getBoard();
        form.capacity = room.getCapacity();
        form.type = room.getType();
        
        roomNumber = room.getRoomNumber();
        return form;
    }
    
    public Set<String> getBuildingNames(char mode, AddUpdateRoom AddUpdateForm) {
        try {
            // Determine button label based on mode
            String buttonLabel = (Character.toUpperCase(mode) == 'A') ? "ADD" : "UPDATE";
            AddUpdateForm.ARAddButton.setText(buttonLabel);

            // Fetch building names once
            Set<String> buildingNames = roomDAO.getBuildingNames();

            // Update ComboBox model
            buildingNameModel.removeAllElements();
            for (String name : buildingNames) {
                buildingNameModel.addElement(name);
            }

            return buildingNames; // Return the same set
        } catch (Exception e) {
            // Optionally log or handle the exception
            return Collections.emptySet();
        }
    }
}

