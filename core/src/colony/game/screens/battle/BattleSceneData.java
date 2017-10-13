/*
 * see license.txt 
 */
package colony.game.screens.battle;

import colony.gfx.ImageData;

/**
 * @author Tony
 *
 */
public class BattleSceneData {
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
        
        public float rotation;
        public boolean flipX, flipY;
        
        public ImageData image;
    }

    public String backgroundImage;
    public ImageData slotImage;
    public SceneObject[] objects;   

}
