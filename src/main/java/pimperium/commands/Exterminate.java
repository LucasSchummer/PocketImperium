package pimperium.commands;

import java.util.List;

import pimperium.elements.Hexagon;
import pimperium.elements.Ship;
import pimperium.players.Player;

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


        // Faut déplacer les vaisseaux dans les hexagones cibles depuis les hexagones adjacents ?
        

        // Resolve the invasion
        for (Hexagon hex : targetHexagons) {
            Player defender = hex.getSystem().getController();
            List<Ship> defenderShips = hex.getShips();
            int smallestFleetSize = Math.min(shipsInvolved.size(), defenderShips.size());

            // Remove ships equal to the size of the smallest fleet
            for (int i = 0; i < smallestFleetSize; i++) {
            shipsInvolved.remove(0);
            defenderShips.remove(0);
            }

            // Determine the outcome of the invasion
            if (shipsInvolved.size() > 0 && defenderShips.size() == 0) {
            hex.getSystem().setController(player);
            hex.setShips(shipsInvolved);
            System.out.println(this.player + "has exterminate " + defender + "on the system " + hex.getSystem());
            } else if (shipsInvolved.size() == 0 && defenderShips.size() > 0) {
                System.out.println(defender.getPseudo() + " has defended!");
            } else {
            hex.getSystem().setController(null);
            }
        }
    }
}
