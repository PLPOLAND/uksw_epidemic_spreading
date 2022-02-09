package uksw.epidemic_spread;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;


public class Tools {
 
    private Tools(){}
    
    /**
     * Pause thread for time in millis
     * 
     * @param time time in millis
     */
    public static void pause(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
   

    
    /**
     * Stops program, and waits for click
     * 
     */
    public static void hitAKey() {
        hitAKkey("",false);
    }
    /**
     * Stops program, and waits for click
     * 
     * @param msg message to show on the screen
     */
    public static void hitAKkey(String msg, boolean print) {
        if (print) {
            System.out.println();
            for (int i = 0; i < msg.length() + 15; i++) {
                System.out.print("-");
            }
            System.out.println("\n\t" + msg);
            for (int i = 0; i < msg.length() + 15; i++) {
                System.out.print("-");
            }
        }
        try {
            System.in.read();
            while (System.in.available() != 0) {
                System.in.read();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static double calculateLength(double x1,double y1, double x2, double y2) {
        
        double xL = Math.abs(x1 - x2);
        double yL = Math.abs(y1 - y2);

        return Math.sqrt((xL*xL)+(yL*yL));

    }

    public static boolean checkPosition(int x, int y) {

        if (x >= Constants.SIZE_OF_SCREAN || x <= 0 || y >= Constants.SIZE_OF_SCREAN || y <= 0) {
            return false;
        } else {
            return true;
        }
    }
}
