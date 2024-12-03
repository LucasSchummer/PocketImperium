/*
package pimperium.players;

import java.util.*;
import javafx.util.Pair;
import pimperium.elements.Hexagon;
import pimperium.elements.Ship;
import pimperium.models.Game;

public class OffensiveBot extends Bot {

    public OffensiveBot(Game game) {
        super(game);
    }

    @Override
    public void chooseOrderCommands() {
        // Priorité : Exterminate, Expand, Explore
        this.orderCommands = new int[]{2, 0, 1};
    }

    @Override
    public void doExterminate(int efficiency) {
        System.out.println(this.getPseudo() + " utilise une stratégie offensive avancée pour exterminer.");

        List<Pair<List<Ship>, Hexagon>> possibleMoves = possibilities.exterminate(this);
        List<Pair<List<Ship>, Hexagon>> prioritizedMoves = prioritizeExtermination(possibleMoves);

        List<Pair<List<Ship>, Hexagon>> moves = new ArrayList<>();
        Set<Hexagon> targets = new HashSet<>();

        for (Pair<List<Ship>, Hexagon> move : prioritizedMoves) {
            if (moves.size() >= efficiency) break;
            if (!targets.contains(move.getValue())) {
                moves.add(move);
                targets.add(move.getValue());
            }
        }

        for (Pair<List<Ship>, Hexagon> move : moves) {
            this.exterminate.setShips(move.getKey());
            this.exterminate.setTargets(Collections.singletonList(move.getValue()));
            this.exterminate.execute();
        }
    }

    */
/**
     * Priorise les cibles en fonction de leur vulnérabilité et importance stratégique.
     *//*

    private List<Pair<List<Ship>, Hexagon>> prioritizeExtermination(List<Pair<List<Ship>, Hexagon>> possibleMoves) {
        // Exemple d'évaluation basée sur le nombre de navires ennemis et le contrôle sectoriel
        possibleMoves.sort((move1, move2) -> {
            int score1 = evaluateHexagon(move1.getValue());
            int score2 = evaluateHexagon(move2.getValue());
            return Integer.compare(score2, score1); // Descendant
        });
        return possibleMoves;
    }

    */
/**
     * Évalue un hexagone en fonction de critères définis.
     *//*

    private int evaluateHexagon(Hexagon hex) {
        int score = 0;
        Player controller = hex.getSystem().getController();

        if (controller == null) {
            score += 5; // Hexagone non contrôlé
        } else {
            score += 10 - controller.countShips(); // Moins de navires, plus c'est vulnérable
            if (hex.getSystem().isStrategic()) {
                score += 5; // Importance stratégique
            }
        }

        return score;
    }

    @Override
    public void doExpand(int efficiency) {
        System.out.println(this.getPseudo() + " étend ses territoires de manière stratégique.");

        List<Ship> possShips = possibilities.expand(this);
        List<Ship> expandShips = new ArrayList<>();

        // Prioriser les navires déjà proches des frontières
        possShips.sort(Comparator.comparingInt(ship -> distanceToNearestEnemy(ship)));

        for (int i = 0; i < Math.min(efficiency, possShips.size()); i++) {
            expandShips.add(possShips.get(i));
        }

        this.expand.setShips(expandShips);
        this.expand.execute();
    }

    */
/**
     * Calcule la distance au joueur ennemi le plus proche.
     *//*

    private int distanceToNearestEnemy(Ship ship) {
        int minDistance = Integer.MAX_VALUE;
        Hexagon[][] map = game.getMap();

        for (Player player : game.getPlayers()) {
            if (player == this) continue;
            for (Ship enemyShip : player.getShips()) {
                int distance = calculateDistance(ship.getPosition(), enemyShip.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }

        return minDistance;
    }

    */
/**
     * Calcule la distance entre deux hexagones.
     *//*

    private int calculateDistance(Hexagon a, Hexagon b) {
        // Implémenter une méthode de calcul de distance adaptée à la grille hexagonale
        // Exemple simplifié :
        int dx = Math.abs(a.getx() - b.getx());
        int dy = Math.abs(a.gety() - b.gety());
        return dx + dy;
    }

    @Override
    public void doExplore(int efficiency) {
        System.out.println(this.getPseudo() + " explore de manière ciblée.");

        List<Pair<List<Ship>, List<Hexagon>>> possibleMoves = possibilities.explore(this);
        List<Pair<List<Ship>, List<Hexagon>>> prioritizedMoves = prioritizeExploration(possibleMoves);

        List<Pair<List<Ship>, List<Hexagon>>> moves = new ArrayList<>();
        Set<Hexagon> origins = new HashSet<>();

        for (Pair<List<Ship>, List<Hexagon>> move : prioritizedMoves) {
            if (moves.size() >= efficiency) break;
            if (!origins.contains(move.getKey().get(0).getPosition())) {
                moves.add(move);
                origins.add(move.getKey().get(0).getPosition());
            }
        }

        for (Pair<List<Ship>, List<Hexagon>> move : moves) {
            this.explore.setShips(move.getKey());
            this.explore.setTargets(move.getValue());
            this.explore.execute();
        }
    }

    */
/**
     * Priorise les mouvements d'exploration en fonction de la découverte de nouveaux territoires.
     *//*

    private List<Pair<List<Ship>, List<Hexagon>>> prioritizeExploration(List<Pair<List<Ship>, List<Hexagon>>> possibleMoves) {
        // Exemple d'évaluation basée sur la découverte de secteurs non explorés
        possibleMoves.sort((move1, move2) -> {
            int score1 = evaluateExploration(move1.getValue());
            int score2 = evaluateExploration(move2.getValue());
            return Integer.compare(score2, score1); // Descendant
        });
        return possibleMoves;
    }

    */
/**
     * Évalue un mouvement d'exploration.
     *//*

    private int evaluateExploration(List<Hexagon> targets) {
        int score = 0;
        for (Hexagon hex : targets) {
            if (!hex.isExplored()) {
                score += 3; // Découverte de nouveaux hexagones
            }
            if (hex.getSystem().isStrategic()) {
                score += 2; // Importance stratégique
            }
        }
        return score;
    }
}*/
