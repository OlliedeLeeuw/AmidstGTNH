package sedridor.amidst.map.widget;

import java.awt.Graphics2D;
import java.awt.Point;

import sedridor.amidst.project.MapViewer;

public class CursorInformationWidget extends PanelWidget {

    private String message = "";

    public CursorInformationWidget(MapViewer mapViewer) {
        super(mapViewer);
        setDimensions(20, 30);
        forceVisibility(false);
    }

    public void draw(Graphics2D g2d, float time) {
        Point mouseLocation = null;
        if ((mouseLocation = this.mapViewer.getMousePosition()) != null) {
            mouseLocation = this.map.screenToLocal(mouseLocation);
            String biomeName = this.map.getBiomeAliasAt(mouseLocation);
            this.message = biomeName + " [ " + mouseLocation.x + ", " + mouseLocation.y + " ]";
        }
        int stringWidth = this.mapViewer.getFontMetrics()
            .stringWidth(this.message);
        setWidth(stringWidth + 20);
        super.draw(g2d, time);
        g2d.setColor(this.textColor);
        g2d.drawString(this.message, this.x + 10, this.y + 20);
    }

    protected boolean onVisibilityCheck() {
        return (this.mapViewer.getMousePosition() != null);
    }
}
