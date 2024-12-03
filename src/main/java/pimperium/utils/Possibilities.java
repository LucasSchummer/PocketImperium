package pimperium.utils;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collections;

import javafx.util.Pair;

import pimperium.elements.Hexagon;
import pimperium.elements.Ship;
import pimperium.models.Game;
import pimperium.players.Player;

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

    public List<Pair<List<Ship>, List<Hexagon>>> explore(Player player) {

        // The possible moves are all the pairs (Fleet, Set of Destination)
        List<Pair<List<Ship>, List<Hexagon>>> possibleMoves = new ArrayList<>();

        Set<Hexagon> controlledHexs = new HashSet<Hexagon>();
        for (Ship ship : player.getShips()) {
            controlledHexs.add(ship.getPosition());
        }

        // For each controlled hex, we create all the possible moves starting from it
        for (Hexagon origin : controlledHexs) {

            // The amont of ships moving may vary from 1 to the size of the whole fleet
            // For each of these subsets, we generate all the possible moves
            for (int numShips = 1; numShips < origin.getShips().size()+1 ; numShips++) {
                List<Ship> fleet = new ArrayList<>(origin.getShips().subList(0, numShips));

                // Get all the direct neighbors of the origin
                Set<Hexagon> distance1Targets = origin.getNeighbours();
                // Remove from the list of possible targets the hexs controlled by another player
                distance1Targets.removeIf(hex -> hex.getOccupant() != null && hex.getOccupant() != player);

                for (Hexagon target1 : distance1Targets) {
                    // Add the simplest move (the whole fleet moves to the 1-hex away destination)
                    possibleMoves.add(new Pair<>(fleet, new ArrayList<>(Collections.nCopies(fleet.size(), target1))));

                    // Add all the distance-2 moves if the hex is not Tri-Prime
                    if (!target1.isTriPrime()) {

                        // Add to the fleet the ships located on the hex
                        fleet.addAll(target1.getShips());

                        Set<Hexagon> distance2Targets = target1.getNeighbours();
                        // Remove from the list of possible targets the hexs controlled by another player
                        distance2Targets.removeIf(hex -> hex.getOccupant() != null && hex.getOccupant() != player);

                        // Add all the possible moves for each destination
                        for (Hexagon target2 : distance2Targets) {
                            for (int numShipsDropped = 0; numShipsDropped < fleet.size(); numShipsDropped++) {
                                // The fleet goes to destination, except for numShipsDropped that stop halfway
                                List<Ship> subFleet2 = new ArrayList<>(fleet.subList(0, fleet.size() - numShipsDropped));
                                List<Ship> subFleet1 = new ArrayList<>(fleet.subList(fleet.size() - numShipsDropped, fleet.size()));
                                List<Hexagon> destination2 = new ArrayList<>(Collections.nCopies(subFleet2.size(), target2));
                                List<Hexagon> destination1 = new ArrayList<>(Collections.nCopies(subFleet1.size(), target1));

                                // Concatenate the fleets and destinations
                                subFleet1.addAll(subFleet2);
                                destination1.addAll(destination2);
                                possibleMoves.add(new Pair<>( subFleet1, destination1));
                            }
                        }
                    }

                }

            }

        }

        return possibleMoves;

    }

    public List<Pair<List<Ship>, Hexagon>> exterminate(Player player) {

        List<Pair<List<Ship>, Hexagon>> possibleMoves = new ArrayList<>();

        List<Hexagon> targets = new ArrayList<Hexagon>();
        for (Hexagon[] row : game.getMap()) {
            for (Hexagon hex : row) {
                if (hex != null && hex.getSystemLevel() >= 1 && hex.getOccupant() != player) {
                    targets.add(hex);
                }
            }
        }

        for (Hexagon target : targets) {
            List<Hexagon> origins = new ArrayList<Hexagon>();
            // Find all the hexs the player can attack from
            for (Hexagon hex : target.getNeighbours()) {
                if (hex.getOccupant() == player) origins.add(hex);
            }

            // Get the number of ships on each origin hex
            int[] fleetSizes = new int[origins.size()];
            for (int i = 0; i < origins.size(); i++) {
                fleetSizes[i] = origins.get(i).getShips().size();
            }

            // All the possible distributions of ships from the origin fleets
            List<int[]> distributions = new ArrayList<>();
            int numOrigins = origins.size();

            int[] distribution = new int[numOrigins];
            while (true) {
                distributions.add(distribution.clone());

                // Increment the distribution vector
                int i = 0;
                while (i < numOrigins && distribution[i] == fleetSizes[i]) {
                    distribution[i] = 0;
                    i++;
                }
                if (i == numOrigins) break; // We've exhausted all combinations
                distribution[i]++;
            }

            // Convert ship distributions to moves
            for (int[] dist : distributions) {
                List<Ship> fleet = new ArrayList<>();
                for (int i = 0; i < dist.length; i++) {
                    fleet.addAll(origins.get(i).getShips().subList(0, dist[i]));
                }
                possibleMoves.add(new Pair<>(fleet, target));
            }

        }

        return possibleMoves;

    }

}
