/*
 * see license.txt 
 */
package colony.game;

import java.util.ArrayList;
import java.util.List;

import colony.game.entities.Entity;

/**
 * @author Tony
 *
 */
public class Faction {

    private String name;
    private List<Entity> entities;
    
    /**
     * 
     */
    public Faction(String name) {
        this.name = name;
        this.entities = new ArrayList<>();
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    
    public Faction addEntity(Entity ent) {
        this.entities.add(ent);
        return this;
    }
    
    /**
     * @return the entities
     */
    public List<Entity> getEntities() {
        return entities;
    }

}
