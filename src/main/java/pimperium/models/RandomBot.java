package pimperium.models;

import java.util.*;

import javafx.util.Pair;

public class RandomBot extends Bot {

    public RandomBot(Game game) {
        super(game);
    }

    @Override
    public Sector chooseSectorToScore(Set<Sector> scoredSectors, Sector[] sectors) {
        Random random = new Random();
        Sector chosenSector = null;
        boolean validChoice = false;
        int sectorId = -1;
        System.out.println(this.getPseudo() + " chooses the sector to score");

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
        System.out.println(this.getPseudo() + " has chosen the sector " + (sectorId-1) + " to score.");
        return chosenSector;
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

        System.out.println(this.getPseudo() + " is exterminating");

        List<Pair<List<Ship>, Hexagon>> possibleMoves = possibilities.exterminate(this);
        System.out.println("Possible moves : " + possibleMoves.size());

        List<Pair<List<Ship>, Hexagon>> moves = new ArrayList<>();
        Set<Hexagon> targets = new HashSet<Hexagon>();

        Random random = new Random();

        // TODO Check somewhere that the player CAN do 'efficiency' exterminates

        // Adding randomly selected ships and targets to the list of ships/targets to explore
        while (moves.size() < efficiency) {
            int index = random.nextInt(possibleMoves.size());
            Pair<List<Ship>, Hexagon> move = possibleMoves.get(index);
            // Check that the randomly chosen move doesn't target a hex that has has already been targeted
            if (!targets.contains(move.getValue())) {
                moves.add(move);
                targets.add(move.getValue());
            }
        }

        // Execute each move
        for (Pair<List<Ship>, Hexagon> move : moves) {
            //Set the ships and execute the command
            this.explore.setShips(move.getKey());
            this.explore.setTargets(new ArrayList<>(Collections.nCopies(move.getKey().size(), move.getValue())));
            this.explore.execute();
        }


    }

}
