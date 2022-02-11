package uksw.epidemic_spread;

import java.util.ArrayList;
import java.util.Random;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.ui.view.Viewer;

/**
 * The class that is responsible for creating the experiment environment
 */
public class Model {
    /** Graph where the environment is shown */
    SingleGraph screen;
    /** Holds army participating in the experiment */
    ArrayList<Soldier> army = new ArrayList<>();
    /** Holds districts of the experiment */
    ArrayList<District> districts = new ArrayList<>();

    Random random = new Random(System.currentTimeMillis());
    /**Used to updete location of districted if moved*/
    ProxyPipe pipe;
    
    Model(){
        screen = new SingleGraph("EpidemicSpread");
        screen.addAttribute("ui.antialias");

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

        screen.addEdge("LT-RT", corners[0], corners[1], false);
        screen.addEdge("LT-LB", corners[0], corners[3], false);
        screen.addEdge("RT-RB", corners[1], corners[2], false);
        screen.addEdge("RB-LB", corners[2], corners[3], false);

        makeDistricts(Constants.NUMBER_OF_DISTRICTS);

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
            for (int i = 0; i < connetions; i++) {
                try{
                    District districtToConnect = tmpDistricts.get(random.nextInt(tmpDistricts.size()));
                    boolean hasNoMore = false;
                    while (target.hasConnectionTo(districtToConnect)) {
                        if (tmpDistricts.isEmpty()) {
                            hasNoMore = true;
                            break;
                        }
                        int tmp = random.nextInt(tmpDistricts.size());
                        districtToConnect = tmpDistricts.get(tmp);  
                        tmpDistricts.remove(tmp);
                    }
                    if (!hasNoMore) {
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
