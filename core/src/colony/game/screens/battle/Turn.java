/*
 * see license.txt 
 */
package colony.game.screens.battle;

import colony.game.Faction;

/**
 * @author Tony
 *
 */
public class Turn {

    private int number;
    private Faction factionsTurn;
    
    
    
    /**
     * @param number
     * @param factionsTurn
     */
    public Turn(int number, Faction factionsTurn) {
        super();
        this.number = number;
        this.factionsTurn = factionsTurn;
    }

    /**
     * Ends this turn, creating the next {@link Turn}
     * 
     * @param scene
     * @return the next {@link Turn} or if we are not able to
     * end the current turn, the current {@link Turn} is returned.
     */
    public Turn end(BattleScene scene) {
        if(scene.hasPendingCommands()) {
            return this;
        }
        
        return new Turn(this.number+1, scene.getEnemy(this.factionsTurn));
    }
    
    /**
     * @return the factionsTurn
     */
    public Faction getFactionsTurn() {
        return factionsTurn;
    }
    
    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }
}
