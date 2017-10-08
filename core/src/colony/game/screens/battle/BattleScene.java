/*
 * see license.txt 
 */
package colony.game.screens.battle;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;

import colony.assets.WatchedAsset;
import colony.game.ColonyGame;
import colony.game.TimeStep;
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
    public static WatchedAsset<BattleScene> loadScene(ColonyGame game, OrthographicCamera camera, String battleSceneFile) {
        return game.loadAsset(battleSceneFile, BattleScene.class).onAssetChanged(scene -> {
            scene.camera = camera;
            
            // build the backgroundSprite sprite
            game.loadTexture(scene.backgroundImage)
                .onAssetChanged( tex -> {
                    scene.backgroundSprite.setRegion(tex);
                    scene.backgroundSprite.setSize(camera.viewportWidth, camera.viewportHeight);
                })
                .touch();                
            

            for(int i = 0; i < scene.objects.length; i++) {
                SceneObject object = scene.objects[i];
                Sprite sprite = new Sprite();
                game.loadTexture(object.image).onAssetChanged( tex -> {                            
                    sprite.setRegion(tex);
                    sprite.setSize(object.width, object.height);
                    sprite.setPosition(object.x, object.y);
                }).touch();
                
                scene.sceneObjectSprites.add(sprite);
            }
        }).touch();
    }
    
    /**
     * An object on the battle scene
     * 
     * @author Tony
     *
     */
    public static class SceneObject {
        public float width;
        public float height;
        public float x;
        public float y;
        
        public String image;
    }
    
    public String backgroundImage;
    public SceneObject[] objects; 
    

    private Sprite backgroundSprite;
    private List<Sprite> sceneObjectSprites;
    
    private Board board;
    
    private OrthographicCamera camera;
    
    /**
     * 
     */
    public BattleScene() {
        this.backgroundSprite = new Sprite();
        this.sceneObjectSprites = new ArrayList<>();
        
        this.board = new Board();
    }
    
    
    @Override
    public void update(TimeStep timeStep) {        
    }
    
    @Override
    public void render(RenderContext context) {
        this.backgroundSprite.draw(context.batch);
        
        // TODO: Apply Z ordering
        for(int i = 0; i < this.sceneObjectSprites.size(); i++) {
            this.sceneObjectSprites.get(i).draw(context.batch);
        }
        
        final float slotSize = 1.524f;
        float startX = 14.8f;
        float startY = 1.7f;
        this.board.forEachSlot( slot -> {
            
        });
    }
}
