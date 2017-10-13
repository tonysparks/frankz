/*
 * see license.txt 
 */
package colony.game.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;

import colony.game.ColonyGame;
import colony.game.TimeStep;
import colony.gfx.RenderContext;
import colony.gfx.Renderable;

/**
 * @author Tony
 *
 */
public class EntityModel implements Renderable {

    public static EntityModel loadEntityModel(ColonyGame game, Entity ent, EntityModelData data) {
        EntityModel model = new EntityModel(ent);
        
        game.loadTexture(data.image).onAssetChanged(tex -> {
            model.sprite.setRegion(tex);
            model.sprite.setSize(data.width, data.height);
        }).touch();
        
        
        return model;
    }
    
    private Entity entity;    
    private Sprite sprite;
    
    /**
     * @param entity
     */
    private EntityModel(Entity entity) {
        this.sprite = new Sprite();
        
    }

    @Override
    public void update(TimeStep timeStep) {        
    }
    
    @Override
    public void render(RenderContext context) {
        this.sprite.setPosition(entity.pos.x, entity.pos.y);
        this.sprite.draw(context.batch);
    }
}
