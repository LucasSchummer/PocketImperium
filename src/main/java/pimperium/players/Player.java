package pimperium.players;

import java.io.Serializable;
import java.util.*;

import pimperium.commands.Expand;
import pimperium.commands.Explore;
import pimperium.commands.Exterminate;
import pimperium.elements.Hexagon;
import pimperium.elements.Sector;
import pimperium.elements.Ship;
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
    protected float color;

    public Player(Game game) {
        this.ships = new ArrayList<Ship>();
        this.pseudo = ""; 
        this.game = game;
        this.expand = new Expand(this);
        this.explore = new Explore(this);
        this.exterminate = new Exterminate(this);
        Random random = new Random();
        this.color = 2 * random.nextFloat() - 1;
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

    public float getColor() {
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
                throw new IllegalArgumentException("Unexpected value: " + index);
        }
    }

    public abstract Sector chooseSectorToScore(Set<Sector> scoredSectors, Sector[] sectors);

    public abstract void doExpand(int efficiency);
    public abstract void doExplore(int efficiency);
    public abstract void doExterminate(int efficiency);

}
    


