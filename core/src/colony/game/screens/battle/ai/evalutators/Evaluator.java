/*
 * see license.txt 
 */
package colony.game.screens.battle.ai.evalutators;

import colony.game.screens.battle.BattleScene;
import colony.game.screens.battle.commands.Command;

/**
 * @author Tony
 *
 */
public interface Evaluator {

    /**
     * Evaluates a score
     * 
     * @param scene
     * @return the score 0 - 1.0
     */
    public double evaluate(BattleScene scene);
    
    
    /**
     * Creates the {@link Command}
     * @return the {@link Command}
     */
    public Command createCommand();
}
