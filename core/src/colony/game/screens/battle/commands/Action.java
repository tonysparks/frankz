/*
 * see license.txt 
 */
package colony.game.screens.battle.commands;

import colony.game.entities.Entity;
import colony.gfx.Renderable;

/**
 * An action to be taken by an {@link Entity}
 * 
 * @author Tony
 *
 */
public interface Action extends Renderable {

    void start();
    void cancel();
    
    CommandResult status();
}
