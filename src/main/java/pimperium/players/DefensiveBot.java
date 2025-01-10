package pimperium.players;

import java.util.*;
import javafx.util.Pair;
import pimperium.elements.HSystem;
import pimperium.elements.Hexagon;
import pimperium.elements.Sector;
import pimperium.elements.Ship;
import pimperium.models.Game;
import pimperium.utils.Colors;

/**
 * Bot player with a defensive strategy
 */
public class DefensiveBot extends Bot {

    public DefensiveBot(Game game, Colors color) {
        super(game, color);
    }

    /**
     * Choose the sector to score
     * @param scoredSectors The set of sectors that have already been chosen by other players this round (a sector can't be chosen twice)
     * @param sectors The full list of sectors
     * @return The chosen sector to score
     */
    public Sector chooseSectorToScore(Set<Sector> scoredSectors, Sector[] sectors) {
        Set<Sector> availableSectors = new HashSet<>();
        for (Sector sector : sectors) {
            if (!scoredSectors.contains(sector) && !sector.isTriPrime()) 
                availableSectors.add(sector);
        }

        Sector chosenSector = sectors[0];
        int bestScore = -100;

        for (Sector sector : availableSectors) {
            int score = 0;
            int ownedSystems = 0;
            boolean hasHighLevelSystem = false;
            
            for (HSystem system : sector.getSystems()) {
                if (system.getHex().getOccupant() != null) {
                    if (system.getHex().getOccupant() == this) {
                        score += system.getLevel() * 2;
                        ownedSystems++;
                        if (system.getLevel() >= 2) {
                            hasHighLevelSystem = true;
                        }
                    } else {
                        score -= system.getLevel();
                    }
                }
            }

            // Bonus for majority control of the sector
            if (ownedSystems > sector.getSystems().size() / 2) {
                score += 5;
            }

            if (hasHighLevelSystem) {
                score += 3;
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

    /**
     * Count the number of hexagons controlled by the player within a given range around a given position
     * @param hexagon The hexagon considered
     * @param range The maximum distance to find allies
     * @return The number of allies within the range
     */
    private int countNearbyAllies(Hexagon hexagon, int range) {
        Set<Hexagon> considered = new HashSet<>();
        Set<Hexagon> allies = new HashSet<>();
        Set<Hexagon> current = new HashSet<>();
        current.add(hexagon);

        for (int i = 0; i < range; i++) {
            Set<Hexagon> next = new HashSet<>();
            for (Hexagon hex : current) {
                for (Hexagon neighbor : hex.getNeighbours()) {
                    if (!considered.contains(neighbor)) {
                        next.add(neighbor);
                        if (neighbor.getOccupant() == this) {
                            allies.add(neighbor);
                        }
                    }
                }
            }
            considered.addAll(current);
            current = next;
        }
        return allies.size();
    }

    /**
     * Count the number of systems controlled by the player in the sector containing a given hexagon
     * @param hexagon The hexagon considered
     * @return The number os systems controlled in the sector
     */
    private int calculateSectorControl(Hexagon hexagon) {
        // Find which sector the hexagon belongs to
        Sector hexSector = null;
        for (Sector sector : game.getSectors()) {
            if (sector.getSystems().stream().anyMatch(sys -> sys.getHex() == hexagon)) {
            hexSector = sector;
            break;
            }
        }
        
        if (hexSector == null) return 0;
        
        // Count the number of systems controlled in this sector
        int controlledSystems = 0;
        for (HSystem system : hexSector.getSystems()) {
            if (system.getHex().getOccupant() == this) {
            controlledSystems++;
            }
        }
        
        return controlledSystems;
    }

    /**
     * Calculate a strategic score for a given hexagon
     * @param hexagon The hexagon considered
     * @return The defensive score of the hexagon
     */
    private int calculateDefensiveScore(Hexagon hexagon) {
        int score = hexagon.getSystemLevel() * 2; // Base score
        
        // Strong bonus for sector control
        score += calculateSectorControl(hexagon) * 4;
        
        // Bonus for non-saturated systems
        int currentShips = hexagon.getShips().size();
        int maxShips = hexagon.getSystemLevel() + 1;
        if (currentShips < maxShips - 1) {
            score += 3;
        }
        
        // Penalty for proximity to enemies
        for (Hexagon neighbor : hexagon.getNeighbours()) {
            if (neighbor.getOccupant() != null && neighbor.getOccupant() != this) {
            score -= 2;
            }
        }
        
        // Bonus for Tri-Prime if we already have good control
        boolean isTriPrime = false;
        for (Sector sector : game.getSectors()) {
            if (sector.isTriPrime() && sector.getSystems().stream()
            .anyMatch(sys -> sys.getHex() == hexagon)) {
            isTriPrime = true;
            break;
            }
        }
        
        if (isTriPrime && calculateSectorControl(hexagon) >= 2) {
            score += 5;
        }
        
        return score;
    }

    /**
     * Strategically choose which action to perform for Expand
     * @param possShips The list of ships where the Expand is possible
     * @return The ship chosen to Expand on
     */
    public Ship chooseExpand(List<Ship> possShips) {
        List<Ship> bestShips = new ArrayList<>();
        int bestScore = -100;

        for (Ship ship : possShips) {
            int score = calculateDefensiveScore(ship.getPosition());
            
            // Strong penalty if the system is near saturation
            int currentShips = ship.getPosition().getShips().size();
            int maxShips = ship.getPosition().getSystemLevel() + 1;
            if (currentShips >= maxShips - 1) {
                score -= 10; 
            }

            // Bonus for well-protected systems
            score += Math.min(ship.getPosition().getShips().size(), 2);

            if (score > bestScore) {
                bestShips.clear();
                bestShips.add(ship);
                bestScore = score;
            } else if (bestScore == score) {
                bestShips.add(ship);
            }
        }

        Random random = new Random();
        if (!bestShips.isEmpty()) {
            return bestShips.get(random.nextInt(bestShips.size()));
        }
        return possShips.get(random.nextInt(possShips.size()));
    }

    /**
     * Strategically choose which action to perform for Explore
     * @param possibleMoves List of possible Explore moves
     * @return The chosen move
     */
    public Pair<List<Ship>, List<Hexagon>> chooseExplore(List<Pair<List<Ship>, List<Hexagon>>> possibleMoves) {
        int bestScore = -100;
        List<Pair<List<Ship>, List<Hexagon>>> bestMoves = new ArrayList<>();

        for (Pair<List<Ship>, List<Hexagon>> move : possibleMoves) {
            int score = 0;
            
            for (int i = 0; i < move.getKey().size(); i++) {
                Hexagon origin = move.getKey().get(i).getPosition();
                Hexagon destination = move.getValue().get(i);
                
                // Bonus if the destination system is not saturated
                int futureShips = destination.getShips().size() + 1;
                if (futureShips <= destination.getSystemLevel() + 1) {
                    score += 3;
                }
                
                // Bonus for force distribution
                if (origin.getShips().size() > origin.getSystemLevel()) {
                    score += 2; // Encourage moving ships from overloaded systems
                }

                // Evaluate the move defensively
                score += calculateDefensiveScore(destination);
                score -= calculateDefensiveScore(origin) / 2; // Reduced penalty for leaving a position
                
                // Bonus for staying close to allied systems
                if (countNearbyAllies(destination, 1) > countNearbyAllies(origin, 1)) {
                    score += 3;
                }
                
                // Reduced penalty for already controlled hexagons
                score += (destination.getOccupant() == this ? -1 : 0);
            }

            // Check for system overload
            Set<Hexagon> targets = new HashSet<>(move.getValue());
            for (Hexagon hex : targets) {
                int shipsGoing = Collections.frequency(move.getValue(), hex);
                if (hex.getShips().size() + shipsGoing > hex.getSystemLevel() + 1) {
                    score -= 3 * (hex.getShips().size() + shipsGoing - hex.getSystemLevel() - 1);
                }
            }

            if (score > bestScore) {
                bestMoves.clear();
                bestMoves.add(move);
                bestScore = score;
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }

        Random random = new Random();
        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    /**
     * Strategically choose which action to perform for Exterminate
     * @param possibleMoves List of possible Exterminate moves
     * @return The chosen move
     */
    public Pair<Set<Ship>, Hexagon> chooseExterminate(List<Pair<Set<Ship>, Hexagon>> possibleMoves) {
        List<Pair<Set<Ship>, Hexagon>> bestMoves = new ArrayList<>();
        int bestScore = -100;

        for (Pair<Set<Ship>, Hexagon> move : possibleMoves) {
            int score = 0;
            Hexagon target = move.getValue();
            
            // Evaluate the threat level of the target
            int threatLevel = target.getSystemLevel() * target.getShips().size();
            score += threatLevel; // Priority to significant threats
            
            // Bonus if our nearby systems are threatened
            for (Hexagon neighbor : target.getNeighbours()) {
                if (neighbor.getOccupant() == this) {
                    score += 5;
                }
            }
            
            // Consider the number of ships we might lose
            int potentialLosses = Math.min(move.getKey().size(), target.getShips().size());
            score -= potentialLosses * 2; // Penalty for potential losses
            
            if (score > bestScore) {
                bestMoves.clear();
                bestMoves.add(move);
                bestScore = score;
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }

        Random random = new Random();
        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    /**
     * Choose and perform Expand
     * @param efficiency Efficiency of the action
     */
    public void doExpand(int efficiency) {
        System.out.println(this.getPseudo() + " s'étend");
        game.getController().getView().addLogMessage("Expand (efficacité : " + efficiency + ")", this, "normal");

        for (int i = 0; i < efficiency; i++) {
            List<Ship> possShips = possibilities.expand(this);

            if (possShips.isEmpty()) {
                System.out.println("Aucune expansion possible.");
                game.getController().getView().addLogMessage("Aucune expansion possible.", this, "normal");
                return;
            }

            Ship ship = chooseExpand(possShips);

            this.expand.setShip(ship);
            this.expand.execute();

            game.getController().getView().addLogMessage("Vaisseau ajouté en " + ship.getPosition(), this, "normal");

            this.game.triggerInterfaceUpdate();

            try {
                Thread.sleep(Game.DELAY);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Choose and perform Explore
     * @param efficiency Efficiency of the action
     */
    public void doExplore(int efficiency) {
        System.out.println(this.getPseudo() + " explore");
        game.getController().getView().addLogMessage("Explore (efficacité : " + efficiency + ")", this, "normal");
    
        for (int i = 0; i < efficiency; i++) {
            List<Pair<List<Ship>, List<Hexagon>>> possibleMoves = possibilities.explore(this);
    
            if (possibleMoves.isEmpty()) {
                System.out.println("Aucun mouvement d'exploration possible.");
                game.getController().getView().addLogMessage("Aucune exploration possible.", this, "normal");
                return;
            }
    
            Pair<List<Ship>, List<Hexagon>> move = chooseExplore(possibleMoves);
    
            this.explore.setShips(move.getKey());
            this.explore.setTargets(move.getValue());
            this.explore.execute();
    
            int fleetSize = move.getKey().size();
            if (fleetSize > 1) {
                game.getController().getView().addLogMessage("Flotte de " + move.getKey().size() + " vaisseaux déplacés en " + move.getValue(), this, "normal");
            } else {
                game.getController().getView().addLogMessage("Un vaisseau déplacé en " + move.getValue(), this, "normal");
            }
    
            this.game.triggerInterfaceUpdate();
    
            try {
                Thread.sleep(Game.DELAY);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Choose and perform Exterminate
     * @param efficiency Efficiency of the action
     */
    public void doExterminate(int efficiency) {
        System.out.println(this.getPseudo() + " extermine");
        game.getController().getView().addLogMessage("Exterminate (efficacité : " + efficiency + ")", this, "normal");
    
        for (int i = 0; i < efficiency; i++) {
            List<Pair<Set<Ship>, Hexagon>> possibleMoves = possibilities.exterminate(this);
    
            if (possibleMoves.isEmpty()) {
                System.out.println("Aucun mouvement d'extermination possible.");
                game.getController().getView().addLogMessage("Aucune extermination possible", this, "normal");
                return;
            }
    
            Pair<Set<Ship>, Hexagon> move = chooseExterminate(possibleMoves);
    
            this.exterminate.setShips(move.getKey());
            this.exterminate.setTarget(move.getValue());
            this.exterminate.execute();
    
            int fleetSize = move.getKey().size();
            if (fleetSize > 1) {
                game.getController().getView().addLogMessage("Flotte de " + move.getKey().size() + " vaisseaux exterminent en " + move.getValue(), this, "normal");
            } else {
                game.getController().getView().addLogMessage("Un vaisseau extermine en " + move.getValue(), this, "normal");
            }
    
            this.game.triggerInterfaceUpdate();
    
            try {
                Thread.sleep(Game.DELAY);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}