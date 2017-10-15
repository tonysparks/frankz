/*
 *    leola-live 
 *  see license.txt
 */
package colony.game.screens.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import colony.game.entities.Entity;
import colony.game.screens.battle.Board.Slot;
import colony.graph.AStarGraphSearch;
import colony.graph.GraphNode;



/**
 * Feeds the next graph node.  This is the path planner for an agent.  This allows an agent to
 * know which tile to move to next
 * 
 * @author Tony
 *
 */
public class PathPlanner {
    
    private BoardGraph graph;
    private List<GraphNode<Slot>> path;
    private SearchPath searchPath;     
    private int currentNode;
    private Vector2 nextWaypoint;
    private Vector2 finalDestination;    
    private Entity entity;
    private BattleScene scene;
    
    public class SearchPath extends AStarGraphSearch<Slot> {
        public List<Slot> tilesToAvoid = new ArrayList<>();
        
        @Override
        protected int heuristicEstimateDistance(
                GraphNode<Slot> startNode,
                GraphNode<Slot> currentNode,
                GraphNode<Slot> goal) {
            Slot startTile = startNode.getValue();
            Slot currentTile = currentNode.getValue();
            Slot goalTile = goal.getValue();
                        
            int dx = Math.abs(currentTile.x - goalTile.x);
            int dy = Math.abs(currentTile.y - goalTile.y);                        
            
            int sdx = Math.abs(startTile.x - goalTile.x);
            int sdy = Math.abs(startTile.y - goalTile.y);
            
            final int D = 1;
            //final int D2 = 2;
            
            //distance = D * (dx+dy) + (D2 - 2 * D) * Math.min(dx, dy);
            int distance = D * (dx+dy);
            int cross = Math.abs(dx*sdy - sdx*dy);
            return distance + (cross);//
        }    
        
        
        @Override
        protected boolean shouldIgnore(GraphNode<Slot> node) {
            Slot slot = node.getValue();
            Optional<Entity> ent = scene.getEntityOnSlot(slot); 
            return ent.isPresent() && ent.get() != entity;
        }
    }
    
    
    /**
     * @param path
     */
    public PathPlanner(BattleScene scene, BoardGraph graph, Entity entity) {
        this.scene = scene;
        this.graph = graph;
        this.entity = entity;
        
        this.finalDestination = new Vector2();
        this.nextWaypoint = new Vector2();
        
        this.path = new ArrayList<GraphNode<Slot>>();
        
        this.currentNode = 0;        
        this.searchPath = new SearchPath();            
    } 
    
    private void setPath(List<GraphNode<Slot>> newPath) {
        clearPath();
        if(newPath != null) {
            for(int i = 0; i < newPath.size(); i++) {
                this.path.add(newPath.get(i));
            }
        }
    }
    
    /**
     * Clears out the path
     */
    public void clearPath() {
        this.currentNode = 0;
        this.finalDestination.setZero();
        this.nextWaypoint.setZero();
        this.path.clear();
    }
    
    /**
     * Calculate the estimated actionPoints of the path from the start to destination
     * 
     * @param start
     * @param destination
     * @return the estimated actionPoints of moving from start to destination
     */
    public int pathCost(Slot start, Slot destination) {
        List<GraphNode<Slot>> newPath = this.graph.findPath(this.searchPath, start, destination);
        int cost = newPath.size();
        return cost;
    }
    
    /**
     * Finds the optimal path between the start and end point
     * 
     * @param start
     * @param destination
     */
    public void findPath(Slot start, Slot destination) {                
        List<GraphNode<Slot>> newPath = this.graph.findPath(this.searchPath, start, destination);
        setPath(newPath);
        
        Vector3 worldPos = this.scene.getWorldPos(destination);
        this.finalDestination.set(worldPos.x, worldPos.y);
    }
        

    
    /**
     * @return if there is currently a path
     */
    public boolean hasPath() {
        return !this.path.isEmpty();
    }
    
    /**
     * @return the final destination
     */
    public Vector2 getDestination() {
        return this.finalDestination;
    }

    /**
     * @return the path
     */
    public List<GraphNode<Slot>> getPath() {
        return path;
    }

    /**
     * @return the current node that the entity is trying to reach
     */
    public GraphNode<Slot> getCurrentNode() {
        if (!path.isEmpty() && currentNode < path.size()) {
            return path.get(currentNode);
        }
        return null;
    }
    
    /**
     * @return true if this path is on the first node (just started)
     */
    public boolean onFirstNode() {
        return currentNode == 0 && !path.isEmpty();
    }

    
    /**
     * Retrieves the next way-point on the path.
     * 
     * @param ent
     * @return the next way-point on the path
     */
    public Vector2 nextWaypoint(Entity ent) {
        Vector2 cPos = ent.getPos();
        float x = cPos.x; 
        float y = cPos.y;
                
        nextWaypoint.setZero();
        
        if(! path.isEmpty() && currentNode < path.size() ) {
            GraphNode<Slot> node = path.get(currentNode);
            Slot slot = node.getValue();
        
            Vector3 worldPos = scene.getWorldPos(slot);
            
            float centerX = worldPos.x;
            float centerY = worldPos.y;
            
            // if we've arrived at the destination slot 
            // let's set the way point to the next slot
            if( Math.abs(cPos.len() - Vector2.len(centerX, centerY)) < 0.028) {
                currentNode++;
            }
            
            nextWaypoint.x = (centerX - x);
            nextWaypoint.y = (centerY - y);
              
            return nextWaypoint;
        }

        return null;
    }
    
    /**
     * @return true if the current position is about the end of the path
     */
    public boolean atDestination() {
        return (currentNode >= path.size());
    }
    
}

