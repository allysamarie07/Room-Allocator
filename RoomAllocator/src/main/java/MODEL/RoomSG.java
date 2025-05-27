/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MODEL;

/**
 *
 * @author talai
 */
public class RoomSG {
    private String building;
    private String roomNumber;
    private String airconditioned;
    private String board;
    private String capacity;
    private String type;

    public RoomSG(String building, String roomNumber, String airconditioned, String board, String capacity, String type) {
        this.building = building;
        this.roomNumber = roomNumber;
        this.airconditioned = airconditioned;
        this.board = board;
        this.capacity = capacity;
        this.type = type;
    }

    // Getters
    public String getBuilding() { return building; }
    public String getRoomNumber() { return roomNumber; }
    public String getAirconditioned() { return airconditioned; }
    public String getBoard() { return board; }
    public String getCapacity() { return capacity; }
    public String getType() { return type; }
}