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

public class RandomBot extends Bot {

    public RandomBot(Game game, Colors color) {
        super(game, color);
    }

/*    @Override
    public Sector chooseSectorToScore(Set<Sector> scoredSectors, Sector[] sectors) {
        Random random = new Random();
        Sector chosenSector = null;
        boolean validChoice = false;
        int sectorId = -1;
        System.out.println(this.getPseudo() + " choisit le secteur à scorer");

        while (!validChoice) {
            sectorId = random.nextInt(1, sectors.length+1);
            chosenSector = sectors[sectorId-1];

            if (chosenSector == null) {
                continue; // Invalid sector, try another
            } else if (chosenSector.isTriPrime()) {
                continue; // Cannot choose the Tri-Prime sector
            } else if (!chosenSector.isOccupied()) {
                continue; // Cannot choose an unoccupied sector
            } else if (scoredSectors.contains(chosenSector)) {
                continue; // Sector already chosen, try another
            } else {
                validChoice = true;
            }
        }
        System.out.println(this.getPseudo() + " a choisi le secteur " + sectorId + " à scorer.");
        return chosenSector;
    }*/

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

    public void doExpand(int efficiency) {

        System.out.println(this.getPseudo() + " s'étend");
        game.getController().getView().addLogMessage("Expand (efficacité : " + efficiency + ")", this, "normal");

        for (int i = 0; i < efficiency; i++) {

            // Get the ships on which it is possible to expand
            List<Ship> possShips = possibilities.expand(this);
            List<Ship> expandShips = new ArrayList<Ship>();

            Random random = new Random();

            // Adding randomly selected ships to the list of ships so expand on
/*            while (expandShips.size() < efficiency) {
                Ship ship = possShips.get(random.nextInt(possShips.size()));
                if (!expandShips.contains(ship)) {
                    expandShips.add(ship);
                }
            }*/

            Ship ship = possShips.get(random.nextInt(possShips.size()));
            expandShips.add(ship);

            // Verifies that the player can do at least a move
            if (possShips.isEmpty()) {
                System.out.println("Aucune expansion possible.");
                game.getController().getView().addLogMessage("Aucune expansion possible.", this, "normal");
                return;
            }

            //Set the ships and execute the command
            this.expand.setShips(expandShips);
            this.expand.execute();

            game.getController().getView().addLogMessage("Vaisseau(x) ajouté(s) en " + expandShips.get(0).getPosition(), this, "normal");
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

            //Debugger.displayAllExploreMoves(possibleMoves);

/*          List<Pair<List<Ship>, List<Hexagon>>> moves = new ArrayList<>();
            Set<Hexagon> origins = new HashSet<Hexagon>();*/

            Random random = new Random();

/*            // Adding randomly selected ships and targets to the list of ships/targets to explore
            while (moves.size() < efficiency) {
                int index = random.nextInt(possibleMoves.size());
                Pair<List<Ship>, List<Hexagon>> move = possibleMoves.get(index);
                // Check that the randomly chosen move doesn't start from the same hexagon that another ove that has already been chosen
                if (!origins.contains(move.getKey().getFirst().getPosition())) {
                    moves.add(move);
                    origins.add(move.getKey().getFirst().getPosition());
                }
            }*/

            int index = random.nextInt(possibleMoves.size());
            Pair<List<Ship>, List<Hexagon>> move = possibleMoves.get(index);


            // Execute each move
            this.explore.setShips(move.getKey());
            this.explore.setTargets(move.getValue());
            this.explore.execute();

            game.getController().getView().addLogMessage("Vaisseau(x) déplacé(s) de " + move.getKey().get(0).getPosition() + " vers " + move.getValue(), this, "normal");

/*            // After selecting the moves
            for (Pair<List<Ship>, List<Hexagon>> move : moves) {
                // Ensure the lists are the same size
                if (move.getKey().size() != move.getValue().size()) {
                    continue; // Skip this move for now
                }

                this.explore.setShips(move.getKey());
                this.explore.setTargets(move.getValue());
                this.explore.execute();
            }*/

        }

    }

    public void doExterminate(int efficiency) {

        System.out.println(this.getPseudo() + " extermine");
        game.getController().getView().addLogMessage("Exterminate (efficacité : " + efficiency + ")", this, "normal");

        for (int i = 0; i < efficiency; i++) {

            // Generate possible moves
            List<Pair<Set<Ship>, Hexagon>> possibleMoves = possibilities.exterminate(this);

            //Debugger.displayAllExterminateMoves(possibleMoves, this);


            // Verifies that the player can do at least a move
            if (possibleMoves.isEmpty()) {
                System.out.println("Aucun mouvement d'extermination possible.");
                game.getController().getView().addLogMessage("Aucune extermination possible", this, "normal");
                return;
            }

/*            List<Pair<Set<Ship>, Hexagon>> moves = new ArrayList<>();
            Set<Hexagon> targets = new HashSet<Hexagon>();*/

            Random random = new Random();

/*            // Just another verification
            int maxEfficiency = Math.min(efficiency, possibleMoves.size());*/

            // Randomly select a move among the possible ones
            int index = random.nextInt(possibleMoves.size());
            Pair<Set<Ship>, Hexagon> move = possibleMoves.get(index);

            //Set the ships and execute the command
            this.exterminate.setShips(move.getKey());
            this.exterminate.setTarget(move.getValue());
            this.exterminate.execute();

            List<Ship> shipList = new ArrayList<>(move.getKey());
            game.getController().getView().addLogMessage("Vaisseau(x) en " + shipList.get(0).getPosition() + " extermine(nt) " + move.getValue(), this, "normal");

/*            // Adding randomly selected ships and targets to the list of ships/targets to explore
            while (moves.size() < maxEfficiency) {
                int index = random.nextInt(possibleMoves.size());
                Pair<Set<Ship>, Hexagon> move = possibleMoves.get(index);
                // Check that the randomly chosen move doesn't target a hex that has has already been targeted
                if (!targets.contains(move.getValue())) {
                    moves.add(move);
                    targets.add(move.getValue());
                }
            }*/

/*            // Execute each move
            for (Pair<Set<Ship>, Hexagon> move : moves) {
                //Set the ships and execute the command
                this.exterminate.setShips(move.getKey());
                this.exterminate.setTarget(move.getValue());
                this.exterminate.execute();
            }*/

        }


    }

}
