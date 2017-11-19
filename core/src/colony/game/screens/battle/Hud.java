/*
 * see license.txt 
 */
package colony.game.screens.battle;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import colony.game.Logger;
import colony.game.TimeStep;
import colony.game.entities.ActionMeter;
import colony.game.entities.Entity;
import colony.game.screens.battle.ai.AI;
import colony.game.screens.battle.ai.InfluenceMap.Influence;
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
    
    public OrthographicCamera camera, hudCamera;
    
    private BattleScene scene;
    private Vector3 worldPos;
    
    /**
     * 
     */
    public Hud(BattleScene scene, OrthographicCamera camera, OrthographicCamera hudCamera) {
        this.scene = scene;
        this.camera = camera;
        this.hudCamera = hudCamera;
        
        this.worldPos = new Vector3();
        
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
        
        context.setFont("Consola", 28);
        renderEntityInfo(this.scene.getAttacker().getEntities(), context);
        renderEntityInfo(this.scene.getDefender().getEntities(), context);
        
        
      //  renderAI(context, scene.getAI());
    }
    
    private void renderAI(RenderContext context, AI ai) {
        Influence[][] influences = ai.getInfluenceMap().getInfluences();
        
        
        setFont("Consola", 12);
        for(int y = 0; y < influences.length; y++) {
            for(int x = 0; x < influences[y].length; x++) {
                Vector3 worldPos = scene.getWorldPos(x, y);
                                
                Vector3 screenPos = camera.project(worldPos);
                screenPos.y = Gdx.graphics.getHeight() - screenPos.y;
                
                drawString(String.format("I: %3.2f", influences[y][x].influence), screenPos.x, screenPos.y, Color.RED);
                drawString(String.format("T: %3.2f", influences[y][x].tension), screenPos.x, screenPos.y+10, Color.RED);
                drawString(String.format("V: %3.2f", influences[y][x].vulnerability), screenPos.x, screenPos.y+20, Color.RED);
            }
        }
        
    }

    private void renderEntityInfo(List<Entity> entities, RenderContext context) {
        for(int i = 0; i < entities.size(); i++) {
            Entity ent = entities.get(i);
            
            if(ent.isAlive()) {
                String hp = "";
                for(int h = 0; h < ent.getHealth(); h++) {
                    hp += "*";
                }
                
                String max = "";
                for(int h = 0; h < ent.getMaxHealth(); h++) {
                    max += "*";
                }
                
                worldPos.set(ent.pos, 0);
                worldPos.x -= 0.2f;
                worldPos.y += 0.6f;
                
                camera.project(worldPos);
                worldPos.y = Gdx.graphics.getHeight() - worldPos.y;
                
                context.drawString(max, worldPos.x, worldPos.y, Color.WHITE);
                context.drawString(hp, worldPos.x, worldPos.y, Color.NAVY);
                
                ActionMeter meter = ent.getActionMeter();
                context.drawString(meter.getActionPoints() + "/" + meter.getStartingActionPoints(), worldPos.x, worldPos.y + 30f, Color.NAVY);
            }
        }
    }
}
