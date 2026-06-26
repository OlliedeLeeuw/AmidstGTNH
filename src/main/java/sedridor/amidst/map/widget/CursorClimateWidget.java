package sedridor.amidst.map.widget;

import java.awt.Graphics2D;
import java.awt.Point;

import sedridor.amidst.Options;
import sedridor.amidst.project.MapViewer;

public class CursorClimateWidget extends PanelWidget {

    private String message = "";

    public CursorClimateWidget(MapViewer mapViewer) {
        super(mapViewer);
        this.yPadding += 40;
        setDimensions(20, 30);
        forceVisibility(false);
    }

    public void draw(Graphics2D g2d, float time) {
        Point mouseLocation = null;
        if ((mouseLocation = this.mapViewer.getMousePosition()) != null) {
            mouseLocation = this.map.screenToLocal(mouseLocation);
            String climateName = this.map.getClimateAt(mouseLocation);
            this.message = climateName + " Climate";
        }
        int stringWidth = this.mapViewer.getFontMetrics()
            .stringWidth(this.message);
        setWidth(stringWidth + 20);
        super.draw(g2d, time);
        g2d.setColor(this.textColor);
        g2d.drawString(this.message, this.x + 10, this.y + 20);
    }

    protected boolean onVisibilityCheck() {
        return (Options.instance.showClimate.get() && this.mapViewer.getMousePosition() != null);
    }
}
