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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author talai
 */
public class SectionDAO {
    // Method to retrieve all sections in the format "sectionName (sectionPopulation Student[s])"
    public static List<String> getAllSections() {
        List<String> sections = new ArrayList<>();
        String sql = "SELECT sectionName, sectionPopulation FROM sections";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Database.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String sectionName = rs.getString("sectionName");
                int sectionPopulation = rs.getInt("sectionPopulation");
                String formattedSection = sectionName + " (" + sectionPopulation + " Student" + (sectionPopulation > 1 ? "s" : "") + ")";
                sections.add(formattedSection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return sections;
    }
    
    // Method to retrieve all sections from the database
    public static ArrayList<Object[]> getAllSectionsToTable() {
        ArrayList<Object[]> sections = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT sectionName, sectionPopulation FROM sections")) {
            while (rs.next()) {
                sections.add(new Object[]{rs.getString("sectionName"), rs.getInt("sectionPopulation")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sections;
    }

    // Method to get the ID of a section based on its name and population
    public static int getSectionId(String sectionName, String sectionPopulation) {
        int id = -1;
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM sections WHERE sectionName = ? AND sectionPopulation = ?")) {
            stmt.setString(1, sectionName);
            stmt.setString(2, sectionPopulation);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
    
    public static SectionSG getSectionById(int id) {
        SectionSG section = null;

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT sectionName, sectionPopulation FROM sections WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                section = new SectionSG();
                section.setId(id);
                section.setSectionName(rs.getString("sectionName"));
                section.setPopulation(rs.getString("sectionPopulation"));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log or handle exceptions appropriately
        }

        return section;
    }
    
    public static int getSectionPopulation(String sectionName) {
        int population = 0;

        // Extract the section name (omit everything after the first space)
        if (sectionName.contains(" ")) {
            sectionName = sectionName.substring(0, sectionName.indexOf(" ")).trim();
        }

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT sectionPopulation FROM sections WHERE sectionName = ?")) {
            ps.setString(1, sectionName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String populationStr = rs.getString("sectionPopulation");
                    population = Integer.parseInt(populationStr.trim()); // Parse string to integer
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        System.out.println("Population: " + population);
        return population;
    }
    
    public boolean addSection(String sectionName, int sectionPopulation) {
        String sql = "INSERT INTO sections (sectionName, sectionPopulation) VALUES (?, ?)";
        try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sectionName);
            pstmt.setInt(2, sectionPopulation);
            return pstmt.executeUpdate() > 0; // Return true if the insertion was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method to delete a section by ID
    public static boolean deleteSection(int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM sections WHERE id = ?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static String validateRoomCapacity(String sectionName, int population) {
        String errorMessage = null;

        try (Connection conn = Database.getConnection()) {
            // Preprocess sectionName to remove everything after the first space
            String processedSectionName = sectionName.split(" ")[0];

            // Query to find RoomID and Capacity based on the section name
            String sql = "SELECT r.Capacity, r.Building, r.RoomNumber " +
                         "FROM subjects s " +
                         "JOIN rooms r ON s.RoomID = r.ID " +
                         "WHERE TRIM(SUBSTRING_INDEX(s.Section, ' ', 1)) = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, processedSectionName);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Retrieve room details
                String capacityStr = rs.getString("Capacity"); // Retrieve capacity as a string
                int roomCapacity;

                try {
                    // Parse the capacity string to an integer
                    roomCapacity = Integer.parseInt(capacityStr);
                } catch (NumberFormatException e) {
                    // Handle invalid capacity format in the database
                    JOptionPane.showMessageDialog(null, 
                        "Invalid room capacity format in the database. Please check the room configuration.",
                        "Data Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return "Room capacity is not a valid number in the database.";
                }

                String building = rs.getString("Building");
                String roomNumber = rs.getString("RoomNumber");

                // Check if the room capacity is less than the proposed population
                if (roomCapacity < population) {
                    errorMessage = "Current Section is Enrolled in a Subject\n" +
                                   "The room capacity is insufficient for this population.\n" +
                                   "Building: " + building + "\n" +
                                   "Room Number: " + roomNumber + "\n" +
                                   "Capacity: " + roomCapacity + "\n" +
                                   "Proposed Population: " + population;
                }
            } else {
                System.out.println("Query returned no results.");
                // If no matching section is found, allow the update to proceed
                errorMessage = null; // No error; section does not exist, no capacity validation needed
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
            errorMessage = "An error occurred while validating room capacity.";
        }

        return errorMessage;
    }

    public static boolean updateSection(int id, String sectionName, String population) {
        boolean isUpdated = false;

        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE sections SET sectionName = ?, sectionPopulation = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Set query parameters
            stmt.setString(1, sectionName);
            stmt.setString(2, population);
            stmt.setInt(3, id);

            // Execute the update query
            int rowsAffected = stmt.executeUpdate();
            isUpdated = rowsAffected > 0; // Return true if at least one row is updated
        } catch (Exception e) {
            e.printStackTrace(); // Log or handle exceptions as needed
        }

        return isUpdated;
    }
    
    public static boolean hasSubjectsInSection(String section) {
        // Adjust this query according to your actual subject table schema
        String query = "SELECT COUNT(*) FROM subjects WHERE section = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, section);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
