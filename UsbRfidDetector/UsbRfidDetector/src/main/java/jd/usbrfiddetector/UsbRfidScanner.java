/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jd.usbrfiddetector;

import org.usb4java.*;

public class UsbRfidScanner {

    public static boolean isRfidScannerConnected() {
        Context context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS) {
            return false;
        }

        DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(context, list);
        if (result < 0) {
            LibUsb.exit(context);
            return false;
        }

        boolean found = false;

        for (Device device : list) {
            DeviceDescriptor descriptor = new DeviceDescriptor();
            if (LibUsb.getDeviceDescriptor(device, descriptor) == LibUsb.SUCCESS) {
                // You can update these with your actual RFID scanner Vendor ID and Product ID
                int vendorId = descriptor.idVendor();
                int productId = descriptor.idProduct();
                System.out.printf("Found device: VID:%04x, PID:%04x%n", vendorId, productId);

                // Example condition: match known RFID scanner
                if (vendorId == 0xffffffff && productId == 0x0035) {
                    found = true;
                    break;
                }

                // If you don't know the exact RFID scanner IDs,
                // you can just return true if *any* USB device is present
                // or enhance detection later.
            }
        }

        LibUsb.freeDeviceList(list, true);
        LibUsb.exit(context);
        return found;
    }
}
