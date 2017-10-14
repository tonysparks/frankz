/*
 * see license.txt 
 */
package colony.assets;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import colony.game.Logger;
import colony.game.TimeStep;

/**
 * Watches a directory for any changes
 * 
 * @author Tony
 *
 */
public class FileSystemAssetWatcher implements AssetWatcher {

    class WatchedAssetImpl<T> implements WatchedAsset<T> {

        private AtomicReference<T> asset;
        private AssetLoader<T> loader;  
        private String assetName;
        private File filename;
        private List<Consumer<T>> onChanged;
        
        /**
         * @param filename
         * @param loader
         */
        public WatchedAssetImpl(String assetName, File filename, AssetLoader<T> loader) {
            this.assetName = assetName;
            this.asset = new AtomicReference<T>();
            this.loader = loader;
            this.filename = filename;
            
            this.onChanged = new ArrayList<>();
            
            assetChanged();
        }

        
        @Override
        public T getAsset() {
            return this.asset.get();
        }

        @Override
        public void release() {
            this.asset.set(null);
            removeWatchedAsset(this.filename.getAbsolutePath());
        }
        
        
        @Override
        public void assetChanged() {
            this.asset.set(loader.loadAsset(assetName));
            touch();
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
    
    private WatchService watchService;
    private Thread watchThread;
    private AtomicBoolean isActive;
    private Path pathToWatch;
    
    private Map<File, WatchedAsset<?>> watchedAssets;
    private Queue<Runnable> queuedTouches;
    
    private Map<WatchKey, Path> keys;
    
    /**
     * @param dir
     * @throws IOException
     */
    public FileSystemAssetWatcher(File dir) throws IOException {
        FileSystem fileSystem = FileSystems.getDefault();
        this.isActive = new AtomicBoolean(false);
        
        this.watchedAssets = new ConcurrentHashMap<File, WatchedAsset<?>>();
        this.pathToWatch = dir.toPath();
        
        this.queuedTouches = new ConcurrentLinkedQueue<>();
        this.keys = new HashMap<>(); 
        
        this.watchService = fileSystem.newWatchService();
        this.watchThread = new Thread(new Runnable() {
            
            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                while(isActive.get()) {
                    try {
                        WatchKey key = watchService.take();
                        if(key.isValid()) {
                            Path dir = keys.get(key);
                            if(dir == null) {
                                continue;
                            }
                            
                            List<WatchEvent<?>> events = key.pollEvents();
                            for(int i = 0; i < events.size(); i++) {
                                WatchEvent<?> event = events.get(i);
                                WatchEvent.Kind<?> kind = event.kind();
                                
                                /* ignore overflow events */
                                if(kind == StandardWatchEventKinds.OVERFLOW) {
                                    continue;
                                }
                                
                                /* we are only listening for 'changed' events */
                                WatchEvent<Path> ev = (WatchEvent<Path>)event;
                                Path filename = ev.context();
                                                                
                                /* if we have a registered asset, lets go ahead and notify it */
                                WatchedAsset<?> watchedAsset = watchedAssets.get(new File(dir.toFile(), filename.toString()));
                                if(watchedAsset != null) {
                                    Logger.log("Reloading asset: " + filename);
                                    //watchedAsset.assetChanged();  
                                    queuedTouches.add(watchedAsset::assetChanged); 
                                }
                            }
                        }
                        
                        key.reset();
                    }
                    catch (ClosedWatchServiceException e) {
                        break;
                    }
                    catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }, "watcher-thread");
        this.watchThread.setDaemon(true);
        
        Files.walkFileTree(this.pathToWatch, new SimpleFileVisitor<Path>() {
           
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {           
                register(dir);
                return FileVisitResult.CONTINUE;
            } 
        });
    }
    
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        this.keys.put(key, dir);
    }

    @Override
    public void update(TimeStep timeStep) {
        while(!this.queuedTouches.isEmpty()) {
            this.queuedTouches.poll().run();
        }
    }
    
    @Override
    public <T> WatchedAsset<T> loadAsset(String filename, AssetLoader<T> loader) {
        File file = new File(filename.toString());
        WatchedAsset<T> asset = new WatchedAssetImpl<T>(filename, file, loader);
        this.watchedAssets.put(file, asset);
        
        return asset;
    }
    
    
    @Override
    public void removeWatchedAsset(String filename) {
        this.watchedAssets.remove(new File(filename));
    }
    
    
    @Override
    public void clearWatched() {
        this.watchedAssets.clear();
    }
    
    
    @Override
    public void startWatching() {
        this.isActive.set(true);
        this.watchThread.start();
    }
    
    
    @Override
    public void stopWatching() { 
        try {
            this.isActive.set(false);
            this.watchThread.interrupt();            
        }
        finally {
            try {
                this.watchService.close();
            }
            catch(IOException e) {}
        }
    }        
}
