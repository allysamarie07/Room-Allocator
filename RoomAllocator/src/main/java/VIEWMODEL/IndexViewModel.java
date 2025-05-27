/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VIEWMODEL;

import MODEL.RoomDAO;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author talai
 */
public class IndexViewModel {
    private RoomDAO roomDAO;
    private DefaultTableModel tableModel;
    private List<String> buildingNames;
    private Component parentComponent;

    public IndexViewModel() {
        this.roomDAO = new RoomDAO();
        this.tableModel = new DefaultTableModel(new Object[]{"ROOM NUMBER", "AIRCONDITIONED", "BOARD", "CAPACITY", "OPTIONS"}, 0);
        this.parentComponent = parentComponent;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public List<String> getBuildingNames() {
        buildingNames = new ArrayList<>(roomDAO.getBuildingNames());
        Collections.sort(buildingNames);
        return buildingNames;
    }

    public void loadRoomsForBuilding(String buildingName) {
        tableModel.setRowCount(0);
        ArrayList<Object[]> rooms = roomDAO.getRoomsForBuilding(buildingName);
        rooms.sort(Comparator.comparing(room -> (String) room[0]));
        for (Object[] room : rooms) {
            tableModel.addRow(new Object[]{room[0], room[1], room[2], room[3], "Options"});
        }
    }

    public void deleteRoomWithConfirmation(String buildingName, String roomNumber, Runnable onSuccess, Runnable onFailure) {
        int confirm = JOptionPane.showConfirmDialog(parentComponent, 
            "Are you sure you want to delete Room " + roomNumber + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = roomDAO.deleteRoom(buildingName, roomNumber);
            if (success) {
                JOptionPane.showMessageDialog(parentComponent, "Room " + roomNumber + " deleted successfully.");
                onSuccess.run();
            } else {
                JOptionPane.showMessageDialog(parentComponent, "Room " + roomNumber + " could not be found.");
                onFailure.run();
            }
        }
    }
}
