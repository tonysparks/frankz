/*
 * see license.txt 
 */
package colony.game.screens;

import colony.game.TimeStep;
import colony.gfx.RenderContext;

/**
 * @author Tony
 *
 */
public interface Screen {
    public void update(TimeStep timeStep);
    public void render(RenderContext context);
    public void resize(int width, int height);
}
