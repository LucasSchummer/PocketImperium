package pimperium.commands;

import java.util.List;
import java.util.Set;

import pimperium.elements.Ship;
import pimperium.players.Player;


public class Expand implements Command {
    private Player player;
    private List<Ship> shipsInvolved;

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
        }
    }
}
