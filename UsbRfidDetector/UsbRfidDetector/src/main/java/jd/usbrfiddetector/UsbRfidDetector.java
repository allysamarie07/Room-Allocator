/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package jd.usbrfiddetector;

import org.usb4java.*;

import java.util.HashSet;
import java.util.Set;

public class UsbRfidDetector {

    public static void main(String[] args) throws InterruptedException {
        Context context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to initialize libusb.", result);
        }

        System.out.println("Monitoring USB devices for RFID scanner insert/remove...");

        // Initial scan and display of connected devices
        Set<String> previousDevices = getDeviceIds(context);
        for (String id : previousDevices) {
            System.out.println("USB RFID Scanner ALREADY CONNECTED (ON): " + id);
        }

        while (true) {
            Thread.sleep(1000); // Poll every 2 seconds

            Set<String> currentDevices = getDeviceIds(context);

            // Detect added devices
            for (String id : currentDevices) {
                if (!previousDevices.contains(id)) {
                    System.out.println("USB RFID Scanner DETECTED (ON): " + id);
                }
            }

            // Detect removed devices
            for (String id : previousDevices) {
                if (!currentDevices.contains(id)) {
                    System.out.println("USB RFID Scanner REMOVED (OFF): " + id);
                }
            }

            previousDevices = currentDevices;
        }
    }

    private static Set<String> getDeviceIds(Context context) {
        Set<String> ids = new HashSet<>();
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(context, list);
        if (result < 0) throw new LibUsbException("Unable to get device list", result);

        for (Device device : list) {
            DeviceDescriptor descriptor = new DeviceDescriptor();
            result = LibUsb.getDeviceDescriptor(device, descriptor);
            if (result != LibUsb.SUCCESS) continue;

            String id = String.format("VID:%04x PID:%04x",
                    descriptor.idVendor(), descriptor.idProduct());
            ids.add(id);
        }

        LibUsb.freeDeviceList(list, true);
        return ids;
    }
}

