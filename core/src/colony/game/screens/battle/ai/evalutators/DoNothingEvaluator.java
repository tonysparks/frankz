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
public class DoNothingEvaluator implements Evaluator {

    @Override
    public double evaluate(BattleScene scene) {
        return scene.getDice().getRandomRangeMax(0.1);
    }

    @Override
    public Command createCommand() {
        return null;
    }

}
