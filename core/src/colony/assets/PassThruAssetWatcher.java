/*
 * see license.txt 
 */
package colony.assets;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

import colony.game.TimeStep;

/**
 * Simple pass-through implementation of {@link AssetWatcher}.  This takes 
 * very minimal resources as its just a small wrapper around the actual Asset.
 * 
 * @author Tony
 *
 */
public class PassThruAssetWatcher implements AssetWatcher {

    class WatchedAssetImpl<T> implements WatchedAsset<T> {

        private final T asset;                
        private List<Consumer<T>> onChanged;
        
        /**
         * @param asset
         */
        public WatchedAssetImpl(T asset) {
            this.asset = asset;
            this.onChanged = new ArrayList<>();
            assetChanged();
        }

        @Override
        public T getAsset() {
            return asset;
        }

        @Override
        public void release() {            
        }

        @Override
        public void assetChanged() {
            queuedTouches.add(this::touch);
        }
        
        @Override
        public WatchedAsset<T> onAssetChanged(Consumer<T> onChanged) {
            this.onChanged.add(onChanged);
            return this;
        }
        
        @Override
        public WatchedAsset<T> touch() {
            if(!this.onChanged.isEmpty()) {
                this.onChanged.forEach(consumer -> consumer.accept(getAsset()));                
            }
            return this;
        }
        
        @Override
        public WatchedAsset<T> touchLast() {
            if(!this.onChanged.isEmpty()) {
                this.onChanged.get(this.onChanged.size()-1).accept(getAsset());
            }
            return this;
        }
    }
    
    private Queue<Supplier<WatchedAsset<?>>> queuedTouches;
    
    /**
     */
    public PassThruAssetWatcher() {
        this.queuedTouches = new ConcurrentLinkedQueue<>();
    }
    
    @Override
    public void update(TimeStep timeStep) {
        while(!this.queuedTouches.isEmpty()) {
            this.queuedTouches.poll().get();
        }    
    }

    @Override
    public <T> WatchedAsset<T> loadAsset(String filename, AssetLoader<T> loader) {
        return new WatchedAssetImpl<T>(loader.loadAsset(filename));
    }

    @Override
    public void removeWatchedAsset(String filename) {
    }

    @Override
    public void clearWatched() {
    }

    @Override
    public void startWatching() {
    }

    @Override
    public void stopWatching() {
    }

}
