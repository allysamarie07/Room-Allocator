/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package jd.usbrfiddetector;

/**
 *
 * @author talai
 */
import java.util.Set;

public interface UsbEventListener {
    void onDeviceConnected(String id);
    void onDeviceDisconnected(String id);
    void onInitialScan(Set<String> existingIds);
}