/*
 * see license.txt 
 */
package colony.game.screens.battle.commands;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import colony.game.Logger;
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
    
    private Queue<Command> commandQueue, commandsToAdd;
    private Queue<Action>  concurrentQueue, concurrentCommandsToAdd;
    private Action currentAction;
    
    /**
     * 
     */
    public CommandQueue(BattleScene scene) {
        this.scene = scene;
        this.commandQueue = new LinkedList<>();
        this.concurrentQueue = new LinkedList<>();
        
        this.commandsToAdd = new LinkedList<>();
        this.concurrentCommandsToAdd = new LinkedList<>();
    }
    
    
    public boolean addConcurrentCommand(Command cmd) {
        CommandResult result = cmd.checkPreconditions(scene);
        if(result.isFailure()) {
            // TODO: Send notification of failure
            Logger.log("** Failed command preconditions: " + result.getMessage());
            return false;
        }
        
        Action action = cmd.createAction(scene);
        action.start();
        
        this.concurrentCommandsToAdd.add(action);
    
        return true;
    }
    
    public void addCommand(Command cmd) {
        this.commandsToAdd.add(cmd);
    }
    
    public boolean isEmpty() {
        return this.commandQueue.isEmpty() && this.commandsToAdd.isEmpty() &&
                this.concurrentQueue.isEmpty() && this.concurrentCommandsToAdd.isEmpty();
    }
    
    
    @Override
    public void update(TimeStep timeStep) {
        while(!this.commandsToAdd.isEmpty()) {
            this.commandQueue.add(this.commandsToAdd.poll());
        }
        
        
        if(this.currentAction == null || !this.currentAction.status().inProgress()) {
            if(this.currentAction != null) {
                this.currentAction.end();
            }
            
            this.currentAction = null;
            
            if(!this.commandQueue.isEmpty()) {
                Command cmd = this.commandQueue.poll();
                CommandResult result = cmd.checkPreconditions(scene);
                if(result.isFailure()) {
                    // TODO: Send notification of failure
                    Logger.log("** Failed command preconditions: " + result.getMessage());
                }
                else {
                    this.currentAction = cmd.createAction(scene);
                    this.currentAction.start();
                }
            }
        }
        
        while(!this.concurrentCommandsToAdd.isEmpty()) {
            this.concurrentQueue.add(this.concurrentCommandsToAdd.poll());
        }
        
        Iterator<Action> it = this.concurrentQueue.iterator();
        while(it.hasNext()) {
            Action action = it.next();
            
            if(!action.status().inProgress()) {
                action.end();
                it.remove();
            }
            else {
                action.update(timeStep);
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
        
        Iterator<Action> it = this.concurrentQueue.iterator();
        while(it.hasNext()) {
            it.next().render(context);
        }
    }

}
