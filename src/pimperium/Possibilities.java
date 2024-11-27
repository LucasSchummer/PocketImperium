package pimperium;

import java.util.List;
import java.util.ArrayList;

public class Possibilities {

    private static Possibilities instance;
    private static Game game;

    private Possibilities(Game game) {
        Possibilities.game = game;
    }

    public static Possibilities getInstance(Game game) {
        if (instance == null) {
            instance = new Possibilities(game);
        }
        return instance;
    }

    public List<Hexagon> setupFleet() {
        List<Hexagon> hexs = new ArrayList<Hexagon>();
        for (Hexagon[] row : game.getMap()) {
            for (Hexagon hex : row) {
                if (hex != null && hex.getOccupant() == null && hex.getSystemLevel() == 1) {
                    hexs.add(hex);
                }
            }
        }
        return hexs;
    }

}
