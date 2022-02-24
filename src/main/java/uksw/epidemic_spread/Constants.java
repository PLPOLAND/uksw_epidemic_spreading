package uksw.epidemic_spread;

public class Constants {

    private Constants(){}

    public static final long SECOND = 1000;
    public static final long MIN = 60 * SECOND;
    public static final long HOUR = 60 * MIN;
    

    public static final int SIZE_OF_SCREAN = 1000;//Don't make it to big, or, there will be some problems with showing Targets range! (deafult is 1000)
    /**Node size of soldier */
    public static final int SIZE_OF_SOLDIER = 6;
    /**Size of district */
    public static final int SIZE_OF_DISTRICT = 50;

    public static final boolean MANHATTAN_MOBILITY_MODEL = true;
    
    
    public static final int SIZE_OF_ARMY = 500;
    public static final int NUMBER_OF_DISTRICTS = 5;
    public static final int NUMBER_OF_DISTRICTS_IN_MANHATTAN = 10;// then number of districts is NUMBER_OF_DISTRICTS_IN_MANHATTAN ^ 2
    public static final int MAX_NUMBER_OF_CONNECTIONS_FROM_DISTRICT = 3; 
    
    /**Needed to keep the soldier stuck in place within the district*/
    public static final double XY_APROX = SIZE_OF_DISTRICT / 2.d;
    public static final double MAX_SPEED = 0.4; 
    public static final int MAX_STAY_TIME = 10 * (int)SECOND; //in millis


    public static final boolean SPAWN_ON_DISTRICT = true;
    
    public static final boolean NIGHT_MODE = false;
    
    public static final long I_DELAY_TIME = 5 * SECOND;     //d_start
    public static final long INFECTIOUS_TIME = 10 * SECOND;  //d_infectious
    public static final long RECOVER_TIME = 20 * SECOND;     // d_recovered
    public static final int SOLDIERS_TO_MAKE_SICK_ON_BEGIN_OF_SYM = 1;
    

    
}
