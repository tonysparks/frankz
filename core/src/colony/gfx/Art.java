/*
 * see license.txt 
 */
package colony.gfx;

import org.hjson.JsonValue;
import org.hjson.Stringify;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;

import colony.game.Logger;
import colony.gfx.Model.ModelData;

/**
 * @author Tony
 *
 */
public class Art {

    private static Gson gson = new Gson();
    
    /**
     * 
     */
    public Art() {
        // TODO Auto-generated constructor stub
    }
    
    public static Model loadBritishSoldier() {        
        return loadModel("assets/gfx/soldiers/british/british_soldier.json");
    }

    public static Model loadModel(String path) {
        try {
            String json = JsonValue.readHjson(Gdx.files.internal(path).reader()).toString(Stringify.PLAIN);
            ModelData data = gson.fromJson(json, ModelData.class);
            return new Model(data);
        }
        catch(Exception e) {
            throw Logger.elog("Failed to load model: " + path, e);            
        }
    }
}
