/*
 * see license.txt 
 */
package colony.game.screens.battle;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;

import colony.assets.WatchedAsset;
import colony.game.ColonyGame;
import colony.game.TimeStep;
import colony.game.screens.Screen;
import colony.game.screens.battle.Board.Slot;
import colony.gfx.RenderContext;

/**
 * @author Tony
 *
 */
public class BattleScreen implements Screen {

    /**
     * Battle scene is 100ft x 100ft which is roughly
     * 30m x 30m
     */
    private static final float GAME_WIDTH  = 30;
    private static final float GAME_HEIGHT = 30;
    
    private OrthographicCamera camera;
    private Matrix4 transform;
    private ShapeRenderer shapes;
    
    private WatchedAsset<BattleScene> battleScene;
    
    /**
     * 
     */
    public BattleScreen(ColonyGame game, String battleSceneFile) {
        this.shapes = new ShapeRenderer();
        this.transform = new Matrix4();

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(true, GAME_WIDTH, GAME_HEIGHT * (h / w));     
        this.camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        this.camera.update();
        
        this.battleScene = BattleScene.loadScene(game, this.camera, battleSceneFile);
        
        Gdx.input.setInputProcessor(new BattleScreenInputProcessor(this));
    }
    
    /**
     * @return the camera
     */
    public OrthographicCamera getCamera() {
        return camera;
    }
    

    @Override
    public void update(TimeStep timeStep) {
        handleInput();
        
        this.battleScene.getAsset().update(timeStep);
    }

    @Override
    public void render(RenderContext context) {
        this.camera.update();
        
        context.batch.setProjectionMatrix(this.camera.combined);
        context.batch.setTransformMatrix(this.transform);

        shapes.setProjectionMatrix(this.camera.combined);
        shapes.setTransformMatrix(this.transform);
        
        float effectiveViewportWidth = camera.viewportWidth;// * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight;// * camera.zoom;
        context.fillRect(shapes, 0, 0, effectiveViewportWidth, effectiveViewportHeight, Color.valueOf("6B8E23"));
        
        context.batch.begin();       
        
        BattleScene scene = this.battleScene.getAsset();
        scene.render(context);
        
        context.batch.end();
    }
    
    private void handleInput() {
        float zoom = 0.02f;
        float move = 0.94f;
        
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            camera.zoom = 1f;
        }
        
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.zoom += zoom;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom -= zoom;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-move, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(move, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, move, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, -move, 0);
        }
        /*if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.rotate(-rotationSpeed, 0, 0, 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.rotate(rotationSpeed, 0, 0, 1);
        }*/

        final float maxZoom = 200;
        
        camera.zoom = MathUtils.clamp(camera.zoom, 0.1f, maxZoom/camera.viewportWidth);

        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, maxZoom - effectiveViewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, maxZoom - effectiveViewportHeight / 2f);
        
    }

    /* (non-Javadoc)
     * @see colony.game.screens.Screen#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {
        this.camera.viewportWidth = GAME_WIDTH;
        this.camera.viewportHeight = GAME_HEIGHT * height/width;
        this.camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        this.camera.update();
    }
}
