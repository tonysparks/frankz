/*
 * see license.txt 
 */
package colony.game.screens.battle.commands;

import colony.game.screens.battle.BattleScene;

/**
 * A Chunk Toss Attack
 * 
 * @author Tony
 *
 */
public class TossCommand extends AttackCommand {

    public class TossAction extends AttackCommand.AttackAction {
        
        public TossAction(TossCommand cmd, BattleScene scene) {
            super(cmd, scene);
        }
        
        @Override
        protected void onHit() {            
            scene.addConcurrentCommand(new PushedCommand(new CommandParameters(parameters.targetEntity, parameters.targetSlot)));
        }
        
    }
    
    /**
     * @param params
     */
    public TossCommand(CommandParameters params) {
        super(params);
    }
    
    @Override
    public CommandResult checkPreconditions(BattleScene scene) {    
        CommandResult result = super.checkPreconditions(scene);
        // TODO: Verify targetSlot is within tossRadius
        return result;
    }
    

    @Override
    public Action createAction(BattleScene scene) {
        return new TossAction(this, scene);
    }

}
