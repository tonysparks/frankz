/*
 * see license.txt 
 */
package colony.game.screens.battle.commands;

import java.util.Optional;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import colony.game.TimeStep;
import colony.game.entities.Entity;
import colony.game.entities.EntityState;
import colony.game.screens.battle.BattleScene;
import colony.game.screens.battle.Board.Slot;
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
        
//        if(scene.getEntityOnSlot(parameters.targetSlot).isPresent()) {
//            return failed("Target slot is already occupied");
//        }
                        
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
            
            EntityState previousState = parameters.selectedEntity.getState();
            Slot currentSlot = scene.getSlot(parameters.selectedEntity);
            
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
                
                Vector2 pos = entity.getPos();
                float destX = pos.x + deltaX;
                float destY = pos.y + deltaY;
                
                Slot nextSlot = scene.getSlot(destX, destY);
                if(currentSlot != nextSlot) {
                    currentSlot = nextSlot;
                    Optional<Entity> entityToMove = scene.getEntityOnSlot(nextSlot);
                    
                    entityToMove.ifPresent( ent -> {
                        if(ent!=entity) {
                            // if we can not push the entity that's on this slot over,
                            // we stop the current push
                            Optional<Slot> adjacentSlot = scene.getEmptyAdjacentSlot(currentSlot);
                            if(adjacentSlot.isPresent()) {
                                Slot targetSlot = adjacentSlot.get();
                                CommandParameters params = new CommandParameters(ent, targetSlot);
                                scene.addConcurrentCommand(new PushedCommand(params));
                            }
                            else {
                                atDestination = true;
                            }
                        }
                    });
                }
                
                if(!atDestination) {
                    entity.moveBy(deltaX, deltaY);    
                    
                    if(entity.getPos().epsilonEquals(waypoint, 0.1f)) {                        
                        atDestination = true;
                    }
                }
                
            }

            @Override
            protected void doStart() {                        
                parameters.selectedEntity.setState(EntityState.Pushed);
            }
            
            @Override
            protected void doEnd() {
                Entity ent = parameters.selectedEntity;
                // snap the entity to the appropriate slot center
                ent.setPos(scene.getWorldPos(parameters.targetSlot));                
                ent.setState(previousState);                
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
