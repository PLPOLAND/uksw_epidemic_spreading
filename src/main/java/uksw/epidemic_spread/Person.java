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


    ArrayList<Target> targets = null;
    Target target;

    Person(SingleGraph graph, ArrayList<Target> targets){
        this.graph = graph;
        this.targets = targets;
        target = targets.get(random.nextInt(targets.size()));

        me = this.graph.addNode("P"+nextPersonID++);
        me.addAttribute("person", true);
        
        int x=0, y=0; 
        if (Constants.SPAWN_ON_TARGETS) {
            
            x = (int)target.getPosX() ;
            int dx =(random.nextInt(Constants.SIZE_OF_TARGET *2)  ) - Constants.SIZE_OF_TARGET;
            x += dx;
            y = (int)target.getPosY();
            int dy = (random.nextInt(Constants.SIZE_OF_TARGET * 2) ) - Constants.SIZE_OF_TARGET;
            y+= dy;


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

        int r = random.nextInt(100);
        int g = random.nextInt(255);
        int b = random.nextInt(255);

        while (0.2126 * r + 0.7152 * g + 0.0722 * b > 200) {// to make colors darker
            r = random.nextInt(100);
            g = random.nextInt(255);
            b = random.nextInt(255);
        }

        me.setAttribute("ui.style", "fill-color: rgb("+r+","+ g+","+b+"); size: "+Constants.SIZE_OF_PERSON_NODE+";");

    }

    public void tic(){

        if(isOnTarget()){
            int randNum = random.nextInt(target.getNeigh().size());
            this.target = target.getNeigh().get(randNum);
            moveToTarget();
        }
        else{
            moveToTarget();
        }

    }
    private void moveToTarget() {

        double dx = target.getPosX() - posX;
        double dy = target.getPosY() - posY;
        double length  = Tools.calculateLength(posX, posY, target.getPosX(), target.getPosY());
        
        dx /= length;
        dy /= length;
        dx *= Constants.SPEED;
        dy *= Constants.SPEED;

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

    

}
