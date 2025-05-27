/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MODEL;

/**
 *
 * @author talai
 */
public class Subject {
    private int id;
    private String timeStart;
    private String timeEnd;
    private String name;
    private String schedule;
    private String section;
    private String instructor;

    public Subject(int id, String timeStart, String timeEnd, String name, String schedule, String section, String instructor) {
        this.id = id;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.name = name;
        this.schedule = schedule;
        this.section = section;
        this.instructor = instructor;
    }

    public int getId() { return id; }
    public String getTimeStart() { return timeStart; }
    public String getTimeEnd() { return timeEnd; }
    public String getName() { return name; }
    public String getSchedule() { return schedule; }
    public String getSection() { return section; }
    public String getInstructor() { return instructor; }
}
