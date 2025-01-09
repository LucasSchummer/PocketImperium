package pimperium.commands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import pimperium.elements.Hexagon;
import pimperium.elements.Ship;
import pimperium.players.Player;

/**
 * Command card belonging to a player that enables him to perform Exterminate
 */
public class Exterminate implements Command, Serializable {

    private static final long serialVersionUID = 1L;
    private Player player;
    private Hexagon target;
    private Set<Ship> shipsInvolved;

    public Exterminate(Player player) {
        this.player = player;
    }

    /**
     * Set the fleet concerned by the Exterminate move
     * @param shipsInvolved List of ships attacking
     */
    public void setShips(Set<Ship> shipsInvolved) {
    	this.shipsInvolved = shipsInvolved;
    }

    /**
     * Set the target hexagon of the Exterminate move
     *
     * @param target The targeted hexagon containing a system
     */
    public void setTarget(Hexagon target) {
    	this.target = target;
    }

    /**
     * Execute the Exterminate move (Destroy ships in attacking and defending fleet and possibly moving the remaining ships to the conquered hexagon)
     */
    public void execute() {

        List<Ship> attackingFleet = new ArrayList<>(this.shipsInvolved);
        List<Ship> defendingFleet = new ArrayList<>(this.target.getShips());

        if (this.target.getOccupant() != null) {
            System.out.println(
                    attackingFleet.getFirst().getOwner().getPseudo() + " is fighting "
                    + this.target.getOccupant().getPseudo()
                    + " at " + this.target
            );
            //game.getController().getView().addLogMessage("Combat entre " + attackingFleet.getFirst().getOwner().getPseudo() + " et " + this.target.getOccupant().getPseudo() + " sur " + target, null, "normal");
        }

        while (!attackingFleet.isEmpty() && !defendingFleet.isEmpty()) {

            attackingFleet.getLast().destroy();
            attackingFleet.removeLast();

            defendingFleet.getLast().destroy();
            defendingFleet.removeLast();

        }

        // If the attacker won, move all his remainings ships to the target
        if (!attackingFleet.isEmpty()) {
            System.out.println(attackingFleet.getFirst().getOwner().getPseudo() + " took control of " + target);
            //game.getController().getView().addLogMessage("A pris contrôle de " + target, attackingFleet.getFirst().getOwner(), "normal");
            for (Ship ship : attackingFleet) {
                ship.move(this.target);
                ship.setHasExterminated(true);
            }
        }

    }
}
