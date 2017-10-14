/*
 * see license.txt 
 */
package colony.game.screens.battle.commands;

/**
 * Result of executing or attempting to execute a {@link Command}
 * 
 * @author Tony
 *
 */
public class CommandResult {

    public static enum CommandResultType {
        Success,
        InProgress,
        Failure,
    }
    
    public static final CommandResult InProgress = new CommandResult(CommandResultType.InProgress, "");
    public static final CommandResult Success = new CommandResult(CommandResultType.Success, "");
    public static final CommandResult Cancelled = new CommandResult(CommandResultType.Failure, "Cancelled");
    
    private CommandResultType type;
    private String message;
    
    /**
     * @param type
     * @param message
     */
    public CommandResult(CommandResultType type, String message) {
        this.type = type;
        this.message = message;
    }

    public boolean isFailure() {
        return this.type.equals(CommandResultType.Failure);
    }
    
    public boolean inProgress() {
        return this.type.equals(CommandResultType.InProgress);
    }
    
    /**
     * @return the type
     */
    public CommandResultType getType() {
        return type;
    }
    
    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}
