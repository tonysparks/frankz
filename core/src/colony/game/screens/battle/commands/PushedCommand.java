/*
 * see license.txt 
 */
package colony.game.screens.battle.commands;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import colony.game.TimeStep;
import colony.game.entities.Entity;
import colony.game.entities.EntityState;
import colony.game.screens.battle.BattleScene;
import colony.gfx.RenderContext;

/**
 * An {@link Entity} has been pushed
 * 
 * @author Tony
 *
 */
public class PushedCommand extends Command {

    /**
     * @param params
     */
    public PushedCommand(CommandParameters params) {
        super(params);
    }

    @Override
    public CommandResult checkPreconditions(BattleScene scene) {
        Entity entity = parameters.selectedEntity;
        
        if(entity == null) {
            return failed("No selected entity");
        }
                        
        if(parameters.targetSlot == null) {
            return failed("No target slot");
        }
        
        if(scene.getEntityOnSlot(parameters.targetSlot).isPresent()) {
            return failed("Target slot is already occupied");
        }
                        
        return inProgress();
    }

    @Override
    public Action createAction(BattleScene scene) {
        return new Action(this) {

            boolean isCancelled;
            boolean atDestination;
            
            Vector3 worldPos = scene.getWorldPos(parameters.targetSlot);
            Vector2 waypoint = new Vector2(worldPos.x, worldPos.y);
            
            float speed = parameters.selectedEntity.getStats().pushedSpeed;
            
            @Override
            public void render(RenderContext context) {                
            }

            @Override
            public void update(TimeStep timeStep) {
                Entity entity = parameters.selectedEntity;
                                
                
                final float movementSpeed = Math.max(speed *= 0.79f, 3f);                   
                Vector2 vel = waypoint.cpy().sub(entity.getPos()).nor();
                
                float dt = (float)timeStep.asFraction();            
                float deltaX = (vel.x * movementSpeed * dt);
                float deltaY = (vel.y * movementSpeed * dt);
                                
                entity.moveBy(deltaX, deltaY);    
                
                if(entity.getPos().epsilonEquals(waypoint, 0.1f)) {                        
                    atDestination = true;
                }
                
            }

            @Override
            protected void doStart() {            
                parameters.selectedEntity.setState(EntityState.Pushed);
            }
            
            @Override
            protected void doEnd() {
                // snap the entity to the appropriate slot center
                parameters.selectedEntity.setPos(scene.getWorldPos(parameters.targetSlot));
                parameters.selectedEntity.setState(EntityState.Idle);
            }

            @Override
            public void cancel() {
                isCancelled = true;
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
