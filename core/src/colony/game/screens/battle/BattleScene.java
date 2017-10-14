/*
 * see license.txt 
 */
package colony.game.screens.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import colony.game.Faction;
import colony.game.Game;
import colony.game.TimeStep;
import colony.game.entities.Entity;
import colony.game.screens.battle.BattleSceneData.EntityRefData;
import colony.game.screens.battle.BattleSceneData.SceneObject;
import colony.game.screens.battle.Board.Slot;
import colony.game.screens.battle.commands.Command;
import colony.game.screens.battle.commands.CommandParameters;
import colony.game.screens.battle.commands.CommandQueue;
import colony.game.screens.battle.commands.MoveToCommand;
import colony.gfx.PositionableRenderable;
import colony.gfx.RenderContext;
import colony.gfx.Renderable;
import colony.gfx.SpriteRenderable;
import colony.sfx.Sounds;
import colony.util.Timer;

/**
 * Describes a Battle Scene
 * 
 * @author Tony
 *
 */
public class BattleScene implements Renderable {

    /**
     * Loads a {@link BattleScene}
     * 
     * @param game
     * @param battleSceneFile
     * @return
     */
    public static BattleScene loadScene(Game game, Hud hud, OrthographicCamera camera, String battleSceneFile) {
        final BattleScene scene = new BattleScene();
        scene.camera = camera;
        scene.hud = hud;
        
        game.loadAsset(battleSceneFile, BattleSceneData.class).onAssetChanged(data -> {
            
            // build the backgroundSprite sprite
            game.loadTexture(data.backgroundImage)
                .onAssetChanged( tex -> {
                    scene.backgroundSprite.setRegion(tex);
                    scene.backgroundSprite.setSize(camera.viewportWidth, camera.viewportHeight);
                })
                .touch();                
                        
            if(data.tileHighlight != null) {
                game.loadTexture(data.tileHighlight.image).onAssetChanged(tex -> {
                    scene.tileHighlighter.setRegion(tex);                    
                    scene.tileHighlighter.setSize(tileSpriteWidth, tileSpriteHeight);
                    scene.tileHighlighter.setColor(Color.valueOf(data.tileHighlight.color));
                }).touch();                
            }
            else {
                scene.tileHighlighter = new Sprite();
            }
            
            for(int i = 0; i < scene.slotImages.length; i++) {
                Sprite sprite = new Sprite();
                scene.slotImages[i] = sprite; 
                game.loadTexture(data.slotImage).onAssetChanged(tex -> {
                   sprite.setRegion(tex);
                   //sprite.setSize(Board.Slot.WIDTH, Board.Slot.HEIGHT/2.0f);
                   sprite.setSize(tileSpriteWidth, tileSpriteHeight);                  
                }).touch();
            }

            scene.sceneObjectSprites.clear();
            for(int i = 0; i < data.objects.length; i++) {
                SceneObject object = data.objects[i];
                Sprite sprite = new Sprite();
                game.loadTexture(object.image).onAssetChanged( tex -> {                            
                    sprite.setRegion(tex);
                    sprite.setSize(object.width, object.height);
                    sprite.setPosition(object.x, object.y);
                    sprite.setRotation(object.rotation);
                    sprite.flip(object.flipX, object.flipY);
                }).touch();
                
                scene.sceneObjectSprites.add(new SpriteRenderable(sprite));                
            }
            
            
            if(data.attacker != null) {
                scene.attacker = new Faction(data.attacker.name);
                for(EntityRefData entData : data.attacker.entities) {
                    Entity ent = Entity.loadEntity(game, entData.entityData);
                    scene.attacker.addEntity(ent);
                    
                    Vector3 worldPos = scene.getWorldPos(entData.indexX, entData.indexY);
                    //worldPos.x -= tileHalfWidth;
                    //worldPos.y -= tileHalfHeight;
                    
                    ent.setPos(worldPos);
                }
            }
            else {
                scene.attacker = new Faction("<undefined>");
            }
            
            if(data.defender != null) {
                scene.defender = new Faction(data.defender.name);
                for(EntityRefData entData : data.defender.entities) {
                    Entity ent = Entity.loadEntity(game, entData.entityData);
                    scene.defender.addEntity(ent);
                    
                    Vector3 worldPos = scene.getWorldPos(entData.indexX, entData.indexY);
                    //worldPos.x -= tileHalfWidth;
                    //worldPos.y -= tileHalfHeight;
                    
                    ent.setPos(worldPos);
                }
            }
            else {
                scene.defender = new Faction("<undefined>");
            }
            
        }).touch();
        
        return scene;
    }
           

    private Sprite backgroundSprite;
    private Sprite[] slotImages;
    private Sprite tileHighlighter;
    
    private List<PositionableRenderable> sceneObjectSprites;
    
    private Board board;
    
    private OrthographicCamera camera;
    
    private Hud hud;
    private Vector3 worldPos;
    private Rectangle bounds;
    private int index;
    
    private static final float startX = 14.8f;
    private static final float startY = 1.7f;
        
    public static final float tileHalfWidth  = Board.Slot.WIDTH;
    public static final float tileHalfHeight = Board.Slot.HEIGHT/2.0f;
    
    //private static final float tileSpriteWidth  = 1.4f * 2.0f;
    public static final float tileSpriteWidth  = Board.Slot.WIDTH * 2.0f;
    public static final float tileSpriteHeight = tileSpriteWidth / 2.0f;
    
    private Slot highlightedSlot;
    private Timer highlightTimer;
    
    private Faction attacker;
    private Faction defender;
    
    private List<PositionableRenderable> renderables;
    
    private Entity selectedEntity;
    private Slot selectedSlot;
        
    private BoardGraph graph;
    
    private CommandQueue commandQueue;
    
    /**
     * 
     */
    public BattleScene() {               
        this.backgroundSprite = new Sprite();
        this.tileHighlighter = new Sprite();
        this.sceneObjectSprites = new ArrayList<>();
        
        this.renderables = new ArrayList<>();
        
        this.slotImages = new Sprite[Board.HEIGHT * Board.WIDTH];
        
        this.board = new Board();
        this.worldPos = new Vector3();
        this.bounds = new Rectangle();
        this.bounds.width = Slot.WIDTH / 2;
        this.bounds.height = Slot.HEIGHT / 2;
        
        this.highlightTimer = new Timer(true, 33);
        
        this.graph = new BoardGraph(board);   
        
        this.commandQueue = new CommandQueue(this);
    }
    
    
    @Override
    public void update(TimeStep timeStep) {
        this.commandQueue.update(timeStep);
        
        if(this.highlightedSlot != null) {
            Vector3 worldPos = getWorldPos(this.highlightedSlot);
            this.tileHighlighter.setPosition(worldPos.x - tileHalfWidth, worldPos.y - tileHalfHeight);            
        }                
        
        this.highlightTimer.update(timeStep);
        
        this.renderables.clear();
        this.renderables.addAll(this.attacker.getEntities());
        this.renderables.addAll(this.defender.getEntities());
        this.renderables.addAll(this.sceneObjectSprites);
        
        this.renderables.sort( (a,b) -> (int)a.getY() - (int)b.getY());
    }
    
    @Override
    public void render(RenderContext context) {
        this.backgroundSprite.draw(context.batch);
                
        this.index = 0;
        
        this.board.forEachSlot( slot -> {                        
            Vector3 worldPos = getWorldPos(slot);

            Sprite sprite = slotImages[this.index++];
            sprite.setPosition(worldPos.x - tileHalfWidth, worldPos.y - tileHalfHeight);
            sprite.draw(context.batch);
            
            
            /*
            hud.setFont("Consola", 12);               
            float x = worldPos.x;
            float y = worldPos.y;
            
            Vector3 screenPos = camera.project(worldPos);
            screenPos.y = Gdx.graphics.getHeight() - screenPos.y;
            
            hud.drawString( (int)slot.x + "," +  (int)slot.y, screenPos.x, screenPos.y, Color.RED);
            
            String str = String.format("%3.2f, %3.2f", x, y);
            hud.drawString( str, screenPos.x-25, screenPos.y+15, Color.RED);
            
            if(this.index  == 17) 
            {
                context.batch.end();
                
                GraphNode<Slot> node = this.graph.getNode(slot);
                context.fillRect(worldPos.x, worldPos.y, 0.12f, 0.12f, Color.RED);
                float wx = worldPos.x;
                float wy = worldPos.y;
                for(int i = 0; i < node.edges().size(); i++) {
                    Edge<Slot> edge = node.edges().get(i);
                    if(edge != null) {                        
                        worldPos = getSlotScreenPos(edge.getRight().getValue());                    
                        context.drawLine(wx, wy, worldPos.x, worldPos.y, Color.BLUE);
                    }
                }
                context.batch.begin();
            }*/
        });
        
        if(this.highlightedSlot != null) {
            this.tileHighlighter.draw(context.batch);
        }
        
        for(int i = 0; i < this.renderables.size(); i++) {
            this.renderables.get(i).render(context);
        }
        

        if(this.selectedEntity != null) {
            context.batch.end();
            context.drawRect(selectedEntity.bounds.x, selectedEntity.bounds.y, selectedEntity.bounds.width, selectedEntity.bounds.height, Color.PINK);
            context.batch.begin();
        }
        
        this.commandQueue.render(context);

    }

    /**
     * @return the graph
     */
    public BoardGraph getGraph() {
        return graph;
    }
    
    /**
     * @return the pathPlanner
     */
    public PathPlanner newPathPlanner(Entity ent) {
        return new PathPlanner(this, this.graph, ent);
    }
    

    /**
     * Set the highlighted slot
     * 
     * @param worldX
     * @param worldY
     */
    public void setHighlightedSlot(float worldX, float worldY) {
        Slot slot = getSlot(worldX, worldY);
        if(slot != null) {
            if(this.highlightedSlot != slot) {
                if(this.highlightedSlot == null || this.highlightTimer.isTime()) {
                    //Sounds.playSound(Sounds.uiSlotHover);
                }
            }
            
            this.highlightedSlot = slot;
        }
        else {
            this.highlightedSlot = null;
        }
    }
    
    /**
     * @return the tileHighlighter
     */
    public Sprite getTileHighlighter() {
        return tileHighlighter;
    }
        
    
    /**
     * Get the world position of the {@link Slot}
     * 
     * @param slot
     * @return the world position of the {@link Slot}
     */
    public Vector3 getWorldPos(Slot slot) {
        return getWorldPos(slot.x, slot.y);
    }
    
    
    /**
     * Given the array index values, give the corresponding world
     * coordinates.
     * 
     * @param indexX
     * @param indexY
     * @return
     */
    public Vector3 getWorldPos(int indexX, int indexY) {
        worldPos.x = ((indexX - indexY) * tileHalfWidth)  + startX;
        worldPos.y = ((indexX + indexY) * tileHalfHeight) + tileHalfHeight + startY;
        
        return worldPos;
    }
    
    
    /**
     * Select a {@link Slot}
     * 
     * @param worldX
     * @param worldY
     */
    public void selectSlot(float worldX, float worldY) {
        Slot slot = getSlot(worldX, worldY);
        if(slot != null) {
            this.selectedSlot = slot;
        }
    }
    
    
    /**
     * Selects an {@link Entity} who makes them eligible for dispatching
     * {@link Command}s to.
     * 
     * @param worldX
     * @param worldY
     */
    public void selectEntity(float worldX, float worldY) {
        // first try to see if the user selected by clicking
        // on the Entity
        Optional<Entity> ent = findEntity(worldX, worldY);
        
        // if the didn't touch any entity, see if they selected
        // by clicking on the Slot
        if(!ent.isPresent()) {                        
            Slot slot = getSlot(worldX, worldY);
            if(slot!=null) {                                
                ent = getEntityOnSlot(slot);
            }
        }
        
        if(ent.isPresent()) {
            this.selectedEntity = ent.get();
            Sounds.playSound(Sounds.uiSlotSelect);
        }
        else {
            if(this.selectedEntity != null) {
                Sounds.playSound(Sounds.uiSlotSelect);
            }
            
            this.selectedEntity = null;
        }
    }
    
    
    /**
     * If there is a Selected entity
     * 
     * @return true if there is a selected entity
     */
    public boolean hasSelectedEntity() {
        return this.selectedEntity != null;
    }
    
    
    /**
     * Issues a {@link MoveToCommand} for the selected entity
     * 
     * @see #selectEntity(float, float)
     */
    public void issueMoveToCommand() {        
        this.commandQueue.addCommand(new MoveToCommand(new CommandParameters(this.selectedEntity, this.selectedSlot)));
        
        this.selectedEntity = null;
        this.selectedSlot = null;
    }
    
    private Optional<Entity> findEntity(float worldX, float worldY) {
        Optional<Entity> ent = findEntity(this.attacker.getEntities(), worldX, worldY);
        if(!ent.isPresent()) {
            ent = findEntity(this.defender.getEntities(), worldX, worldY);
        }
        return ent;
    }
    
    private Optional<Entity> findEntity(List<Entity> entities, float worldX, float worldY) {
        return entities.stream()
                .filter( ent -> ent.bounds.contains(worldX, worldY))
                .findFirst();
    }
    
    /**
     * Determines if the supplied {@link Entity} is on the supplied {@link Slot}
     * 
     * @param ent
     * @param slot
     * @return true if the {@link Entity} is stationed on the {@link Slot}
     */
    public boolean isEntityOnSlot(Entity ent, Slot slot) {
        Vector3 worldPos = getWorldPos(slot);
        bounds.setCenter(worldPos.x, worldPos.y);        
        return bounds.contains(ent.getPos());
    }
    
    /**
     * Get the {@link Entity} occupying this {@link Slot}
     * 
     * @param slot
     * @return the entity or null if none
     */
    public Optional<Entity> getEntityOnSlot(Slot slot) {                        
        Optional<Entity> entity = this.attacker.getEntities()
                                               .stream()
                                               .filter( ent -> isEntityOnSlot(ent, slot))
                                               .findFirst();
        
        if(!entity.isPresent()) {
            entity = this.defender.getEntities()
                                  .stream()
                                  .filter( ent -> isEntityOnSlot(ent, slot))
                                  .findFirst();
        }
        
        return entity;        
    }
    
    /**
     * Get the {@link Slot} this entity is on
     * 
     * @param entity
     * @return the slot
     */
    public Slot getSlot(Entity entity) {
        return getSlot(entity.getX(), entity.getY());
    }
    
    /**
     * Get a slot by the world coordinates.  Returns null
     * if no slot is present at the specified position.
     * 
     * @param worldX
     * @param worldY
     * @return the Slot if in bounds otherwise null
     */
    public Slot getSlot(float worldX, float worldY) {   
        float adjScreenX = worldX - startX;
        float adjScreenY = worldY - startY;
        
        int x = (int) ((adjScreenY / tileHalfHeight) + (adjScreenX / tileHalfWidth));
        int y = (int) ((adjScreenY / tileHalfHeight) - (adjScreenX / tileHalfWidth));
        
        int indexX = x / 2;
        int indexY = y / 2;
        
        return this.board.getSlotByIndex(indexX, indexY);
    }
}
