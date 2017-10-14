/*
 * see license.txt 
 */
package colony.game.screens.battle;

import java.util.List;

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

    public static class TileHighlight {
        public ImageData image;
        public String color;
    }
    
    public static class FactionData {
        public String name;
        public List<EntityRefData> entities;
    }
    
    public static class EntityRefData {
        public String entityData;
        public int indexX, indexY;
    }
    
    public String backgroundImage;
    public ImageData slotImage;
    public TileHighlight tileHighlight;
    public SceneObject[] objects;
    
    
    public FactionData attacker;
    public FactionData defender;
    

}
