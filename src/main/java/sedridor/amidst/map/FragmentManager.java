package sedridor.amidst.map;

import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import sedridor.amidst.logging.Log;

public class FragmentManager implements Runnable {

    private int cacheSize = 1024;

    private Thread currentThread;

    private boolean running = true;

    private Fragment[] fragmentCache;

    private ConcurrentLinkedQueue<Fragment> fragmentQueue;

    private ConcurrentLinkedQueue<Fragment> requestQueue;

    private ConcurrentLinkedQueue<Fragment> recycleQueue;

    private int sleepTick = 0;

    private Object queueLock = new Object();

    private Stack<ImageLayer> layerList;

    private ImageLayer[] imageLayers;

    private IconLayer[] iconLayers;

    private LiveLayer[] liveLayers;

    public FragmentManager(ImageLayer[] imageLayers, LiveLayer[] liveLayers, IconLayer[] iconLayers) {
        this.fragmentQueue = new ConcurrentLinkedQueue<Fragment>();
        this.requestQueue = new ConcurrentLinkedQueue<Fragment>();
        this.recycleQueue = new ConcurrentLinkedQueue<Fragment>();
        this.layerList = new Stack<ImageLayer>();
        Collections.<ImageLayer>addAll(this.layerList, imageLayers);
        this.fragmentCache = new Fragment[this.cacheSize];
        for (int j = 0; j < imageLayers.length; j++) imageLayers[j].setLayerId(j);
        for (int i = 0; i < this.cacheSize; i++) {
            this.fragmentCache[i] = new Fragment(imageLayers, liveLayers, iconLayers);
            this.fragmentQueue.offer(this.fragmentCache[i]);
        }
        this.imageLayers = imageLayers;
        this.iconLayers = iconLayers;
        this.liveLayers = liveLayers;
    }

    public void updateLayers(float time) {
        for (ImageLayer layer : this.imageLayers) layer.update(time);
        for (LiveLayer layer : this.liveLayers) layer.update(time);
        for (IconLayer layer : this.iconLayers) layer.update(time);
    }

    public void reset() {
        this.running = false;
        try {
            this.currentThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.recycleQueue.clear();
        this.requestQueue.clear();
        this.fragmentQueue.clear();
        for (int i = 0; i < this.cacheSize; i++) {
            this.fragmentCache[i].reset();
            this.fragmentQueue.offer(this.fragmentCache[i]);
        }
    }

    private void increaseFragmentCache() {
        Fragment[] newFragments = new Fragment[this.cacheSize << 1];
        for (int j = 0; j < this.cacheSize; j++) {
            newFragments[j] = this.fragmentCache[j];
            this.fragmentCache[j] = null;
        }
        for (int i = this.cacheSize; i < this.cacheSize << 1; i++) {
            newFragments[i] = new Fragment(this.imageLayers, this.liveLayers, this.iconLayers);
            this.fragmentQueue.offer(newFragments[i]);
        }
        this.fragmentCache = newFragments;
        Log.i(
            new Object[] {
                "FragmentManager cache size increased from " + this.cacheSize + " to " + (this.cacheSize << 1) });
        this.cacheSize <<= 1;
        System.gc();
    }

    public void repaintFragment(Fragment frag) {
        synchronized (this.queueLock) {
            frag.repaint();
        }
    }

    public Fragment requestFragment(int x, int y) {
        if (!this.running) return null;
        Fragment frag = null;
        while ((frag = this.fragmentQueue.poll()) == null) increaseFragmentCache();
        frag.clear();
        frag.blockX = x;
        frag.blockY = y;
        frag.isActive = true;
        this.requestQueue.offer(frag);
        return frag;
    }

    public void returnFragment(Fragment frag) {
        this.recycleQueue.offer(frag);
    }

    public void run() {
        this.currentThread.setPriority(1);
        while (this.running) {
            if (!this.requestQueue.isEmpty() || !this.recycleQueue.isEmpty()) {
                if (!this.requestQueue.isEmpty()) synchronized (this.queueLock) {
                    Fragment frag = this.requestQueue.poll();
                    if (frag.isActive && !frag.isLoaded) {
                        frag.load();
                        this.sleepTick++;
                        if (this.sleepTick == 10) {
                            this.sleepTick = 0;
                            try {
                                Thread.sleep(1L);
                            } catch (InterruptedException e) {}
                        }
                    }
                }
                while (!this.recycleQueue.isEmpty()) {
                    synchronized (this.queueLock) {
                        Fragment frag = this.recycleQueue.poll();
                        frag.recycle();
                        this.fragmentQueue.offer(frag);
                    }
                }
                continue;
            }
            this.sleepTick = 0;
            try {
                Thread.sleep(2L);
            } catch (InterruptedException e) {}
        }
    }

    public void setMap(Map map) {
        for (ImageLayer layer : this.imageLayers) {
            layer.setMap(map);
            layer.reload();
        }
        for (LiveLayer layer : this.liveLayers) {
            layer.setMap(map);
            layer.reload();
        }
        for (IconLayer layer : this.iconLayers) {
            layer.setMap(map);
            layer.reload();
        }
        this.currentThread = new Thread(this);
        this.running = true;
        this.currentThread.start();
    }

    public void reloadLayers() {
        for (ImageLayer layer : this.imageLayers) layer.reload();
        for (LiveLayer layer : this.liveLayers) layer.reload();
        for (IconLayer layer : this.iconLayers) layer.reload();
    }

    public int getCacheSize() {
        return this.cacheSize;
    }

    public int getFreeFragmentQueueSize() {
        return this.fragmentQueue.size();
    }

    public int getRecycleQueueSize() {
        return this.recycleQueue.size();
    }

    public int getRequestQueueSize() {
        return this.requestQueue.size();
    }

    public void repaintFragmentLayer(Fragment frag, int id) {
        synchronized (this.queueLock) {
            frag.repaintImageLayer(id);
        }
    }
}
