package sedridor.amidst.map.widget;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import sedridor.amidst.project.MapViewer;
import sedridor.amidst.resources.ResourceLoader;

public class PanelWidget extends Widget {

    private static BufferedImage dropShadowBottomLeft = ResourceLoader.getImage("dropshadow/outer_bottom_left.png");

    private static BufferedImage dropShadowBottomRight = ResourceLoader.getImage("dropshadow/outer_bottom_right.png");

    private static BufferedImage dropShadowTopLeft = ResourceLoader.getImage("dropshadow/outer_top_left.png");

    private static BufferedImage dropShadowTopRight = ResourceLoader.getImage("dropshadow/outer_top_right.png");

    private static BufferedImage dropShadowBottom = ResourceLoader.getImage("dropshadow/outer_bottom.png");

    private static BufferedImage dropShadowTop = ResourceLoader.getImage("dropshadow/outer_top.png");

    private static BufferedImage dropShadowLeft = ResourceLoader.getImage("dropshadow/outer_left.png");

    private static BufferedImage dropShadowRight = ResourceLoader.getImage("dropshadow/outer_right.png");

    public enum CornerAnchorPoint {
        CENTER,
        TOP_LEFT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        BOTTOM_CENTER,
        TOP_RIGHT,
        TOP_CENTER,
        NONE;
    }

    protected Color textColor = new Color(1.0F, 1.0F, 1.0F);

    protected Color panelColor = new Color(0.15F, 0.15F, 0.15F, 0.8F);

    protected Font textFont = new Font("arial", 1, 15);

    protected CornerAnchorPoint anchor = CornerAnchorPoint.NONE;

    protected int xPadding = 10;

    protected int yPadding = 10;

    protected float alpha = 1.0F;

    protected float targetAlpha = 1.0F;

    protected boolean isFading = false;

    protected boolean targetVisibility = true;

    public PanelWidget(MapViewer mapViewer) {
        super(mapViewer);
    }

    public void draw(Graphics2D g2d, float time) {
        this.targetAlpha = this.targetVisibility ? 1.0F : 0.0F;
        if (this.alpha < this.targetAlpha) {
            this.alpha = Math.min(this.targetAlpha, this.alpha + time * 4.0F);
        } else if (this.alpha > this.targetAlpha) {
            this.alpha = Math.max(this.targetAlpha, this.alpha - time * 4.0F);
        }
        this.isFading = (this.alpha != this.targetAlpha);
        updatePosition();
        g2d.setColor(this.panelColor);
        g2d.drawImage(dropShadowTopLeft, this.x - 10, this.y - 10, null);
        g2d.drawImage(dropShadowTopRight, this.x + this.width, this.y - 10, null);
        g2d.drawImage(dropShadowBottomLeft, this.x - 10, this.y + this.height, null);
        g2d.drawImage(dropShadowBottomRight, this.x + this.width, this.y + this.height, null);
        g2d.drawImage(dropShadowTop, this.x, this.y - 10, this.width, 10, null);
        g2d.drawImage(dropShadowBottom, this.x, this.y + this.height, this.width, 10, null);
        g2d.drawImage(dropShadowLeft, this.x - 10, this.y, 10, this.height, null);
        g2d.drawImage(dropShadowRight, this.x + this.width, this.y, 10, this.height, null);
        g2d.fillRect(this.x, this.y, this.width, this.height);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    protected void updatePosition() {
        switch (this.anchor) {
            case CENTER:
                this.x = (this.mapViewer.getWidth() >> 1) - (this.width >> 1);
                this.y = (this.mapViewer.getHeight() >> 1) - (this.height >> 1);
                break;
            case TOP_LEFT:
                this.x = this.xPadding;
                this.y = this.yPadding;
                break;
            case BOTTOM_LEFT:
                this.x = this.xPadding;
                this.y = this.mapViewer.getHeight() - (this.height + this.yPadding);
                break;
            case BOTTOM_RIGHT:
                this.x = this.mapViewer.getWidth() - (this.width + this.xPadding);
                this.y = this.mapViewer.getHeight() - (this.height + this.yPadding);
                break;
            case BOTTOM_CENTER:
                this.x = (this.mapViewer.getWidth() >> 1) - (this.width >> 1);
                this.y = this.mapViewer.getHeight() - (this.height + this.yPadding);
                break;
            case TOP_RIGHT:
                this.x = this.mapViewer.getWidth() - (this.width + this.xPadding);
                this.y = this.yPadding;
                break;
            case TOP_CENTER:
                this.x = (this.mapViewer.getWidth() >> 1) - (this.width >> 1);
                this.y = this.yPadding;
                break;
        }
    }

    public boolean isVisible() {
        boolean value = ((this.visible && this.targetVisibility) || this.isFading);
        this.targetVisibility = onVisibilityCheck();
        return value;
    }

    protected boolean onVisibilityCheck() {
        return this.visible;
    }

    public void forceVisibility(boolean value) {
        this.targetVisibility = value;
        this.isFading = false;
        this.targetAlpha = value ? 1.0F : 0.0F;
        this.alpha = value ? 1.0F : 0.0F;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public PanelWidget setAnchorPoint(CornerAnchorPoint anchor) {
        this.anchor = anchor;
        return this;
    }
}
