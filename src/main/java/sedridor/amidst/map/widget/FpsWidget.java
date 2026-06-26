package sedridor.amidst.map.widget;

import java.awt.Graphics2D;

import sedridor.amidst.Options;
import sedridor.amidst.project.MapViewer;
import sedridor.amidst.utilties.FramerateTimer;

public class FpsWidget extends PanelWidget {

    private FramerateTimer fps = new FramerateTimer(2);

    public FpsWidget(MapViewer mapViewer) {
        super(mapViewer);
        setDimensions(20, 30);
        forceVisibility(onVisibilityCheck());
    }

    public void draw(Graphics2D g2d, float time) {
        String framerate = this.fps.toString();
        setWidth(
            this.mapViewer.getFontMetrics()
                .stringWidth(framerate) + 20);
        super.draw(g2d, time);
        this.fps.tick();
        g2d.setColor(this.textColor);
        g2d.drawString(framerate, this.x + 10, this.y + 20);
    }

    protected boolean onVisibilityCheck() {
        return Options.instance.showFPS.get();
    }
}
