package pimperium.players;

import java.util.*;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.util.Pair;

import pimperium.elements.HSystem;
import pimperium.elements.Hexagon;
import pimperium.elements.Sector;
import pimperium.elements.Ship;
import pimperium.models.Game;
import pimperium.utils.Colors;
import pimperium.utils.Debugger;

public class OffensiveBot extends Bot {

    public OffensiveBot(Game game, Colors color) {
        super(game, color);
    }

    @Override
    public Sector chooseSectorToScore(Set<Sector> scoredSectors, Sector[] sectors) {

        Set<Sector> availableSectors = new HashSet<>();
        Collections.addAll(availableSectors, sectors);
        availableSectors.removeIf(Sector::isTriPrime);

        Sector chosenSector = sectors[0];

        int bestScore = -100;
        int score = 0;

        for (Sector sector : availableSectors) {
            score = 0;
            for (HSystem system : sector.getSystems()) {
                if (system.getHex().getOccupant() != null) {
                    score += system.getLevel() * ( system.getHex().getOccupant() == this ? 1 : -1);
                }
            }
            if (score > bestScore) {
                bestScore = score;
                chosenSector = sector;
            }
        }

        System.out.println(this.getPseudo() + " choisit le secteur à scorer");

        System.out.println(this.getPseudo() + " a choisi le secteur " + game.findSectorId(chosenSector) + " à scorer.");
        game.getController().getView().addLogMessage(" A choisi le secteur " + game.findSectorId(chosenSector) + ".", this, "normal");
        return chosenSector;
    }

    public Ship chooseExpand(List<Ship> possShips) {

        // Current best options
        List<Ship> bestShips = new ArrayList<>();
        int sysLevel = 0;

        for (Ship ship : possShips) {
            // Find the maxSystemLevel among neighbors
            List<Hexagon> possibleTargets = ship.getPosition()
                    .getNeighbours()
                    .stream()
                    .filter(hex -> hex.getSystemLevel() > 0 && hex.getOccupant() != this)
                    .toList();

            int maxSystemLevel = possibleTargets
                    .stream()
                    .mapToInt(Hexagon::getSystemLevel)
                    .max()
                    .orElse(0);

            // Check that the ship won't be deleted on next sustain
            if (ship.getPosition().getShips().size() >= ship.getPosition().getSystemLevel() + 1) continue;


            // This ship is better than all the ones currently in bestShips
            if (maxSystemLevel > sysLevel) {
                bestShips.clear();
                bestShips.add(ship);
                sysLevel = maxSystemLevel;
            }
            // This ship is as good as the ones currently in bestShips
            else if (maxSystemLevel == sysLevel) {
                bestShips.add(ship);
            }

        }

        Random random = new Random();

        if (!bestShips.isEmpty()) {
            return bestShips.get(random.nextInt(bestShips.size()));
        } else {
            return possShips.get(random.nextInt(possShips.size()));
        }


    }

    public Pair<List<Ship>, List<Hexagon>> chooseExplore(List<Pair<List<Ship>, List<Hexagon>>> possibleMoves) {

        Random random = new Random();

        return  possibleMoves.get(random.nextInt(possibleMoves.size()));

    }

    public Pair<Set<Ship>, Hexagon> chooseExterminate(List<Pair<Set<Ship>, Hexagon>> possibleMoves) {

        List<Pair<Set<Ship>, Hexagon>> bestMoves = new ArrayList<>();
        int bestShipsDestroyed = 0;

        for (Pair<Set<Ship>, Hexagon> move : possibleMoves) {
            int shipsDestroyed = Math.min(move.getKey().size(), move.getValue().getShips().size());

            if (shipsDestroyed > bestShipsDestroyed) {
                bestMoves.clear();
                bestMoves.add(move);
                bestShipsDestroyed = shipsDestroyed;
            } else if (shipsDestroyed == bestShipsDestroyed) {
                bestMoves.add(move);
            }
        }

        Random random = new Random();

        if (!bestMoves.isEmpty()) {
            return bestMoves.get(random.nextInt(bestMoves.size()));
        } else {
            return possibleMoves.get(random.nextInt(possibleMoves.size()));
        }
    }

    public void doExpand(int efficiency) {

        System.out.println(this.getPseudo() + " s'étend");
        game.getController().getView().addLogMessage("Expand (efficacité : " + efficiency + ")", this, "normal");

        for (int i = 0; i < efficiency; i++) {

            // Get the ships on which it is possible to expand
            List<Ship> possShips = possibilities.expand(this);
            List<Ship> expandShips = new ArrayList<Ship>();

            // Verifies that the player can do at least a move
            if (possShips.isEmpty()) {
                System.out.println("Aucune expansion possible.");
                game.getController().getView().addLogMessage("Aucune expansion possible.", this, "normal");
                return;
            }

            Ship ship = chooseExpand(possShips);
            expandShips.add(ship);

            //Set the ships and execute the command
            this.expand.setShips(expandShips);
            this.expand.execute();

            game.getController().getView().addLogMessage("Vaisseau ajouté en " + expandShips.get(0).getPosition(), this, "normal");
        }

    }

    public void doExplore(int efficiency) {

        System.out.println(this.getPseudo() + " explore with efficiency " + efficiency);
        game.getController().getView().addLogMessage("Explore (efficacité : " + efficiency + ")", this, "normal");

        for (int i = 0; i < efficiency; i++) {

            List<Pair<List<Ship>, List<Hexagon>>> possibleMoves = possibilities.explore(this);

            // Verifies that the player can do at least a move
            if (possibleMoves.isEmpty()) {
                System.out.println("Aucun mouvement d'exploration possible.");
                game.getController().getView().addLogMessage("Aucune exploration possible.", this, "normal");
                return;
            }

            Pair<List<Ship>, List<Hexagon>> move = chooseExplore(possibleMoves);

            // Execute each move
            this.explore.setShips(move.getKey());
            this.explore.setTargets(move.getValue());
            this.explore.execute();

            int fleetSize = move.getKey().size();
            if (fleetSize > 1) {
                game.getController().getView().addLogMessage("Flotte de " + move.getKey().size() + " vaisseaux déplacés en " + move.getValue(), this, "normal");
            } else {
                game.getController().getView().addLogMessage("Un vaisseau déplacé en " + move.getValue(), this, "normal");
            }

        }

    }

    public void doExterminate(int efficiency) {

        System.out.println(this.getPseudo() + " extermine");
        game.getController().getView().addLogMessage("Exterminate (efficacité : " + efficiency + ")", this, "normal");

        for (int i = 0; i < efficiency; i++) {

            // Generate possible moves
            List<Pair<Set<Ship>, Hexagon>> possibleMoves = possibilities.exterminate(this);

            // Verifies that the player can do at least a move
            if (possibleMoves.isEmpty()) {
                System.out.println("Aucun mouvement d'extermination possible.");
                game.getController().getView().addLogMessage("Aucune extermination possible", this, "normal");
                return;
            }

            Pair<Set<Ship>, Hexagon> move = chooseExterminate(possibleMoves);

            //Set the ships and execute the command
            this.exterminate.setShips(move.getKey());
            this.exterminate.setTarget(move.getValue());
            this.exterminate.execute();

            int fleetSize = move.getKey().size();
            if (fleetSize > 1) {
                game.getController().getView().addLogMessage("Flotte de " + move.getKey().size() + " vaisseaux exterminent en " + move.getValue(), this, "normal");
            } else {
                game.getController().getView().addLogMessage("Un vaisseau extermine en " + move.getValue(), this, "normal");
            }

        }

    }

}
