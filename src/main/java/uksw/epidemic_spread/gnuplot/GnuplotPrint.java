package uksw.epidemic_spread.gnuplot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import uksw.epidemic_spread.Constants;

public class GnuplotPrint {

    PrintWriter writer;
    String fileName;
    public GnuplotPrint(File f){
        try {
            writer = new PrintWriter(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                writer =new PrintWriter(new File("gnuplot.txt"));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public GnuplotPrint(String s){
        fileName = s;
        try {
            writer = new PrintWriter(new File(s));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                writer = new PrintWriter(new File("gnuplot.txt"));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public void printHeader(String s) {
        if (s.equals("")) {
            writer.println("#Time_millis number_of_S number_of_I number_of_R"); 
        }
        else{
            writer.println(s);
        }
    }
    public void printLine(long time, int numberOfS, int numberOfI, int numberOfR) {
        writer.println(time +" " + numberOfS + " " + numberOfI + " " + numberOfR);
    }
    public void close() {
        writer.flush();
        writer.close();
    }
    /**
     * @warning call after close() method!
     * @param iterations
     */
    public void createPLT(){
        try {
            writer = new PrintWriter(new File("number_of_infected_plot.plt"));

            if (fileName.equals("")) {
                fileName = "gnuplotSourse.txt";
            }
            writer.println("set style data lines \n"
                +"set xrange [0:"+Constants.TIME_OF_EXPERIMENT/1000+"]\n"
                +"set yrange [0:"+Constants.SIZE_OF_ARMY+"]\n"
                +"set label 'Size of Army: "+Constants.SIZE_OF_ARMY+"' at " +10 + ", " +(Constants.SIZE_OF_ARMY/2 -20)+   "\n"
                +"set label 'dStart: "+Constants.I_DELAY_TIME+"(millis)' at " +10 + ", " +(Constants.SIZE_OF_ARMY/2 )+ "\n"
                +"set label 'dInfectious: "+Constants.INFECTIOUS_TIME+"(millis)' at " +10 + ", " + (Constants.SIZE_OF_ARMY/2 +20) +  "\n"
                +"set label 'dRecovered: "+Constants.RECOVER_TIME+"(millis)' at " +10 + ", " +(Constants.SIZE_OF_ARMY/2 +40) +   "\n"
                +"set xlabel \"time in seconds\"\n"
                +"set ylabel \"number\"\n"
                +"plot '"+fileName+"' using ($1/1000):2 with lines lc 2 lw 2 title \"Susceptible\" , '"+fileName+"' using ($1/1000):3 with lines lc rgb 'red' lw 2 title \"Infected\" , '"+fileName+"' using ($1/1000):4 with lines lc rgb '#AAAAFF' lw 2 title \"Recovered\" \n");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}