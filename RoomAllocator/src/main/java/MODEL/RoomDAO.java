/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MODEL;

import MODEL.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JOptionPane;

public class RoomDAO {
    // Method to get all unique building names
    public HashSet<String> getBuildingNames() {
        HashSet<String> uniqueBuildings = new HashSet<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Database.getConnection();
            String sql = "SELECT DISTINCT Building FROM rooms";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                uniqueBuildings.add(rs.getString("Building"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading building names.", "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return uniqueBuildings;
    }

    // Method to load rooms for a specific building
    public ArrayList<Object[]> getRoomsForBuilding(String buildingName) {
        ArrayList<Object[]> rooms = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Database.getConnection();
            String sql = "SELECT RoomNumber, Airconditioned, Board, Capacity, Type FROM rooms WHERE Building = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, buildingName);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String roomNumber = rs.getString("RoomNumber");
                String airconditioned = rs.getString("Airconditioned");
                String board = rs.getString("Board");
                String capacity = rs.getString("Capacity");
                String type = rs.getString("Type");

                // Modify RoomNumber if Type is LABORATORY
                if ("LABORATORY".equalsIgnoreCase(type)) {
                    roomNumber += " - LAB";
                }

                // Add room details as an Object array
                rooms.add(new Object[]{roomNumber, airconditioned, board, capacity, type});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading rooms.", "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return rooms;
    }

    public boolean addRoom(String buildingName, String roomNumber, String Airconditioned, String Board, String Capacity, String Type) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = Database.getConnection();
            String sql = "INSERT INTO rooms (Building, RoomNumber, Airconditioned, Board, Capacity, Type) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, buildingName.toUpperCase());
            pstmt.setString(2, roomNumber.toUpperCase());
            pstmt.setString(3, Airconditioned.toUpperCase());
            pstmt.setString(4, Board.toUpperCase());
            pstmt.setString(5, Capacity.toUpperCase());
            pstmt.setString(6, Type.toUpperCase());
            pstmt.executeUpdate();

            return true; // Return true if insertion was successful
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding room. " + e, "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    public RoomSG getRoom(String roomNumber) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Database.getConnection();
            String sql = "SELECT Building, RoomNumber, Airconditioned, Board, Capacity, Type FROM rooms WHERE RoomNumber = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, roomNumber.toUpperCase());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String building = rs.getString("Building");
                String room = rs.getString("RoomNumber");
                String airconditioned = rs.getString("Airconditioned");
                String board = rs.getString("Board");
                String capacity = rs.getString("Capacity");
                String type = rs.getString("Type");

                // Return a new RoomSG object with the fetched details
                return new RoomSG(building, room, airconditioned, board, capacity, type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching room details. " + e, "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return null; // Return null if no room is found
    }
    
    public boolean updateRoom(String oldRoomNumber, String buildingName, String roomNumber, String airconditioned, String board, String capacity, String type) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = Database.getConnection();
            String sql = "UPDATE rooms SET Building = ?, RoomNumber = ?, Airconditioned = ?, Board = ?, Capacity = ?, Type = ? WHERE RoomNumber = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, buildingName.toUpperCase());
            pstmt.setString(2, roomNumber.toUpperCase());
            pstmt.setString(3, airconditioned.toUpperCase());
            pstmt.setString(4, board.toUpperCase());
            pstmt.setString(5, capacity.toUpperCase());
            pstmt.setString(6, type.toUpperCase());
            pstmt.setString(7, oldRoomNumber.toUpperCase());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Return true if at least one row was updated
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating room. " + e, "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    // Method to delete a room based on room number
    public boolean deleteRoom(String buildingName, String roomNumber) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = Database.getConnection();

            // Step 1: Verify if the room exists
            String checkRoomSql = "SELECT ID FROM rooms WHERE Building = ? AND RoomNumber = ?";
            pstmt = conn.prepareStatement(checkRoomSql);
            pstmt.setString(1, buildingName);
            pstmt.setString(2, roomNumber);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                // Room does not exist
                JOptionPane.showMessageDialog(null, "Room not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            int roomId = rs.getInt("ID"); // Retrieve Room ID
            rs.close();
            pstmt.close();

            // Step 2: Delete the room (subjects linked via foreign key will be automatically deleted)
            String deleteRoomSql = "DELETE FROM rooms WHERE ID = ?";
            pstmt = conn.prepareStatement(deleteRoomSql);
            pstmt.setInt(1, roomId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Room and related subjects deleted successfully.");
                return true;
            } else {
                System.err.println("Failed to delete room.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting room.", "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }


    // Utility method to close resources
    private void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static int getRoomCapacity(String buildingName, String roomNumber) {
        int capacity = 0;
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT Capacity FROM rooms WHERE Building = ? AND RoomNumber = ?")) {
            ps.setString(1, buildingName);
            ps.setString(2, roomNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String capacityStr = rs.getString("Capacity");
                    capacity = Integer.parseInt(capacityStr.trim()); // Parse string to integer
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        System.out.println("Capacity: " + capacity);
        return capacity;
    }
}

