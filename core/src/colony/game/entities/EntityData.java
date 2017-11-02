/*
 * see license.txt 
 */
package colony.game.entities;

/**
 * @author Tony
 *
 */
public class EntityData {
        
    public static class AttackData {
        public int actionPoints=2;
        public int offenseFactor=20;
        public int attackRange=3;
        public int damage=1;
    }
    
    public static class MovementData {
        public int actionPoints=1;
    }
    
    public static class StatData {
        public int startingActionPoints=3;
        public int maxHealth=5;
        public int startingHealth=3;
        
        public int defenseFactor = 10;
        public int defenseNeighborFactor = 15;
        
        public float movementSpeed = 16.5f;
    }
    
    public float x, y;
    public float width, height;    
    
    public StatData stats = new StatData();
    
    public EntityModelData model;
    
    public AttackData attack = new AttackData();
    public MovementData movement = new MovementData();
}
