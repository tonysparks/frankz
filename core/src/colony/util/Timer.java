/*
 * see license.txt 
 */
package colony.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import colony.game.TimeStep;
import colony.game.Updatable;

/**
 * @author Tony
 * 
 */
public class Timer implements Updatable {

    private long currentTime;
    private long endTime;

    private boolean update, loop, isTime, onFirstTime;

    private List<Consumer<Timer>> onFinished;
    
    /**
     * @param loop
     * @param endTime
     */
    public Timer(boolean loop, long endTime) {
        this.loop = loop;
        this.endTime = endTime;                
        this.onFirstTime = false;
        reset();
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    /**
     * @return the endTime
     */
    public long getEndTime() {
        return endTime;
    }
    
    /**
     * @return the remaining time
     */
    public long getRemainingTime() {
        return this.endTime - this.currentTime;
    }
    
    /**
     * @return true if this timer is currently being updated
     */
    public boolean isUpdating() {
        return update;
    }
    
    /**
     * @param loop the loop to set
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
    
    /**
     * @return true if set to loop
     */
    public boolean isLooping() {
        return loop;
    }
    
    public void reset() {
        this.currentTime = 0;
        this.update = true;
        this.isTime = false;
    }
    
    public void stop() {
        this.currentTime = 0;
        this.isTime = false;
        this.update = false;
    }

    public void start() {
        this.update = true;
    }

    public void pause() {
        this.update = false;
    }

    public boolean isExpired() {
        return isTime && !isLooping();
    }
    
    /**
     * @return the isTime
     */
    public boolean isTime() {
        return isTime;
    }
    
    /**
     * @return the onFirstTime
     */
    public boolean isOnFirstTime() {
        return onFirstTime;
    }
    
    /**
     * You can override this method to get invoked when
     * the timer has been reached
     */
    public void onFinish(Consumer<Timer> onFinished) {
        if(this.onFinished == null) {
            this.onFinished = new ArrayList<>();
        }
        
        this.onFinished.add(onFinished);
    }
    
    /**
     * Updates the timer
     * @param timeStep
     */
    public void update(TimeStep timeStep) {
        this.onFirstTime = false;
        if (this.update) {            
            this.currentTime += timeStep.getDeltaTime();
            if (this.currentTime >= this.endTime) {
                if(this.isLooping()) {
                    reset();
                }
                else {
                    this.update = false;
                }
                
                this.isTime = true;
                this.onFirstTime = true;
                
                if(this.onFinished != null) {
                    this.onFinished.forEach(c -> c.accept(this));
                }
                
            }
            else {
                this.isTime = false;
            }
        }
    }
}
