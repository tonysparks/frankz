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
    
    /**
     * @param percentage the percentage of the chance being true. Range 0 to 1.0
     * @return randomly returns true or false; the frequency is of which is
     * influenced by the supplied percentage.  A 0% will always return false,
     * and a 100% chance will always return true;
     */
    public boolean chances(double percentage) {
        return rand.nextDouble() < percentage;
    }
    
    public double getRandomRange(double min, double max) {
        return getRandomRange(this.rand, min, max);
    }    
    
    public double getRandomRangeMin(double min) {
        return getRandomRangeMin(this.rand, min);
    }
    
    public double getRandomRangeMax(double max) {
        return getRandomRangeMax(this.rand, max);
    }
    
    
    public static double getRandomRange(Random random, double min, double max) {
        return min + (random.nextDouble() * (max - min));
    }    
    
    public static double getRandomRangeMin(Random random, double min) {
        return getRandomRange(random, min, 1.0);
    }
    
    public static double getRandomRangeMax(Random random, double max) {
        return getRandomRange(random, 0.0, max);
    }
}
