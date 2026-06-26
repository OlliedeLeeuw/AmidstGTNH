package sedridor.amidst.map;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import sedridor.amidst.Options;
import sedridor.amidst.logging.Log;
import sedridor.amidst.minecraft.MinecraftUtil;

public class Fragment {

    public static final int SIZE = 512;

    public static final int SIZE_SHIFT = 9;

    public static final int MAX_OBJECTS_PER_FRAGMENT = 32;

    public static final int MIPMAP_LEVELS = 3;

    public static final int BIOME_SIZE = 128;

    private static AffineTransform drawMatrix = new AffineTransform();

    public int blockX;

    public int blockY;

    public short[] biomeData = new short[16384];

    private ImageLayer[] imageLayers;

    private LiveLayer[] liveLayers;

    private IconLayer[] iconLayers;

    private Object loadLock = new Object();

    private BufferedImage[] images;

    public MapObject[] objects;

    public int objectsLength = 0;

    private float alpha = 0.0F;

    public boolean isActive = false;

    public boolean isLoaded = false;

    public Fragment nextFragment = null;

    public Fragment prevFragment = null;

    public boolean hasNext = false;

    public boolean endOfLine = false;

    private static int[] dataCache = new int[262144];

    public Fragment(ImageLayer... layers) {
        this(layers, null, null);
    }

    public Fragment(ImageLayer[] imageLayers, LiveLayer[] liveLayers, IconLayer[] iconLayers) {
        this.imageLayers = imageLayers;
        this.liveLayers = liveLayers;
        this.images = new BufferedImage[imageLayers.length];
        for (int i = 0; i < imageLayers.length; i++) this.images[imageLayers[i]
            .getLayerId()] = new BufferedImage((imageLayers[i]).size, (imageLayers[i]).size, 2);
        this.iconLayers = iconLayers;
        this.objects = new MapObject[32];
    }

    public void load() {
        synchronized (this.loadLock) {
            if (this.isLoaded) Log.w(new Object[] { "This should never happen!" });
            int[] data = MinecraftUtil.getBiomeData(this.blockX >> 2, this.blockY >> 2, 128, 128, true);
            for (int k = 0; k < 16384; k++) this.biomeData[k] = (short) data[k];
            for (int j = 0; j < this.imageLayers.length; j++) this.imageLayers[j].load(this);
            for (int i = 0; i < this.iconLayers.length; i++) this.iconLayers[i].generateMapObjects(this);
            this.alpha = Options.instance.mapFading.get() ? 0.0F : 1.0F;
            this.isLoaded = true;
        }
    }

    public void recycle() {
        this.isActive = false;
        this.isLoaded = false;
    }

    public void clearData() {
        for (IconLayer layer : this.iconLayers) layer.clearMapObjects(this);
        this.isLoaded = false;
    }

    public void clear() {
        for (IconLayer layer : this.iconLayers) layer.clearMapObjects(this);
        this.hasNext = false;
        this.endOfLine = false;
        this.isActive = true;
    }

    public void drawLiveLayers(float time, Graphics2D g, AffineTransform mat) {
        for (int i = 0; i < this.liveLayers.length; i++) {
            if (this.liveLayers[i].isVisible()) this.liveLayers[i].drawLive(this, g, mat);
        }
    }

    public void drawImageLayers(float time, Graphics2D g, AffineTransform mat) {
        if (!this.isLoaded) return;
        this.alpha = Math.min(1.0F, time * 3.0F + this.alpha);
        for (int i = 0; i < this.images.length; i++) {
            if (this.imageLayers[i].isVisible()) {
                g.setComposite(AlphaComposite.getInstance(3, this.alpha * this.imageLayers[i].getAlpha()));
                g.setTransform(this.imageLayers[i].getScaledMatrix(mat));
                if (g.getTransform()
                    .getScaleX() < 1.0D) {
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                } else {
                    g.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                }
                g.drawImage(this.images[i], 0, 0, null);
            }
        }
        g.setComposite(AlphaComposite.getInstance(3, 1.0F));
    }

    public void drawObjects(Graphics2D g, AffineTransform inMatrix) {
        if (this.alpha != 1.0F) g.setComposite(AlphaComposite.getInstance(3, this.alpha));
        if (!Options.instance.hideObjects.get()) for (int i = 0; i < this.objectsLength; i++) {
            if ((this.objects[i]).parentLayer.isVisible()) {
                drawMatrix.setTransform(inMatrix);
                drawMatrix.translate((double) (this.objects[i]).x, (double) (this.objects[i]).y);
                double invZoom = 1.0D / (this.objects[i]).parentLayer.map.getZoom();
                drawMatrix.scale(invZoom, invZoom);
                g.setTransform(drawMatrix);
                g.drawImage(
                    this.objects[i].getImage(),
                    -(this.objects[i].getWidth() >> 1),
                    -(this.objects[i].getHeight() >> 1),
                    this.objects[i].getWidth(),
                    this.objects[i].getHeight(),
                    null);
            }
        }
        if (this.alpha != 1.0F) g.setComposite(AlphaComposite.getInstance(3, 1.0F));
    }

    public void addObject(MapObject object) {
        object.rx = object.x + this.blockX;
        object.ry = object.y + this.blockY;
        if (this.objectsLength >= this.objects.length) {
            MapObject[] tempObjects = new MapObject[this.objects.length << 1];
            for (int i = 0; i < this.objects.length; i++) tempObjects[i] = this.objects[i];
            this.objects = tempObjects;
        }
        this.objects[this.objectsLength] = object;
        this.objectsLength++;
    }

    public void setImageData(int layerId, int[] data) {
        this.images[layerId].setRGB(
            0,
            0,
            (this.imageLayers[layerId]).size,
            (this.imageLayers[layerId]).size,
            data,
            0,
            (this.imageLayers[layerId]).size);
    }

    public int getBlockX() {
        return this.blockX;
    }

    public int getBlockY() {
        return this.blockY;
    }

    public int getChunkX() {
        return this.blockX >> 4;
    }

    public int getChunkY() {
        return this.blockY >> 4;
    }

    public int getFragmentX() {
        return this.blockX >> 9;
    }

    public int getFragmentY() {
        return this.blockY >> 9;
    }

    public void setNext(Fragment frag) {
        this.nextFragment = frag;
        frag.prevFragment = this;
        this.hasNext = true;
    }

    public void remove() {
        if (this.hasNext) {
            this.prevFragment.setNext(this.nextFragment);
        } else {
            this.prevFragment.hasNext = false;
        }
    }

    public static int[] getIntArray() {
        return dataCache;
    }

    public void removeObject(MapObjectPlayer player) {
        for (int i = 0; i < this.objectsLength; i++) {
            if (this.objects[i] == player) {
                this.objects[i] = this.objects[this.objectsLength - 1];
                this.objectsLength--;
            }
        }
    }

    public BufferedImage getBufferedImage(int layer) {
        return this.images[layer];
    }

    public void reset() {
        this.objectsLength = 0;
        this.isActive = false;
        this.isLoaded = false;
        this.nextFragment = null;
        this.prevFragment = null;
        this.hasNext = false;
        this.endOfLine = false;
    }

    public void repaint() {
        synchronized (this.loadLock) {
            if (this.isLoaded) for (int i = 0; i < this.imageLayers.length; i++) this.imageLayers[i].load(this);
        }
    }

    public void repaintImageLayer(int id) {
        synchronized (this.loadLock) {
            if (this.isLoaded) this.imageLayers[id].load(this);
        }
    }
}
