package pimperium;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

        //TODO Add the concept of fleet (for each move, the player can move the ships of a fleet together)
        List<Pair<Ship, Hexagon>> possibleMoves = possibilities.explore(this);

        List<Pair<Ship, Hexagon>> moves = new ArrayList<>();
        List<Ship> exploreShips = new ArrayList<Ship>();
        List<Hexagon> targets = new ArrayList<Hexagon>();

        Random random = new Random();

        // Adding randomly selected ships and targets to the list of ships/targets to explore
        while (exploreShips.size() < efficiency) {
            int index = random.nextInt(possibleMoves.size());
            Ship ship = possibleMoves.get(index).getKey();
            Hexagon target = possibleMoves.get(index).getValue();
            Pair<Ship, Hexagon> move = new Pair<>(ship, target);
            if (!moves.contains(move)) {
                moves.add(move);
                exploreShips.add(ship);
                targets.add(target);
            }
        }

        //Set the ships and execute the command
        this.explore.setShips(exploreShips);
        this.explore.setTargets(targets);
        this.explore.execute();

    }

    public void doExterminate(int efficiency) {

    }

}
