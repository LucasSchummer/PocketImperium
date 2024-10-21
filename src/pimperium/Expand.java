package pimperium;

import java.util.List;

public class Expand implements Command {
    private Player player;
    private Hexagon targetHexagon;
    private List<Ship> shipsInvolved;

    public Expand(Player player, Hexagon targetHexagon, List<Ship> shipsInvolved) {
        this.player = player;
        this.targetHexagon = targetHexagon;
        this.shipsInvolved = shipsInvolved;
    }

    @Override
    public void execute() {
        // Moving ships to a new hexagon
        System.out.println(player.getPseudo() + " is expanding to hex " + targetHexagon);

        // Add ships to hex
        for (Ship ship : shipsInvolved) {
            player.addShip(ship, targetHexagon);
        }
    }
}
