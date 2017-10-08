/*
 * see license.txt 
 */
package colony.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * @author Tony
 *
 */
public class RenderContext {

    /**
     * Render left over time 
     */
    public float alpha;
 
    public SpriteBatch batch;
    
    public void drawLine(ShapeRenderer shapes, int x1, int y1, int x2, int y2, Color color) {
        
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.setColor(color);
        
        shapes.begin(ShapeType.Line);
        shapes.line(x1, y1, x2, y2);
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void drawLine(ShapeRenderer shapes, float x1, float y1, float x2, float y2, Color color) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.setColor(color);
        
        shapes.begin(ShapeType.Line);
        shapes.line(x1, y1, x2, y2);
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void drawRect(ShapeRenderer shapes, int x, int y, int width, int height, Color color) {        
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.setColor(color);
        
        shapes.begin(ShapeType.Line);
        shapes.rect(x, y, width, height);
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    
    public void drawRect(ShapeRenderer shapes, float x, float y, float width, float height, Color color) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.setColor(color);
        
        shapes.begin(ShapeType.Line);
        shapes.rect(x, y, width, height);
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void fillRect(ShapeRenderer shapes, int x, int y, int width, int height, Color color) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);                
        
        shapes.begin(ShapeType.Filled);
        shapes.setColor(color);
        shapes.rect(x, y, width, height);
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

    }
    
    public void fillRect(ShapeRenderer shapes, float x, float y, float width, float height, Color color) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);                
        
        shapes.begin(ShapeType.Filled);
        shapes.setColor(color);
        shapes.rect(x, y, width, height);
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void drawCircle(ShapeRenderer shapes, float radius, int x, int y, Color color) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.setColor(color);
        
        shapes.begin(ShapeType.Line);
        shapes.circle(x+radius, y+radius, radius);
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    
    public void drawCircle(ShapeRenderer shapes, float radius, float x, float y, Color color) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.setColor(color);
        
        shapes.begin(ShapeType.Line);
        shapes.circle(x+radius, y+radius, radius);
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void fillCircle(ShapeRenderer shapes, float radius, int x, int y, Color color) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.setColor(color);
        
        shapes.begin(ShapeType.Filled);
        shapes.circle(x+radius, y+radius, radius);
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    
    public void fillCircle(ShapeRenderer shapes, float radius, float x, float y, Color color) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.setColor(color);
        
        shapes.begin(ShapeType.Filled);
        shapes.circle(x+radius, y+radius, radius);
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
