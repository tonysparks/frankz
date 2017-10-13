/*
 * see license.txt 
 */
package colony.sfx;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.audio.Sound;

import colony.assets.WatchedAsset;
import colony.game.ColonyGame;

/**
 * Manages sounds
 * 
 * @author Tony
 *
 */
public class Sounds {

    private static final Random random = new Random(); 
    public static final int[] uiSlotHover  = {0};
    public static final int[] uiSlotSelect = {1};
    
    
    private static final List<WatchedAsset<Sound>> soundBank = new ArrayList<>();
    private static float gameVolume;
    private static float musicVolume;
    
    public static void init(ColonyGame game) {
        destroy();
        
        game.getConfig().onAssetChanged(config -> {
            if(config.sound != null) {
                setGameVolume(config.sound.gameVolume);
                setMusicVolume(config.sound.musicVolume);
            }
        }).touchLast();
        
        soundBank.add(game.loadSound("./assets/sfx/ui/element_hover.wav"));
        soundBank.add(game.loadSound("./assets/sfx/ui/element_select.wav"));
        
    }
    
    public static void destroy() {
        for(WatchedAsset<Sound> snd : soundBank) {
            snd.getAsset().dispose();
        }
    }
    
    
    /**
     * @return the gameVolume
     */
    public static float getGameVolume() {
        return gameVolume;
    }
    
    /**
     * @param gameVolume the gameVolume to set
     */
    public static void setGameVolume(float gameVolume) {
        Sounds.gameVolume = gameVolume;
    }
    
    /**
     * @return the musicVolume
     */
    public static float getMusicVolume() {
        return musicVolume;
    }
    
    /**
     * @param musicVolume the musicVolume to set
     */
    public static void setMusicVolume(float musicVolume) {
        Sounds.musicVolume = musicVolume;
    }
    
    
    public static void playSound(int[] sound) {
        playSound(sound, getGameVolume());
    }
    
    public static void playSound(int[] sound, float volume) {
        int n = sound[random.nextInt(sound.length)];
        if(n > -1 && n < soundBank.size()) {
            WatchedAsset<Sound> snd = soundBank.get(n);
            snd.getAsset().play(volume);
        }
    }

}
