package sedridor.amidst.map.widget;

import java.awt.Graphics2D;

import sedridor.amidst.Options;
import sedridor.amidst.project.MapViewer;

public class SeedWidget extends PanelWidget {

    public SeedWidget(MapViewer mapViewer) {
        super(mapViewer);
        setDimensions(20, 30);
    }

    public void draw(Graphics2D g2d, float time) {
        setWidth(
            this.mapViewer.getFontMetrics()
                .stringWidth(Options.instance.getSeedText()) + 20);
        super.draw(g2d, time);
        g2d.setColor(this.textColor);
        g2d.drawString(Options.instance.getSeedText(), this.x + 10, this.y + 20);
    }
}
