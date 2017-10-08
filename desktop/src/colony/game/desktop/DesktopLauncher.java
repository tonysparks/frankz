package colony.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import colony.assets.JsonAssetLoader;
import colony.game.ColonyGame;
import colony.game.Config;

public class DesktopLauncher {
	public static void main (String[] arg) {	    
	    Config cfg = JsonAssetLoader.loadAsset(Config.class, "./assets/config.cfg");
	    
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Franks";		
		config.fullscreen = cfg.video.fullscreen;
		config.vSyncEnabled = cfg.video.vSync;
		
		config.width = cfg.video.width;
		config.height = cfg.video.height;
		
		config.x = -1;
		config.y = -1;
				
		new LwjglApplication(new ColonyGame(cfg), config);
	}
}
