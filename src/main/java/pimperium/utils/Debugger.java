package pimperium.utils;

import javafx.util.Pair;
import pimperium.elements.Hexagon;
import pimperium.elements.Ship;
import pimperium.players.Player;

import java.util.List;

public class Debugger {

    public static void displayTargets(List<Hexagon> targets, Player player) {
        System.out.println("Generating exterminate moves for " + player.getPseudo());
        System.out.println("List of possible targets : ");
        for (Hexagon hex : targets) {
            System.out.println(hex);
        }
    }

    public static void displayAllNumberNeighbors(Hexagon[][] hexs) {
        for (Hexagon[] row : hexs) {
            for (Hexagon hex : row) {
                if (hex != null) {
                    System.out.println(hex + " : " + hex.getNeighbours().size() + " neighbors");
                }
            }
        }
    }

    public static void displayAllExterminateMoves(List<Pair<List<Ship>, Hexagon>> moves, Player player) {

        System.out.println("Total number of moves : " + moves.size());
        for (Pair<List<Ship>, Hexagon> move : moves) {
            displayExterminateMove(move);
            System.out.println();
        }
    }

    public static void displayExterminateMove(Pair<List<Ship>, Hexagon> move) {
        System.out.println("System attacked : " + move.getValue());
        System.out.println("Attacking ships : ");
        for (Ship ship : move.getKey()) {
            System.out.println(ship);
        }
    }

}
