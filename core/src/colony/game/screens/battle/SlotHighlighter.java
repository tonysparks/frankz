/*
 * see license.txt 
 */
package colony.game.screens.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import colony.game.Game;
import colony.game.TimeStep;
import colony.game.entities.Entity;
import colony.game.screens.battle.Board.Slot;
import colony.gfx.ImageData;
import colony.gfx.RenderContext;
import colony.gfx.Renderable;
import colony.graph.Edge;
import colony.graph.Edges;
import colony.graph.Edges.Directions;
import colony.graph.GraphNode;

/**
 * Responsible for highlighting slots centered around a supplied {@link Slot} - this
 * will highlight around obstacles (be it map objects or {@link Entity}s).
 * 
 * @author Tony
 *
 */
public class SlotHighlighter implements Renderable {

    
    class HighlightedSlot {
        Slot slot;
        Sprite sprite;
        
        HighlightedSlot(Slot slot, Sprite sprite) {
            this.slot = slot;
            this.sprite = sprite;
        }
        
        void render(RenderContext context) {
            Vector3 worldPos = scene.getWorldPos(slot);
            
            sprite.setPosition(worldPos.x - BattleScene.tileHalfWidth, worldPos.y - BattleScene.tileHalfHeight);
            sprite.setColor(color);
            sprite.draw(context.batch);
        }
    }

    private List<HighlightedSlot> highlightedSlots;
    
    private Sprite bottomLeft,
                   bottomRight,
                   cornerBottom,
                   cornerLeft,
                   cornerRight,
                   cornerTop,
                   doubleDown,
                   doubleUp,
                   hookBottomLeft,
                   hookBottomRight,
                   hookTopLeft,
                   hookTopRight,
                   innerCornerBottom,
                   innerCornerLeft,
                   innerCornerRight,
                   innerCornerTop,
                   upLeft,
                   upRight;
                   
    
    private BoardGraph graph;
    private BattleScene scene;
    
    private Color color;
    
    /**
     * 
     */
    public SlotHighlighter(Game game, BattleScene scene) {        
        this.scene = scene;
        this.graph = scene.getGraph();
        
        this.highlightedSlots = new ArrayList<>();
        
        this.bottomLeft = new Sprite();        
        this.bottomRight = new Sprite();
        this.cornerBottom = new Sprite();
        this.cornerLeft = new Sprite();
        this.cornerRight = new Sprite();
        this.cornerTop = new Sprite();
        this.doubleDown = new Sprite();
        this.doubleUp = new Sprite();
        this.hookBottomLeft = new Sprite();
        this.hookBottomRight = new Sprite();
        this.hookTopLeft = new Sprite();
        this.hookTopRight = new Sprite();
        this.innerCornerBottom = new Sprite();
        this.innerCornerLeft = new Sprite();
        this.innerCornerRight = new Sprite();
        this.innerCornerTop = new Sprite();
        this.upLeft = new Sprite();
        this.upRight = new Sprite();
        
        loadSprite(game, "./assets/gfx/battle/ui/UI__Bottom-Left.png", this.bottomLeft);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Bottom-Right.png", this.bottomRight);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Corner-Bottom.png", this.cornerBottom);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Corner-Left.png", this.cornerLeft);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Corner-Right.png", this.cornerRight);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Corner-Top.png", this.cornerTop);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Double-Down.png", this.doubleDown);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Double-Up.png", this.doubleUp);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Hook-Bottom-Left.png", this.hookBottomLeft);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Hook-Bottom-Right.png", this.hookBottomRight);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Hook-Top-Left.png", this.hookTopLeft);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Hook-Top-Right.png", this.hookTopRight);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Inner-Corner-Bottom.png", this.innerCornerBottom);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Inner-Corner-Left.png", this.innerCornerLeft);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Inner-Corner-Right.png", this.innerCornerRight);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Inner-CornerTop.png", this.innerCornerTop);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Up-Left.png", this.upLeft);
        loadSprite(game, "./assets/gfx/battle/ui/UI__Up-Right.png", this.upRight);
    }

    
    private void loadSprite(Game game, String filename, Sprite sprite) {
        ImageData imageData = new ImageData();
        imageData.filename = filename;
        imageData.width = 536;
        imageData.height = 314;
        
        game.loadTexture(imageData).onAssetChanged(tex -> {
            
            sprite.setRegion(tex);            
            sprite.setSize(BattleScene.tileSpriteWidth, BattleScene.tileSpriteHeight);
        }).touch();
    }

    
    /**
     * Clear out the highlighted slots
     */
    public void clear() {
        this.highlightedSlots.clear();
        this.color = Color.WHITE;
    }
    
    /**
     * Center the slot highlighter around the supplied {@link Slot}.  This
     * will omit unwalkable tiles
     * 
     * @param slot
     * @param range
     */
    public void centerAround(Slot slot, int range, Color color) {
        centerAround(slot, false, range, color);
    }
    
    /**
     * Center the slot highlighter around the supplied {@link Slot}
     * 
     * @param slot
     * @param all - if this should include all tiles (including unwalkable tiles)
     * @param range
     */
    public void centerAround(Slot slot, boolean all, int range, Color color) {
        clear();
        
        this.color = color;
        
        GraphNode<Slot> node = this.graph.getNode(slot);
        Set<GraphNode<Slot>> slots = this.graph.getNodesInRadius(node, all, range);                 
        for(GraphNode<Slot> n : slots) {
            
            // TODO account for rounding errors, ignore any slots
            // that fall out of our range
//            if(ai.distance(scene, slot) > attack.attackRange) {
//                continue;
//            }
            
            //this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.bottomLeft));
            
            if(!isCenter(slots, n)) {
                if(isBottomLeft(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.bottomLeft));
                }
                else if(isBottomRight(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.bottomRight));
                }
                else if(isCornerBottom(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.cornerBottom));
                }
                else if(isCornerLeft(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.cornerLeft));
                }
                else if(isCornerRight(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.cornerRight));
                }
                else if(isCornerTop(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.cornerTop));
                }
                else if(isDoubleDown(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.doubleDown));
                }
                else if(isDoubleUp(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.doubleUp));
                }
                else if(isHookBottomLeft(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.hookBottomLeft));
                }
                else if(isHookBottomRight(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.hookBottomRight));
                }
                else if(isHookTopLeft(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.hookTopLeft));
                }
                else if(isHookTopRight(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.hookTopRight));
                }
                else if(isInnerCornerBottom(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.innerCornerBottom));
                }
                else if(isInnerCornerLeft(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.innerCornerLeft));
                }
                else if(isInnerCornerRight(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.innerCornerRight));
                }
                else if(isInnerCornerTop(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.innerCornerTop));
                }
                else if(isUpLeft(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.upLeft));
                }
                else if(isUpRight(slots, n)) {
                    this.highlightedSlots.add(new HighlightedSlot(n.getValue(), this.upRight));
                }
            }
        }
    }
    
    private boolean isCenter(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        
        int count = 0;
        for(int i = 0; i < edges.size(); i++) {
            Edge<Slot> e = edges.get(i);
            if(e!=null) {
                count++;
                GraphNode<Slot> other = e.getRight();
                if(!slots.contains(other)) {
                    return false;
                }
            }
        }
        
        return count==8;
    }
    
    private boolean hasEdge(Set<GraphNode<Slot>> slots, Edges<Slot> edges, Directions dir) {
        return edges.hasEdge(dir) && slots.contains(edges.get(dir).getRight());
    }
    
    private boolean isBottomLeft(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.S) &&
                hasEdge(slots, edges, Directions.E) &&
                hasEdge(slots, edges, Directions.W) &&
                hasEdge(slots, edges, Directions.N);
        
    }
    
    private boolean isBottomRight(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.E) &&
                hasEdge(slots, edges, Directions.S) &&
                hasEdge(slots, edges, Directions.W) &&
                hasEdge(slots, edges, Directions.N);
    }
    
    private boolean isCornerBottom(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.E) &&
             //  !hasEdge(slots, edges, Directions.SE) &&
               !hasEdge(slots, edges, Directions.S) &&
                hasEdge(slots, edges, Directions.W) &&
                hasEdge(slots, edges, Directions.N);
    }
    
    private boolean isCornerLeft(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.W) &&
              // !hasEdge(slots, edges, Directions.SW) &&
               !hasEdge(slots, edges, Directions.S) &&
                hasEdge(slots, edges, Directions.E) &&
                hasEdge(slots, edges, Directions.N);
    }
    
    private boolean isCornerRight(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.N) &&
             //  !hasEdge(slots, edges, Directions.NE) &&
               !hasEdge(slots, edges, Directions.E) &&
                hasEdge(slots, edges, Directions.S) &&
                hasEdge(slots, edges, Directions.W);
    }
    
    private boolean isCornerTop(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.N) &&
             //  !hasEdge(slots, edges, Directions.NW) &&
               !hasEdge(slots, edges, Directions.W) &&
                hasEdge(slots, edges, Directions.S) &&
                hasEdge(slots, edges, Directions.E);
    }
    private boolean isDoubleDown(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.N) &&
               !hasEdge(slots, edges, Directions.S) &&
                hasEdge(slots, edges, Directions.W) &&
                hasEdge(slots, edges, Directions.E);
    }
    private boolean isDoubleUp(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.W) &&
               !hasEdge(slots, edges, Directions.E) &&
                hasEdge(slots, edges, Directions.N) &&
                hasEdge(slots, edges, Directions.S);
    }
    private boolean isHookBottomLeft(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.W) &&
               !hasEdge(slots, edges, Directions.E) &&
          //     !hasEdge(slots, edges, Directions.SE) &&
          //     !hasEdge(slots, edges, Directions.SW) &&
               !hasEdge(slots, edges, Directions.S) &&
                hasEdge(slots, edges, Directions.N);
    }
    private boolean isHookBottomRight(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.N) &&
           //    !hasEdge(slots, edges, Directions.NE) &&
               !hasEdge(slots, edges, Directions.E) &&
           //    !hasEdge(slots, edges, Directions.SE) &&
               !hasEdge(slots, edges, Directions.S) &&
                hasEdge(slots, edges, Directions.W);
    }
    private boolean isHookTopLeft(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.N) &&
           //    !hasEdge(slots, edges, Directions.NW) &&
               !hasEdge(slots, edges, Directions.W) &&
           //    !hasEdge(slots, edges, Directions.SW) &&
               !hasEdge(slots, edges, Directions.S) &&
                hasEdge(slots, edges, Directions.E);
    }
    private boolean isHookTopRight(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.N) &&
           //    !hasEdge(slots, edges, Directions.NW) &&
               !hasEdge(slots, edges, Directions.W) &&
               !hasEdge(slots, edges, Directions.E) &&
           //    !hasEdge(slots, edges, Directions.NE) &&
               !hasEdge(slots, edges, Directions.E) &&
                hasEdge(slots, edges, Directions.S);
    }
    private boolean isInnerCornerBottom(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.SE) &&
                hasEdge(slots, edges, Directions.N) &&
                hasEdge(slots, edges, Directions.NE) &&
                hasEdge(slots, edges, Directions.E) &&
                hasEdge(slots, edges, Directions.S) &&
                hasEdge(slots, edges, Directions.NW) &&
                hasEdge(slots, edges, Directions.SW) &&
                hasEdge(slots, edges, Directions.W);
    }
    private boolean isInnerCornerLeft(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.SW) &&
                hasEdge(slots, edges, Directions.N) &&
                hasEdge(slots, edges, Directions.NE) &&
                hasEdge(slots, edges, Directions.E) &&
                hasEdge(slots, edges, Directions.S) &&
                hasEdge(slots, edges, Directions.SE) &&
                hasEdge(slots, edges, Directions.NW) &&
                hasEdge(slots, edges, Directions.W);
    }
    private boolean isInnerCornerRight(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.NE) &&
                hasEdge(slots, edges, Directions.N) &&
                hasEdge(slots, edges, Directions.SW) &&
                hasEdge(slots, edges, Directions.E) &&
                hasEdge(slots, edges, Directions.S) &&
                hasEdge(slots, edges, Directions.SE) &&
                hasEdge(slots, edges, Directions.NW) &&
                hasEdge(slots, edges, Directions.W);
    }
    private boolean isInnerCornerTop(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.NW) &&
                hasEdge(slots, edges, Directions.N) &&
                hasEdge(slots, edges, Directions.NE) &&
                hasEdge(slots, edges, Directions.E) &&
                hasEdge(slots, edges, Directions.S) &&
                hasEdge(slots, edges, Directions.SE) &&
                hasEdge(slots, edges, Directions.SW) &&
                hasEdge(slots, edges, Directions.W);
    }
    private boolean isUpLeft(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.W) &&
                hasEdge(slots, edges, Directions.N) &&
                hasEdge(slots, edges, Directions.E) &&
                hasEdge(slots, edges, Directions.S);
    }
    private boolean isUpRight(Set<GraphNode<Slot>> slots, GraphNode<Slot> currentNode) {
        Edges<Slot> edges = currentNode.edges();
        return !hasEdge(slots, edges, Directions.N) &&
                hasEdge(slots, edges, Directions.W) &&
                hasEdge(slots, edges, Directions.E) &&
                hasEdge(slots, edges, Directions.S);
    }
        
    
    @Override
    public void update(TimeStep timeStep) {
    }
    
    @Override
    public void render(RenderContext context) {
        for(int i = 0; i < this.highlightedSlots.size(); i++) {
            this.highlightedSlots.get(i).render(context);
        }
    }
}
