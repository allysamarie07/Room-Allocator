/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jd.usbrfiddetector;

/**
 *
 * @author talai
 */
import org.usb4java.*;
import java.util.*;
import java.util.concurrent.*;

public class UsbRfidService {
    private final Context context = new Context();
    private volatile boolean running = false;
    private ExecutorService executor;
    private Set<String> previousDevices = new HashSet<>();
    private UsbEventListener listener;

    public UsbRfidService() {
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to initialize libusb.", result);
        }
    }

    public void startMonitoring(UsbEventListener listener) {
        if (running) return;
        this.listener = listener;
        running = true;
        executor = Executors.newSingleThreadExecutor();

        executor.submit(() -> {
            previousDevices = getDeviceIds();
            listener.onInitialScan(previousDevices); // Send current state

            while (running) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    break;
                }

                Set<String> currentDevices = getDeviceIds();

                for (String id : currentDevices) {
                    if (!previousDevices.contains(id)) {
                        listener.onDeviceConnected(id);
                    }
                }

                for (String id : previousDevices) {
                    if (!currentDevices.contains(id)) {
                        listener.onDeviceDisconnected(id);
                    }
                }

                previousDevices = currentDevices;
            }

            LibUsb.exit(context);
        });
    }

    public void stopMonitoring() {
        running = false;
        if (executor != null) executor.shutdownNow();
    }

    private Set<String> getDeviceIds() {
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
