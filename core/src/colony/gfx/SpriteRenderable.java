/*
 * see license.txt 
 */
package colony.gfx;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * @author Tony
 *
 */
public class SpriteRenderable implements PositionableRenderable {

    private Sprite sprite;
    
    public SpriteRenderable(Sprite sprite) {
        this.sprite = sprite;
    }
    
    @Override
    public float getX() {
        return sprite.getX();
    }

    @Override
    public float getY() {     
        return sprite.getY();
    }
    @Override
    public void render(RenderContext context) {
        sprite.draw(context.batch);
    }

}
