/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MODEL;

import MODEL.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;

public class SubjectDAO {
    public static List<Object[]> getSubjects(String building, String roomNumber) {
        List<Object[]> subjects = new ArrayList<>();

        String query = "SELECT s.ID, s.TimeStart, s.TimeEnd, s.Subject, s.Schedule, s.accountName, s.Section " +
                       "FROM subjects s " +
                       "JOIN rooms r ON s.RoomID = r.ID " +
                       "WHERE r.Building = ? AND r.RoomNumber = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            // Set the parameters for the query
            ps.setString(1, building);
            ps.setString(2, roomNumber);

            // Execute the query and process the results
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Extract Section and trim it to remove everything after a space
                String section = rs.getString("Section");
                if (section != null && section.contains(" ")) {
                    section = section.split(" ")[0]; // Get only the part before the space
                }

                // Add the record to the subjects list
                subjects.add(new Object[]{
                    rs.getInt("ID"),
                    rs.getString("TimeStart"),
                    rs.getString("TimeEnd"),
                    rs.getString("Subject"),
                    rs.getString("Schedule"),
                    section, // Formatted Section
                    rs.getString("accountName")// Instructor (accountName)   
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subjects;
    }
    
    public static boolean addSubject(String building, String roomNumber, String timeStart, String timeEnd, String subject, String schedule, String accountName, String section) {
        String selectRoomIdQuery = "SELECT ID FROM rooms WHERE Building = ? AND RoomNumber = ?";
        String insertQuery = "INSERT INTO subjects (RoomID, TimeStart, TimeEnd, Subject, Schedule, AccountName, Section) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectRoomIdQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            // Step 1: Retrieve the Room ID
            selectStmt.setString(1, building);
            selectStmt.setString(2, roomNumber);

            ResultSet rs = selectStmt.executeQuery();
            if (!rs.next()) {
                // If no matching Room ID is found, return false
                System.err.println("No matching room found for Building: " + building + " and RoomNumber: " + roomNumber);
                return false;
            }

            int roomId = rs.getInt("ID"); // Get the Room ID

            // Step 2: Insert the subject with the Room ID
            insertStmt.setInt(1, roomId); // RoomID
            insertStmt.setString(2, timeStart); // TimeStart
            insertStmt.setString(3, timeEnd); // TimeEnd
            insertStmt.setString(4, subject); // Subject
            insertStmt.setString(5, schedule); // Schedule
            insertStmt.setString(6, accountName); // AccountName
            insertStmt.setString(7, section); // Section

            // Step 3: Execute the insert and return true if successful
            int rowsAffected = insertStmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an error occurred
        }
    }


    
    public static boolean hasConflict(int id, String buildingName, String roomNumber, String timeStart, String timeEnd, String schedule, String accountName, String subject, String section) {
        if (id == -1) {
            // For adding a subject, check time overlaps with existing subjects in the database
            return hasTimeOverlapWithExistingSubjects(buildingName, roomNumber, timeStart, timeEnd, schedule, accountName, subject, section);
        } else {
            // For updating a subject, check for conflicts excluding the current subject
            return hasConflictForUpdating(id, buildingName, roomNumber, timeStart, timeEnd, schedule, subject, section, accountName);
        }
    }

    private static boolean hasTimeOverlapWithExistingSubjects(String buildingName, String roomNumber, String timeStart, String timeEnd, String schedule, String accountName, String subject, String section) {
        String getRoomIdQuery = "SELECT ID FROM rooms WHERE Building = ? AND RoomNumber = ?";
        String sql = """
            SELECT s.*, r.Building, r.RoomNumber
            FROM subjects s
            JOIN rooms r ON s.RoomID = r.ID
        """;

        Connection conn = null;
        PreparedStatement pstmtRoomId = null;
        PreparedStatement pstmtSubjects = null;
        ResultSet rsRoomId = null;
        ResultSet rsSubjects = null;

        try {
            conn = Database.getConnection();

            // Step 1: Get the RoomID from the rooms table
            pstmtRoomId = conn.prepareStatement(getRoomIdQuery);
            pstmtRoomId.setString(1, buildingName);
            pstmtRoomId.setString(2, roomNumber);
            rsRoomId = pstmtRoomId.executeQuery();

            int roomId = -1;
            if (rsRoomId.next()) {
                roomId = rsRoomId.getInt("ID");
            } else {
                JOptionPane.showMessageDialog(null, "Room not found for the specified Building and Room Number.", "Error", JOptionPane.ERROR_MESSAGE);
                return true; // Treat as conflict because the room does not exist
            }

            // Step 2: Check for time overlaps in the subjects table
            pstmtSubjects = conn.prepareStatement(sql);
            rsSubjects = pstmtSubjects.executeQuery();

            while (rsSubjects.next()) {
                int existingRoomId = rsSubjects.getInt("RoomID");
                String existingBuilding = rsSubjects.getString("Building");
                String existingRoom = rsSubjects.getString("RoomNumber");
                String existingStartTime = rsSubjects.getString("TimeStart");
                String existingEndTime = rsSubjects.getString("TimeEnd");
                String existingSchedule = rsSubjects.getString("Schedule");
                String existingAccountName = rsSubjects.getString("AccountName");
                String existingSubject = rsSubjects.getString("Subject");
                String existingSection = rsSubjects.getString("Section");

                // Check for time overlap in the same room
                if (existingRoomId == roomId && timeOverlaps(existingStartTime, existingEndTime, timeStart, timeEnd)) {
                    // Same room overlapping time is not allowed unless the schedules are different
                    if (schedulesConflict(existingSchedule, schedule)) {
                        JOptionPane.showMessageDialog(null,
                            "Conflict detected with an existing subject in the same room:\n" +
                            "- Existing Building: " + existingBuilding + "\n" +
                            "- Existing Room: " + existingRoom + "\n" +
                            "- Existing Time: " + existingStartTime + " - " + existingEndTime + "\n" +
                            "- Existing Schedule: " + existingSchedule + "\n" +
                            "- Existing Subject: " + existingSubject + "\n" +
                            "- Existing Section: " + existingSection + "\n" +
                            "Please choose another time for this subject.",
                            "Room Conflict Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        return true; // Conflict detected in the same room with conflicting schedule
                    }
                }


                // Check for teacher conflict (same time, same teacher, regardless of room)
                if (timeOverlaps(existingStartTime, existingEndTime, timeStart, timeEnd) && accountName.equalsIgnoreCase(existingAccountName)) {
                    // If the teacher has a conflicting schedule
                    if (schedulesConflict(existingSchedule, schedule)) {
                        JOptionPane.showMessageDialog(null,
                            "Conflict detected with the teacher's schedule:\n" +
                            "The teacher already has a subject scheduled at this time with a conflicting schedule.\n\n" +
                            "- Existing Teacher: " + existingAccountName + "\n" +
                            "- Existing Time: " + existingStartTime + " - " + existingEndTime + "\n" +
                            "- Existing Schedule: " + existingSchedule + "\n" +
                            "Please choose another time or room for the subject.",
                            "Teacher Schedule Conflict Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        return true; // Conflict detected for the teacher's schedule
                    }
                }

                // Allow same subject, same time, same teacher, but different schedules
                if (timeOverlaps(existingStartTime, existingEndTime, timeStart, timeEnd) &&
                    subject.equalsIgnoreCase(existingSubject) &&
                    accountName.equalsIgnoreCase(existingAccountName)) {

                    // If the schedules are different but they are allowed, skip conflict
                    if (!schedule.equals(existingSchedule) && !schedulesConflict(existingSchedule, schedule)) {
                        continue; // No conflict, different schedule allowed
                    }

                    // If schedules are the same and overlap, it's a conflict
                    if (schedulesConflict(existingSchedule, schedule)) {
                        JOptionPane.showMessageDialog(null,
                            "Conflict detected with an existing subject:\n" +
                            "- Existing Building: " + existingBuilding + "\n" +
                            "- Existing Room: " + existingRoom + "\n" +
                            "- Existing Time: " + existingStartTime + " - " + existingEndTime + "\n" +
                            "- Existing Schedule: " + existingSchedule + "\n" +
                            "- Existing Subject: " + existingSubject + "\n" +
                            "- Existing Section: " + existingSection + "\n" +
                            "- Existing Teacher: " + existingAccountName + "\n\n" +
                            "Current:\n" +
                            "- Building: " + buildingName + "\n" +
                            "- Room: " + roomNumber + "\n" +
                            "- Time: " + timeStart + " - " + timeEnd + "\n" +
                            "- Schedule: " + schedule + "\n" +
                            "- Subject: " + subject + "\n" +
                            "- Section: " + section + "\n" +
                            "- Teacher: " + accountName,
                            "Conflict Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        return true; // Conflict detected for same subject and teacher with same time
                    }
                }

                // Check if the subject is the same time but with different teacher and different room (this is acceptable)
                if (timeOverlaps(existingStartTime, existingEndTime, timeStart, timeEnd) &&
                    subject.equalsIgnoreCase(existingSubject) &&
                    !accountName.equalsIgnoreCase(existingAccountName) &&
                    existingRoomId != roomId) {
                    // No conflict here, it's acceptable
                    continue;
                }

                // Check for different schedule conflict (same time, different schedule)
                if (timeOverlaps(existingStartTime, existingEndTime, timeStart, timeEnd) &&
                    !schedule.equals(existingSchedule) &&
                    schedulesConflict(existingSchedule, schedule)) {
                    // If there is a time overlap but a different schedule (this is not allowed)
                    JOptionPane.showMessageDialog(null,
                        "Schedule conflict detected:\n" +
                        "The schedules overlap, even though the times are the same.\n" +
                        "Existing Schedule: " + existingSchedule + "\n" +
                        "Current Schedule: " + schedule,
                        "Schedule Conflict Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return true; // Conflict detected for the schedule
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rsRoomId != null) rsRoomId.close();
                if (pstmtRoomId != null) pstmtRoomId.close();
                if (rsSubjects != null) rsSubjects.close();
                if (pstmtSubjects != null) pstmtSubjects.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false; // No conflicts found
    }

    // Method to check time conflicts for updating a subject
    private static boolean hasConflictForUpdating(int id, String buildingName, String roomNumber, String timeStart, String timeEnd, String schedule, String subject, String section, String accountName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Database.getConnection();

            // Query to select overlapping subjects excluding the current subject
            String sql = "SELECT s.*, r.Building, r.RoomNumber FROM subjects s " +
                         "JOIN rooms r ON s.RoomID = r.ID " +
                         "WHERE s.id <> ?"; // Excluding the current subject
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            // Check for time and schedule conflicts
            while (rs.next()) {
                int existingRoomId = rs.getInt("RoomID");
                String existingBuilding = rs.getString("Building");
                String existingRoom = rs.getString("RoomNumber");
                String existingStartTime = rs.getString("TimeStart");
                String existingEndTime = rs.getString("TimeEnd");
                String existingSchedule = rs.getString("Schedule");
                String existingAccountName = rs.getString("AccountName");
                String existingSubject = rs.getString("Subject");
                String existingSection = rs.getString("Section");

                // Check for time overlap
                if (timeOverlaps(existingStartTime, existingEndTime, timeStart, timeEnd)) {
                    // Check for conflict in the same room
                    boolean sameRoomConflict = buildingName.equals(existingBuilding) && roomNumber.equals(existingRoom);
                    boolean teacherConflict = accountName.equalsIgnoreCase(existingAccountName);
                    boolean subjectConflict = subject.equalsIgnoreCase(existingSubject);
                    boolean sectionConflict = section.equalsIgnoreCase(existingSection);
                    boolean scheduleConflict = schedulesConflict(schedule, existingSchedule);

                    // Same room and time conflict, but different schedules are allowed
                    if (sameRoomConflict && timeOverlaps(existingStartTime, existingEndTime, timeStart, timeEnd) && schedulesConflict(schedule, existingSchedule)) {
                        JOptionPane.showMessageDialog(null,
                            "Conflict detected with an existing subject in the same room:\n" +
                            "- Existing Building: " + existingBuilding + "\n" +
                            "- Existing Room: " + existingRoom + "\n" +
                            "- Existing Time: " + existingStartTime + " - " + existingEndTime + "\n" +
                            "- Existing Schedule: " + existingSchedule + "\n" +
                            "- Existing Subject: " + existingSubject + "\n" +
                            "- Existing Section: " + existingSection + "\n" +
                            "Please choose another time for this subject.",
                            "Room Conflict Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        return true; // Conflict detected in the same room
                    }

                    // Check for teacher conflict across rooms
                    if (teacherConflict && !sameRoomConflict) {
                        // If the same teacher has overlapping times across different rooms
                        JOptionPane.showMessageDialog(null,
                            "Conflict detected with the teacher's schedule:\n" +
                            "The teacher already has a subject scheduled at this time in another room.\n\n" +
                            "Existing Teacher: " + existingAccountName + "\n" +
                            "Existing Time: " + existingStartTime + " - " + existingEndTime + "\n" +
                            "Current Time: " + timeStart + " - " + timeEnd + "\n\n" +
                            "Please choose another time or room for the subject.",
                            "Teacher Conflict Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        return true; // Conflict detected for the teacher's schedule
                    }

                    // Allow same subject, same time, same teacher, but different schedules
                    if (subject.equalsIgnoreCase(existingSubject) && accountName.equalsIgnoreCase(existingAccountName)) {
                        // If the schedules are different but allowed, skip conflict
                        if (!schedule.equals(existingSchedule) && !schedulesConflict(existingSchedule, schedule)) {
                            continue; // No conflict, different schedule allowed
                        }

                        // If schedules are the same and overlap, it's a conflict
                        if (schedulesConflict(existingSchedule, schedule)) {
                            JOptionPane.showMessageDialog(null,
                                "Conflict detected with an existing subject:\n" +
                                "- Existing Building: " + existingBuilding + "\n" +
                                "- Existing Room: " + existingRoom + "\n" +
                                "- Existing Time: " + existingStartTime + " - " + existingEndTime + "\n" +
                                "- Existing Schedule: " + existingSchedule + "\n" +
                                "- Existing Subject: " + existingSubject + "\n" +
                                "- Existing Section: " + existingSection + "\n" +
                                "- Existing Teacher: " + existingAccountName + "\n\n" +
                                "Current:\n" +
                                "- Building: " + buildingName + "\n" +
                                "- Room: " + roomNumber + "\n" +
                                "- Time: " + timeStart + " - " + timeEnd + "\n" +
                                "- Schedule: " + schedule + "\n" +
                                "- Subject: " + subject + "\n" +
                                "- Section: " + section + "\n" +
                                "- Teacher: " + accountName,
                                "Conflict Error",
                                JOptionPane.ERROR_MESSAGE
                            );
                            return true; // Conflict detected for same subject and teacher with same time
                        }
                    }

                    // Check for other conflicts (e.g., same time, different schedule)
                    if (!schedule.equals(existingSchedule) && schedulesConflict(existingSchedule, schedule)) {
                        JOptionPane.showMessageDialog(null,
                            "Schedule conflict detected:\n" +
                            "The schedules overlap, even though the times are the same.\n" +
                            "Existing Schedule: " + existingSchedule + "\n" +
                            "Current Schedule: " + schedule,
                            "Schedule Conflict Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        return true; // Conflict detected for the schedule
                    }
                }
            }

            // Check if details are identical to the existing subject
            String checkSameSql = "SELECT TimeStart, TimeEnd, Schedule, Subject, Section FROM subjects WHERE id = ?";
            pstmt = conn.prepareStatement(checkSameSql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String existingTimeStart = rs.getString("TimeStart");
                String existingTimeEnd = rs.getString("TimeEnd");
                String existingSchedule = rs.getString("Schedule");
                String existingSubject = rs.getString("Subject");
                String existingSection = rs.getString("Section");

                // Check if any changes were made to the subject
                if (existingTimeStart.equals(timeStart) &&
                    existingTimeEnd.equals(timeEnd) &&
                    existingSchedule.equals(schedule) &&
                    existingSubject.equals(subject) &&
                    existingSection.equals(section)) {
                    JOptionPane.showMessageDialog(null, "No changes detected.", "Update Info", JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
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

        return false; // No conflicts found
    }

    // Helper method to check if two time intervals overlap
    private static boolean timeOverlaps(String start1, String end1, String start2, String end2) {
        LocalTime time1Start = LocalTime.parse(start1);
        LocalTime time1End = LocalTime.parse(end1);
        LocalTime time2Start = LocalTime.parse(start2);
        LocalTime time2End = LocalTime.parse(end2);

        // Check if the time intervals overlap in any way
        return (time2Start.isBefore(time1End) && time2End.isAfter(time1Start));
    }
    
    private static boolean schedulesConflict(String schedule1, String schedule2) {
        Set<String> daysSet1 = parseDays(schedule1);
        Set<String> daysSet2 = parseDays(schedule2);

        // Check for any overlap between the two sets of days
        for (String day : daysSet1) {
            if (daysSet2.contains(day)) {
                return true; // Conflict detected if there is any common day
            }
        }
        return false; // No conflict based on the schedule
    }

    // Helper method to parse a schedule string into a set of individual days
    private static Set<String> parseDays(String schedule) {
        Set<String> days = new HashSet<>();
        if (schedule.contains("M")) days.add("M");
        if (schedule.contains("T") && !schedule.contains("TH")) days.add("T");
        if (schedule.contains("W")) days.add("W");
        if (schedule.contains("TH")) days.add("TH");
        if (schedule.contains("F")) days.add("F");
        if (schedule.contains("S")) days.add("S");
        return days;
    }

    public static boolean deleteSubjectById(int id, String accountName) {
        String getAccountTypeSQL = "SELECT AccountType FROM accounts WHERE AccountName = ?";
        String getSubjectAccountNameSQL = "SELECT AccountName FROM subjects WHERE ID = ?";
        String deleteSQL = "DELETE FROM subjects WHERE ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmtAccountType = conn.prepareStatement(getAccountTypeSQL);
             PreparedStatement pstmtSubjectAccountName = conn.prepareStatement(getSubjectAccountNameSQL);
             PreparedStatement pstmtDelete = conn.prepareStatement(deleteSQL)) {

            // Step 1: Get account type
            pstmtAccountType.setString(1, accountName);
            ResultSet rsAccountType = pstmtAccountType.executeQuery();

            if (!rsAccountType.next()) {
                return false; // account not found
            }

            String accountType = rsAccountType.getString("AccountType");

            if ("Dean".equalsIgnoreCase(accountType)) {
                // Dean can delete any subject
                pstmtDelete.setInt(1, id);
                int rowsAffected = pstmtDelete.executeUpdate();
                return rowsAffected > 0;

            } else if ("Faculty".equalsIgnoreCase(accountType)) {
                // Check if faculty owns the subject
                pstmtSubjectAccountName.setInt(1, id);
                ResultSet rsSubjectAccountName = pstmtSubjectAccountName.executeQuery();

                if (!rsSubjectAccountName.next()) {
                    return false; // subject not found
                }

                String subjectAccountName = rsSubjectAccountName.getString("AccountName");

                if (accountName.equalsIgnoreCase(subjectAccountName)) {
                    pstmtDelete.setInt(1, id);
                    int rowsAffected = pstmtDelete.executeUpdate();
                    return rowsAffected > 0;
                } else {
                    return false; // not allowed to delete other's subject
                }

            } else {
                return false; // unsupported account type
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static Subject getSubjectById(int id) {
        Subject subject = null;
        String query = "SELECT id, TimeStart, TimeEnd, Subject, Schedule, Section, AccountName FROM subjects WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int subjectId = rs.getInt("id");
                String timeStart = rs.getString("TimeStart");
                String timeEnd = rs.getString("TimeEnd");
                String name = rs.getString("Subject");
                String schedule = rs.getString("Schedule");
                String section = rs.getString("Section");
                String instructor = rs.getString("AccountName");

                subject = new Subject(subjectId, timeStart, timeEnd, name, schedule, section, instructor);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Replace with proper logging if necessary
        }

        return subject;
    }
    
    public static boolean updateSubject(int id, String timeStart, String timeEnd, String subject, String schedule, String section, String instructor) {
        Connection conn = Database.getConnection();
        PreparedStatement ps = null;

        try {
            // SQL query to update the subject with new details
            String query = "UPDATE subjects SET TimeStart = ?, TimeEnd = ?, Subject = ?, Schedule = ?, Section = ?, accountName = ? WHERE id = ?";
            ps = conn.prepareStatement(query);

            // Set the parameters for the prepared statement
            ps.setString(1, timeStart);
            ps.setString(2, timeEnd);
            ps.setString(3, subject);
            ps.setString(4, schedule);
            ps.setString(5, instructor);
            ps.setString(6, section);
            ps.setInt(7, id);

            // Execute the update query
            int updatedRows = ps.executeUpdate();

            if (updatedRows > 0) {
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No matching subject found to update.", "Update Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating subject: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
