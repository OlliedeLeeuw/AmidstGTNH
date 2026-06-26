package sedridor.amidst.map;

import java.awt.Point;
import java.awt.image.BufferedImage;

import sedridor.amidst.Options;
import sedridor.amidst.Util;

public class MapObject extends Point {

    public MapMarkers type;

    public int rx;

    public int ry;

    public double localScale = 1.0D;

    @Deprecated
    public double tempDist = 0.0D;

    public IconLayer parentLayer;

    public MapObject(MapMarkers eType, int x, int y) {
        super(x, y);
        this.type = eType;
    }

    public String getName() {
        return Util.capitalizeString(this.type.toString());
    }

    public int getWidth() {
        return (int) ((double) this.type.image.getWidth() * this.localScale);
    }

    public int getHeight() {
        return (int) ((double) this.type.image.getHeight() * this.localScale);
    }

    public BufferedImage getImage() {
        return this.type.image;
    }

    public boolean isSelectable() {
        return Options.instance.showVillages.isSelected();
    }

    public MapObject setParent(IconLayer layer) {
        this.parentLayer = layer;
        return this;
    }
}
