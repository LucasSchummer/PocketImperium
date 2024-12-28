package pimperium.commands;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import pimperium.elements.Ship;
import pimperium.players.Player;


public class Expand implements Command, Serializable {

    private static final long serialVersionUID = 1L;
    private Player player;
    private List<Ship> shipsInvolved;

    //TODO Change the logic so that expands applies only on a ship instead of a list of ships
    public Expand(Player player) {
        this.player = player;      
    }
    
    public void setShips(List<Ship> shipsInvolved) {
    	this.shipsInvolved = shipsInvolved;
    }
    

    @Override
    public void execute() {

        // Add ships to hex
        for (int i=0; i<this.shipsInvolved.size(); i++) {
        	System.out.println(this.shipsInvolved.get(i) + " expanded");
            this.player.createShip(this.shipsInvolved.get(i).getPosition());
            shipsInvolved.get(i).setHasExpanded(true);
        }

        this.player.deleteExtraShips();

    }
}
