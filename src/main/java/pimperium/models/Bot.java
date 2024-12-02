package pimperium.models;

import java.util.Random;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;


public abstract class Bot extends Player{

    protected Possibilities possibilities;

    public Bot(Game game) {
        super(game);
        this.possibilities = Possibilities.getInstance(game);
    }

    public void setupInitialFleet() {

        List<Hexagon> possHexs = possibilities.setupFleet();

        Random random = new Random();
        Hexagon hex = possHexs.get(random.nextInt(possHexs.size()));

        // Add ships to the selected hexagon
        this.createShip(hex);
        this.createShip(hex);

        System.out.println("Deux navires de " + this.getPseudo() + " ont été placés sur l'hexagone " + hex);
    }

    public void chooseOrderCommands() {

        List<Integer> numbers = new ArrayList<>();
        numbers.add(0);
        numbers.add(1);
        numbers.add(2);

        Collections.shuffle(numbers);

        // Convert random ArrayList to array
        this.orderCommands = numbers.stream().mapToInt(i -> i).toArray();
    }

}
