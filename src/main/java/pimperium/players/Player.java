package pimperium.players;

import java.io.Serializable;
import java.util.*;

import pimperium.commands.Expand;
import pimperium.commands.Explore;
import pimperium.commands.Exterminate;
import pimperium.elements.Hexagon;
import pimperium.elements.Sector;
import pimperium.elements.Ship;
import pimperium.utils.Colors;
import pimperium.models.Game;


/**
 * A player in the game
 */
public abstract class Player implements Serializable {

    protected static final long serialVersionUID = 1L;

    /**
     * List of ships belonging to the player
     */
    protected List<Ship> ships;
    protected String pseudo;
    /**
     * Current score
     */
    protected int score;
    /**
     * Instance of the game used to access the map
     */
    protected Game game;
    /**
     * The order of commands for the current round, as a length-3 array (0:Expand, 1:Explore, 2:Exterminate)
     */
    protected int[] orderCommands;
    /**
     * The command card used to perform Expand
     */
    protected Expand expand;
    /**
     * The command card used to perform Explore
     */
    protected Explore explore;
    /**
     * The command card used to perform Exterminate
     */
    protected Exterminate exterminate;
    /**
     * The color representing the player in the view
     */
    protected Colors color;

    /**
     * Create a new player with a chosen color and create its command cards
     * @param game The instance of Game
     * @param color The color the player will use on the view
     */
    public Player(Game game, Colors color) {
        this.ships = new ArrayList<Ship>();
        this.pseudo = ""; 
        this.game = game;
        this.expand = new Expand(this);
        this.explore = new Explore(this);
        this.exterminate = new Exterminate(this);
        this.color = color;
    }
    
    //Return and set the order of commands as a list of int
    //Ex: {1,0,2} : Explore/Expand/Exterminate

    /**
     * Choose the order of commands for the round to go
     */
    public abstract void chooseOrderCommands();

    public int[] getOrderCommands() {
        return this.orderCommands;
    }

    /**
     * Create a new ship belonging to the player on a designated hexagon
     * @param target The hexagon to place the new ship on
     */
    public void createShip(Hexagon target) {
    	Ship ship = new Ship(target, this);
        this.ships.add(ship);
    }

    /**
     * Remove a ship from the player's fleet
     * @param ship The ship to remove
     */
    public void removeShip(Ship ship) {
        this.ships.remove(ship);
    }

    /**
     * Count the size of the player's fleet
     * @return The number of ships belonging to the player
     */
    public int countShips() {
        return this.ships.size();
    }

    public List<Ship> getShips() {
        return this.ships;
    }

    /**
     * Delete the extra ships in case the player currently controls more than the max amount of ships
     */
    public void deleteExtraShips() {

        int numExtraShips = this.countShips() - Game.MAX_SHIPS;
        System.out.println("num ships of " + pseudo + " : " + countShips());
        if (numExtraShips > 0) {
            System.out.println(pseudo + " a déjà 15 vaisseaux sur le plateau");
            game.getController().getView().addLogMessage("A déjà 15 vaisseaux sur le plateau.", this, "normal");
            for (int i = 0; i < numExtraShips ; i++) {
                this.getShips().getLast().destroy();
            }
        }

    }

    public String getPseudo() {
        return this.pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
    
    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public Colors getColor() {
        return this.color;
    }

    /**
     * Perform the initial fleet setup on a lvl-1 system
     */
    public abstract void setupInitialFleet();

    /**
     * Perform one of the 3 actions of the round, depending on the order chosen by the player
     * @param index The index of the round step (0-1-2)
     * @param efficiency The efficiency of the action
     */
    public void doAction(int index, int efficiency) {
        int action = this.orderCommands[index];
        switch (action) {
            case 0: {
                this.doExpand(efficiency);
                break;
            }
            case 1: {
                this.doExplore(efficiency);
                break;
            }
            case 2: {
                this.doExterminate(efficiency);
                break;
            }
            default:
                throw new IllegalArgumentException("Valeur innatendue : " + index);
        }
    }

    public void setOrderCommands(int[] orderCommands) {
        this.orderCommands = orderCommands;
    }

    public void resetOrderCommands() {
        this.orderCommands = null;
    }

    /**
     * Reset all the player's ships to make them available to Expand/Explore/Exterminate
     */
    public void resetShips() {
        for (Ship ship : this.ships) {
            ship.setHasExpanded(false);
            ship.setHasExplored(false);
            ship.setHasExterminated(false);
        }
    }

    /**
     * Choose the sector where points will be added to players controlling systems
     * @param scoredSectors The set of sectors that have already been chosen by other players this round (a sector can't be chosen twice)
     * @param sectors The full list of sectors
     * @return The sector chosen by the player, different from all sectors already chosen
     */
    public abstract Sector chooseSectorToScore(Set<Sector> scoredSectors, Sector[] sectors);

    /**
     * Perform Expand
     * @param efficiency Efficiency of the action
     */
    public abstract void doExpand(int efficiency);
    /**
     * Perform Explore
     * @param efficiency Efficiency of the action
     */
    public abstract void doExplore(int efficiency);
    /**
     * Perform Exterminate
     * @param efficiency Efficiency of the action
     */
    public abstract void doExterminate(int efficiency);

}
