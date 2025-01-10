package pimperium.players;

import java.util.*;

import pimperium.elements.HSystem;
import pimperium.elements.Hexagon;
import pimperium.elements.Sector;
import pimperium.utils.Colors;
import pimperium.models.Game;
import pimperium.utils.Possibilities;

/**
 * Bot player
 */
public abstract class Bot extends Player{

    /**
     * Instance of Possibilities used to calculate all possible moves for each action
     */
    protected Possibilities possibilities;

    public Bot(Game game, Colors color) {
        super(game, color);
        this.possibilities = Possibilities.getInstance(game);
    }

    /**
     * Randomly place initial fleet on a free lvl-1 system
     */
    public void setupInitialFleet() {

        List<Hexagon> possHexs = possibilities.setupFleet();

        Random random = new Random();
        Hexagon hex = possHexs.get(random.nextInt(possHexs.size()));

        // Add ships to the selected hexagon
        this.createShip(hex);
        this.createShip(hex);

        System.out.println("Deux navires de " + this.getPseudo() + " ont été placés sur l'hexagone " + hex);
        game.getController().getView().addLogMessage("Deux navires ont été placés sur l'hexagone " + hex, this, "normal");
    }

    /**
     * Randomly choose the order of commands for the round to go
     */
    public void chooseOrderCommands() {

        List<Integer> numbers = new ArrayList<>();
        numbers.add(0);
        numbers.add(1);
        numbers.add(2);

        Collections.shuffle(numbers);

        // Convert random ArrayList to array
        this.orderCommands = numbers.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Choose the sector to score by balancing earned points and points given to the opponents
     * @param scoredSectors The set of sectors that have already been chosen by other players this round (a sector can't be chosen twice)
     * @param sectors The full list of sectors
     * @return The sector chosen to score
     */
    public Sector chooseSectorToScore(Set<Sector> scoredSectors, Sector[] sectors) {

        Set<Sector> availableSectors = new HashSet<>();
        for (Sector sector : sectors) {
            if (!scoredSectors.contains(sector) && !sector.isTriPrime()) availableSectors.add(sector);
        }

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

    public void setPossibilities() {
        this.possibilities = Possibilities.getInstance(this.game);
    }

}
