/*
 * see license.txt 
 */
package colony.game.screens.battle;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import colony.game.Logger;

/**
 * Handles user input for the battle scene
 * 
 * @author Tony
 *
 */
public class BattleScreenInputProcessor implements InputProcessor {

    private BattleScreen screen;
    private Vector3 mousePos;
    /**
     * @param screen
     */
    public BattleScreenInputProcessor(BattleScreen screen) {
        this.screen = screen;
        this.mousePos = new Vector3();
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
        OrthographicCamera camera = screen.getCamera();
        
        mousePos.set(screenX, screenY, 0);
        
        Logger.log(camera.unproject(mousePos));
        
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }

}
