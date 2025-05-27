package VIEWMODEL;

import MODEL.AccountDAO;
import MODEL.RoomDAO;
import MODEL.Subject;
import MODEL.SubjectDAO;
import VIEW.AddUpdateSubject;
import MODEL.SectionDAO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

public class AddUpdateSubjectViewModel {

    public Subject getSubjectById(int id) {
        return SubjectDAO.getSubjectById(id);
    }

    public String formatTime(String time) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
            return outputFormat.format(inputFormat.parse(time));
        } catch (ParseException e) {
            return time;
        }
    }

    public List<String> getAllSections() {
        return SectionDAO.getAllSections();
    }

    public List<String> getAllInstructors(String accountName) {
        List<String> accounts = AccountDAO.getAllAccounts(accountName);
        return accounts.stream()
                .filter(a -> !a.equalsIgnoreCase(accountName))
                .collect(Collectors.toList());
    }

    public void populateInputs(int subjectId, AddUpdateSubject AUForm) {
        Subject subject = getSubjectById(subjectId);
        if (subject == null) {
            System.err.println("Subject not found for ID: " + subjectId);
            return;
        }

        AUForm.TimeStart.setText(formatTime(subject.getTimeStart()));
        AUForm.TimeEnd.setText(formatTime(subject.getTimeEnd()));
        AUForm.Subject.setText(subject.getName());
        AUForm.Schedule.setText(subject.getSchedule());
        AUForm.SectionName.setSelectedItem(subject.getSection());
        AUForm.Teacher.setSelectedItem(subject.getInstructor());
    }

    public void addSubject(AddUpdateSubject view, String buildingName, String roomNumber,
                            String timeStart, String timeEnd, String subject, String schedule,
                            String instructor, String section) {
        handleSaveOrUpdate(view, -1, buildingName, roomNumber, timeStart, timeEnd, subject, schedule, instructor, section, true);
    }

    public void updateSubject(AddUpdateSubject view, int id, String buildingName, String roomNumber,
                               String timeStart, String timeEnd, String subject, String schedule,
                               String instructor, String section) {
        handleSaveOrUpdate(view, id, buildingName, roomNumber, timeStart, timeEnd, subject, schedule, instructor, section, false);
    }

    private void handleSaveOrUpdate(AddUpdateSubject view, int id, String buildingName, String roomNumber,
                                    String timeStart, String timeEnd, String subject, String schedule,
                                    String instructor, String section, boolean isAdd) {

        if (timeStart.isEmpty() || timeEnd.isEmpty() || subject.isEmpty() || schedule.isEmpty() || section == null || section.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Fill-up all text fields!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int roomCapacity = RoomDAO.getRoomCapacity(buildingName, roomNumber);
        int sectionPopulation = SectionDAO.getSectionPopulation(section);

        
        if (sectionPopulation > roomCapacity) {
            JOptionPane.showMessageDialog(view,
                    "The section's population exceeds the room capacity! " +
                            "Room capacity: " + roomCapacity + ", Section population: " + sectionPopulation,
                    "Capacity Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.ENGLISH);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");

            LocalTime start = LocalTime.parse(timeStart, inputFormatter);
            LocalTime end = LocalTime.parse(timeEnd, inputFormatter);

            if (!isValidTimeInterval(start) || !isValidTimeInterval(end)) {
                JOptionPane.showMessageDialog(view, "Invalid time! Only 30-minute intervals are allowed (e.g., 12:00, 12:30).", "Time Format Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Enforce time boundaries
            LocalTime minStart = LocalTime.of(7, 30);  // 7:30 AM
            LocalTime maxEnd = LocalTime.of(22, 0);    // 10:00 PM

            if (start.isBefore(minStart)) {
                JOptionPane.showMessageDialog(view, "Time Start cannot be earlier than 7:30 AM.", "Time Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (end.isAfter(maxEnd)) {
                JOptionPane.showMessageDialog(view, "Time End cannot be later than 10:00 PM.", "Time Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (Duration.between(start, end).toMinutes() < 0) {
                JOptionPane.showMessageDialog(view, "Time Start must not be after Time End!", "Time Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            timeStart = start.format(outputFormatter);
            timeEnd = end.format(outputFormatter);

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(view, "Invalid time format! Use HH:mm AM/PM.", "Time Format Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidSchedule(schedule)) {
            JOptionPane.showMessageDialog(view, "Invalid schedule format! Use valid day abbreviations like M, TTH, MWF, etc.", "Schedule Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (SubjectDAO.hasConflict(id, buildingName, roomNumber, timeStart, timeEnd, schedule, instructor, subject, section)) {
            JOptionPane.showMessageDialog(view, "Conflict detected with another subject having the same schedule, time, and room.", "Conflict", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success;
        if (isAdd) {
            success = SubjectDAO.addSubject(buildingName, roomNumber, timeStart, timeEnd, subject, schedule, instructor, section);
        } else {
            success = SubjectDAO.updateSubject(id, timeStart, timeEnd, subject, schedule, instructor, section);
        }

        if (success) {
            JOptionPane.showMessageDialog(view, isAdd ? "Subject added successfully!" : "Subject updated successfully!");
            view.dispose();
        } else {
            JOptionPane.showMessageDialog(view, "Failed to " + (isAdd ? "add" : "update") + " subject. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidSchedule(String schedule) {
        String[] validDays = {"M", "T", "W", "TH", "F", "S"};
        List<String> dayList = Arrays.asList(validDays);
        Pattern pattern = Pattern.compile("TH|M|T|W|F|S");
        Matcher matcher = pattern.matcher(schedule);

        List<String> extractedDays = new ArrayList<>();
        int end = 0;

        while (matcher.find()) {
            if (matcher.start() != end) return false;
            extractedDays.add(matcher.group());
            end = matcher.end();
        }

        if (end != schedule.length()) return false;

        int lastIndex = -1;
        for (String day : extractedDays) {
            int currentIndex = dayList.indexOf(day);
            if (currentIndex <= lastIndex) return false;
            lastIndex = currentIndex;
        }

        return true;
    }

    private boolean isValidTimeInterval(LocalTime time) {
        int minutes = time.getMinute();
        return minutes == 0 || minutes == 30;
    }
}
