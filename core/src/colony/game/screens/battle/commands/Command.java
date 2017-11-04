/*
 * see license.txt 
 */
package colony.game.screens.battle.commands;

import java.util.function.Consumer;

import colony.game.entities.Entity;
import colony.game.screens.battle.BattleScene;
import colony.game.screens.battle.commands.CommandResult.CommandResultType;
import colony.game.screens.battle.commands.events.ActionEndedEvent;
import colony.game.screens.battle.commands.events.ActionStartedEvent;
import colony.util.EventDispatcher;

/**
 * A {@link Command} is something issued by a player to
 * an {@link Entity} which causes the {@link Entity} to do
 * an {@link Action}
 * 
 * @author Tony
 *
 */
public abstract class Command {

    protected CommandParameters parameters;
    protected EventDispatcher dispatcher;
    /**
     * 
     */
    public Command(CommandParameters params) {
        this.parameters = params;
        this.dispatcher = new EventDispatcher();
    }
    
    public Command onStart(Consumer<ActionStartedEvent> listener) {
        this.dispatcher.addEventListener(ActionStartedEvent.class, listener);
        return this;
    }
    
    public Command onEnd(Consumer<ActionEndedEvent> listener) {
        this.dispatcher.addEventListener(ActionEndedEvent.class, listener);
        return this;
    }
    
    protected void emitStartEvent() {
        this.dispatcher.sendNow(new ActionStartedEvent(this, this));
    }
    
    protected void emitEndEvent() {
        this.dispatcher.sendNow(new ActionEndedEvent(this, this));
    }

    protected CommandResult failed(String message) {
        return new CommandResult(CommandResultType.Failure, message);
    }
    
    protected CommandResult inProgress() {
        return CommandResult.InProgress;
    }
    
    protected CommandResult success() {
        return CommandResult.Success;
    }
    
    
    /**
     * Checks to see if this command can be executed
     * 
     * @param scene
     * @return 
     */
    public abstract CommandResult checkPreconditions(BattleScene scene);
    
    /**
     * Creates the associated action for this command
     * 
     * @param scene
     * @return
     */
    public abstract Action createAction(BattleScene scene);
}
