package uksw.epidemic_spread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class Target {
    
    private Node target;
    private SingleGraph graph;
    private static int nextTargetID = 0;

    private static final Random random = new Random(System.currentTimeMillis());

    private double posX = 0;
    private double posY = 0;

    ArrayList<Target> neigh;

    Target(SingleGraph graph){
        neigh = new ArrayList<>();

        this.graph = graph;
        target = this.graph.addNode("T"+nextTargetID++);
        target.addAttribute("person", true);
        int x, y; 
        x = random.nextInt(Constants.SIZE_OF_SCREAN - 1);
        y = random.nextInt(Constants.SIZE_OF_SCREAN - 1);

        while(!Tools.checkPosition(x, y)){
            x = random.nextInt(Constants.SIZE_OF_SCREAN - 1);
            y = random.nextInt(Constants.SIZE_OF_SCREAN - 1);
        }
        //TODO CHECK BORDER!
        posX = x;
        posY = y;
        target.setAttribute("x", x);
        target.setAttribute("y", y);

        int r = random.nextInt(100);
        int g = random.nextInt(255);
        int b = random.nextInt(255);

        while (0.2126 * r + 0.7152 * g + 0.0722 * b > 200) {// to make colors darker
            r = random.nextInt(100);
            g = random.nextInt(255);
            b = random.nextInt(255);
        }

        target.setAttribute("ui.style", "stroke-color: rgb("+r+","+ g+","+b+"); fill-color: rgba("+r+","+ g+","+b+",100);size: "+Constants.SIZE_OF_TARGET+
                                "; stroke-mode: plain; ");

    }

    public void connectTo(Target t){
        graph.addEdge(target.getId()+"-"+t.getTarget().getId(), target, t.getTarget(), false);
        addToNeigh(t);
        t.addToNeigh(this);
    }

    private void addToNeigh(Target t) {
        if(!neigh.contains(t))
            neigh.add(t);
    }
    
    public boolean hasConnectionTo(Target t){

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

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public ArrayList<Target> getNeigh() {
        return this.neigh;
    }

    public void upgrade() {

        double[] tmp = Toolkit.nodePosition(graph, target.getId());
        posX = tmp[0];
        posY = tmp[1];
    }
    
}
