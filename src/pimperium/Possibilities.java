package pimperium;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import javafx.util.Pair;

public class Possibilities {

    private static Possibilities instance;
    private static Game game;

    private Possibilities(Game game) {
        Possibilities.game = game;
    }

    public static Possibilities getInstance(Game game) {
        if (instance == null) {
            instance = new Possibilities(game);
        }
        return instance;
    }

    public List<Hexagon> setupFleet() {
        List<Hexagon> hexs = new ArrayList<Hexagon>();
        for (Hexagon[] row : game.getMap()) {
            for (Hexagon hex : row) {
                if (hex != null && hex.getOccupant() == null && hex.getSystemLevel() == 1 && !game.findSector(hex).isOccupied()) {
                    hexs.add(hex);
                }
            }
        }
        return hexs;
    }

    public List<Ship> expand(Player player) {
        List<Ship> ships = new ArrayList<>();
        for (Hexagon[] row : game.getMap()) {
            for (Hexagon hex : row) {
                if (hex != null && hex.getOccupant() == player ) {
                    ships.addAll(hex.getShips());
                }
            }
        }
        return ships;
    }

    public List<Pair<Ship, Hexagon>> explore(Player player) {
        List<Pair<Ship, Hexagon>> possibleMoves = new ArrayList<>();
        //For each player's ship, get all the possible destinations
        for(Ship ship : player.getShips()) {

            Set<Hexagon> distance1Targets = ship.getPosition().getNeighbours();

            // Remove from the list of possible targets the hexs controlled by another player
            distance1Targets.removeIf(hex -> hex.getOccupant() != null && hex.getOccupant() != player);

            Set<Hexagon> distance2Targets = new HashSet<Hexagon>();

            //We add the 2nd degree neighbors for each hex except triPrime
            for (Hexagon hex : distance1Targets) {
                if (!hex.isTriPrime()) {
                    distance2Targets.addAll(hex.getNeighbours());
                }
            }

            //Get all the possible targets for the ship (and remove the hexs controlled by another player)
            distance1Targets.addAll(distance2Targets);
            distance1Targets.removeIf(hex -> hex.getOccupant() != null & hex.getOccupant() != player);

            //Add all the available moves for the ship as pairs
            for (Hexagon hex : distance1Targets) {
                possibleMoves.add(new Pair<>(ship, hex));
            }

        }
        return possibleMoves;
    }

}
