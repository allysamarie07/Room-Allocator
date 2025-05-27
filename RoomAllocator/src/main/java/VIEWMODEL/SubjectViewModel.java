/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VIEWMODEL;

import MODEL.AccountDAO;
import MODEL.Subject;
import MODEL.SubjectDAO;
import MODEL.SectionDAO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author talai
 */
import javax.swing.table.DefaultTableModel;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SubjectViewModel {
    public final String building;
    public final String roomNumber;
    private final String accountName;
    private final String accountType;

    public SubjectViewModel(String building, String roomNumber, String accountName, String accountType) {
        this.building = building;
        this.roomNumber = roomNumber;
        this.accountName = accountName;
        this.accountType = accountType;
    }

    public List<Subject> getFilteredSubjects(String selectedSchedule) {
        List<Object[]> data = SubjectDAO.getSubjects(building, roomNumber);

        return data.stream()
            .filter(s -> "ALL".equals(selectedSchedule) || s[4].toString().contains(selectedSchedule))
            .sorted(Comparator.comparing(s -> (String) s[1]))
            .map(s -> new Subject(
                (int) s[0],
                (String) s[1],
                (String) s[2],
                (String) s[3],
                (String) s[4],
                (String) s[5],
                (String) s[6]
            )).collect(Collectors.toList());
    }

    public boolean canEditSubject(Subject subject) {
        if ("Faculty".equals(accountType)) {
            return accountName.equals(subject.getInstructor());
        }
        return true;
    }

    public boolean deleteSubject(int id) {
        return SubjectDAO.deleteSubjectById(id, accountName);
    }

    public String getAccountName() {
        return accountName;
    }
    
}