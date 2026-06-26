package sedridor.amidst.map.widget;

import java.awt.Graphics2D;

import sedridor.amidst.map.Map;
import sedridor.amidst.project.MapViewer;

public class Widget {

    protected MapViewer mapViewer;

    protected Map map;

    protected int x;

    protected int y;

    protected int width;

    protected int height;

    protected boolean visible = true;

    public Widget(MapViewer mapViewer) {
        this.mapViewer = mapViewer;
        this.map = mapViewer.getMap();
    }

    public void draw(Graphics2D g2d, float time) {}

    public boolean onClick(int x, int y) {
        return true;
    }

    public boolean onMouseWheelMoved(int x, int y, int rotation) {
        return false;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisibility(boolean value) {
        this.visible = value;
    }

    public float getAlpha() {
        return 1.0F;
    }

    public boolean onMousePressed(int x, int y) {
        return true;
    }

    public void onMouseReleased() {}
}
