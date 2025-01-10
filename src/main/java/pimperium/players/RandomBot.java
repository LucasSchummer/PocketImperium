package pimperium.players;

import java.util.*;

import javafx.util.Pair;

import pimperium.elements.Hexagon;
import pimperium.elements.Ship;
import pimperium.models.Game;
import pimperium.utils.Colors;

/**
 * Bot player with a random strategy
 */
public class RandomBot extends Bot {

    public RandomBot(Game game, Colors color) {
        super(game, color);
    }

    /**
     * Choose and perform Expand
     * @param efficiency Efficiency of the action
     */
    public void doExpand(int efficiency) {

        System.out.println(this.getPseudo() + " s'étend");
        game.getController().getView().addLogMessage("Expand (efficacité : " + efficiency + ")", this, "normal");

        for (int i = 0; i < efficiency; i++) {

            // Get the ships on which it is possible to expand
            List<Ship> possShips = possibilities.expand(this);

            Random random = new Random();

            // Verifies that the player can do at least a move
            if (possShips.isEmpty()) {
                System.out.println("Aucune expansion possible.");
                game.getController().getView().addLogMessage("Aucune expansion possible.", this, "normal");
                return;
            }

            Ship ship = possShips.get(random.nextInt(possShips.size()));

            //Set the ship and execute the command
            this.expand.setShip(ship);
            this.expand.execute();

            game.getController().getView().addLogMessage("Vaisseau ajouté en " + ship.getPosition(), this, "normal");

            this.game.triggerInterfaceUpdate();

            try {
                Thread.sleep(Game.DELAY);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }

    /**
     * Choose and perform Explore
     * @param efficiency Efficiency of the action
     */
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

            int fleetSize = move.getKey().size();
            if (fleetSize > 1) {
                game.getController().getView().addLogMessage("Flotte de " + move.getKey().size() + " vaisseaux déplacés en " + move.getValue(), this, "normal");
            } else {
                game.getController().getView().addLogMessage("Un vaisseau déplacé en " + move.getValue(), this, "normal");
            }

            this.game.triggerInterfaceUpdate();

            try {
                Thread.sleep(Game.DELAY);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

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

    /**
     * Choose and perform Exterminate
     * @param efficiency Efficiency of the action
     */
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

            int fleetSize = move.getKey().size();
            if (fleetSize > 1) {
                game.getController().getView().addLogMessage("Flotte de " + move.getKey().size() + " vaisseaux exterminent en " + move.getValue(), this, "normal");
            } else {
                game.getController().getView().addLogMessage("Un vaisseau extermine en " + move.getValue(), this, "normal");
            }

            this.game.triggerInterfaceUpdate();

            try {
                Thread.sleep(Game.DELAY);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

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
