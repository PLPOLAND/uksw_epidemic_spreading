package uksw.epidemic_spread;

import java.io.IOException;
import java.util.Random;


public class Main {
    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        
        
        Model m = new Model();

        Tools.hitAKkey("Hit ENTER, to START SYMULATION",true);
        Random random = new Random(System.currentTimeMillis());
        while (true) {//TODO set correct end argument
            // double c = random.nextDouble();//For tests
            // if (c <0.05) {
            //     m.army.get(random.nextInt(Constants.SIZE_OF_ARMY)).makeRecovered();
            // }
            m.tic();
            Tools.pause(1);
        }


        // Tools.hitAKkey("Hit ENTER, to END program",true);
        // System.exit(0);
    }
    
}
