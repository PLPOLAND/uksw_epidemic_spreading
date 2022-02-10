package uksw.epidemic_spread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;



import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
 * @author Marek Pałdyna
 * 
 * Class represents person
 * 
 */
public class Person {
    private Node me;
    private SingleGraph graph;
    private static int nextPersonID = 0;
    private static final Random random = new Random(System.currentTimeMillis());

    private double posX = 0;
    private double posY = 0;

    private double speed = 0;

    private ArrayList<Target> targets = null;
    private Target target;

    private double tmpTarX = 0;
    private double tmpTarY = 0;


    private int r = 0;
    private int g = 0;
    private int b = 0;

    private long timeToLeaveTargetField = System.currentTimeMillis();

    Person(SingleGraph graph, ArrayList<Target> targets){
        this.graph = graph;
        this.targets = targets;
        target = targets.get(random.nextInt(targets.size()));

        speed = Constants.SPEED* random.nextDouble();
        if (speed<0.1) {
            speed = 0.1;
        }
        me = this.graph.addNode("P"+nextPersonID++);
        me.addAttribute("person", true);
        me.addAttribute("sick", "S");
        
        int x=0, y=0; 
        if (Constants.SPAWN_ON_TARGETS) {
            
            x = (int)target.getPosX() ;
            int dx =(random.nextInt((int)(Constants.SIZE_OF_TARGET * 0.5) )* 2) - (int)(Constants.SIZE_OF_TARGET * 0.5);
            x += dx;
            y = (int)target.getPosY();
            int dy = (random.nextInt((int)(Constants.SIZE_OF_TARGET * 0.5) )* 2) - (int)(Constants.SIZE_OF_TARGET * 0.5);
            y+= dy;

            while (!Tools.checkPosition(x, y)) {
                x = (int) target.getPosX();
                dx = (random.nextInt((int)(Constants.SIZE_OF_TARGET * 0.5))* 2) - (int)(Constants.SIZE_OF_TARGET * 0.5);
                x += dx;
                y = (int) target.getPosY();
                dy = (random.nextInt((int)(Constants.SIZE_OF_TARGET * 0.5))* 2) - (int)(Constants.SIZE_OF_TARGET * 0.5);
                y += dy;
            }

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


        int randNum = random.nextInt(target.getNeigh().size());
        this.target = target.getNeigh().get(randNum);

        r = random.nextInt(100);
        g = random.nextInt(255);
        b = random.nextInt(255);

        while (0.2126 * r + 0.7152 * g + 0.0722 * b > 200) {// to make colors darker
            r = random.nextInt(100);
            g = random.nextInt(255);
            b = random.nextInt(255);
        }

        me.setAttribute("ui.style", "fill-color: rgb("+r+","+ g+","+b+"); size: "+Constants.SIZE_OF_PERSON_NODE+";");

    }

    public void tic(){

        if(isOnTarget() ){
            long now = System.currentTimeMillis();

            String stateOfIllnes = me.getAttribute("sick");

            if (timeToLeaveTargetField < now && !stateOfIllnes.equals("E")) {//start the counter
                timeToLeaveTargetField = now + random.nextInt(Constants.MAX_STAY_TIME);
                if (stateOfIllnes.equals("S")) {//only if Susceptible
                    me.setAttribute("sick", "E");
                }
            }
            else if (timeToLeaveTargetField>= now && !stateOfIllnes.equals("S")) {

                if (Tools.calculateLength(posX, posY, tmpTarX, tmpTarY) < Constants.XY_APROX) {//TODO not working as it should
                    //take new target point in target circle
                    int x =(int)target.getPosX() , y= (int)target.getPosY();
                    int dx = (random.nextInt((int) (Constants.SIZE_OF_TARGET * 0.5)) * 2)- (int) (Constants.SIZE_OF_TARGET * 0.5);
                    x += dx;
                    int dy = (random.nextInt((int) (Constants.SIZE_OF_TARGET * 0.5)) * 2)- (int) (Constants.SIZE_OF_TARGET * 0.5);
                    y += dy;

                    while (!Tools.checkPosition(x, y)) {
                        x = (int) target.getPosX();
                        dx = (random.nextInt((int) (Constants.SIZE_OF_TARGET * 0.5)) * 2)
                                - (int) (Constants.SIZE_OF_TARGET * 0.5);
                        x += dx;
                        y = (int) target.getPosY();
                        dy = (random.nextInt((int) (Constants.SIZE_OF_TARGET * 0.5)) * 2)
                                - (int) (Constants.SIZE_OF_TARGET * 0.5);
                        y += dy;
                    }
                    tmpTarX = x;
                    tmpTarY = y;
                }

                moveToTarget(tmpTarX, tmpTarY);
            }
            else if(timeToLeaveTargetField < now && !stateOfIllnes.equals("S")){
                if (stateOfIllnes.equals("E")) {
                    me.setAttribute("sic", "S");
                }
                chooseNewTarget();
            }
        }
        else{
            moveToTarget();
        }

    }

    private void chooseNewTarget() {
        int randNum = random.nextInt(target.getNeigh().size());
        this.target = target.getNeigh().get(randNum);
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
        if(Tools.calculateLength(posX, posY, target.getPosX(), target.getPosY()) < Constants.SIZE_OF_TARGET/2 ){
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
        }
        else{
            //TODO ERROR
        }
    }
    /**
     * Marking guy as I
     */
    public void makeInfected() {
        me.addAttribute("ui.style", "fill-color: rgb(255,"+ g/3+","+b/3+");");
        me.addAttribute("sick", "I");
    }
    /**
     * Marking guy as R
     */
    public void makeRecovered() {
        me.addAttribute("ui.style", "fill-color: rgb(180,180,180);");
        me.addAttribute("sick", "R");
    }
    /**
     * Marking guy as S
     */
    public void makeSusceptible() {
        me.addAttribute("ui.style", "fill-color: rgb(" + r + "," + g + "," + b + ");");
        me.addAttribute("sick", "S");
    }
    /**
     * Marking guy as E
     */
    public void makeExposed() {
        // me.addAttribute("ui.style", "fill-color: rgb(180,180,180);");
        me.addAttribute("sick", "E");
    }

    

}
