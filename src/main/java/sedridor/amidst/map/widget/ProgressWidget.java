package sedridor.amidst.map.widget;

import java.awt.Graphics2D;

import sedridor.amidst.Options;
import sedridor.amidst.map.FragmentManager;
import sedridor.amidst.project.MapViewer;

public class ProgressWidget extends PanelWidget {

    private String GENERATING = "Generating map...";

    public ProgressWidget(MapViewer mapViewer) {
        super(mapViewer);
        setDimensions(100, 30);
        forceVisibility(false);
    }

    public void draw(Graphics2D g2d, float time) {
        if (Options.instance.showScale.get()) {
            this.yPadding = 50;
        } else {
            this.yPadding = 10;
        }
        int stringWidth = this.mapViewer.getFontMetrics()
            .stringWidth(this.GENERATING);
        setWidth(stringWidth + 20);
        super.draw(g2d, time);
        g2d.setColor(this.textColor);
        g2d.setFont(this.textFont);
        g2d.drawString(this.GENERATING, this.x + 10, this.y + 20);
    }

    protected boolean onVisibilityCheck() {
        FragmentManager fragmentManager = this.mapViewer.getFragmentManager();
        return (fragmentManager.getRequestQueueSize() != 0);
    }
}
