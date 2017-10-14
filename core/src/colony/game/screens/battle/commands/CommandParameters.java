/*
 * see license.txt 
 */
package colony.game.screens.battle.commands;

import colony.game.entities.Entity;
import colony.game.screens.battle.Board.Slot;

/**
 * Parameters to be used by a {@link Command}
 * 
 * @author Tony
 *
 */
public class CommandParameters {

    public Entity selectedEntity;
    public Entity targetEntity;
    public Slot targetSlot;
    
    /**
     * @param selectedEntity
     * @param targetEntity
     * @param targetSlot
     */
    public CommandParameters(Entity selectedEntity, Entity targetEntity, Slot targetSlot) {
        super();
        this.selectedEntity = selectedEntity;
        this.targetEntity = targetEntity;
        this.targetSlot = targetSlot;
    }
    
    /**
     * @param selectedEntity
     * @param targetEntity
     */
    public CommandParameters(Entity selectedEntity, Entity targetEntity) {
        this(selectedEntity, targetEntity, null);
    }
    
    /**
     * @param selectedEntity
     * @param targetSlot
     */
    public CommandParameters(Entity selectedEntity, Slot targetSlot) {
        this(selectedEntity, null, targetSlot);
    }

    /**
     * @param selectedEntity
     */
    public CommandParameters(Entity selectedEntity) {
        this(selectedEntity, null, null);
    }

}
