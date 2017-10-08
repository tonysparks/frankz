/*
 * see license.txt 
 */
package colony.gfx;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import colony.game.TimeStep;

/**
 * @author Tony
 *
 */
public class Model {
    
    public static class ModelStateData {
        public String image;
        public int rows = 1;
        public int cols = 1;
        public int frameTimeMSec = 300;
        public boolean loop = false;
    }
    
    public static class ModelData {
        public Map<String, ModelStateData> states;
    }

       
    private Map<String, AnimatedImage> states;
    private String currentState;
    private AnimatedImage currentImage;
    
    public Model(ModelData data) {
        this.states = new HashMap<String, AnimatedImage>();
        data.states.entrySet().stream().forEach( (entry) -> {
            ModelStateData stateData = entry.getValue();
            TextureRegion tex = TextureUtil.loadImage(stateData.image);
            TextureRegion[] images = TextureUtil.splitImage(tex, stateData.rows, stateData.cols);
            for(TextureRegion r : images) {
                r.flip(false, true);
            }
            
            AnimationFrame[] frames = new AnimationFrame[images.length];
            for(int i = 0; i < frames.length; i++) {
                frames[i] = new AnimationFrame(stateData.frameTimeMSec, i);
            }
            
            FramedAnimation anim = new FramedAnimation(frames);
            anim.loop(stateData.loop);
            
            states.put(entry.getKey().toLowerCase(), new AnimatedImage(images, anim));
        });
    }
    
    /**
     * @param currentState the currentState to set
     */
    public void setCurrentState(String currentState) {
        this.currentState = currentState.toLowerCase();
        this.currentImage = this.states.get(this.currentState);
    }
    
    /**
     * @return the currentState
     */
    public String getCurrentState() {
        return currentState;
    }
        
    public TextureRegion getCurrentFrame() {
        return this.currentImage.getCurrentImage();
    }
    
    public void update(TimeStep timeStep) {
        if(this.currentImage!=null) {
            this.currentImage.update(timeStep);
        }
    }
    

}
