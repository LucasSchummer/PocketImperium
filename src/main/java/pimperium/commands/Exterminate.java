package pimperium.commands;

import java.util.List;

import pimperium.elements.Hexagon;
import pimperium.elements.Ship;
import pimperium.players.Player;

public class Exterminate implements Command {
    private Player player;
    private Hexagon target;
    private List<Ship> shipsInvolved;

    public Exterminate(Player player) {
        this.player = player;
    }
    
    public void setShips(List<Ship> shipsInvolved) {
    	this.shipsInvolved = shipsInvolved;
    }
    
    public void setTarget(Hexagon target) {
    	this.target = target;
    }

    @Override
    public void execute() {

        List<Ship> attackingFleet = this.shipsInvolved;
        List<Ship> defendingFleet = this.target.getShips();

        while (!attackingFleet.isEmpty() && !defendingFleet.isEmpty()) {

            attackingFleet.getFirst().destroy();
            attackingFleet.removeLast();

            defendingFleet.getFirst().destroy();
            defendingFleet.removeLast();

        }

        // If the attacker won, move all his remainings ships to the target
        if (!attackingFleet.isEmpty()) {
            System.out.println(attackingFleet.getFirst().getOwner().getPseudo() + " defeated his opponent at " + target);
            for (Ship ship : attackingFleet) {
                ship.move(this.target);
            }
        }

    }
}
