/*
 * see license.txt 
 */
package colony.game.screens.battle;

import java.util.Random;

/**
 * @author Tony
 *
 */
public class Dice {

    private Random rand;
    /**
     * 
     */
    public Dice() {
        this.rand = new Random();
    }
    
    /**
     * D10 dice roll (1 in 10)
     * 
     * @return d10 dice roll
     */
    public int d10() {
        int d10 = rand.nextInt(10) * 10;
        return d10;
    }

    public int rand(int max) {
        return this.rand.nextInt(max);
    }
    
    public int rand(int min, int max) {
        return min + this.rand.nextInt(max - min);
    }
}
