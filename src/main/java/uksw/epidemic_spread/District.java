package uksw.epidemic_spread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import scala.language;

public class District {
    
    private Node target;
    private SingleGraph graph;
    private static int nextTargetID = 0;

    private static final Random random = new Random(System.currentTimeMillis());

    private double posX = 0;
    private double posY = 0;

    private int r = 0;
    private int g = 0;
    private int b = 0;

    private static int edgeGrayColor = random.nextInt(150);

    ArrayList<District> neigh;

    District(SingleGraph graph){
        neigh = new ArrayList<>();

        this.graph = graph;
        target = this.graph.addNode("T"+nextTargetID++);
        target.addAttribute("person", true);
        // target.setAttribute("ui.label", target.getId());
        int x, y; 
        x = random.nextInt(Constants.SIZE_OF_SCREAN - 1);
        y = random.nextInt(Constants.SIZE_OF_SCREAN - 1);

        while(!Tools.checkPosition(x, y, Constants.SIZE_OF_DISTRICT)){
            x = random.nextInt(Constants.SIZE_OF_SCREAN - 1);
            y = random.nextInt(Constants.SIZE_OF_SCREAN - 1);
        }
        posX = x;
        posY = y;
        target.setAttribute("x", x);
        target.setAttribute("y", y);
        target.setAttribute("z", 0);

        r = random.nextInt(255);
        g = random.nextInt(255);
        b = random.nextInt(255);

        if (Constants.NIGHT_MODE) {
            while (0.2126 * r + 0.7152 * g + 0.0722 * b < 50 ) {// to make colors darker
                r = random.nextInt(255);
                g = random.nextInt(255);
                b = random.nextInt(255);
            }
            while (edgeGrayColor < 50) {
                District.edgeGrayColor = random.nextInt(255);
            }
        } else {
            while (0.2126 * r + 0.7152 * g + 0.0722 * b > 200 ) {// to make colors darker
                r = random.nextInt(255);
                g = random.nextInt(255);
                b = random.nextInt(255);
            }
        }

        target.setAttribute("ui.style", "stroke-color: rgb("+r+","+ g+","+b+"); fill-color: rgba("+r+","+ g+","+b+",100);size: "+Constants.SIZE_OF_DISTRICT+
                                "; stroke-mode: plain; ");

    }


    public void connectTo(District t){
        if (!this.target.equals(t.getTarget())) {
            Edge e = graph.addEdge(target.getId()+"-"+t.getTarget().getId(), target, t.getTarget(), false);
            
            e.setAttribute("ui.style", "size : 0.5px; fill-color: rgba("+ edgeGrayColor+","+ edgeGrayColor+","+ edgeGrayColor+",100);");
            addToNeigh(t);
            t.addToNeigh(this);
        }
    }

    private void addToNeigh(District t) {
        if(!neigh.contains(t))
            neigh.add(t);
    }
    
    public boolean hasConnectionTo(District t){

        if (this.target.equals(t.getTarget())) {
            return true;
        }

        Iterator<Node> iterator = target.getNeighborNodeIterator();
        boolean hasConnection = false;
        while(iterator.hasNext()){
            Node n = iterator.next();
            if(t.getTarget().equals(n)){
                hasConnection = true;
                break;
            }

        }
        return hasConnection;
    }

    public Node getTarget() {
        return this.target;
    }

    public double getPosX() {
        return this.posX;
    }


    public double getPosY() {
        return this.posY;
    }

    public void setNewPos(double x, double y) {
        target.setAttribute("x", x);
        target.setAttribute("y", y);
        posX = x;
        posY = y;
    }

    public ArrayList<District> getNeigh() {
        return this.neigh;
    }

    public void update() {

        double[] tmp = Toolkit.nodePosition(graph, target.getId());
        posX = tmp[0];
        posY = tmp[1];
    }
    
}
