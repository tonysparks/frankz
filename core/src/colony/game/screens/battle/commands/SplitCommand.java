/*
 * see license.txt 
 */
package colony.game.screens.battle.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import colony.game.entities.Entity;
import colony.game.entities.EntityData.AttackData;
import colony.game.screens.battle.BattleScene;
import colony.game.screens.battle.Board.Slot;
import colony.game.screens.battle.PathPlanner;
import colony.game.screens.battle.PathPlanner.SearchType;
import colony.graph.GraphNode;

/**
 * A Squirt Split Attack
 * 
 * @author Tony
 *
 */
public class SplitCommand extends AttackCommand {

    public class SplitAction extends AttackCommand.AttackAction {
        
        public SplitAction(SplitCommand cmd, BattleScene scene) {
            super(cmd, scene);
        }
        
        @Override
        protected void onHit() {    
            PathPlanner planner = scene.newPathPlanner(parameters.selectedEntity);
            planner.findPath(SearchType.AllSlots, scene.getSlot(parameters.selectedEntity), parameters.targetSlot);
            
            List<CommandParameters> pushCmds = new ArrayList<>();
            for(GraphNode<Slot> n : planner.getPath()) {
                Slot currentSlot = n.getValue();
                scene.getEntityOnSlot(currentSlot).ifPresent(ent -> {
                    scene.getEmptyAdjacentSlot(currentSlot)
                         .ifPresent(nextSlot -> pushCmds.add(new CommandParameters(ent, nextSlot)));                                        
                });
            }            
            
            scene.addConcurrentCommand(new CompositeCommand(pushCmds.stream()
                    .map(param -> new PushedCommand(param))
                    .collect(Collectors.toList()))
            );
        }
        
    }
    
    /**
     * @param params
     */
    public SplitCommand(CommandParameters params) {
        super(params);
    }
    
    @Override
    protected CommandResult checkTarget(BattleScene scene, Entity entity, AttackData data) {     
        return inProgress();
    }
           

    @Override
    public Action createAction(BattleScene scene) {
        return new SplitAction(this, scene);
    }

}
