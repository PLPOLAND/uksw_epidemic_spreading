package uksw.epidemic_spread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
 * 
 * 
 * Class represents soldier
 * 
 */
public class Soldier {
    private Node me;
    private SingleGraph graph;
    private static int nextSoldierID = 0;//for having id of netx soldier
    private static final Random random = new Random(System.currentTimeMillis());

    /**Position of soldier in graph */
    private double posX = 0;
    /**Position of soldier in graph */
    private double posY = 0;

    /**Speed of soldier */
    private double speed = 0;

    /**Target district */
    private District target;

    //Temporary variables
    private double tmpTarX = 0;
    private double tmpTarY = 0;
    private double tmpTardX = 0;
    private double tmpTardY = 0;

    //RGB colors of node
    private int red = 0;
    private int green = 0;
    private int blue = 0;

    /**if soldier should stay on target */
    private boolean shouldBeOnTarget = false;

    /**For timming leave from target */
    private long timeToLeaveTargetDistrict = System.currentTimeMillis();
    /**For timming time of infection from target */
    private long timeStartInfection = 0;
    /**For timming time of recovered */
    private long timeRecovered = 0;

    /**Is  soldier preinfected*/
    private boolean isPreinfected = false;

    
    private ArrayList<Soldier> neighSoldiers = new ArrayList<>();
    /**For containing time of contact with neighbour */
    private HashMap<Soldier,Long> neighSoldiersTime = new HashMap<>();

    /**For calculating delay between "ticks" */
    long lastTickTime = 0;

    Soldier(SingleGraph graph, List<District> targets){
        this.graph = graph;
        target = targets.get(random.nextInt(targets.size()));

        speed = Constants.MAX_SPEED* random.nextDouble();
        if (speed<Constants.MIN_SPEED) {
            speed = Constants.MIN_SPEED;
        }
        me = this.graph.addNode("S"+nextSoldierID++);
        me.addAttribute("person", true);
        me.addAttribute("sick", "S");
        me.addAttribute("ui.label", me.getId());
        
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

        me.setAttribute("ui.style", "fill-color: rgb("+red+","+ green+","+blue+"); size: "+Constants.SIZE_OF_SOLDIER+"; z-index: 4;");
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

            if (timeToLeaveTargetDistrict < now && !shouldBeOnTarget) {//start the counter
                timeToLeaveTargetDistrict = now + random.nextInt(Constants.MAX_STAY_TIME);
                if (stateOfIllnes.equals("S")) {//only if Susceptible
                    me.setAttribute("sick", "E");
                }
                tmpTarX = target.getPosX();
                tmpTarY = target.getPosY();
                target.addSoldierToMe(this);
                shouldBeOnTarget = true;
            } 
            else if (timeToLeaveTargetDistrict>= now ) {
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
    /**Calculates movement to target (Target position is from target field of object) & move soldier */
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
    
    /**
     * Calculates movement to target (target position is given by argument) & move soldier
     * 
     * @param tarX X position of target 
     * @param tarY Y position of target 
     */
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
    
    /**
     * Checks if the Soldier is in the target district
     * 
     * @return boolean - true if distance from the target is smaller than Constants.SIZE_OF_DISTRICT/2
     */
    private boolean isOnTarget(){
        if(Tools.calculateLength(posX, posY, target.getPosX(), target.getPosY()) < Constants.SIZE_OF_DISTRICT/2 ){
            return true;
        }
        return false;
    }

    /**Changing position of Soldier */
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
        }
        //else do Nothing
    }

    /**
     * Makes soldier Preinfected
     */
    public void mekePreinfected() {
        
        timeStartInfection = System.currentTimeMillis();
        isPreinfected = true;
        
        System.out.println(me.getId() + " made preinfected");
        me.addAttribute("ui.style", "stroke-color: rgb(255,100,100); stroke-mode:plain;");
    }

    /**
     * Marking soldier as I
     */
    private void makeInfected() {
        me.addAttribute("ui.style", "fill-color: rgb(255,0,0); stroke-mode:none;");
        // me.addAttribute("ui.style", "fill-color: rgb(255,"+ g/3+","+b/3+");"); //if we want red to be not only one color
        me.addAttribute("sick", "I");
        System.out.println(me.getId() + " made I");
        isPreinfected = false;
        //remove edges to others soldiers
        Iterator<Node> tmp = me.getNeighborNodeIterator();
        ArrayList<Node> tmp2 = new ArrayList<Node>();
        while(tmp.hasNext()){
            tmp2.add(tmp.next());
        }
        for (int i = 0; i < tmp2.size(); i++) {
            graph.removeEdge(tmp2.get(i),me);
        }
    }
    /**
     * Marking soldier as R
     */
    public void makeRecovered() {
        me.addAttribute("ui.style", "fill-color: rgb(250,250,250);");
        me.addAttribute("sick", "R");
        System.out.println(me.getId() + " made R");
        timeRecovered = System.currentTimeMillis();
    }
    /**
     * Marking soldier as S
     */
    public void makeSusceptible() {
        me.addAttribute("ui.style", "fill-color: rgb(" + red + "," + green + "," + blue + ");");
        me.addAttribute("sick", "S");
        System.out.println(me.getId() + " made S");
    }
    /**
     * Marking soldier as E
     */
    public void makeExposed() {
        me.addAttribute("ui.style", "fill-color: rgb(" + red + "," + green + "," + blue + ");");
        me.addAttribute("sick", "E");
    }

    /**
     * Calculates everything what is related with sickness
     * @param tickTime delay time from last calculate
     */
    private void calculateSick(long tickTime) {
        String stateOfSick = me.getAttribute("sick");
        long now = System.currentTimeMillis();
        if (this.isPreinfected && this.timeStartInfection + Constants.I_DELAY_TIME < now) {
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
                        if (rand < probability && ((String)(soldier.getNode().getAttribute("sick"))).equals("E") && !soldier.isPreinfected) {
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
    /**
     * Sets "contact" time to "0" for soldiers which left district
     */
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

    public String getStateOfIllnes(){
        return (String) this.me.getAttribute("sick");
    }

}
