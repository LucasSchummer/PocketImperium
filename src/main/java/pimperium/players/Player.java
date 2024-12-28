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


public abstract class Player implements Serializable {

    protected static final long serialVersionUID = 1L;

    protected List<Ship> ships;
    protected String pseudo;
    protected int score;
    protected Game game;
    protected int[] orderCommands;
    protected Expand expand;
    protected Explore explore;
    protected Exterminate exterminate;
    protected Colors color;

    public Player(Game game, Colors color) {
        this.ships = new ArrayList<Ship>();
        this.pseudo = ""; 
        this.game = game;
        this.expand = new Expand(this);
        this.explore = new Explore(this);
        this.exterminate = new Exterminate(this);
        Random random = new Random();
        this.color = color;
    }
    
    //Return and set the order of commands as a list of int
    //Ex: {1,0,2} : Explore/Expand/Exterminate
    public abstract void chooseOrderCommands();

    public int[] getOrderCommands() {
        return this.orderCommands;
    }

    public void createShip(Hexagon target) {
    	Ship ship = new Ship(target, this);
        this.ships.add(ship);
    }

    public void removeShip(Ship ship) {
        this.ships.remove(ship);
    }

    public int countShips() {
        return this.ships.size();
    }

    public List<Ship> getShips() {
        return this.ships;
    }

    public void deleteExtraShips() {

        int numExtraShips = this.countShips() - 15;
        System.out.println("num ships of " + pseudo + " : " + countShips());
        if (numExtraShips > 0) {
            System.out.println(pseudo + " a déjà 15 vaisseaux sur le plateau");
            game.getController().getView().addLogMessage("A déjà 15 vaisseaux sur le plateau.", this, "normal");
            for (int i = 0; i < numExtraShips ; i++) {
                this.getShips().getLast().destroy();
            }
        }

    }

    // Getter and setter for the pseudo
    public String getPseudo() {
        return this.pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
    
    // Getter and setter for the pseudo
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

    public abstract void setupInitialFleet();

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

    public void resetShips() {
        for (Ship ship : this.ships) {
            ship.setHasExpanded(false);
            ship.setHasExplored(false);
            ship.setHasExterminated(false);
        }
    }

    public abstract Sector chooseSectorToScore(Set<Sector> scoredSectors, Sector[] sectors);

    public abstract void doExpand(int efficiency);
    public abstract void doExplore(int efficiency);
    public abstract void doExterminate(int efficiency);

}
    


