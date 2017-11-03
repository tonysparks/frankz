/*
 * see license.txt 
 */
package colony.game.entities;

/**
 * @author Tony
 *
 */
public class ActionMeter {

    private int actionPoints;
    private int startingActionPoints;
    
    public ActionMeter(int startingActionPoints) {
        this.setStartingActionPoints(startingActionPoints);
    }
    
    /**
     * @param startingActionPoints the startingActionPoints to set
     */
    public void setStartingActionPoints(int startingActionPoints) {
        this.startingActionPoints = startingActionPoints;
    }
    
    /**
     * @return the startingActionPoints
     */
    public int getStartingActionPoints() {
        return startingActionPoints;
    }
    
    /**
     * @return the actionPoints
     */
    public int getActionPoints() {
        return actionPoints;
    }
    
    public boolean hasPoints() {
        return this.actionPoints > 0;
    }
    
    public boolean hasPoints(int amount) {
        return amount <= this.actionPoints;
    }
    
    public void usePoints(int amount) {
        this.actionPoints -= amount;
    }
    
    /**
     * Reset to the starting action points
     */
    public void reset() {
        this.actionPoints = this.startingActionPoints;
    }

}
