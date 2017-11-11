/*
 * The Seventh
 * see license.txt 
 */
package colony.gfx.effects;

import colony.gfx.Renderable;

/**
 * A visual effect
 * 
 * @author Tony
 *
 */
public interface Effect extends Renderable {

    /**
     * @return true if this effect is done
     */
    public boolean isDone();
    public default void onDone() {};    
    public void destroy();
}
