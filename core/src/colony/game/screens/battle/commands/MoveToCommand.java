/*
 * see license.txt 
 */
package colony.game.screens.battle.commands;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import colony.game.TimeStep;
import colony.game.entities.Entity;
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
        if(parameters.selectedEntity == null) {
            return failed("No selected entity");
        }
        
        if(parameters.targetSlot == null) {
            return failed("No target slot");
        }
        
        if(scene.getEntityOnSlot(parameters.targetSlot).isPresent()) {
            return failed("Target slot is already occupied");
        }
        
        Slot start = scene.getSlot(parameters.selectedEntity);
        Slot end = parameters.targetSlot;
        
        this.pathPlanner = scene.newPathPlanner();
        this.pathPlanner.findPath(start, end);
        
        if(!this.pathPlanner.hasPath()) {
            return failed("No valid path to target slot");
        }
        
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
                    sprite.setPosition(worldPos.x, worldPos.y);
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
