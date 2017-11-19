/*
 * see license.txt 
 */
package colony.game.screens.battle.ai.evalutators;

import java.util.ArrayList;
import java.util.List;

import colony.game.entities.Entity;
import colony.game.screens.battle.BattleScene;
import colony.game.screens.battle.ai.AI;
import colony.game.screens.battle.commands.Command;

/**
 * @author Tony
 *
 */
public class UberEvaluator implements Evaluator {

    private List<Evaluator> evaluators;
    private Evaluator bestEvaluator;
    
    /**
     * @param aiSystem
     * @param ai
     */
    public UberEvaluator(AI aiSystem, Entity ai) {
        this.evaluators = new ArrayList<>();
        this.evaluators.add(new DoNothingEvaluator());
        this.evaluators.add(new MoveEvaluator(aiSystem, ai));
        this.evaluators.add(new AttackEvaluator(ai));
    }

    @Override
    public double evaluate(BattleScene scene) {
        bestEvaluator = this.evaluators.get(0);
        double bestScore = 0;
        for(Evaluator e : this.evaluators) {
            double score = e.evaluate(scene);
            if(bestEvaluator == null || bestScore < score) {
                bestEvaluator = e;
                bestScore = score;
            }
        
        }
        return bestScore;
    }

    @Override
    public Command createCommand() {
        return this.bestEvaluator.createCommand();
    }

}
