package pimperium;

import java.util.List;

public class Exterminate implements Command {
    private Player player;
    private Hexagon targetHexagons;
    //private List<Ship> shipsInvolved;

    public Exterminate(Player player, Hexagon targetHexagons, List<Ship> shipsInvolved) {
        this.player = player;
        this.targetHexagons = targetHexagons;
       //this.shipsInvolved = shipsInvolved;
    }

    @Override
    public void execute() {
        // Logic for exterminating (attacking enemies in the hex)
        System.out.println(player.getPseudo() + " is exterminating forces in hex " + targetHexagons);

        // TODO : Add exterminate logic
    }
}
