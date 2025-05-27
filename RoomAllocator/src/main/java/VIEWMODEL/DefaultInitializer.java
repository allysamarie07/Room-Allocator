/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VIEWMODEL;

import VIEW.Splashscreen;
/**
 *
 * @author talai
 */
public class DefaultInitializer implements Initializer {
    @Override
    public void runInitialization(Splashscreen screen, String baseText) throws Exception {
        String[] dots = {"", ".", "..", "..."};
        int dotIndex = 0;

        for (int row = 0; row <= 100; row++) {
            screen.loadingnumber.setText(row + "%");
            screen.loadingprogress.setValue(row);

            if (row % 2 == 0) {
                screen.loadingLabel.setText(baseText + dots[dotIndex]);
                dotIndex = (dotIndex + 1) % dots.length;
            }

            Thread.sleep(25);
        }
    }
}
