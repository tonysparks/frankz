/*
 * see license.txt 
 */
package colony.game.screens.battle.commands.events;

import colony.game.screens.battle.commands.Command;
import colony.util.Event;

/**
 * @author Tony
 *
 */
public class ActionEndedEvent extends Event {

    private Command command;
    
    /**
     * @param source
     */
    public ActionEndedEvent(Object source, Command cmd) {
        super(source);
        this.command = cmd;
    }

    /**
     * @return the command
     */
    public Command getCommand() {
        return command;
    }
}
