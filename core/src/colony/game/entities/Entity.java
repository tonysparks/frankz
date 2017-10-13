/*
 * see license.txt 
 */
package colony.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import colony.game.ColonyGame;
import colony.game.TimeStep;
import colony.gfx.RenderContext;
import colony.gfx.Renderable;

/**
 * 
 * 
 * @author Tony
 *
 */
public class Entity implements Renderable {
    
    public Entity loadEntity(ColonyGame game, String entityType) {
        Entity ent = new Entity();
        game.loadAsset(entityType, EntityData.class).onAssetChanged( data -> {
            ent.bounds.width = data.width;
            ent.bounds.height = data.height;
            
            if(!ent.pos.isZero()) {
                ent.pos.x = data.x;
                ent.pos.y = data.y;
            }
            
            if(data.model != null) {
                ent.model = EntityModel.loadEntityModel(game, ent, data.model);
            }
            
        }).touch();
        
        return ent;
    }
    
    public Vector2 pos;
    public Rectangle bounds;
    
    public EntityModel model;
    
    /**
     * 
     */
    public Entity() {
        this.pos = new Vector2();
        this.bounds = new Rectangle();
    }

    @Override
    public void update(TimeStep timeStep) {
        this.model.update(timeStep);
    }
    
    @Override
    public void render(RenderContext context) {
        this.model.render(context);
    }
}
