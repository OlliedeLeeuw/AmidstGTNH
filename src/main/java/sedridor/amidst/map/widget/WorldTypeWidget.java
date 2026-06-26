package sedridor.amidst.map.widget;

import java.awt.Graphics2D;

import sedridor.amidst.Options;
import sedridor.amidst.project.FinderWindow;
import sedridor.amidst.project.MapViewer;
import sedridor.amidst.project.SaveLoader;

public class WorldTypeWidget extends PanelWidget {

    public WorldTypeWidget(MapViewer mapViewer) {
        super(mapViewer);
        setDimensions(20, 30);
    }

    public void draw(Graphics2D g2d, float time) {
        setWidth(
            this.mapViewer.getFontMetrics()
                .stringWidth(
                    "World Type: " + SaveLoader.Type.fromMixedCase(FinderWindow.instance.curProject.worldType)
                        .getName())
                + 20);
        super.draw(g2d, time);
        g2d.setColor(this.textColor);
        g2d.drawString(
            "World Type: " + SaveLoader.Type.fromMixedCase(FinderWindow.instance.curProject.worldType)
                .getName(),
            this.x + 10,
            this.y + 20);
    }

    protected boolean onVisibilityCheck() {
        return Options.instance.showWorldTypeWidget.get();
    }
}
