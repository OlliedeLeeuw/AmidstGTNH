package sedridor.amidst.map.widget;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import sedridor.amidst.map.MapObject;
import sedridor.amidst.project.MapViewer;

public class SelectedObjectWidget extends PanelWidget {

    private String message = "";

    private BufferedImage icon;

    public SelectedObjectWidget(MapViewer mapViewer) {
        super(mapViewer);
        this.yPadding += 40;
        setDimensions(20, 35);
        forceVisibility(false);
    }

    public void draw(Graphics2D g2d, float time) {
        if (this.targetVisibility) {
            MapObject selectedObject = this.mapViewer.getSelectedObject();
            this.message = selectedObject.getName() + " [" + selectedObject.rx + ", " + selectedObject.ry + "]";
            this.icon = selectedObject.getImage();
        }
        setWidth(
            45 + this.mapViewer.getFontMetrics()
                .stringWidth(this.message));
        super.draw(g2d, time);
        g2d.setColor(this.textColor);
        double imgWidth = (double) this.icon.getWidth();
        double imgHeight = (double) this.icon.getHeight();
        double ratio = imgWidth / imgHeight;
        g2d.drawImage(this.icon, this.x + 5, this.y + 7, (int) imgWidth, (int) imgHeight, null);
        g2d.drawString(this.message, this.x + 35, this.y + 23);
    }

    protected boolean onVisibilityCheck() {
        return (this.mapViewer.getSelectedObject() != null);
    }
}
