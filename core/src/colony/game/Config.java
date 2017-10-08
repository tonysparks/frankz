/*
 * see license.txt 
 */
package colony.game;

/**
 * Configuration 
 * 
 * @author Tony
 *
 */
public class Config {

    public static class VideoConfig {
        public boolean fullscreen = false;
        public boolean vSync = true;
        public int width = 1920;
        public int height = 1080;
    }
    
    /**
     * If this is in development mode
     */
    public boolean developmentMode = false;
    
    public VideoConfig video = new VideoConfig();
    
}
