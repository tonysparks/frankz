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
    
    public static class SoundConfig {
        public float musicVolume = 0.6f;
        public float gameVolume = 0.8f;
    }
    
    /**
     * If this is in development mode
     */
    public boolean developmentMode = false;
    
    public VideoConfig video = new VideoConfig();
    public SoundConfig sound = new SoundConfig();
    
}
