package uksw.epidemic_spread.gnuplot;

import java.util.ArrayList;
import java.util.List;

import uksw.epidemic_spread.Soldier;
import uksw.epidemic_spread.Tools;

public class Counter extends Thread{
    
    private volatile List<Soldier> soldiers;

    private volatile boolean endFlag = false;
    private long startTime = 0;

    GnuplotPrint printer;
    public Counter(List<Soldier> sol,long startTime, GnuplotPrint print){
        this.soldiers = sol;
        printer = print;
        printer.printHeader("");
        this.startTime =startTime;
    }

    @Override
    public void run() {
        super.run();
        while (!endFlag) {
            int Rsol =0;
            int Ssol =0;
            int Isol =0;
    
            for (Soldier soldier : soldiers) {
                if (soldier.getStateOfIllnes().equals("R")) {
                    Rsol++;
                }
                if (soldier.getStateOfIllnes().equals("S") || soldier.getStateOfIllnes().equals("E")) {
                    Ssol++;
                }
                if (soldier.getStateOfIllnes().equals("I")) {
                    Isol++;
                }
            }
            printer.printLine(System.currentTimeMillis() - startTime, Ssol, Isol, Rsol);
            Tools.pause(100);
        }
        

    }

    public void end() {
        this.endFlag = true;
    }



}
