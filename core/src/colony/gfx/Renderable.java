/*
 * see license.txt 
 */
package colony.gfx;

import colony.game.Updatable;

/**
 * Something that can be rendered
 * 
 * @author Tony
 *
 */
public interface Renderable extends Updatable {

    void render(RenderContext context);
}
