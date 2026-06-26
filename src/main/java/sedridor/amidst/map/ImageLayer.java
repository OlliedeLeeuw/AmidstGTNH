package sedridor.amidst.map;

import java.awt.geom.AffineTransform;

public abstract class ImageLayer extends Layer {

    protected float alpha = 1.0F;

    protected double scale;

    protected int size;

    private AffineTransform cachedScalingMatrix = new AffineTransform();

    protected int layerId;

    private int[] defaultData;

    public ImageLayer(int size) {
        this.size = size;
        this.defaultData = new int[size * size];
        this.scale = 512.0D / (double) size;
        for (int i = 0; i < this.defaultData.length; i++) this.defaultData[i] = 0;
    }

    public int[] getDefaultData() {
        return this.defaultData;
    }

    public void load(Fragment frag) {
        drawToCache(frag);
    }

    public AffineTransform getMatrix(AffineTransform inMat) {
        this.cachedScalingMatrix.setTransform(inMat);
        return this.cachedScalingMatrix;
    }

    public AffineTransform getScaledMatrix(AffineTransform inMat) {
        this.cachedScalingMatrix.setTransform(inMat);
        this.cachedScalingMatrix.scale(this.scale, this.scale);
        return this.cachedScalingMatrix;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public int getLayerId() {
        return this.layerId;
    }

    public void setLayerId(int id) {
        this.layerId = id;
    }

    public abstract void drawToCache(Fragment paramFragment);
}
