/*
    Leola Programming Language
    Author: Tony Sparks
    See license.txt
*/
package colony.util;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * Dispatches events.  This implementation is thread safe. 
 * 
 * @author Tony
 *
 */
public class EventDispatcher {

    /**
     * Handle to all listeners
     */
    private Map<Class<? extends Event>, Collection<Consumer<? extends Event>>> eventListenerMap;
    
    /**
     * Queue of events
     */
    private Queue<Event> eventQueue;        
    
   
    
    /**
     */
    public EventDispatcher() {
        this.eventListenerMap = new ConcurrentHashMap<>();        
        this.eventQueue = new ConcurrentLinkedQueue<>();
    }
    
    
    /**
     * Queues the {@link Event}
     * @param event
     */
    public void queueEvent(Event event) {
        this.eventQueue.add(event);
    }
    
    /**
     * Clear the event queue
     */
    public void clearQueue() {
        this.eventQueue.clear();
    }
    
    /**
     * Processes the next event in the event queue.
     * 
     * @return true processed an event, false if no events where processed (due to
     * the queue being empty).
     */
    public boolean processQueue() {
        boolean eventProcessed = false;
        
        /* Poll from the queue */
        Event event = this.eventQueue.poll();
        if ( event != null ) {
            /* Send the event to the listeners */
            sendNow(event);
            
            eventProcessed = true;
        }
        
        return eventProcessed;
    }
    
    /**
     * Sends the supplied event now, bypassing the queue.
     * @param event
     */
    @SuppressWarnings("unchecked")
    public <E extends Event> void sendNow(E event) {
        Class<?> eventClass = event.getClass();
        if ( this.eventListenerMap.containsKey(eventClass)) {
            Collection<Consumer<? extends Event>> eventListeners = this.eventListenerMap.get(eventClass);
            for(Consumer<?> listener : eventListeners) {
                Consumer<E> l = (Consumer<E>)listener;
                l.accept(event);
                                
                /* If its been consumed, don't continue */
                if ( event.isConsumed() ) {
                    break;
                }
            }
        }
    }
        
    /**
     * Add an {@link Consumer<?>}.
     * 
     * @param eventClass
     * @param eventListener
     */
    public <E extends Event> void addEventListener(Class<E> eventClass, Consumer<E> eventListener) {         
        if ( ! this.eventListenerMap.containsKey(eventClass)) {
            this.eventListenerMap.put(eventClass, new ConcurrentLinkedQueue<>());
        }
                
        /* Add the listener object */
        Collection<Consumer<? extends Event>> eventListeners = this.eventListenerMap.get(eventClass);
        eventListeners.add(eventListener);                
    }
    
    /**
     * Removes an {@link Consumer<?>}
     * 
     * @param eventClass
     * @param eventListener
     */
    public void removeEventListener(Class<? extends Event> eventClass, Consumer<? extends Event> eventListener) {         
        if ( this.eventListenerMap.containsKey(eventClass)) {
            Collection<Consumer<? extends Event>> eventListeners = this.eventListenerMap.get(eventClass);
            eventListeners.remove(eventListener);            
        }            
    }
    
    /**
     * Remove all the {@link Consumer<?>}s
     * 
     */
    public void removeAllEventListeners() {
        this.eventListenerMap.clear();        
    }
    
    /**
     * Get all the {@link Event} classes that currently have listeners.
     * 
     * @return the set of event classes
     */
    public Set<Class<? extends Event>> getEventClasses() {
        return this.eventListenerMap.keySet();
    }

}

