package uksw.epidemic_spread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
 * @author Marek Pałdyna
 * 
 * Class represents soldier
 * 
 */
public class Soldier {
    private Node me;
    private SingleGraph graph;
    private static int nextPersonID = 0;
    private static final Random random = new Random(System.currentTimeMillis());

    private double posX = 0;
    private double posY = 0;

    private double speed = 0;

    private ArrayList<District> targets = null;
    private District target;

    private double tmpTarX = 0;
    private double tmpTarY = 0;
    private double tmpTardX = 0;
    private double tmpTardY = 0;


    private int red = 0;
    private int green = 0;
    private int blue = 0;

    private boolean shouldBeOnTarget = false;
    private long timeToLeaveTargetField = System.currentTimeMillis();
    private long timeStartInfection = 0;
    private long timeRecovered = 0;

    private boolean preinfected = false;


    private ArrayList<Soldier> neighSoldiers = new ArrayList<>();
    private HashMap<Soldier,Long> neighSoldiersTime = new HashMap<>();

    long lastTickTime = 0;

    Soldier(SingleGraph graph, ArrayList<District> targets){
        this.graph = graph;
        this.targets = targets;
        target = targets.get(random.nextInt(targets.size()));

        speed = Constants.MAX_SPEED* random.nextDouble();
        if (speed<0.1) {
            speed = 0.1;
        }
        me = this.graph.addNode("P"+nextPersonID++);
        me.addAttribute("person", true);
        me.addAttribute("sick", "S");
        // me.addAttribute("ui.label", me.getId());
        
        int x=0, y=0; 
        if (Constants.SPAWN_ON_DISTRICT) {
            
            double r = Constants.SIZE_OF_DISTRICT * Math.sqrt(random.nextDouble()) * 0.95;
            double theta = random.nextDouble() * 2 * Math.PI;

            x += r * Math.cos(theta);
            y += r * Math.sin(theta);

        } else {
            x = random.nextInt(Constants.SIZE_OF_SCREAN - 1);
            y = random.nextInt(Constants.SIZE_OF_SCREAN - 1);
    
            while(!Tools.checkPosition(x, y)){
                x = random.nextInt(Constants.SIZE_OF_SCREAN - 1);
                y = random.nextInt(Constants.SIZE_OF_SCREAN - 1);
            }
        }

        posX = x;
        posY = y;
        me.setAttribute("x", x);
        me.setAttribute("y", y);
        // me.setAttribute("z", 10);


        int randNum = random.nextInt(target.getNeigh().size());
        this.target = target.getNeigh().get(randNum);

        red = random.nextInt(100);
        green = random.nextInt(255);
        blue = random.nextInt(255);
        if (Constants.NIGHT_MODE) {
            while (0.2126 * red + 0.7152 * green + 0.0722 * blue < 150 || (red>=blue && red>=green)) {// to make colors darker
                red = random.nextInt(100);
                green = random.nextInt(255);
                blue = random.nextInt(255);
            }
        }
        else{
            while (0.2126 * red + 0.7152 * green + 0.0722 * blue > 200 || (red >= blue && red >= green)) {// to make colors darker
                red = random.nextInt(100);
                green = random.nextInt(255);
                blue = random.nextInt(255);
            }
        }

        me.setAttribute("ui.style", "fill-color: rgb("+red+","+ green+","+blue+"); size: "+Constants.SIZE_OF_SOLDIER+";");
        lastTickTime = System.currentTimeMillis();
    }
    
    /**
     * It controls traffic and everything that needs to be done on a regular basis
     */
    public void tic(){
        long now = System.currentTimeMillis();
        calculateSick(now - lastTickTime);
        if(isOnTarget() ){
            String stateOfIllnes = me.getAttribute("sick");

            this.neighSoldiers = target.getInTargetSoldiers();

            if (timeToLeaveTargetField < now && !shouldBeOnTarget) {//start the counter
                timeToLeaveTargetField = now + random.nextInt(Constants.MAX_STAY_TIME);
                if (stateOfIllnes.equals("S")) {//only if Susceptible
                    me.setAttribute("sick", "E");
                }
                tmpTarX = target.getPosX();
                tmpTarY = target.getPosY();
                target.addSoldierToMe(this);
                shouldBeOnTarget = true;
            } 
            else if (timeToLeaveTargetField>= now ) {
                if ( Tools.calculateLength(posX, posY, tmpTarX + tmpTardX, tmpTarY + tmpTardY) < Constants.XY_APROX) {
                    //take new target point in target circle
                    int x =0 , y= 0;

                    double r = Constants.SIZE_OF_DISTRICT * Math.sqrt(random.nextDouble())* 0.95;
                    double theta = random.nextDouble() * 2 * Math.PI;

                    x += r * Math.cos(theta);
                    y += r * Math.sin(theta);


                    tmpTardX = x;
                    tmpTardY = y;
                }

                moveToTarget(tmpTarX+ tmpTardX, tmpTarY + tmpTardY);
            }
            // else if(timeToLeaveTargetField < now ){
            else {
                if (stateOfIllnes.equals("E")) {
                    me.setAttribute("sick", "S");
                }
                target.removeSoldierFromMe(this);
                this.neighSoldiersTime.clear();//remove all soldiers time 
                chooseNewDistrict();
            }
        }
        else{
            moveToTarget();
        }
        lastTickTime = now;
    }
    

    /** choosing new target disctrict */
    private void chooseNewDistrict() {
        int randNum = random.nextInt(target.getNeigh().size());
        this.target = target.getNeigh().get(randNum);

        tmpTarX = target.getPosX();
        tmpTarY = target.getPosY();
        this.shouldBeOnTarget = false;
        moveToTarget();
    }
    private void moveToTarget() {

        double dx = target.getPosX() - posX;
        double dy = target.getPosY() - posY;
        double length  = Tools.calculateLength(posX, posY, target.getPosX(), target.getPosY());
        
        dx /= length;
        dy /= length;
        dx *= speed;
        dy *= speed;

        move(posX + dx,posY +dy);


    }

    private void moveToTarget(double tarX, double tarY) {

        double dx = tarX - posX;
        double dy = tarY - posY;
        double length  = Tools.calculateLength(posX, posY, tarX, tarY);
        
        dx /= length;
        dy /= length;
        dx *= speed;
        dy *= speed;

        move(posX + dx,posY +dy);


    }

    private boolean isOnTarget(){
        if(Tools.calculateLength(posX, posY, target.getPosX(), target.getPosY()) < Constants.SIZE_OF_DISTRICT/2 ){
            return true;
        }
        return false;
    }

    private void move(double x, double y){
        if (Tools.checkPosition((int)x, (int)y)) {
            posX = x;
            me.setAttribute("x", x);
            posY = y;
            me.setAttribute("y", y);

            me.setAttribute("z", 10);   
        }
        else{
            //TODO ERROR
        }
    }
    /**Updates target district position */
    public void update() {
        this.tmpTarX = target.getPosX();
        this.tmpTarY = target.getPosY();
    }
    /**
     * Make new start position
     */
    public void replaceToNewStartPosition(){
        if (tmpTarX != target.getPosX() || tmpTarY != target.getPosY()) {
            this.tmpTarX = target.getPosX();
            this.tmpTarY = target.getPosY();
    
            double x = tmpTarX;
            double y = tmpTarY;
    
            double r = Constants.SIZE_OF_DISTRICT * Math.sqrt(random.nextDouble()) * 0.95;
            double theta = random.nextDouble() * 2 * Math.PI;
    
            x += r * Math.cos(theta);
            y += r * Math.sin(theta);
            
            posX = x;
            posY = y;
            me.setAttribute("x", x);
            me.setAttribute("y", y);
            me.setAttribute("z", 10);
        }
        //else do Nothing
    }

    public void mekePreinfected() {
        
        timeStartInfection = System.currentTimeMillis();
        preinfected = true;
        
        System.out.println(me.getId() + " made preinfected");
        me.addAttribute("ui.style", "fill-color: rgb(0,0,0);");
    }

    /**
     * Marking guy as I
     */
    private void makeInfected() {
        me.addAttribute("ui.style", "fill-color: rgb(255,0,0);");
        // me.addAttribute("ui.style", "fill-color: rgb(255,"+ g/3+","+b/3+");"); //if we want red to be not only one color
        me.addAttribute("sick", "I");
        System.out.println(me.getId() + " made I");
        preinfected = false;
        
    }
    /**
     * Marking guy as R
     */
    public void makeRecovered() {
        me.addAttribute("ui.style", "fill-color: rgb(250,250,250);");
        me.addAttribute("sick", "R");
        System.out.println(me.getId() + " made R");
        timeRecovered = System.currentTimeMillis();
    }
    /**
     * Marking guy as S
     */
    public void makeSusceptible() {
        me.addAttribute("ui.style", "fill-color: rgb(" + red + "," + green + "," + blue + ");");
        me.addAttribute("sick", "S");
        System.out.println(me.getId() + " made S");
    }
    /**
     * Marking guy as E
     */
    public void makeExposed() {
        me.addAttribute("ui.style", "fill-color: rgb(" + red + "," + green + "," + blue + ");");
        me.addAttribute("sick", "E");
    }

    private void calculateSick(long tickTime) {
        String stateOfSick = me.getAttribute("sick");
        long now = System.currentTimeMillis();
        if (this.preinfected && this.timeStartInfection + Constants.I_DELAY_TIME < now) {
            this.makeInfected();
        }
        else if(stateOfSick.equals("I") && this.timeStartInfection + Constants.I_DELAY_TIME+ Constants.INFECTIOUS_TIME < now){
            this.makeRecovered();
        }
        else if(stateOfSick.equals("R") && this.timeRecovered + Constants.RECOVER_TIME < now){
            if (this.isOnTarget()) {
                this.makeExposed();
            }
            else{
                this.makeSusceptible();
            }
        }

        if (stateOfSick.equals("I") && isOnTarget()) {

            removeOutSoldiersFromTime();

            if (!this.neighSoldiers.isEmpty()) {
                for (Soldier soldier : this.neighSoldiers) {
                    if (this.neighSoldiersTime.containsKey(soldier)) {
                        neighSoldiersTime.replace(soldier, neighSoldiersTime.get(soldier)+tickTime);
                        // double tmpT = neighSoldiersTime.get(soldier);
                        double tmpT = neighSoldiersTime.get(soldier)/(double)Constants.SECOND;
                        if (tmpT<1)
                            tmpT = 0d;
                        double probability = (1/Math.sqrt(tmpT));
                        double rand = random.nextDouble();
                        if (rand < probability && ((String)(soldier.getNode().getAttribute("sick"))).equals("E") && !soldier.preinfected) {
                                soldier.mekePreinfected();
                        }
                    }
                    else{
                        neighSoldiersTime.put(soldier, 0l);
                    }
                }
            }
        }

    }

    private void removeOutSoldiersFromTime() {
        HashMap<Soldier, Long> tmp = (HashMap<Soldier, Long>) this.neighSoldiersTime.clone();

        for (Soldier soldier : tmp.keySet()) {
            if (!this.neighSoldiers.contains(soldier)) {
                this.neighSoldiersTime.replace(soldier, 0l);
            }
        }

    }

    public Node getNode() {
        return this.me;
    }


}
