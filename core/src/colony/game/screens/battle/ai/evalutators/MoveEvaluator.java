/*
 * see license.txt 
 */
package colony.game.screens.battle.ai.evalutators;

import java.util.Optional;
import java.util.Set;

import colony.game.entities.Entity;
import colony.game.entities.EntityData.MovementData;
import colony.game.screens.battle.BattleScene;
import colony.game.screens.battle.Board.Slot;
import colony.game.screens.battle.Dice;
import colony.game.screens.battle.ai.AI;
import colony.game.screens.battle.ai.InfluenceMap;
import colony.game.screens.battle.ai.StrategySettings;
import colony.game.screens.battle.ai.InfluenceMap.Influence;
import colony.game.screens.battle.commands.Command;
import colony.game.screens.battle.commands.CommandParameters;
import colony.game.screens.battle.commands.MoveToCommand;
import colony.graph.GraphNode;

/**
 * Determines if the AI agent should move to a slot;
 * 
 * @author Tony
 *
 */
public class MoveEvaluator implements Evaluator {

    private AI aiSystem;
    
    private Entity ai;
    private Optional<Slot> destinationSlot;
    
    /**
     * @param ai
     */
    public MoveEvaluator(AI aiSystem, Entity ai) {
        this.aiSystem = aiSystem;
        this.ai = ai;
    }

    private double calculateScore(Influence influence, double defenseWeight, double offenseWeight) {                
        double score = influence.vulnerability * defenseWeight +
                       influence.tension * offenseWeight +                       
                       influence.aiInfluence * .2;
        
        score -= influence.enemyInfluence * .3;
        
        return score;
    }
    
    @Override
    public double evaluate(BattleScene scene) {
        double score = 0;
        this.destinationSlot = Optional.empty();
        
        
        MovementData movement = this.ai.getMovementData();
        Set<GraphNode<Slot>> aroundMe = scene.getGraph().getNodesInRadius(this.ai, false, movement.actionPoints * ai.getActionMeter().getActionPoints());
        if(!aroundMe.isEmpty()) {
            // TODO: Do some logic on what is more important to the overall
            // team strategy (attack a weak component or backup a friend)
            InfluenceMap map = this.aiSystem.getInfluenceMap();
            
            Influence currentInfluence = map.getInfluence(ai);
            
            Slot bestSlot = scene.getSlot(ai);
            Influence bestInfluence = currentInfluence;
            double bestScore = 0;
            
            StrategySettings settings = aiSystem.getSettings();
            Dice dice = scene.getDice();
            
            double defenseWeight = 0.2;
            double offenseWeight = 0.3;
            
            // randomly determine if we should be more aggressive or
            // defensive
            if(dice.chances(settings.defensiveness)) {
                double tmp = defenseWeight;
                defenseWeight = offenseWeight;
                offenseWeight = tmp;
            }
               
            
            for(GraphNode<Slot> n : aroundMe) {
                Slot targetSlot = n.getValue();
                Influence influence = map.getInfluence(targetSlot);
                
                double currentScore = calculateScore(bestInfluence, defenseWeight, offenseWeight);
                int currentDefenseScore = ai.calculateDefense(bestSlot, scene);
                
                double targetScore = calculateScore(influence, defenseWeight, offenseWeight); 
                int targetDefenseScore = ai.calculateDefense(targetSlot, scene);
                
                if(currentDefenseScore < targetDefenseScore) {
                    currentScore *= .9;
                }
                else if (currentDefenseScore > targetDefenseScore) {
                    targetScore *= .9;
                }
                
                if(currentScore < targetScore) {                    
                    bestSlot = targetSlot;
                    bestInfluence = influence;
                    bestScore = targetScore;
                }               
            }
            
            if(bestSlot != scene.getSlot(ai)) {
                if(bestScore > 100) {
                    score = dice.getRandomRangeMin(0.8);
                }
                else if (bestScore > 50) {
                    score = dice.getRandomRangeMin(0.5);
                }
                else if (bestScore > 25) {
                    score = dice.getRandomRangeMin(0.2);
                }
                
                destinationSlot = Optional.of(bestSlot);
            }
        }
                
        
        return score;
    }

    @Override
    public Command createCommand() {
        return new MoveToCommand(new CommandParameters(ai, destinationSlot.get()));
    }

}
