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
import colony.sfx.Sounds;
import colony.util.Timer;

/**
 * @author Tony
 *
 */
public class AttackCommand extends Command {

    public class AttackAction extends Action {
        protected Timer timer;
        protected BattleScene scene;
        
        public AttackAction(AttackCommand cmd, BattleScene scene) {
            super(cmd);
            
            this.scene = scene;            
            this.timer = new Timer(false, 1_500);
        }
        
        protected CommandParameters parameters() {
            return parameters;
        }
        
        @Override
        public void render(RenderContext context) {
            // TODO Attack effects!
        }

        @Override
        public void update(TimeStep timeStep) {
            this.timer.update(timeStep);
        }

        @Override
        protected void doStart() {        
            Sounds.playSound(Sounds.melee);
            
            parameters.selectedEntity.setState(EntityState.Attacking);
            
            Dice dice = scene.getDice();
                            
            int defenseScore = dice.d10() + (parameters.targetEntity!=null ? parameters.targetEntity.calculateDefense(scene) : 0);
            int offenseScore = dice.d10() + parameters.selectedEntity.calculateOffense(scene);
            
            if(offenseScore > defenseScore) {
                if(parameters.targetEntity!=null) {
                    parameters.targetEntity.damage(parameters.selectedEntity.getAttackData().damage);
                }
                
                onHit();
            }
            else {
                onMiss();
            }
        }
        
        /**
         * The attack failed
         */
        protected void onMiss() {            
        }
        
        /**
         * The attack was successful
         */
        protected void onHit() {            
        }
        
        @Override
        protected void doEnd() {                
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
    }
    
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
        
        CommandResult result = checkTarget(scene, entity, data);
        if(!result.inProgress()) {
            return result;
        }
                
        entity.usePoints(data.actionPoints);
        
        return inProgress();
    }
    
    
    /**
     * checks the target is valid
     * 
     * @param scene
     * @param entity
     * @param data
     * @return
     */
    protected CommandResult checkTarget(BattleScene scene, Entity entity, AttackData data) {
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
        
        return inProgress();
    }

    @Override
    public Action createAction(BattleScene scene) {
        return new AttackAction(this, scene);           
    }

}
