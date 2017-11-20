/*
 * see license.txt 
 */
package colony.game.screens.battle.ai;

import java.util.Set;

import colony.game.Faction;
import colony.game.TimeStep;
import colony.game.entities.Entity;
import colony.game.screens.battle.BattleScene;
import colony.game.screens.battle.Board;
import colony.game.screens.battle.Board.Slot;
import colony.gfx.RenderContext;
import colony.gfx.Renderable;
import colony.graph.GraphNode;

/**
 * @author Tony
 *
 */
public class InfluenceMap implements Renderable {

    public static class Influence {
        public float aiInfluence;
        public float enemyInfluence;
        public float influence;
        public float tension;
        public float vulnerability;
        
        void clear() {
           this.influence = this.aiInfluence = this.enemyInfluence = 
           this.tension = this.vulnerability = 0.0f; 
        }
    }
    
    private BattleScene scene;
    
    private Influence[][] influences;
    private Faction aiFaction;
    /**
     * 
     */
    public InfluenceMap(AI ai, BattleScene scene) {
        this.scene = scene;
        this.aiFaction = ai.getAIFaction();
        
        Board board = scene.getBoard();
        this.influences = new Influence[board.getHeight()][board.getWidth()];
        for(int y = 0; y < influences.length; y++) {
            for(int x = 0; x < influences[y].length; x++) {
                this.influences[y][x] = new Influence();
            }
        }
    }
    
    public void applyInfluences() {
        for(int y = 0; y < influences.length; y++) {
            for(int x = 0; x < influences[y].length; x++) {
                influences[y][x].clear();
            }
        }
        
        scene.getAttacker().getEntities().forEach(this::applyInfluence);
        scene.getDefender().getEntities().forEach(this::applyInfluence);
    }
    
    public void applyInfluence(Entity ent) {
        if(ent.isAlive()) {
            Slot slot = scene.getSlot(ent);
            
            int attackRange = ent.getAttackData().attackRange;
            boolean isAI = ent.inFaction(aiFaction);
            
            float initialInfluence = 3.0f;
            float fallOff = 0.5f;
            float distance = attackRange;
            
            Set<GraphNode<Slot>> slots = scene.getGraph().getNodesInRadius(slot.x, slot.y, true, attackRange);
            for(GraphNode<Slot> n : slots) {
                Slot s = n.getValue();
                float influence = influenceContribution(s.x, s.y, initialInfluence, fallOff, distance);
                
                influences[s.y][s.x].aiInfluence    += isAI ? influence : 0;
                influences[s.y][s.x].enemyInfluence += !isAI ? influence : 0;
                influences[s.y][s.x].influence      += isAI ? influence : -influence;
                influences[s.y][s.x].tension        += influence;
                influences[s.y][s.x].vulnerability   = influences[s.y][s.x].tension - Math.abs(influences[s.y][s.x].influence);
            }
        }
    }
    
    private float influenceContribution(int x, int y, float unitInfluence, float fallOff, float distance) {
        if(fallOff-distance == 0) {
            return 0;
        }
        
        return unitInfluence * Math.max(0, 1 - (fallOff / (fallOff - distance)));
    }
    
    /**
     * @return the influences
     */
    public Influence[][] getInfluences() {
        return influences;
    }
    
    public Influence getInfluence(Slot slot) {
        return influences[slot.y][slot.y];
    }
    
    public Influence getInfluence(Entity entity) {
        return getInfluence(scene.getSlot(entity));
    }

    @Override
    public void update(TimeStep timeStep) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void render(RenderContext context) {
    }
}
