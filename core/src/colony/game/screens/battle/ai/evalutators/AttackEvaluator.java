/*
 * see license.txt 
 */
package colony.game.screens.battle.ai.evalutators;

import java.util.Optional;
import java.util.Set;

import colony.game.entities.Entity;
import colony.game.entities.EntityData.AttackData;
import colony.game.screens.battle.BattleScene;
import colony.game.screens.battle.Board.Slot;
import colony.game.screens.battle.commands.AttackCommand;
import colony.game.screens.battle.commands.Command;
import colony.game.screens.battle.commands.CommandParameters;
import colony.graph.GraphNode;

/**
 * Determines if the AI agent should do a vanilla attack
 * 
 * @author Tony
 *
 */
public class AttackEvaluator implements Evaluator {

    private Entity ai;
    private Optional<Entity> entityToAttack;
    
    /**
     * @param ai
     */
    public AttackEvaluator(Entity ai) {
        this.ai = ai;
        this.entityToAttack = Optional.empty();
    }

    @Override
    public double evaluate(BattleScene scene) {
        double score = 0;
        this.entityToAttack = Optional.empty();
        
        
        AttackData attack = this.ai.getAttackData();
        if(this.ai.hasPoints(attack.actionPoints)) {
            Set<GraphNode<Slot>> aroundMe = scene.getGraph().getNodesInRadius(this.ai, true, attack.attackRange);
            
            int weakestDefensiveScore = 0;
            Entity entityToAttack = null;
            for(GraphNode<Slot> n : aroundMe) {
                Slot slot = n.getValue();
                
                // account for rounding errors, ignore any slots
                // that fall out of our range
                int distance = ai.distance(scene, slot);
                if(distance > attack.attackRange || distance < 0) {
                    continue;
                }
                
                Optional<Entity> optEntity = scene.getEntityOnSlot(slot);
                if(optEntity.isPresent()) {
                    Entity potentialTarget = optEntity.get(); 
                
                    if(potentialTarget.isEnemy(ai)) {
                        int defenseScore = potentialTarget.calculateDefense(scene);
                        if(entityToAttack == null || defenseScore < weakestDefensiveScore) {
                            entityToAttack = potentialTarget;
                            weakestDefensiveScore = defenseScore;
                        }
                    }
                }
            }
            
            if(entityToAttack != null) {
                this.entityToAttack = Optional.of(entityToAttack);
                
                if(attack.offenseFactor > weakestDefensiveScore/2) {
                    score = scene.getDice().getRandomRangeMin(0.3);
                }
                else {
                    score = scene.getDice().getRandomRange(0.1, 0.8);
                }
            }
        }
        
        return score;
    }

    @Override
    public Command createCommand() {
        return new AttackCommand(new CommandParameters(ai, entityToAttack.get()));
    }

}
