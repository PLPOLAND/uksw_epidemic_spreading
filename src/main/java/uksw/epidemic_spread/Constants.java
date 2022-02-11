package uksw.epidemic_spread;

public class Constants {

    private Constants(){}


    public static final int SIZE_OF_SCREAN = 1000;//Don't make it to big, or, there will be some problems with showing Targets range! (deafult is 1000)
    /**Node size of soldier */
    public static final int SIZE_OF_SOLDIER = 6;
    /**Size of district */
    public static final int SIZE_OF_DISTRICT = 70;
    
    
    public static final int SIZE_OF_ARMY = 1000;
    public static final int NUMBER_OF_DISTRICTS = 10;
    public static final int MAX_NUMBER_OF_CONNECTIONS_FROM_DISTRICT = 3; 
    
    /**Needed to keep the soldier stuck in place within the district*/
    public static final double XY_APROX = SIZE_OF_DISTRICT / 2.d;
    public static final double MAX_SPEED = 0.4; 
    public static final int MAX_STAY_TIME = 10000; //in millis


    public static final boolean SPAWN_ON_TARGETS = true;
    
    
}
