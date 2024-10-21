package pimperium;

import java.util.List;

public class Explore implements Command {
    private Player player;
    private Hexagon targetHexagons;
    private List<Ship> shipsInvolved;

    public Explore(Player player, Hexagon targetHexagons, List<Ship> shipsInvolved) {
        this.player = player;
        this.targetHexagons = targetHexagons;
        this.shipsInvolved = shipsInvolved;
    }

    @Override
    public void execute() {
        // Logic for exploring (scouting or revealing information in the hex)
        System.out.println(player.getPseudo() + " is exploring hex " + targetHexagons);
        
         // Add the ships to the target hex 
         for (Ship ship : shipsInvolved) {
            ship.move(targetHexagons); 
        }
    }
}
