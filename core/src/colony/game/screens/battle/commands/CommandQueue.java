/*
 * see license.txt 
 */
package colony.game.screens.battle.commands;

import java.util.LinkedList;
import java.util.Queue;

import colony.game.TimeStep;
import colony.game.screens.battle.BattleScene;
import colony.gfx.RenderContext;
import colony.gfx.Renderable;

/**
 * @author Tony
 *
 */
public class CommandQueue implements Renderable {

    private BattleScene scene;
    
    private Queue<Command> commandQueue;
    private Action currentAction;
    
    /**
     * 
     */
    public CommandQueue(BattleScene scene) {
        this.scene = scene;
        this.commandQueue = new LinkedList<>();
    }
    
    public void addCommand(Command cmd) {
        this.commandQueue.add(cmd);
    }
    
    public boolean isEmpty() {
        return this.commandQueue.isEmpty();
    }
    
    
    @Override
    public void update(TimeStep timeStep) {
        if(this.currentAction == null || !this.currentAction.status().inProgress()) {
            this.currentAction = null;
            
            if(!this.commandQueue.isEmpty()) {
                Command cmd = this.commandQueue.poll();
                CommandResult result = cmd.checkPreconditions(scene);
                if(result.isFailure()) {
                    // TODO: Send notification of failure
                }
                else {
                    this.currentAction = cmd.createAction(scene);
                    this.currentAction.start();
                }
            }
        }
        
        if(this.currentAction != null) {
            this.currentAction.update(timeStep);
        }
    }
    
    @Override
    public void render(RenderContext context) {
        if(this.currentAction != null) {
            this.currentAction.render(context);
        }
    }

}
