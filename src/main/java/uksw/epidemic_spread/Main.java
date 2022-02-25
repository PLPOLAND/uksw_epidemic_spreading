package uksw.epidemic_spread;

import java.io.IOException;
import java.util.Random;

import uksw.epidemic_spread.gnuplot.Counter;
import uksw.epidemic_spread.gnuplot.GnuplotPrint;


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
        m.preInfectArmyForExperiment();
        long start_time = System.currentTimeMillis();
        GnuplotPrint printer = new GnuplotPrint("tmp.txt");
        
        Counter counter = new Counter(m.army, start_time, printer);
        counter.start();
        
        while (System.currentTimeMillis()< Constants.TIME_OF_EXPERIMENT + start_time) {//TODO set correct end argument
            
            m.tic();
            Tools.pause(1);
        }

        counter.end();
        printer.close();
        printer.createPLT();

        Tools.hitAKkey("Experiment Ended! \nHit ENTER, to END program",true);
        System.exit(0);
    }
    
}
