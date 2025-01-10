package pimperium.utils;

/**
 * Enumeration storing all the available colors for players
 */
public enum Colors {
    RED(0.0f), 
    ORANGE(0.17f),    
    YELLOW(0.33f),   
    GREEN(0.65f),  
    BLUE(-0.65f),    
    PURPLE(-0.33f);    


    private final float hue;

    Colors(float hue) {
        this.hue = hue;
    }

    public float getHue() {
        return hue;
    }
}