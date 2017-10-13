/*
 * see license.txt 
 */
package colony.game.screens.battle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;

import colony.game.Logger;
import colony.game.TimeStep;
import colony.gfx.RenderContext;
import colony.gfx.Renderable;

/**
 * @author Tony
 *
 */
public class Hud implements Renderable {

    static class RenderText {
        String font;
        int size;
        
        String text;        
        float x,y;
        Color color;
                
        void set(String font, int size, String text, float x, float y, Color color) {
            this.font = font;
            this.size = size;
            
            this.text = text;
            this.x = x;
            this.y = y;
            this.color = color;            
        }
    }
        
    private RenderText[] textQueue;    
    private int textQueueCount;
    
    private String font;
    private int size;
    
    public OrthographicCamera camera;
    
    /**
     * 
     */
    public Hud(OrthographicCamera camera) {
        this.camera = camera;
        
        this.textQueue = new RenderText[1024];
        this.textQueueCount = 0;
        
        for(int i = 0; i < this.textQueue.length; i++) {
            this.textQueue[i] = new RenderText();
        }
    }
    
    public void setFont(String font, int size) {
        this.font = font;
        this.size = size;
    }

    public void drawString(String text, float x, float y, Color color) {
        if(this.textQueueCount + 1 > this.textQueue.length) {
            Logger.elog("Reached HUD draw text limit!");
        }
        else {
            this.textQueue[this.textQueueCount++].set(this.font, this.size, text, x, y, color);
        }
    }
    
    
    @Override
    public void update(TimeStep timeStep) {

    }

    @Override
    public void render(RenderContext context) {        
        for(int i = 0; i < this.textQueueCount; i++) {
            RenderText text = this.textQueue[i];
            context.setFont(text.font, text.size);
            context.drawString(text.text, text.x, text.y, text.color);            
        }
        
        this.textQueueCount = 0;        
    }

}
