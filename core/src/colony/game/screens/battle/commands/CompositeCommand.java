/*
 * see license.txt 
 */
package colony.game.screens.battle.commands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import colony.game.TimeStep;
import colony.game.screens.battle.BattleScene;
import colony.gfx.RenderContext;

/**
 * Build a sequenceable set of {@link Command}s to be executed in order.
 * 
 * @author Tony
 *
 */
public class CompositeCommand extends Command {

    private Queue<Command> cmds;
    
    /**
     * @param params
     */
    public CompositeCommand(Queue<Command> cmds) {
        super(null);
    }
    
    public CompositeCommand(Command ... cmds) {
        this(new LinkedList<>(Arrays.asList(cmds)));
    }

    @Override
    public CommandResult checkPreconditions(BattleScene scene) {
        return this.cmds.peek().checkPreconditions(scene);
    }

    @Override
    public Action createAction(BattleScene scene) {
        return new Action(this.cmds.peek()) {

            Action currentAction = command.createAction(scene);
            
            @Override
            public void render(RenderContext context) {
                currentAction.render(context);
            }

            @Override
            public void update(TimeStep timeStep) {
                currentAction.update(timeStep);
            }

            @Override
            protected void doStart() {
                currentAction.doStart();
            }

            @Override
            protected void doEnd() {
                currentAction.doEnd();
                
                // if one of the commands fails, then
                // we fail the sequence
                if(currentAction.status().isFailure()) {
                    cmds.clear();
                }
                
                if(!cmds.isEmpty()) {
                    Command nextCommand = cmds.poll();
                    command = nextCommand; 
                    currentAction = nextCommand.createAction(scene);
                }
                
            }

            @Override
            public void cancel() {
                currentAction.cancel();
                cmds.clear();
            }

            @Override
            public CommandResult status() {
                if(cmds.isEmpty()) {
                    return currentAction.status();
                }
                
                return inProgress();
            }
            
        };
    }

}
