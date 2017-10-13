/*
 * see license.txt 
 */
package colony.game.screens.battle;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import colony.game.ColonyGame;
import colony.game.Logger;
import colony.game.TimeStep;
import colony.game.screens.battle.BattleSceneData.SceneObject;
import colony.game.screens.battle.Board.Slot;
import colony.gfx.RenderContext;
import colony.gfx.Renderable;

/**
 * Describes a Battle Scene
 * 
 * @author Tony
 *
 */
public class BattleScene implements Renderable {

    /**
     * Loads a {@link BattleScene}
     * 
     * @param game
     * @param battleSceneFile
     * @return
     */
    public static BattleScene loadScene(ColonyGame game, Hud hud, OrthographicCamera camera, String battleSceneFile) {
        final BattleScene scene = new BattleScene();
        scene.camera = camera;
        scene.hud = hud;
        
        game.loadAsset(battleSceneFile, BattleSceneData.class).onAssetChanged(data -> {
            
            // build the backgroundSprite sprite
            game.loadTexture(data.backgroundImage)
                .onAssetChanged( tex -> {
                    scene.backgroundSprite.setRegion(tex);
                    scene.backgroundSprite.setSize(camera.viewportWidth, camera.viewportHeight);
                })
                .touch();                
                        
            for(int i = 0; i < scene.slotImages.length; i++) {
                Sprite sprite = new Sprite();
                scene.slotImages[i] = sprite; 
                game.loadTexture(data.slotImage).onAssetChanged(tex -> {
                   sprite.setRegion(tex);
                   sprite.setSize(Board.Slot.WIDTH, Board.Slot.HEIGHT/2.0f);                   
                }).touch();
            }

            scene.sceneObjectSprites.clear();
            for(int i = 0; i < data.objects.length; i++) {
                SceneObject object = data.objects[i];
                Sprite sprite = new Sprite();
                game.loadTexture(object.image).onAssetChanged( tex -> {                            
                    sprite.setRegion(tex);
                    sprite.setSize(object.width, object.height);
                    sprite.setPosition(object.x, object.y);
                    sprite.setRotation(object.rotation);
                    sprite.flip(object.flipX, object.flipY);
                }).touch();
                
                scene.sceneObjectSprites.add(sprite);
                scene.sceneObjectSprites.sort( (a,b) -> (int)a.getY() - (int)b.getY());
            }
        }).touch();
        
        return scene;
    }
           

    private Sprite backgroundSprite;
    private Sprite[] slotImages;
    
    private List<Sprite> sceneObjectSprites;
    
    private Board board;
    
    private OrthographicCamera camera;
    
    private Hud hud;
    
    private static final float startX = 14.8f;
    private static final float startY = 1.7f;
        
    private static final float tileHalfWidth  = Board.Slot.WIDTH;
    private static final float tileHalfHeight = Board.Slot.HEIGHT/2.0f;
    
    //private static final float tileSpriteWidth  = 1.4f * 2.0f;
    private static final float tileSpriteWidth  = Board.Slot.WIDTH * 2.0f;
    private static final float tileSpriteHeight = tileSpriteWidth / 2.0f;
    
    /**
     * 
     */
    public BattleScene() {               
        this.backgroundSprite = new Sprite();
        this.sceneObjectSprites = new ArrayList<>();
        
        this.slotImages = new Sprite[Board.HEIGHT * Board.WIDTH];
        
        this.board = new Board();
    }
    
    
    @Override
    public void update(TimeStep timeStep) {        
    }
    
    @Override
    public void render(RenderContext context) {
        this.backgroundSprite.draw(context.batch);
                
        hud.setFont("Consola", 12);
        
     //   final float slotSize = 1.524f;        
        Vector3 worldPos = new Vector3();
        int[] index = {0};
        this.board.forEachSlot( slot -> {
            worldPos.x = ((slot.bounds.x - slot.bounds.y) * tileHalfWidth)  + startX;
            worldPos.y = ((slot.bounds.x + slot.bounds.y) * tileHalfHeight) + startY;
            

            Sprite sprite = slotImages[index[0]++];
            sprite.setPosition(worldPos.x - tileHalfWidth, worldPos.y - tileHalfHeight);
            sprite.setSize(tileSpriteWidth, tileSpriteHeight);
            sprite.draw(context.batch);
            
            float x = worldPos.x;
            float y = worldPos.y;
            
            Vector3 screenPos = camera.project(worldPos);
            hud.drawString( (int)slot.x + "," +  (int)slot.y, screenPos.x, screenPos.y, Color.RED);
            
            String str = String.format("%3.2f, %3.2f", x, y);
            hud.drawString( str, screenPos.x-25, screenPos.y+15, Color.RED);
        });
        

        // TODO: Apply Z ordering
        for(int i = 0; i < this.sceneObjectSprites.size(); i++) {
            this.sceneObjectSprites.get(i).draw(context.batch);
        }
    }
    
    public Slot getSlot(float worldX, float worldY) {
        float adjScreenX = worldX - startX;// - tileHalfWidth;
        float adjScreenY = worldY - startY;// - tileHalfHeight;
        
        int x = (int) ((adjScreenY / tileHalfHeight) + (adjScreenX / tileHalfWidth));
        int y = (int) ((adjScreenY / tileHalfHeight) - (adjScreenX / tileHalfWidth));
        
        int indexX = x / 2;
        int indexY = y / 2;
               
        
        Logger.log("Index: " + indexX + ", " + indexY);
        
        return this.board.getSlotByIndex(indexX, indexY);
    }
}
