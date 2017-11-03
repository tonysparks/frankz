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
        
        public TossAction(BattleScene scene) {
            super(scene);
        }
        
        @Override
        protected void onHit() {
//            this.scene.get
        }
        
    }
    
    /**
     * @param params
     */
    public TossCommand(CommandParameters params) {
        super(params);
    }

    @Override
    public Action createAction(BattleScene scene) {
        // TODO Auto-generated method stub
        return null;
    }

}
