package colony.game.desktop;

import java.io.File;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import colony.assets.JsonAssetLoader;
import colony.game.Game;
import colony.game.Config;

public class DesktopLauncher {
	public static void main (String[] arg) {
	    String filename = "./config.cfg";
	    if(new File("./assets/config.cfg").exists()) {
	        filename = "./assets/config.cfg";
	    }
	    
	    Config cfg = JsonAssetLoader.loadAsset(Config.class, filename);
	    
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Franks";		
		config.fullscreen = cfg.video.fullscreen;
		config.vSyncEnabled = cfg.video.vSync;
		
		config.width = cfg.video.width;
		config.height = cfg.video.height;
		
		config.x = -1;
		config.y = -1;
				
		new LwjglApplication(new Game(cfg), config);
	}
}
