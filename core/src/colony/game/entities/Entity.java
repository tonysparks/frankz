/*
 * see license.txt 
 */
package colony.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import colony.game.Game;
import colony.game.TimeStep;
import colony.game.Updatable;
import colony.gfx.PositionableRenderable;
import colony.gfx.RenderContext;

/**
 * 
 * 
 * @author Tony
 *
 */
public class Entity implements Updatable, PositionableRenderable {
    
    public static Entity loadEntity(Game game, String entityType) {
        Entity ent = new Entity();
        game.loadAsset(entityType, EntityData.class).onAssetChanged( data -> {
            ent.bounds.width = data.width;
            ent.bounds.height = data.height;
                        
            if(ent.pos.isZero()) {
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
    public Vector2 centerPos;
    public Rectangle bounds;
    
    public EntityModel model;
    
    /**
     * 
     */
    public Entity() {
        this.pos = new Vector2();
        this.centerPos = new Vector2();
        this.bounds = new Rectangle();
    }
    
    /**
     * @return the pos
     */
    public Vector2 getPos() {
        return pos;
    }

    
    /**
     * @return the centerPos
     */
    public Vector2 getCenterPos() {
        centerPos.x = pos.x + bounds.width/2;
        centerPos.y = pos.y + bounds.height/2;
        return centerPos;
    }
    
    /**
     * @return the bounds
     */
    public Rectangle getBounds() {
        return bounds;
    }
    
    public Entity setPos(Vector3 pos) {
        return setPos(pos.x, pos.y);
    }
    
    public Entity setPos(Vector2 pos) {
        return setPos(pos.x, pos.y);
    }
    
    public Entity setPos(float x, float y) {
        this.pos.set(x, y);
        

        float offsetX = this.bounds.width / 2;
        float offsetY = this.bounds.height;
      
        x -= offsetX - 0.2f;
        y -= offsetY - 0.2f;
        
        this.bounds.setPosition(x, y);
        return this;
    }
    
    public Entity moveBy(float deltaX, float deltaY) {
        setPos(this.pos.x + deltaX, this.pos.y + deltaY);
        return this;
    }
        
    @Override
    public float getX() {    
        return pos.x;
    }
    
    @Override
    public float getY() {    
        return pos.y;
    }

    @Override
    public void update(TimeStep timeStep) {
        this.model.update(timeStep);
    }
    
    @Override
    public void render(RenderContext context) {        
        this.model.render(context);    
        
        
//        context.batch.end();
//        context.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, Color.GOLD);
//        context.drawRect(pos.x, pos.y, 0.15f, 0.15f, Color.BLUE);
//        context.batch.begin();
                
    }
}

