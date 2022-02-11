package uksw.epidemic_spread;

import java.io.IOException;
import java.util.Random;


public class Main {
    static volatile boolean endStart = false;
    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        
        
        Model m = new Model();//init model
        
        // replace army when District moved by user.
        Thread t = new Thread(){
            @Override
            public void run(){
                while (!Main.endStart) {
                    m.updateBeforeStart();
                    Tools.pause(10);
                }
                System.out.println("ENDING THREAD");
            }
        };
        t.start();
        
        Tools.hitAKkey("Hit ENTER, to START SYMULATION",true);
        endStart = true;//end replacing!

        while (true) {//TODO set correct end argument
            // static Random random = new Random(System.currentTimeMillis());
            // double c = random.nextDouble();//For tests
            // if (c <0.05) {
            //     m.army.get(random.nextInt(Constants.SIZE_OF_ARMY)).makeRecovered();
            // }
            m.tic();
            // Tools.pause(1);
        }


        // Tools.hitAKkey("Hit ENTER, to END program",true);
        // System.exit(0);
    }
    
}
