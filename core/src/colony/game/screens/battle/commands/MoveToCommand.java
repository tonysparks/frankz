/*
 * see license.txt 
 */
package colony.game.screens.battle.commands;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import colony.game.TimeStep;
import colony.game.entities.Entity;
import colony.game.entities.EntityData.MovementData;
import colony.game.entities.EntityState;
import colony.game.screens.battle.BattleScene;
import colony.game.screens.battle.Board.Slot;
import colony.game.screens.battle.PathPlanner;
import colony.gfx.RenderContext;
import colony.graph.GraphNode;

/**
 * @author Tony
 *
 */
public class MoveToCommand extends Command {

    private PathPlanner pathPlanner;
    
    /**
     * @param params
     */
    public MoveToCommand(CommandParameters params) {
        super(params);
    }

    @Override
    public CommandResult checkPreconditions(BattleScene scene) {
        Entity entity = parameters.selectedEntity;
        
        if(entity == null) {
            return failed("No selected entity");
        }
        
        MovementData data = entity.getMovementData();
                
        if(parameters.targetSlot == null) {
            return failed("No target slot");
        }
        
        if(scene.getEntityOnSlot(parameters.targetSlot).isPresent()) {
            return failed("Target slot is already occupied");
        }
        
        Slot start = scene.getSlot(entity);
        Slot end = parameters.targetSlot;
        
        this.pathPlanner = scene.newPathPlanner(entity);
        
        int cost = this.pathPlanner.pathCost(start, end) * data.actionPoints;
        if(!entity.hasPoints(cost)) {
            return failed("Not enough action points");
        }
        
        this.pathPlanner.findPath(start, end);
        if(!this.pathPlanner.hasPath()) {
            return failed("No valid path to target slot");
        }
        
        entity.usePoints(cost);
        
        return inProgress();
    }

    @Override
    public Action createAction(BattleScene scene) {
        return new Action() {

            boolean isCancelled;
            boolean atDestination;
            
            
            @Override
            public void render(RenderContext context) {
                for(GraphNode<Slot> node : pathPlanner.getPath()) {
                    
                    Sprite sprite = scene.getTileHighlighter();
                    Vector3 worldPos = scene.getWorldPos(node.getValue());
                    sprite.setPosition(worldPos.x-BattleScene.tileHalfWidth, worldPos.y-BattleScene.tileHalfHeight);
                    sprite.draw(context.batch);
                }
            }

            @Override
            public void update(TimeStep timeStep) {
                Entity entity = parameters.selectedEntity;
                Vector2 waypoint = pathPlanner.nextWaypoint(entity);
                if(waypoint==null) {
                    atDestination = true;
                    return;
                }
                
                final float movementSpeed = 1.8f;
                
                Vector2 vel = waypoint.cpy().nor();
                
                // entity.setCurrentDirection(Direction.getDirection(vel));                
                
                float dt = (float)timeStep.asFraction();            
                float deltaX = (vel.x * movementSpeed * dt);
                float deltaY = (vel.y * movementSpeed * dt);
                                
                entity.moveBy(deltaX, deltaY);    
                
                if(pathPlanner.atDestination()) {
                    if(entity.getCenterPos().epsilonEquals(pathPlanner.getDestination(), 0.001f)) {                        
                        atDestination = true;
                    }
                }
            }

            @Override
            public void start() {            
                parameters.selectedEntity.setState(EntityState.Walking);
            }
            
            @Override
            public void end() {
                // snap the entity to the appropriate slot center
                parameters.selectedEntity.setPos(scene.getWorldPos(parameters.targetSlot));
                parameters.selectedEntity.setState(EntityState.Idle);
            }

            @Override
            public void cancel() {
                isCancelled = true;
                pathPlanner.clearPath();                
            }

            @Override
            public CommandResult status() {
                if(isCancelled) {
                    return CommandResult.Cancelled;
                }
                
                if(atDestination) {
                    return CommandResult.Success;
                }
                
                return CommandResult.InProgress;
            }
            
        };
    }

}
