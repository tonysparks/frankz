/*
 * see license.txt 
 */
package colony.gfx;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;

/**
 * @author Tony
 *
 */
public class RenderContext {

    private int fontSize;
    private int defaultFontSize;
    private String currentFontName;
    private String defaultFontName;
    
    private Map<String, FreeTypeFontGenerator> generators;
    private Map<String, BitmapFont> fonts;
    private BitmapFont font, defaultFont;
    private GlyphLayout bounds;

    
    /**
     * Render left over time 
     */
    public float alpha;
    
    public float textScaleFactor = 1.0f;
 
    public SpriteBatch batch;
    
    /**
     * 
     */
    public RenderContext() {
        this.generators = new HashMap<String, FreeTypeFontGenerator>();
        this.fonts = new HashMap<String, BitmapFont>();
        this.bounds = new GlyphLayout();
    }

        
    
    public void setProjectionTransform(Matrix4 projection, Matrix4 transform) {
        this.batch.setProjectionMatrix(projection);
        this.batch.setTransformMatrix(transform);
    }
    
    
    public void loadFont(String filename, String alias) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(filename));        
        this.generators.put(alias, generator);        
    }

    public void setFont(String alias, int size) {
        if(this.currentFontName == null || !this.currentFontName.equals(alias) || this.fontSize != size) {
            this.font = getFont(alias, size);                        
            this.fontSize = size;
            this.currentFontName = alias;
        }
    }
    
  
    public void setDefaultFont(String alias, int size) {
        this.defaultFont = getFont(alias, size);        
        this.defaultFontSize = size;
        this.defaultFontName = alias;
    }
    
    public void resizeFont(int size) {
        if(font!=null) {                
            if(this.fontSize != 0 && this.fontSize != size) {
                setFont(currentFontName, size);
            }            
        }
    }
    
    /**
     * Retrieve the desired font and size (may load the font
     * if not cached).
     * 
     * @param alias
     * @param size
     * @return the font
     */
    public BitmapFont getFont(String alias, int size) {
        BitmapFont font = null;
        
        String mask = alias + ":" + size;
        if(this.fonts.containsKey(mask)) {
            font = this.fonts.get(mask);
        }
        else if(this.generators.containsKey(alias)) {
            FreeTypeFontParameter params = new FreeTypeFontParameter();
            params.size = size;
            params.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
            params.flip = true;
            params.magFilter = TextureFilter.Linear;
            params.minFilter = TextureFilter.Linear;            
                        
            font = this.generators.get(alias).generateFont(params);
           // font.getData().setScale(this.textScaleFactor);
            
            this.fonts.put(mask, font);
        }
        
        return font;
    }
        
    public void setDefaultFont() {
        this.font = defaultFont;
        this.fontSize = this.defaultFontSize;
        this.currentFontName = this.defaultFontName;
    }
    
    public GlythData getGlythData(String alias, int size) {
        BitmapFont font = getFont(alias, size);
        return new GlythData(font, bounds);
    }
    
    public GlythData getGlythData() {
        return new GlythData(font, bounds);
    }
    
    public int getWidth(String str) {
        return GlythData.getWidth(font, bounds, str);
    }

    public int getHeight(String str) {
        return GlythData.getHeight(font, bounds, str);
    }
    
    
    public void drawString(String text, int x, int y, Color color) {                
        if(font!=null) {            
            font.setColor(color);                    
            font.draw(batch, text, x, y - font.getCapHeight());
        }
    }
    
    public void drawString(String text, float x, float y, Color color) {
        if(font!=null) {            
            font.setColor(color);             
            font.draw(batch, text, x, y - font.getCapHeight());
        }        
    }
    
    
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
