/*
 * see license.txt 
 */
package colony.game.screens.battle.commands;

import colony.game.TimeStep;
import colony.game.entities.Entity;
import colony.game.entities.EntityData.AttackData;
import colony.game.entities.EntityState;
import colony.game.screens.battle.BattleScene;
import colony.game.screens.battle.Dice;
import colony.gfx.RenderContext;
import colony.util.Timer;

/**
 * @author Tony
 *
 */
public class AttackCommand extends Command {

    /**
     * @param params
     */
    public AttackCommand(CommandParameters params) {
        super(params);
    }

    @Override
    public CommandResult checkPreconditions(BattleScene scene) {
        Entity entity = parameters.selectedEntity;
        
        if(entity == null) {
            return failed("No selected entity");
        }
        
        AttackData data = entity.getAttackData();
        
        if(!entity.hasPoints(data.actionPoints)) {
            return failed("Not enough action points");
        }
        
        if(parameters.targetEntity == null) {
            return failed("No target entity");
        }
        
        if(!parameters.targetEntity.isAlive()) {
            return failed("Target is already dead");
        }
        
        if(!entity.isEnemy(parameters.targetEntity)) {
            return failed("Target is not an enemy");
        }
        
        if(entity.distance(parameters.targetEntity) > data.attackRange) {
            return failed("Target is out of reach");
        }
                
        entity.usePoints(data.actionPoints);
        
        return inProgress();
    }

    @Override
    public Action createAction(BattleScene scene) {
        return new Action() {           
            
            Timer timer = new Timer(false, 1_500);
            
            @Override
            public void render(RenderContext context) {
                // TODO Attack effects!
            }

            @Override
            public void update(TimeStep timeStep) {
                this.timer.update(timeStep);
            }

            @Override
            public void start() {            
                parameters.selectedEntity.setState(EntityState.Attacking);
                
                Dice dice = scene.getDice();
                                
                int defenseScore = dice.d10() + parameters.targetEntity.calculateDefense(scene);
                int offenseScore = dice.d10() + parameters.selectedEntity.calculateDefense(scene);
                
                if(offenseScore > defenseScore) {
                    parameters.targetEntity.damage(parameters.selectedEntity.getAttackData().damage);
                }
            }
            
            @Override
            public void end() {
                // snap the entity to the appropriate slot center
                parameters.selectedEntity.setPos(scene.getWorldPos(parameters.targetSlot));
                parameters.selectedEntity.setState(EntityState.Idle);
            }

            @Override
            public void cancel() {             
            }

            @Override
            public CommandResult status() {
                if(this.timer.isExpired()) {
                    return CommandResult.Success;
                }
                                
                return CommandResult.InProgress;
            }
            
        };
    }

}
