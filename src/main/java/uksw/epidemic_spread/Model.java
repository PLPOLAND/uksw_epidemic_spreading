package uksw.epidemic_spread;

import java.util.ArrayList;
import java.util.Random;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.ui.view.Viewer;

public class Model {
    SingleGraph screen;
    ArrayList<Person> army = new ArrayList<>();
    ArrayList<Target> targets = new ArrayList<>();
    Random random = new Random(System.currentTimeMillis());
    ProxyPipe pipe;
    
    Model(){
        screen = new SingleGraph("EpidemicSpread");
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

        makeTargets(Constants.NUMBER_OF_TARGETS);

        Tools.hitAKkey("Hit ENTER, to PUT the army", true);

        pipe.pump();
        
        for (Target target : targets) {
            target.upgrade();

        }
        makeTheArmyOfAsgard(Constants.SIZE_OF_ARMY);

    }

    public void makeTargets(int targetNum) {
        for (int i = 0; i < targetNum; i++) {
            targets.add(new Target(screen));
        }

        for (Target target : targets) {
            int connetions = random.nextInt(Constants.MAX_NUMBER_OF_CONNECTIONS_FROM_TARGET);
            if (connetions == 0) {
                connetions++;
            }
            for (int i = 0; i < connetions; i++) {
                Target targetToConnect = targets.get(random.nextInt(targets.size()));
                while (target.hasConnectionTo(targetToConnect)) {
                    targetToConnect = targets.get(random.nextInt(targets.size()));
                }
                target.connectTo(targetToConnect);
            }

        }

    }

    void makeTheArmyOfAsgard(int size){
        for (int i = 0; i < size; i++) {
            army.add(new Person(screen,targets));
        }
    }

    public void tic() {
        pipe.pump();
        for (Person person : army) {
            person.tic();
        }
        for (Target target : targets){
            target.upgrade();

        }

        //TODO add epidemic spread method!

    }



}
