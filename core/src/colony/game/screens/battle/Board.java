/*
 * see license.txt 
 */
package colony.game.screens.battle;

import java.util.function.Consumer;

import com.badlogic.gdx.math.Rectangle;

/**
 * @author Tony
 *
 */
public class Board {

    /**
     * Board array index width and height
     */
    public static final int WIDTH = 10,
                            HEIGHT = 10;
    
    /**
     * A slot on the board
     * 
     * @author Tony
     *
     */
    public static class Slot {
        /**
         * The world coordinate size of a slot 
         */
//        public static final float WIDTH = 0.97f, //1.524f / 2.0f, 
//                                  HEIGHT = 0.97f; // 1.524f / 2.0f;
        //1.46304f
        public static final float WIDTH = 1.42304f, //1.201524f, 
                                  HEIGHT = WIDTH;
        
        /**
         * Array index positions
         */
        public int x, y;       
        
        /**
         * World coordinate bounds
         */
        public Rectangle bounds;
       
        
        public Slot(int x, int y) {
            this.x = x;
            this.y = y;
            
            this.bounds = new Rectangle();
            this.bounds.setSize(WIDTH, HEIGHT);
            this.bounds.setPosition(x * WIDTH, y * HEIGHT);
        }
       
       
    }
    
    private Slot[][] slots;
    
    public Board() {
        this.slots = new Slot[HEIGHT][WIDTH];
        for(int y = 0; y < HEIGHT; y++) {
            for(int x = 0; x < WIDTH; x++) {
                this.slots[y][x] = new Slot(x,y);
            }
        }
    }
    
    /**
     * Width in number of slots
     * 
     * @return Width in number of slots
     */
    public int getWidth() {
        return WIDTH;
    }
    
    /**
     * Height in number of slots
     * 
     * @return Height in number of slots
     */
    public int getHeight() {
        return HEIGHT;
    }
    
    /**
     * Scan each slot
     * 
     * @param c
     */
    public void forEachSlot(Consumer<Slot> c) {
        for(int y = 0; y < HEIGHT; y++) {
            for(int x = 0; x < WIDTH; x++) {
                c.accept(this.slots[y][x]);
            }
        }
    }
    
    /**
     * Get the slot by array index
     * 
     * @param indexX
     * @param indexY
     * @return the slot
     */
    public Slot getSlotByIndex(int indexX, int indexY) {
        if(indexX >= 0 && indexX < Board.WIDTH && indexY >= 0 && indexY < Board.HEIGHT) {
            return this.slots[indexY][indexX];
        }
        return null;
    }

    
}
