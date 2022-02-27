package uksw.epidemic_spread;

public class Constants {

    private Constants(){}
    
    //For timing
    public static final long SECOND = 1000;
    public static final long MIN = 60 * SECOND;
    public static final long HOUR = 60 * MIN;
    
    /**Size of experiment in px*/
    public static final int SIZE_OF_SCREAN = 1000;//Don't make it to big, or, there will be some problems with showing Targets range! (deafult is 1000)
    /**Node size of soldier */
    public static final int SIZE_OF_SOLDIER = 6;
    /**Size of district */
    public static final int SIZE_OF_DISTRICT = 50;

    /**True makes districts placed in grid, false makes region Based Mobility model*/
    public static final boolean AKA_MANHATTAN_MOBILITY_MODEL = true;
    
    /**How many soldiers will be in experiment */
    public static final int SIZE_OF_ARMY = 500;
    /**How many districts will be placed in  Region Based Mobility model*/
    public static final int NUMBER_OF_DISTRICTS = 15;
    /**How many rows/columns will grid have */
    public static final int NUMBER_OF_DISTRICTS_IN_MANHATTAN = 10;// then number of districts is NUMBER_OF_DISTRICTS_IN_MANHATTAN ^ 2
    /**Max number of connections between districts will be.  */
    public static final int MAX_NUMBER_OF_CONNECTIONS_FROM_DISTRICT = 3; //Don't make it smaller than 3!
    
    /**Needed to keep the soldier stuck in place within the district*/
    public static final double XY_APROX = SIZE_OF_DISTRICT / 2.d;
    /**Max multiplayer of speed for Soldier */
    public static final double MAX_SPEED = 1.5; 
    /**Min multiplayer of speed for Soldier */
    public static final double MIN_SPEED = 0.5; 
    /**Max stay time of soldier in target district */
    public static final int MAX_STAY_TIME = 10 * (int)SECOND; //in millis

    /**If Soldiers at the begginning of experiment will be spawn in districts or not!*/
    public static final boolean SPAWN_ON_DISTRICT = true;
    
    /**d_start */
    public static final long I_DELAY_TIME = 10 * SECOND;     //d_start
    /**d_infectious */
    public static final long INFECTIOUS_TIME = 20 * SECOND;  //d_infectious
    /**d_recovered */
    public static final long RECOVER_TIME = 10 * SECOND;     // d_recovered
    /**How many of soldiers will be infected on start of symulation */
    public static final int SOLDIERS_TO_MAKE_SICK_ON_BEGIN_OF_SYM = 1;
    
    /**Time of Experiment */
    public static final long TIME_OF_EXPERIMENT = 5 * MIN;
    
    /**Changing colors from white to dark */
    public static final boolean NIGHT_MODE = false;
    
}
