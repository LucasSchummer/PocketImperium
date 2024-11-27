package pimperium;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    }

    public void doExterminate(int efficiency) {

    }

}
