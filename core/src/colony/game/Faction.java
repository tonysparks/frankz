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
public class Faction implements Updatable {

    private String name;
    private List<Entity> entities;
    
    /**
     * 
     */
    public Faction(String name) {
        this.name = name;
        this.entities = new ArrayList<>();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this==obj) {
            return true;
        }
        
        if (!(obj instanceof Faction)) {
            return false;
        }
        
        Faction other = (Faction) obj;        
        return other.name.equals(this.name);
    }
    
    @Override
    public void update(TimeStep timeStep) {
        for(int i = 0; i < this.entities.size();) {
            Entity ent = this.entities.get(i);
            if(ent.isAlive()) {
                i++;
            }
            else {
                this.entities.remove(i);
            }
        }
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    
    public Faction addEntity(Entity ent) {
        this.entities.add(ent);
        ent.join(this);
        return this;
    }
    
    /**
     * @return the entities
     */
    public List<Entity> getEntities() {
        return entities;
    }

}
