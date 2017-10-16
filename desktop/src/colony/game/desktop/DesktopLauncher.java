package colony.game.desktop;

import java.io.File;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;

import colony.assets.JsonAssetLoader;
import colony.game.Config;
import colony.game.Game;


/**
 * Desktop version of the game
 * 
 * @author Tony
 *
 */
public class DesktopLauncher {
	public static void main (String[] arg) {
	    /*
         * LibGDX spawns another thread which we don't have access
         * to for catching its exceptions
         */
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if(t.getName().equals("LWJGL Application")) {
                    catchException(e);
                }
            }
        });
	    
	    String filename = "./config.cfg";
	    if(new File("./assets/config.cfg").exists()) {
	        filename = "./assets/config.cfg";
	    }
	    
	    Config cfg = JsonAssetLoader.loadAsset(Config.class, filename);
	    
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Franks v" + Game.VERSION;		
		config.fullscreen = cfg.video.fullscreen;
		config.vSyncEnabled = cfg.video.vSync;
		
		config.width = cfg.video.width;
		config.height = cfg.video.height;
		
		config.x = -1;
		config.y = -1;
				
		new LwjglApplication(new Game(cfg), config);
	}
	
	
    /**
     * Handle the exception -- piping out to a log file
     * @param e
     */
    private static void catchException(Throwable e) {
        try {
            PrintStream logger = new PrintStream(new File("./franks_error.log"));
            try {                
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                logger.println("Date: " + formatter.format(new Date()));
                logSystemSpecs(logger);
                logVideoSpecs(logger);
                
                e.printStackTrace(logger);
            }
            finally {
                logger.close();
            }
        }
        catch(Exception e1) {
            e1.printStackTrace();
        }
        finally {
            System.exit(1);
        }
    }
    
    public static void logVideoSpecs(PrintStream console) {
        try {
            if(Gdx.graphics!=null) {                
                console.println("GL30: " + Gdx.graphics.isGL30Available());
                console.println("OpenGL Version: " + Gdx.gl.glGetString(GL20.GL_VERSION));
                console.println("OpenGL Vendor: " + Gdx.gl.glGetString(GL20.GL_VENDOR));
                console.println("Renderer: " + Gdx.gl.glGetString(GL20.GL_RENDERER));
                console.println("Gdx Version: " + Gdx.app.getVersion());
                console.println("Is Fullscreen: " + Gdx.graphics.isFullscreen());
            }
            else {
                console.println("OpenGL Version: " + Gdx.gl.glGetString(GL20.GL_VERSION));
                console.println("OpenGL Vendor: " + Gdx.gl.glGetString(GL20.GL_VENDOR));
                console.println("Renderer: " + Gdx.gl.glGetString(GL20.GL_RENDERER));                
            }
        }
        catch(Throwable t) {
            console.println("Error retrieving video specifications: " + t);
        }
    }

    /**
     * Prints out system specifications
     * 
     * @param console
     */
    public static void logSystemSpecs(PrintStream console) {
        Runtime runtime = Runtime.getRuntime();
        final long MB = 1024 * 1024;
        console.println("");
        console.println("Franks: " + Game.VERSION);
        console.println("Available processors (cores): " + runtime.availableProcessors());
        console.println("Free memory (MiB): " + runtime.freeMemory()/MB);
        console.println("Max memory (MiB): " + (runtime.maxMemory()==Long.MAX_VALUE ? "no limit" : Long.toString(runtime.maxMemory()/MB)) );
        console.println("Available for JVM (MiB): " + runtime.totalMemory() / MB);
        
        /* Get a list of all filesystem roots on this system */
        File[] roots = File.listRoots();

        /* For each filesystem root, print some info */
        for (File root : roots) {
          console.println("File system root: " + root.getAbsolutePath());
          console.println("\tTotal space (MiB): " + root.getTotalSpace()/MB);
          console.println("\tFree space (MiB): " + root.getFreeSpace()/MB);
          console.println("\tUsable space (MiB): " + root.getUsableSpace()/MB);
        }
        
        
        console.println("Java Version: " + System.getProperty("java.version"));
        console.println("Java Vendor: " + System.getProperty("java.vendor"));
        console.println("Java VM Version: " + System.getProperty("java.vm.version"));
        console.println("Java VM Name: " + System.getProperty("java.vm.name"));
        console.println("Java Class Version: " + System.getProperty("java.class.version"));
        console.println("Java VM Spec. Version: " + System.getProperty("java.vm.specification.version"));
        console.println("Java VM Spec. Vendor: " + System.getProperty("java.vm.specification.vendor"));
        console.println("Java VM Spec. Name: " + System.getProperty("java.vm.specification.name"));
        
        console.println("OS: " + System.getProperty("os.name"));
        console.println("OS Arch: " + System.getProperty("os.arch"));
        console.println("OS Version: " + System.getProperty("os.version"));
        console.println("");
    }
}
