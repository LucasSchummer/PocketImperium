package pimperium;

import java.util.List;
import java.util.ListResourceBundle;

public class Exterminate implements Command {
    private Player player;
    private List<Hexagon> targetHexagons;
    private List<Ship> shipsInvolved;

    public Exterminate(Player player) {
        this.player = player;
    }
    
    public void setShips(List<Ship> shipsInvolved) {
    	this.shipsInvolved = shipsInvolved;
    }
    
    public void setTargets(List<Hexagon> targetHexagons) {
    	this.targetHexagons = targetHexagons;
    }

    @Override
    public void execute() {
        // Logic for exterminating (attacking enemies in the hex)
        //System.out.println(player.getPseudo() + " is exterminating forces in hex " + targetHexagons);

        // TODO : Add exterminate logic
    }
}
