/*
 * see license.txt 
 */
package colony.assets;

import java.io.FileReader;

import org.hjson.JsonValue;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;

import colony.game.Logger;

/**
 * Loads a Json file
 * 
 * @author Tony
 *
 */
public class JsonAssetLoader<T> implements AssetLoader<T> {
    private static Gson gson = new Gson();
    
    /**
     * Can't load with Gdx.files before Gdx is initialized, this is a work around
     * 
     * @param type
     * @param filename
     * @return
     */
    public static <T> T loadAsset(Class<T> type, String filename) {
        try {            
            String json = JsonValue.readHjson(new FileReader(filename)).toString();
            return gson.fromJson(json, type);
        }
        catch(Exception e) {
            Logger.elog(e);
        }
        
        return gson.fromJson("{}", type);
    }
    
    
    private Class<T> type;
    
    /**
     * @param type
     */
    public JsonAssetLoader(Class<T> type) {
        this.type = type;
    }
    
    @Override
    public T loadAsset(String filename) {
        try {            
            String json = JsonValue.readHjson(Gdx.files.internal(filename).reader(1024)).toString();
            return gson.fromJson(json, type);
        }
        catch(Exception e) {
            Logger.elog(e);
        }
        
        return gson.fromJson("{}", type);
    }

}
