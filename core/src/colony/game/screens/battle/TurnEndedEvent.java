/*
 * see license.txt 
 */
package colony.game.screens.battle;

import colony.util.Event;

/**
 * @author Tony
 *
 */
public class TurnEndedEvent extends Event {
    public Turn last;
    public Turn current;
    
    public TurnEndedEvent(Object source, Turn last, Turn current) {
        super(source);
        this.last = last;
        this.current = current;
    }
}
