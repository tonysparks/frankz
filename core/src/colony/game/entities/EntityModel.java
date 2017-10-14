/*
 * see license.txt 
 */
package colony.game.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;

import colony.game.Game;
import colony.game.TimeStep;
import colony.gfx.RenderContext;
import colony.gfx.Renderable;

/**
 * @author Tony
 *
 */
public class EntityModel implements Renderable {

    public static EntityModel loadEntityModel(Game game, Entity ent, EntityModelData data) {
        EntityModel model = new EntityModel(ent);
        
        game.loadTexture(data.image).onAssetChanged(tex -> {
            model.sprite.setRegion(tex);
            
            float width = ent.bounds.width;
            float height = ent.bounds.height;
            if(data.width > 0) {
                width = data.width;
            }
            
            if(data.height > 0) {
                height = data.height;
            }
            
            model.sprite.setSize(width, height);
            
        }).touch();
        
        
        return model;
    }
    
    private Entity entity;    
    private Sprite sprite;
    
    /**
     * @param entity
     */
    private EntityModel(Entity entity) {
        this.entity = entity;
        this.sprite = new Sprite();
        
    }

    @Override
    public void update(TimeStep timeStep) {        
    }
    
    @Override
    public void render(RenderContext context) {
        float x = entity.bounds.x;
        float y = entity.bounds.y;
                
        this.sprite.setPosition(x, y);
        this.sprite.draw(context.batch);
    }
}
