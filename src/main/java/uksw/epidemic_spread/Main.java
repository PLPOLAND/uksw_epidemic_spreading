package uksw.epidemic_spread;

import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        
        
        Model m = new Model();

        Tools.hitAKkey("Hit ENTER, to START SYMULATION",true);
        
        while (true) {//TODO set correct end argument
            m.tic();
            Tools.pause(1);
        }


        // Tools.hitAKkey("Hit ENTER, to END program",true);
        // System.exit(0);
    }
    
}
