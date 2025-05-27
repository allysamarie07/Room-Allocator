/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package jd.roomallocator;

import VIEWMODEL.Initializer;
import VIEWMODEL.DefaultInitializer;
import VIEW.Splashscreen;
import VIEW.IndexView;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author talai
 */
public class RoomAllocator {

    public static void main(String[] args) {
        try {
            FlatMacDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize Look and Feel");
            ex.printStackTrace();
        }

        try {
            Splashscreen screen = new Splashscreen();
            screen.setVisible(true);

            Initializer initializer = new DefaultInitializer();
            initializer.runInitialization(screen, "STARTING UP");

            SwingUtilities.invokeLater(() -> {
                screen.setVisible(false);
                IndexView call = new IndexView("STUDENT", "STUDENT");
                call.setVisible(true);
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}
