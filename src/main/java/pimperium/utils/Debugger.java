package pimperium.utils;

import javafx.util.Pair;
import pimperium.elements.Hexagon;
import pimperium.elements.Ship;
import pimperium.models.Game;
import pimperium.players.Player;

import java.util.List;
import java.util.Set;

/**
 * Class used only for debugging purposes, with display methods
 */
public class Debugger {

    /**
     * Display all the hexagons targetable with an Exterminate by a certain player
     * @param targets The list of targets
     * @param player The player performing the Exterminate
     */
    public static void displayTargets(List<Hexagon> targets, Player player) {
        System.out.println("Generating exterminate moves for " + player.getPseudo());
        System.out.println("List of possible targets : ");
        for (Hexagon hex : targets) {
            System.out.println(hex);
        }
    }

    /**
     * Display all possible distributions of ships
     * @param distributions The list of distribution created previously
     */
    public static void displayDistributions(List<int[]> distributions) {
        for (int[] distribution : distributions) {
            System.out.print("\n[ ");
            for (int i : distribution) {
                System.out.print(i + " ");
            }
            System.out.print("]");
        }
    }

    /**
     * Display the number of neighbors of each hexagon on the map
     * @param hexs The map
     */
    public static void displayAllNumberNeighbors(Hexagon[][] hexs) {
        for (Hexagon[] row : hexs) {
            for (Hexagon hex : row) {
                if (hex != null) {
                    System.out.println(hex + " : " + hex.getNeighbours().size() + " neighbors");
                }
            }
        }
    }

    /**
     * Display all possible Exterminate moves
     * @param moves The list of moves
     * @param player The player performing the move
     */
    public static void displayAllExterminateMoves(List<Pair<Set<Ship>, Hexagon>> moves, Player player) {

        System.out.println("Total number of moves : " + moves.size());
        for (Pair<Set<Ship>, Hexagon> move : moves) {
            displayExterminateMove(move);
            System.out.println();
        }
    }

    /**
     * Display all possible Exterminate moves
     * @param player The player performing the move
     * @param game the instance of Game
     */
    public static void displayAllExterminateMoves(Player player, Game game) {

        List<Pair<Set<Ship>, Hexagon>> moves = Possibilities.getInstance(game).exterminate(player);
        System.out.println("Total number of moves : " + moves.size());
        for (Pair<Set<Ship>, Hexagon> move : moves) {
            displayExterminateMove(move);
            System.out.println();
        }
    }

    /**
     * Display an Exterminate move in a comprehensible way
     * @param move The move to display
     */
    public static void displayExterminateMove(Pair<Set<Ship>, Hexagon> move) {
        System.out.println("System attacked : " + move.getValue());
        System.out.println("Attacking ships : ");
        for (Ship ship : move.getKey()) {
            System.out.println(ship);
        }
    }

    /**
     * Display all possible Explore moves
     * @param moves The list of possible moves
     */
    public static void displayAllExploreMoves(List<Pair<List<Ship>, List<Hexagon>>> moves) {
        System.out.println("Total number of moves : " + moves.size());
        for (Pair<List<Ship>, List<Hexagon>> move : moves) {
            displayExploreMove(move);
            System.out.println();
        }
    }

    /**
     * Display all possible moves
     * @param player The player performing the move
     * @param game The instance of Game
     */
    public static void displayAllExploreMoves(Player player, Game game) {

        List<Pair<List<Ship>, List<Hexagon>>> moves = Possibilities.getInstance(game).explore(player);

        System.out.println("Total number of moves : " + moves.size());
        for (Pair<List<Ship>, List<Hexagon>> move : moves) {
            displayExploreMove(move);
            System.out.println();
        }
    }

    /**
     * Display an Explore move in a comprehensible way
     * @param move The move to display
     */
    public static void displayExploreMove(Pair<List<Ship>, List<Hexagon>> move) {
        System.out.println("Num Ships : " + move.getKey().size());
        System.out.println("Num targets : " + move.getValue().size());
        for (int i = 0; i < move.getKey().size() ; i++) {
            System.out.println(move.getKey().get(i) + " to " + move.getValue().get(i));
        }
    }

}
