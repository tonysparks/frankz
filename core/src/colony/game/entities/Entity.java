/*
 * see license.txt 
 */
package colony.game.entities;

import java.util.Optional;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import colony.game.Faction;
import colony.game.Game;
import colony.game.TimeStep;
import colony.game.Updatable;
import colony.game.entities.EntityData.AttackData;
import colony.game.entities.EntityData.MovementData;
import colony.game.entities.EntityData.StatData;
import colony.game.screens.battle.BattleScene;
import colony.game.screens.battle.Board.Slot;
import colony.game.screens.battle.PathPlanner.SearchType;
import colony.gfx.PositionableRenderable;
import colony.gfx.RenderContext;
import colony.gfx.effects.Effect;
import colony.gfx.effects.Effects;
import colony.gfx.effects.FlickerEffect;
import colony.graph.Edge;
import colony.graph.GraphNode;
import colony.sfx.Sounds;
import colony.util.Timer;

/**
 * 
 * 
 * @author Tony
 *
 */
public class Entity implements Updatable, PositionableRenderable {
    
    public static Entity loadEntity(Game game, String entityType) {
        Entity ent = new Entity();
        game.loadAsset(entityType, EntityData.class).onAssetChanged( data -> {
            ent.bounds.width = data.width;
            ent.bounds.height = data.height;
                        
            if(ent.pos.isZero()) {
                ent.pos.x = data.x;
                ent.pos.y = data.y;
            }

            
            if(data.model != null) {
                ent.model = EntityModel.loadEntityModel(game, ent, data.model);
            }
            
            ent.actionMeter.setStartingActionPoints(data.stats.startingActionPoints);
            
            ent.stats = data.stats;
            ent.movementData = data.movement;
            ent.attackData = data.attack;
            
        }).touch();
        
        ent.init();
        
        return ent;
    }
    
    public Vector2 pos;
    public Vector2 centerPos;
    public Rectangle bounds;
    public Vector2 facing;
    
    private EntityModel model;
    private EntityState state;
    
    private MovementData movementData;
    private AttackData attackData;
    private StatData stats;
    
    private ActionMeter actionMeter;
    
    private int health;
    private Faction faction;
    
    private Effects effects;
    private Timer dyingTimer;
    
    private boolean isSelected;
    
    public Entity() {
        this.pos = new Vector2();
        this.centerPos = new Vector2();
        this.facing = new Vector2();
        
        this.bounds = new Rectangle();
        
        this.actionMeter = new ActionMeter(0);
        
        this.effects = new Effects();
        this.dyingTimer = new Timer(false, 2_000);
        this.dyingTimer.stop();
        
        this.state = EntityState.Idle;
        this.isSelected = false;
    }
    
    /**
     * Called on initial creation
     */
    private void init() {
        this.health = stats.startingHealth;
        this.actionMeter.reset();
    }
    
    /**
     * Joins the supplied faction
     * 
     * @param faction
     */
    public void join(Faction faction) {
        this.faction = faction;
    }
    
    /**
     * @return the faction
     */
    public Faction getFaction() {
        return faction;
    }
    
    /**
     * If this Entity is in the supplied {@link Faction}
     * 
     * @param f
     * @return true if this entity is in the supplied faction
     */
    public boolean inFaction(Faction f) {
        return f.equals(this.faction);
    }
    
    /**
     * If these two {@link Entity}s are enemies
     * 
     * @param ent
     * @return true if enemies
     */
    public boolean isEnemy(Entity ent) {
        return !ent.getFaction().equals(this.faction);
    }
    
    /**
     * The distance between this {@link Entity} and the supplied one
     * by number of slots
     * 
     * @param ent
     * @return the distance in number of slots
     */
    public int distance(BattleScene scene, Entity ent) {
        return distance(scene, scene.getSlot(ent));        
    }
    
    public int distance(BattleScene scene, Slot slot) {
        return scene.newPathPlanner(this).pathCost(SearchType.AllSlots, scene.getSlot(this), slot);        
    }
    
    
    /**
     * @return the isSelected
     */
    public boolean isSelected() {
        return isSelected;
    }
    
    /**
     * @param isSelected the isSelected to set
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    /**
     * If this entity is alive
     * 
     * @return true if alive
     */
    public boolean isAlive() {
        return this.state != EntityState.Dead;
    }
    
    /**
     * @return the health
     */
    public int getHealth() {
        return health;
    }
    
    /**
     * @return the max health
     */
    public int getMaxHealth() {
        return this.stats.maxHealth;
    }
    
    /**
     * Heal this entity to their heal factor
     */
    public void heal() {
        this.health += this.stats.healFactor;
        if(this.health > this.stats.maxHealth) {
            this.health = this.stats.maxHealth;
        }
    }
    
    public void addFlickerEffect(Color start, Color end, long timeToLive, long flickerTime) {
        addEffect(new FlickerEffect(this.model.getSprite(), start, end, timeToLive, flickerTime));
    }
    
    public void addEffect(Effect effect) {
        this.effects.addEffect(effect);
    }
    
    /**
     * Damage this entity
     * 
     * @param amount
     */
    public void damage(int amount) {
        if(isAlive()) {
            this.health -= amount;
            
            Sounds.playSound(Sounds.hit);
            addFlickerEffect(Color.WHITE, Color.RED, 500, 80);
                        
            if(this.health < 1) {
                this.dyingTimer.start();                
            
                addFlickerEffect(Color.BLACK, Color.RED, this.dyingTimer.getEndTime(), 200);                    
                this.state = EntityState.Dying;
            }
        }
    }
    
    /**
     * @return the stats
     */
    public StatData getStats() {
        return stats;
    }
    
    /**
     * @return the attackData
     */
    public AttackData getAttackData() {
        return attackData;
    }
    
    /**
     * @return the movementData
     */
    public MovementData getMovementData() {
        return movementData;
    }
    
    /**
     * Calculates the movement range for this Entity given
     * their current amount of action points
     * 
     * @return the number of slots they can move
     */
    public int getCurrentMovementRange() {
        if(this.movementData.actionPoints > 0) {
            return this.actionMeter.getActionPoints() / this.movementData.actionPoints;
        }
        
        return 0;
    }
    
    /**
     * @return the actionMeter
     */
    public ActionMeter getActionMeter() {
        return actionMeter;
    }
    
    /**
     * Calculates the offense score
     * 
     * @param scene
     * @return the offense score
     */
    public int calculateOffense(BattleScene scene) {
        return attackData.offenseFactor;
    }
    
    /**
     * Calculates the defense score at this {@link Entity}s current {@link Slot}
     * 
     * @param scene
     * @return the defense score
     */
    public int calculateDefense(BattleScene scene) {        
        return calculateDefense(scene.getSlot(this), scene);
    }
    
    /**
     * Calculates the defense score if this {@link Entity} was at the desired {@link Slot}
     * 
     * @param scene
     * @return the defense score
     */
    public int calculateDefense(Slot desiredSlot, BattleScene scene) {
        int score = stats.defenseFactor;
        
        // Calculate Neighbor bonus
        GraphNode<Slot> node = scene.getGraph().getNode(desiredSlot);
        for(int i = 0; i < node.edges().size(); i++) {
            Edge<Slot> edge = node.edges().get(i);
            if(edge!=null) {
                Slot neighbor = edge.getRight().getValue();
                Optional<Entity> ent = scene.getEntityOnSlot(neighbor);
                if(ent.isPresent()) {
                    Entity neighborEntity = ent.get();
                    if(isEnemy(neighborEntity)) {
                        score -= 0;
                    }
                    else {
                        score += neighborEntity.stats.defenseNeighborFactor;
                    }
                }
            }
        }
        
        return score;
    }
    
    /**
     * This {@link Entity}'s turn has ended
     */
    public void endTurn() {
        actionMeter.reset();
        heal();
    }
    
    /**
     * If this {@link Entity} has enough action points
     * 
     * @param amount
     * @return true if it has enough
     */
    public boolean hasPoints(int amount) {
        return actionMeter.hasPoints(amount);
    }
    
    /**
     * Use the points
     * @param amount
     */
    public void usePoints(int amount) {
        actionMeter.usePoints(amount);
    }
    
    /**
     * @param state the state to set
     */
    public void setState(EntityState state) {
        this.state = state;
    }
    
    /**
     * @return the state
     */
    public EntityState getState() {
        return state;
    }
    
    /**
     * @return the model
     */
    public EntityModel getModel() {
        return model;
    }
    
    /**
     * @return the pos
     */
    public Vector2 getPos() {
        return pos;
    }

    
    /**
     * @return the centerPos
     */
    public Vector2 getCenterPos() {
        centerPos.x = pos.x + bounds.width/2;
        centerPos.y = pos.y + bounds.height/2;
        return centerPos;
    }
    
    /**
     * @return the facing
     */
    public Vector2 getFacing() {
        return facing;
    }
    
    /**
     * @return the bounds
     */
    public Rectangle getBounds() {
        return bounds;
    }
    
    public void setAsAttacker() {
        this.facing.set(1, 0);
    }
    
    public void setAsDefender() {
        this.facing.set(-1, 0);
    }
    
    public Entity setPos(Vector3 pos) {
        return setPos(pos.x, pos.y);
    }
    
    public Entity setPos(Vector2 pos) {
        return setPos(pos.x, pos.y);
    }
    
    public Entity setPos(float x, float y) {
        this.pos.set(x, y);
        

        float offsetX = this.bounds.width / 2;
        float offsetY = this.bounds.height;
      
        x -= offsetX - 0.2f;
        y -= offsetY - 0.2f;
        
        this.bounds.setPosition(x, y);
        return this;
    }
    
    public Entity moveBy(float deltaX, float deltaY) {
        setPos(this.pos.x + deltaX, this.pos.y + deltaY);
        return this;
    }
        
    @Override
    public float getX() {    
        return pos.x;
    }
    
    @Override
    public float getY() {    
        return pos.y;
    }

    @Override
    public void update(TimeStep timeStep) {
        this.dyingTimer.update(timeStep);
        if(this.dyingTimer.isOnFirstTime()) {
            this.state = EntityState.Dead;
        }
        
        this.model.update(timeStep);
        this.effects.update(timeStep);
    }
    
    @Override
    public void render(RenderContext context) {
        this.model.render(context);    

        this.effects.render(context);
        
//        context.batch.end();
//        context.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, Color.GOLD);
//        context.drawRect(pos.x, pos.y, 0.15f, 0.15f, Color.BLUE);
//        context.batch.begin();
                
    }
}

