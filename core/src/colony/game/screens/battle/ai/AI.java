/*
 * see license.txt 
 */
package colony.game.screens.battle.ai;

import java.util.ArrayList;
import java.util.List;

import colony.game.Faction;
import colony.game.TimeStep;
import colony.game.screens.battle.BattleScene;
import colony.game.screens.battle.Turn;
import colony.game.screens.battle.TurnEndedEvent;
import colony.game.screens.battle.ai.evalutators.Evaluator;
import colony.game.screens.battle.ai.evalutators.UberEvaluator;
import colony.game.screens.battle.commands.Command;
import colony.game.screens.battle.commands.CommandQueue;
import colony.gfx.RenderContext;
import colony.gfx.Renderable;

/**
 * Tactical Battle AI
 * 
 * @author Tony
 *
 */
public class AI implements Renderable {

    private BattleScene scene;
    private Faction aiFaction;
    
    private InfluenceMap influenceMap;
    
    private StrategySettings settings;
        
    private List<Evaluator> evaluators;
    private boolean isAITurn;
    
    /**
     * @param scene
     * @param aiFaction
     */
    public AI(BattleScene scene, Faction aiFaction) {
        this.scene = scene;
        this.aiFaction = aiFaction;
        this.settings = new StrategySettings();
        this.settings.aggressiveness = scene.getDice().getRandomRange(0.1, 0.9);
        this.settings.defensiveness = scene.getDice().getRandomRange(0.3, 0.8);
        
        this.influenceMap = new InfluenceMap(this, scene);
        this.evaluators = new ArrayList<>();
        this.isAITurn = true;
                
        this.scene.getDispatcher().addEventListener(TurnEndedEvent.class, event -> prepareTurn(event.current));
    }
    
    private void prepareTurn(Turn currentTurn) {
        this.isAITurn = currentTurn.getFactionsTurn().equals(this.aiFaction);
        
        // if it's the AI's turn, do something
        if(this.isAITurn) {            
            this.evaluators.clear();            
            this.aiFaction.getEntities()
                          .forEach(ent -> evaluators.add(new UberEvaluator(this, ent)));
        }
    }

    @Override
    public void update(TimeStep timeStep) {
        if(this.isAITurn) {
            this.influenceMap.applyInfluences();
            
            CommandQueue queue = scene.getCommandQueue();
            if(queue.isEmpty()) {
                if(this.evaluators.isEmpty()) {
                    prepareTurn(scene.getCurrentTurn());
                }
                
                Command command = null;
                for(int i = 0; i < this.evaluators.size(); i++) {
                    Evaluator eval = this.evaluators.get(i);
                    eval.evaluate(scene);
                    
                    Command cmd = eval.createCommand();                    
                    if(cmd != null) {
                        command = cmd;
                        break;
                    }
                }
                
                if(command == null) {                                        
                    scene.endTurn();
                }
                else {
                    queue.addCommand(command);
                }
            }
        }
        
        
    }
    
    @Override
    public void render(RenderContext context) {
        this.influenceMap.render(context);        
    }
    
    
    /**
     * @return the settings
     */
    public StrategySettings getSettings() {
        return settings;
    }
    
    /**
     * @return the aiFaction
     */
    public Faction getAIFaction() {
        return aiFaction;
    }
    
    /**
     * @return the influenceMap
     */
    public InfluenceMap getInfluenceMap() {
        return influenceMap;
    }

}
