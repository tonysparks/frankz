/*
 * see license.txt 
 */
package colony.game;

/**
 * @author Tony
 *
 */
public class Player {

    private String name;
    private Faction faction;
    
    /**
     * 
     */
    public Player(String name, Faction faction) {
        this.name = name;
        this.faction = faction;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return the faction
     */
    public Faction getFaction() {
        return faction;
    }
    
}
