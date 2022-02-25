package uksw.epidemic_spread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.ui.view.Viewer;

import scala.collection.immutable.Stream.Cons;

/**
 * The class that is responsible for creating the experiment environment
 */
public class Model {
    /** Graph where the environment is shown */
    SingleGraph screen;
    /** Holds army participating in the experiment */
    List<Soldier> army = Collections.synchronizedList(new ArrayList<>());
    /** Holds districts of the experiment */
    List<District> districts = Collections.synchronizedList(new ArrayList<>());

    Random random = new Random(System.currentTimeMillis());
    /**Used to updete location of districted if moved*/
    ProxyPipe pipe;
    
    Model(){
        screen = new SingleGraph("EpidemicSpread");
        screen.addAttribute("ui.antialias");
        String shortStyle = "";
        if (Constants.NIGHT_MODE) {
             shortStyle = "graph {fill-color: #111111; " + "padding: 5px; }";
        }
        else{
            shortStyle = "graph {fill-color: #DDDDDD; " + "padding: 5px; }";
        }
        screen.addAttribute("ui.stylesheet", shortStyle);

        Viewer v =  screen.display(false);
        pipe = v.newViewerPipe();
        pipe.addAttributeSink(screen);

        Node[] corners = new Node[4];
        
        corners[0] = screen.addNode("LT");
        corners[0].addAttribute("x", 0);
        corners[0].addAttribute("y", Constants.SIZE_OF_SCREAN);
        corners[0].setAttribute("ui.style", "size:0px;");
        corners[1] = screen.addNode("RT");
        corners[1].addAttribute("x", Constants.SIZE_OF_SCREAN);
        corners[1].addAttribute("y", Constants.SIZE_OF_SCREAN);
        corners[1].setAttribute("ui.style", "size:0px;");
        corners[2] = screen.addNode("RB");
        corners[2].addAttribute("x", Constants.SIZE_OF_SCREAN);
        corners[2].addAttribute("y", 0);
        corners[2].setAttribute("ui.style", "size:0px;");
        corners[3] = screen.addNode("LB");
        corners[3].addAttribute("x", 0);
        corners[3].addAttribute("y", 0);
        corners[3].setAttribute("ui.style", "size:0px;");
        Edge e1 = screen.addEdge("LT-RT", corners[0], corners[1], false);
        Edge e2 = screen.addEdge("LT-LB", corners[0], corners[3], false);
        Edge e3 = screen.addEdge("RT-RB", corners[1], corners[2], false);
        Edge e4 = screen.addEdge("RB-LB", corners[2], corners[3], false);
        
        if (Constants.NIGHT_MODE) {
            String style = "fill-color: #EEEEEE;";
            e1.addAttribute("ui.style", style);
            e2.addAttribute("ui.style", style);
            e3.addAttribute("ui.style", style);
            e4.addAttribute("ui.style", style);
        } else {
            String style = "fill-color: #000000;";
            e1.addAttribute("ui.style", style);
            e2.addAttribute("ui.style", style);
            e3.addAttribute("ui.style", style);
            e4.addAttribute("ui.style", style);
        }

        if (Constants.AKA_MANHATTAN_MOBILITY_MODEL) {
            makeManhattanDistricts(Constants.NUMBER_OF_DISTRICTS_IN_MANHATTAN);
        }
        else{
            makeDistricts(Constants.NUMBER_OF_DISTRICTS);
            
        }

        // Tools.hitAKkey("Hit ENTER, to PUT the army", true); //TODO uncomment

        pipe.pump();
        
        for (District target : districts) {
            target.update();

        }
        for (Soldier person : army) {
            person.update();
        }
        makeTheArmyOfAsgard(Constants.SIZE_OF_ARMY);
        
        

    }


    public void preInfectArmyForExperiment() {
        for (int i = 0; i < Constants.SOLDIERS_TO_MAKE_SICK_ON_BEGIN_OF_SYM; i++) {
            army.get(random.nextInt(army.size())).mekePreinfected();
        }
    }

    /**
     * Creating districts and connects them
     * @param districtsNum numver of distrticts to create
     */
    public void makeDistricts(int districtsNum) {
        for (int i = 0; i < districtsNum; i++) {
            districts.add(new District(screen));
        }
        for (District target : districts) {
            int connetions = random.nextInt(Constants.MAX_NUMBER_OF_CONNECTIONS_FROM_DISTRICT);
            if (connetions == 0) {
                connetions+=2;
            }
            if(target.neigh.size()> Constants.MAX_NUMBER_OF_CONNECTIONS_FROM_DISTRICT){// To don't get to much connections
                continue;
            }

            ArrayList<District> tmpDistricts = new ArrayList<>(districts);
            for (int i = 0; i < connetions && target.neigh.size()< Constants.MAX_NUMBER_OF_CONNECTIONS_FROM_DISTRICT; i++) {
                try{
                    District districtToConnect = tmpDistricts.get(random.nextInt(tmpDistricts.size()));
                    boolean hasNoMore = false;
                    while (target.hasConnectionTo(districtToConnect) || districtToConnect.getNeigh().size()>=Constants.MAX_NUMBER_OF_CONNECTIONS_FROM_DISTRICT) {
                        if (tmpDistricts.isEmpty()) {
                            hasNoMore = true;
                            break;
                        }
                        int tmp = random.nextInt(tmpDistricts.size());
                        districtToConnect = tmpDistricts.get(tmp);  
                        tmpDistricts.remove(tmp);
                    }
                    if (!hasNoMore && districtToConnect.getNeigh().size()<Constants.MAX_NUMBER_OF_CONNECTIONS_FROM_DISTRICT) {
                        target.connectTo(districtToConnect);
                    }
                }
                catch(Exception e){
                    continue;
                }
            }

        }

    }

/**
     * Creating districts and connects them
     * @param districtsNum numver of distrticts to create
     */
    public void makeManhattanDistricts(int nSize) {
        int districtsNum = nSize * nSize;
        for (int i = 0; i < nSize; i++) {
            for (int j = 0; j < nSize; j++) {
                District tmp = new District(screen);
    
                int numZ = nSize*2;
                double z = Constants.SIZE_OF_SCREAN/ (double) numZ;
                if (z < Constants.SIZE_OF_DISTRICT) {
                    System.err.println("ERROR! Too much districts or size of districts are too big!");
                    System.exit(1);
                }
                double x = 0;
                double y = 0;

                x = z + i*2*z;
                y = z + j*2*z;

                tmp.setNewPos(x, y);
                districts.add(tmp);
                
            }
        }

        for (int i = 0; i < nSize; i++) {
            for (int j = 0; j < nSize; j++) {
                District tmp =districts.get(i*nSize + j);
                
                int x = 0;
                int y = 0;
                int index = 0;
                
                if (i - 1 >= 0) {
                    y = i-1;
                    x = j;
                    index = x + y*nSize;
                    District tmpTp = null;
                    if (j<= districts.size()) {
                        tmpTp = districts.get(index);
                        tmp.connectTo(tmpTp);
                    }
                }
                if (j - 1 >=0) {
                    y = i;
                    x = j - 1;
                    index = x + y*nSize;
                    District tmpTp = null;
                    if (j<= districts.size()) {
                        tmpTp = districts.get(index);
                        tmp.connectTo(tmpTp);
                    }
                }
                
            }
        }

    }

    /**
     * Creates the army to test on
     * @param size how many soldiers
     */
    void makeTheArmyOfAsgard(int size){
        for (int i = 0; i < size; i++) {
            army.add(new Soldier(screen,districts));
        }
    }
    /**
     * to make calculations, movment etc.
     */
    public void tic() {
        for (Soldier person : army) {
            person.tic();
        }
        update();
        
        //TODO add epidemic spread method!
        
    }
    /**
     * Updates Disticts location if moved.
     */
    public void update(){
        pipe.pump();
        for (District target : districts){
            target.update();
        }
        for (Soldier person : army) {
            person.update();
        }
        
    }
    
    /**
     * Updates Disticts location if moved. And move soldiers to new start location
     */
    public void updateBeforeStart(){
        try {
            pipe.blockingPump();
            for (District target : districts) {
                target.update();
            }
            for (Soldier person : army) {
                person.replaceToNewStartPosition();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
