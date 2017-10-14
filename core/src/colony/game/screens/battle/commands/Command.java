/*
 * see license.txt 
 */
package colony.game.screens.battle.commands;

import colony.game.entities.Entity;
import colony.game.screens.battle.BattleScene;
import colony.game.screens.battle.commands.CommandResult.CommandResultType;

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
    
    /**
     * 
     */
    public Command(CommandParameters params) {
        this.parameters = params;
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
