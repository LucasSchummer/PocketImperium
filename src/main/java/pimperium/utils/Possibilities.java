package pimperium.utils;

import java.io.Serializable;
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

/**
 * Singleton class used to generate all the possible moves for multiple actions
 */
public class Possibilities implements Serializable{

    protected static final long serialVersionUID = 1L;
    private static Possibilities instance;
    private static Game game;

    private Possibilities(Game game) {
        Possibilities.game = game;
    }

    /**
     * Return the instance of Possibilities according to the Singleton design pattern
     * @param game
     * @return
     */
    public static Possibilities getInstance(Game game) {
        if (instance == null) {
            instance = new Possibilities(game);
        }
        return instance;
    }

    /**
     * Generate all the possible hexagons for initial fleet setup
     * @return The list of available hexagons
     */
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

    /**
     * Generate all the possible Expand moves
     * @param player The player for which we generate the move
     * @return The list os possible moves as a list of Ships
     */
    public List<Ship> expand(Player player) {
        List<Ship> ships = new ArrayList<>();
        for (Hexagon[] row : game.getMap()) {
            for (Hexagon hex : row) {
                if (hex != null && hex.getOccupant() == player && hex.getSystemLevel() >= 1) {
                    ships.addAll(hex.getShips().stream()
                            .filter(ship -> !ship.hasExpanded()) // Condition: keep ships that have not expanded yet
                            .toList());
                }
            }
        }
        return ships;
    }

    /**
     * Generate all the possible Explore moves
     * @param player The player for which we generate the move
     * @return The list of possible moves
     */
    public List<Pair<List<Ship>, List<Hexagon>>> explore(Player player) {

        // The possible moves are all the pairs (Fleet, Set of Destination)
        List<Pair<List<Ship>, List<Hexagon>>> possibleMoves = new ArrayList<>();

        Set<Hexagon> controlledHexs = new HashSet<Hexagon>();
        List<Ship> usableShips = player.getShips().stream()
                .filter(ship -> !ship.hasExplored())
                .toList();
        for (Ship ship : usableShips) {
            controlledHexs.add(ship.getPosition());
        }

        // For each controlled hex, we create all the possible moves starting from it
        for (Hexagon origin : controlledHexs) {

            // The amont of ships moving may vary from 1 to the size of the whole fleet
            // For each of these subsets, we generate all the possible moves
            List<Ship> totalUsableFleet = origin.getShips().stream()
                    .filter(ship -> !ship.hasExplored())
                    .toList();

            for (int numShips = 1; numShips < totalUsableFleet.size()+1 ; numShips++) {
                List<Ship> fleet = new ArrayList<>(totalUsableFleet.subList(0, numShips));

                // Get all the direct neighbors of the origin
                Set<Hexagon> distance1Targets = new HashSet<>(origin.getNeighbours());
                // Remove from the list of possible targets the hexs controlled by another player
                distance1Targets.removeIf(hex -> hex.getOccupant() != null && hex.getOccupant() != player);

                for (Hexagon target1 : distance1Targets) {
                    // Add the simplest move (the whole fleet moves to the 1-hex away destination)
                    possibleMoves.add(new Pair<>(new ArrayList<>(fleet), new ArrayList<>(Collections.nCopies(fleet.size(), target1))));

                    // Add all the distance-2 moves if the hex is not Tri-Prime
                    if (!target1.isTriPrime()) {

                        // Add to the fleet the ships located on the hex
                        //fleet.addAll(target1.getShips());
                        List<Ship> extendedFleet = new ArrayList<>(fleet);
                        extendedFleet.addAll(target1.getShips());

                        Set<Hexagon> distance2Targets = new HashSet<>(target1.getNeighbours());

                        // Remove from the list of possible targets the hexs controlled by another player
                        distance2Targets.removeIf(hex -> hex.getOccupant() != null && hex.getOccupant() != player);
                        distance2Targets.removeIf(hex -> hex == origin);

                        // Add all the possible moves for each destination
                        for (Hexagon target2 : distance2Targets) {
                            for (int numShipsDropped = 0; numShipsDropped < extendedFleet.size(); numShipsDropped++) {
                                // The fleet goes to destination, except for numShipsDropped that stop halfway
                                List<Ship> subFleet2 = new ArrayList<>(extendedFleet.subList(0, extendedFleet.size() - numShipsDropped));
                                List<Ship> subFleet1 = new ArrayList<>(extendedFleet.subList(extendedFleet.size() - numShipsDropped, extendedFleet.size()));
                                List<Hexagon> destination2 = new ArrayList<>(Collections.nCopies(subFleet2.size(), target2));
                                List<Hexagon> destination1 = new ArrayList<>(Collections.nCopies(subFleet1.size(), target1));

                                // Create deep copies for the concatenated lists
                                List<Ship> fullFleet = new ArrayList<>(subFleet1); // Deep copy of subFleet1
                                fullFleet.addAll(subFleet2); // Add subFleet2

                                List<Hexagon> fullDestinations = new ArrayList<>(destination1); // Deep copy of destination1
                                fullDestinations.addAll(destination2); // Add destination2

                                possibleMoves.add(new Pair<>( fullFleet, fullDestinations));

                            }
                        }
                    }

                }

            }

        }

        // Just for debugging
        for (Pair<List<Ship>, List<Hexagon>> move : possibleMoves) {
            if (move.getKey().size() != move.getValue().size()) {
                System.out.println("Warning !!");
                for (Ship ship : move.getKey()) {
                    System.out.println(ship);
                }
                for (Hexagon hex : move.getValue()) {
                    System.out.println(hex);
                }
            }
        }

        return possibleMoves;

    }

    /**
     * Generate all the possible Exterminate moves
     * @param player The player for which we generate the move
     * @return The list of possible moves
     */
    public List<Pair<Set<Ship>, Hexagon>> exterminate(Player player) {

        List<Pair<Set<Ship>, Hexagon>> possibleMoves = new ArrayList<>();

        Set<Hexagon> targets = new HashSet<Hexagon>();
        for (Hexagon[] row : game.getMap()) {
            for (Hexagon hex : row) {
                if (hex != null && hex.getSystemLevel() >= 1 && hex.getOccupant() != player) {
                    targets.add(hex);
                }
            }
        }


        //Debugger.displayTargets(targets, player);

        for (Hexagon target : targets) {
            List<Hexagon> origins = new ArrayList<Hexagon>();
            // Find all the hexs the player can attack from
            for (Hexagon hex : target.getNeighbours()) {
                boolean isOccupant = hex.getOccupant() == player;
                List<Ship> usableShips = hex.getShips().stream()
                        .filter(ship -> !ship.hasExterminated())
                        .toList();
                boolean hasUsableShips = !usableShips.isEmpty();
                if (isOccupant && hasUsableShips) {
                    origins.add(hex);
                }
            }

            if (origins.isEmpty()) continue;

            // Get the number of ships on each origin hex
            int[] fleetSizes = new int[origins.size()];
            for (int i = 0; i < origins.size(); i++) {
                //fleetSizes[i] = origins.get(i).getShips().size();
                fleetSizes[i] = (int) origins.get(i).getShips().stream()
                        .filter(ship -> !ship.hasExterminated())
                        .count();
            }

            // All the possible distributions of ships from the origin fleets
            List<int[]> distributions = new ArrayList<>();
            int numOrigins = origins.size();

            int[] distribution = new int[numOrigins];

            boolean done = false;
            while (!done) {
                distributions.add(distribution.clone()); // Add a copy of the current distribution
                // Increment the distribution vector
                int i = 0;
                while (i < numOrigins) {
                    distribution[i]++;
                    if (distribution[i] <= fleetSizes[i]) {
                        break; // Valid increment; exit inner loop
                    } else {
                        distribution[i] = 0; // Reset current position
                        i++; // Carry over to the next position
                    }
                }

                if (i == numOrigins) {
                    done = true; // We've exhausted all combinations
                }
            }

/*            System.out.println("Distributions for target " + target);
            Debugger.displayDistributions(distributions);*/

            // Convert ship distributions to moves
            for (int[] dist : distributions) {
                Set<Ship> fleet = new HashSet<>();
                for (int i = 0; i < dist.length; i++) {
                    List<Ship> usableShips = origins.get(i).getShips().stream()
                            .filter(ship -> !ship.hasExterminated())
                            .toList();
                    //fleet.addAll(new ArrayList<>(origins.get(i).getShips().subList(0, dist[i])));
                    fleet.addAll(new ArrayList<>(usableShips.subList(0, dist[i])));
                }
                // Make sure the fleet is not empty (possible considering how we generate the distributions)
                if (!fleet.isEmpty()) {
                    possibleMoves.add(new Pair<>(new HashSet<>(fleet), target));
                }
            }

        }

        return possibleMoves;

    }

}
