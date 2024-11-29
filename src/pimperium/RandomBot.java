package pimperium;

import java.util.*;

import javafx.util.Pair;

public class RandomBot extends Bot{

    public RandomBot(Game game) {
        super(game);
    }

    public void doExpand(int efficiency) {

        System.out.println(this.getPseudo() + " is expanding");

        // Get the ships on which it is possible to expand
        List<Ship> possShips = possibilities.expand(this);
        List<Ship> expandShips = new ArrayList<Ship>();

        Random random = new Random();

        // Adding randomly selected ships to the list of ships so expand on
        while (expandShips.size() < efficiency) {
            Ship ship = possShips.get(random.nextInt(possShips.size()));
            if (!expandShips.contains(ship)) {
                expandShips.add(ship);
            }
        }

        //Set the ships and execute the command
        this.expand.setShips(expandShips);
        this.expand.execute();

    }

    public void doExplore(int efficiency) {

        System.out.println(this.getPseudo() + " is exploring");

        List<Pair<List<Ship>, List<Hexagon>>> possibleMoves = possibilities.explore(this);

        List<Pair<List<Ship>, List<Hexagon>>> moves = new ArrayList<>();
        Set<Hexagon> origins = new HashSet<Hexagon>();

        Random random = new Random();

        // Adding randomly selected ships and targets to the list of ships/targets to explore
        while (moves.size() < efficiency) {
            int index = random.nextInt(possibleMoves.size());
            Pair<List<Ship>, List<Hexagon>> move = possibleMoves.get(index);
            // Check that the randomly chosen move doesn't start from the same hexagon that another ove that has already been chosen
            if (!origins.contains(move.getKey().getFirst().getPosition())) {
                moves.add(move);
                origins.add(move.getKey().getFirst().getPosition());
            }
        }

        // Execute each move
        for (Pair<List<Ship>, List<Hexagon>> move : moves) {
            //Set the ships and execute the command
            this.explore.setShips(move.getKey());
            this.explore.setTargets(move.getValue());
            this.explore.execute();
        }

    }

    public void doExterminate(int efficiency) {

    }

}
