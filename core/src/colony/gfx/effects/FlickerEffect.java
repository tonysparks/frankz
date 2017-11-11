/*
 * see license.txt 
 */
package colony.gfx.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

import colony.game.TimeStep;
import colony.gfx.RenderContext;
import colony.util.Timer;

/**
 * @author Tony
 *
 */
public class FlickerEffect implements Effect {

    private Sprite sprite;
    private Color baseColor, flickerColor;
    private boolean useBaseColor;
    
    private Timer flickerTimer, ttlTimer;

    /**
     * @param sprite
     * @param baseColor
     * @param flickerColor
     */
    public FlickerEffect(Sprite sprite, Color baseColor, Color flickerColor, long ttl, long flickerTime) {
        super();
        this.sprite = sprite;
        this.baseColor = baseColor;
        this.flickerColor = flickerColor;
        
        this.sprite.setColor(flickerColor);
        this.useBaseColor = false;
        
        this.ttlTimer = new Timer(false, ttl);
        this.flickerTimer = new Timer(true, flickerTime);
        
        this.ttlTimer.start();
        this.flickerTimer.start();
    }


    @Override
    public void update(TimeStep timeStep) {
        this.ttlTimer.update(timeStep);
        this.flickerTimer.update(timeStep);
        
        if(this.flickerTimer.isTime()) {
            this.useBaseColor = !this.useBaseColor;
            this.sprite.setColor(useBaseColor?this.baseColor:this.flickerColor);            
        }
    }
    
    @Override
    public void render(RenderContext context) {
    }

    @Override
    public void destroy() {
        this.sprite.setColor(this.baseColor);
    }
    
    @Override
    public void onDone() {
        
    }
    
    @Override
    public boolean isDone() {     
        return this.ttlTimer.isExpired();
    }
}
