/*
 * see license.txt 
 */
package colony.game.screens.battle;

import java.util.Optional;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import colony.game.entities.Entity;
import colony.game.entities.EntityData.AttackData;
import colony.game.screens.battle.Board.Slot;

/**
 * Handles user input for the battle scene
 * 
 * @author Tony
 *
 */
public class BattleScreenInputProcessor implements InputProcessor {

    private BattleScreen screen;
    private Vector3 mousePos, mousePosCopy;
    /**
     * @param screen
     */
    public BattleScreenInputProcessor(BattleScreen screen) {
        this.screen = screen;
        this.mousePos = new Vector3();
        this.mousePosCopy = new Vector3();
    }

    /**
     * @return the mousePos
     */
    public Vector3 getMousePos() {
        this.mousePosCopy.set(this.mousePos);
        return this.mousePosCopy;
    }
    
    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Keys.CONTROL_LEFT) {
            BattleScene scene = this.screen.getBattleScene();
            if(scene.hasSelectedEntity()) {
                Entity ent = scene.getSelectedEntity();
                AttackData attack = ent.getAttackData();
                if(attack.hasSplitAttack()) {
                    Vector2 pos = ent.getPos();
                    scene.setHighlighter(pos.x, pos.y, attack.splitRange, Color.SCARLET);
                }
            }
        }
        
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {        
        mousePos.set(screenX, screenY, 0);
        
        OrthographicCamera camera = screen.getCamera();
        Vector3 pos = camera.unproject(mousePos);
        
        BattleScene scene = this.screen.getBattleScene();
        if(button == Buttons.RIGHT) {
            if(scene.hasSelectedEntity()) {
                Optional<Entity> ent = scene.getEntity(pos.x, pos.y);
                
                if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
                    
                    AttackData attack = scene.getSelectedEntity().getAttackData();
                    
                    if(attack.hasTossAttack()) {
                    
                        if(scene.hasSecondSelectedEntity() /*&& !ent.isPresent()*/) {
                            Slot slot = scene.getSlot(pos.x, pos.y);
                            if(slot!=null) {
                                scene.issueTossCommand(slot);
                            }
                        }
                        else {
                            scene.setHighlighter(pos.x, pos.y, attack.tossRange, Color.SCARLET);
                            scene.selectSecondEntity(ent);
                        }
                    }
                    else if(attack.hasSplitAttack()) {
                        Slot slot = scene.getSlot(pos.x, pos.y);
                        if(slot!=null) {
                            scene.issueSplitCommand(slot);
                        }
                    }
                }
                else {
                    if(ent.isPresent() && scene.getSelectedEntity().isEnemy(ent.get())) {
                        scene.issueAttackCommand(ent.get());
                    }
                    else {
    
                        Slot slot = scene.getSlot(pos.x, pos.y);
                        if(slot!=null) {
                            scene.issueMoveToCommand(slot);
                        }
                    }
                }
            }
            
        }
        else {
            if(button == Buttons.LEFT) {
                scene.selectEntity(pos.x, pos.y);
            }
        }
        
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mousePos.set(screenX, screenY, 0);
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }

}
