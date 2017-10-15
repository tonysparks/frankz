package colony.game;

import java.io.File;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;

import colony.assets.AssetLoader;
import colony.assets.AssetWatcher;
import colony.assets.FileSystemAssetWatcher;
import colony.assets.JsonAssetLoader;
import colony.assets.PassThruAssetWatcher;
import colony.assets.WatchedAsset;
import colony.game.screens.Screen;
import colony.game.screens.battle.BattleScreen;
import colony.gfx.ImageData;
import colony.gfx.RenderContext;
import colony.gfx.TextureUtil;
import colony.sfx.Sounds;
import colony.util.EventDispatcher;

public class Game extends ApplicationAdapter {
    private TimeStep timeStep;
    private long gameClock;

    private double currentTime;
    private double accumulator;
    private static final double step = 1.0 / 30.0;
    private static final long DELTA_TIME = 1000 / 30;

    private Screen currentScreen;
    private RenderContext context;

    private WatchedAsset<Config> config;
    private boolean isDevelopmentMode;
    
    private AssetWatcher watcher;
    private EventDispatcher dispatcher;
    
    public Game(Config config) {
        this.timeStep = new TimeStep();     
        this.isDevelopmentMode = config.developmentMode;
        
        this.dispatcher = new EventDispatcher();
    }
    
    /**
     * @return the dispatcher
     */
    public EventDispatcher getDispatcher() {
        return dispatcher;
    }
    
    /**
     * @return the config
     */
    public WatchedAsset<Config> getConfig() {
        return config;
    }
    
    
    /**
     * Loads a {@link Sound}
     * 
     * @param filename
     * @return the Sound
     */
    public WatchedAsset<Sound> loadSound(String filename) {
        return this.watcher.loadAsset(filename, new AssetLoader<Sound>() {            
            @Override
            public Sound loadAsset(String filename) {
                return Gdx.audio.newSound(Gdx.files.internal(filename));
            }
        });
    }
    
    
    public WatchedAsset<TextureRegion> loadTexture(ImageData image) {
        if(image.width > 0 && image.height > 0) {            
            return loadTexture(image.filename, image.x, image.y, image.width, image.height);
        }
        return loadTexture(image.filename); 
    }
    
    /**
     * Loads a {@link TextureRegion}
     * 
     * @param filename
     * @return the Texture
     */
    public WatchedAsset<TextureRegion> loadTexture(String filename) {
        return this.watcher.loadAsset(filename, TextureUtil::loadImage);
    }

    /**
     * Loads a {@link TextureRegion}
     * 
     * @param filename
     * @param width
     * @param height
     * @return the texture
     */
    public WatchedAsset<TextureRegion> loadTexture(String filename, int width, int height) {
        return this.watcher.loadAsset(filename, file -> TextureUtil.loadImage(file, width, height));
    }
    
    public WatchedAsset<TextureRegion> loadTexture(String filename, int x, int y, int width, int height) {
        return this.watcher.loadAsset(filename, file -> TextureUtil.subImage(TextureUtil.loadImage(file), x, y, width, height) );
    }
    
    /**
     * Load an asset
     * 
     * @param filename
     * @param type
     * @return
     */
    public <T> WatchedAsset<T> loadAsset(String filename, Class<T> type) {
        return this.watcher.loadAsset(filename, new JsonAssetLoader<>(type));
    }
    
    @Override
    public void create() {
        if(this.isDevelopmentMode) {
            try {
                Logger.log("Development mode activated...");
                this.watcher = new FileSystemAssetWatcher(new File("./assets"));            
            }
            catch(Exception e) {
                Logger.elog("Could not enter development mode!", e);
            }            
        }
        
        if(this.watcher == null) {
            this.watcher = new PassThruAssetWatcher();
        }
        
        this.config = this.watcher.loadAsset("./assets/config.cfg", new JsonAssetLoader<>(Config.class));        
        this.watcher.startWatching();
        
        this.context = new RenderContext();
        this.context.batch = new SpriteBatch();
        this.context.textScaleFactor = BattleScreen.GAME_HEIGHT / Gdx.graphics.getHeight();
        
        this.context.loadFont("./assets/gfx/fonts/Consola.ttf", "Consola");
        this.context.setDefaultFont("Consola", 12);
        
        Sounds.init(this);
        
        this.currentScreen = new BattleScreen(this, "./assets/battle_scene01.json");
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        double newTime = TimeUtils.millis() / 1000.0;
        double frameTime = Math.min(newTime - currentTime, 0.25);

        currentTime = newTime;
        accumulator += frameTime;

        while (accumulator >= step) {
            timeStep.setDeltaTime(DELTA_TIME);
            timeStep.setGameClock(gameClock);

            updateScreen(timeStep);

            accumulator -= step;
            gameClock += DELTA_TIME;
        }

        context.alpha = (float) (accumulator / step);
        renderScreen(context);
    }

    private void updateScreen(TimeStep timeStep) {
        this.watcher.update(timeStep);
        this.currentScreen.update(timeStep);
    }

    private void renderScreen(RenderContext context) {
        this.currentScreen.render(context);
    }

    @Override
    public void resize(int width, int height) {
        this.context.textScaleFactor = BattleScreen.GAME_HEIGHT * Gdx.graphics.getHeight();
        
        this.currentScreen.resize(width, height);
    }

    @Override
    public void dispose() {
        Sounds.destroy();
        this.watcher.stopWatching();
    }
}
