/*
 * see license.txt 
 */
package colony.game.screens.battle;

import java.util.Optional;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import colony.game.entities.Entity;
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
        // TODO Auto-generated method stub
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
                if(ent.isPresent()) {
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
