package uksw.epidemic_spread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;


public class District {
    
    private Node target;
    private SingleGraph graph;
    private static int nextTargetID = 0;

    private static final Random random = new Random(System.currentTimeMillis());

    private double posX = 0;
    private double posY = 0;

    //RGB color
    private int r = 0;
    private int g = 0;
    private int b = 0;

    /** Color of edge */
    private static int edgeGrayColor = random.nextInt(150);

    ArrayList<District> neigh;




    /**Soldiers in district */
    private ArrayList<Soldier> inDistrictSoldiers = new ArrayList<>();
    //For chacking 
    private ArrayList<Edge> inDistrictSoldiersEdges = new ArrayList<>();


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
            while (edgeGrayColor < 100) {
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


    /**
     * Adds edge to this district
     * @param dist
     */
    public void connectTo(District dist){
        if (!this.target.equals(dist.getTarget())) {
            Edge e = graph.addEdge(target.getId()+"-"+dist.getTarget().getId(), target, dist.getTarget(), false);
            
            e.setAttribute("ui.style", "size : 0.5px; fill-color: rgba("+ edgeGrayColor+","+ edgeGrayColor+","+ edgeGrayColor+",100);");
            addToNeigh(dist);
            dist.addToNeigh(this);
        }
    }

    /**
     * Adds district from param to container
     * @param dist district to add
     */
    private void addToNeigh(District dist) {
        if(!neigh.contains(dist))
            neigh.add(dist);
    }
    
    /**
     * Checks if there is edge to that district
     * @param dis district to check connection
     */
    public boolean hasConnectionTo(District dis){

        if (this.target.equals(dis.getTarget())) {
            return true;
        }

        Iterator<Node> iterator = target.getNeighborNodeIterator();
        boolean hasConnection = false;
        while(iterator.hasNext()){
            Node n = iterator.next();
            if(dis.getTarget().equals(n)){
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
    /**
     * Changing position of district
     * @param x x cord of position
     * @param y y cord of position
     */
    public void setNewPos(double x, double y) {
        target.setAttribute("x", x);
        target.setAttribute("y", y);
        posX = x;
        posY = y;
    }

    public ArrayList<District> getNeigh() {
        return this.neigh;
    }
    /**Updates position of target, after moving it by mouse */
    public void update() {

        double[] tmp = Toolkit.nodePosition(graph, target.getId());
        posX = tmp[0];
        posY = tmp[1];
    }
    
    public ArrayList<Soldier> getInTargetSoldiers() {
        return this.inDistrictSoldiers;
    }
    
    /**
     * Adds soldier to district, and connects it others
     * @param soldier
     */
    public void addSoldierToMe(Soldier soldier) {
        if (!inDistrictSoldiers.contains(soldier)) {
            inDistrictSoldiers.add(soldier);
        }
        // disconnetAllSoldiers();
        connectSoldiers();
    }


    /**
     * Removing soldier from district, and removes it's connects to others
     * @param soldier soldier to remove
     */
    public void removeSoldierFromMe(Soldier soldier){
        if (inDistrictSoldiers.contains(soldier)) {
            Edge tmpToDelete = null;
            for (Edge edge : inDistrictSoldiersEdges) {
                if (edge.getNode0().equals(soldier.getNode()) || edge.getNode1().equals(soldier.getNode())) {
                    if (graph.getEdgeSet().contains(edge)) {
                        try {
                           tmpToDelete = graph.removeEdge(edge.getId());
                            
                        } catch (ElementNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (tmpToDelete != null) {
                inDistrictSoldiersEdges.remove(tmpToDelete);
            }
            inDistrictSoldiers.remove(soldier);
        }

    }

    /**
     * Connecting all soldiers, but not that with "R" flag!
     */
    public void connectSoldiers(){
        for (Soldier soldier : inDistrictSoldiers) {
            for (Soldier soldier2 : inDistrictSoldiers) {
                try {
                    if(soldier.getStateOfIllnes().equals("I")&& !soldier2.getStateOfIllnes().equals("I")){
                        if (!soldier.equals(soldier2) && !soldier.getNode().getAttribute("sick", String.class).equals("R") && 
                                                        !soldier2.getNode().getAttribute("sick", String.class).equals("R")) {
                                Edge e = graph.addEdge(soldier.getNode().getId() + "_" + soldier2.getNode().getId(), soldier.getNode(), soldier2.getNode(), false);
                                e.addAttribute("ui.style", "z-index: 3;");
                                if (!inDistrictSoldiersEdges.contains(e)) {
                                    inDistrictSoldiersEdges.add(e);
                                }
                        }
                    }
                } catch (Exception e) {
                    //DO nothing, there is already the edge
                }
            }
        }
    }
}
