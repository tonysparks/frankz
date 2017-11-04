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
public abstract class Action implements Renderable {

    protected Command command;
    
    public Action(Command command) {
        this.command = command;
    }
    
    public void start() {
        doStart();
        this.command.emitStartEvent();
    }
    
    public void end() {
        doEnd();
        this.command.emitEndEvent();
    }
    
    abstract protected void doStart();
    abstract protected void doEnd();
    
    abstract public void cancel();
    
    abstract public CommandResult status();
}
