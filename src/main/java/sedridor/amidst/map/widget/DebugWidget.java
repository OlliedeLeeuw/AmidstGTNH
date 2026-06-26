package sedridor.amidst.map.widget;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import sedridor.amidst.Options;
import sedridor.amidst.map.FragmentManager;
import sedridor.amidst.project.MapViewer;

public class DebugWidget extends PanelWidget {

    public DebugWidget(MapViewer mapViewer) {
        super(mapViewer);
        this.yPadding += 40;
        forceVisibility(onVisibilityCheck());
    }

    public void draw(Graphics2D g2d, float time) {
        FragmentManager fragmentManager = this.mapViewer.getFragmentManager();
        ArrayList<String> panelText = new ArrayList<String>();
        panelText.add("Fragment Manager:");
        panelText.add("Pool Size: " + fragmentManager.getCacheSize());
        panelText.add("Free Queue Size: " + fragmentManager.getFreeFragmentQueueSize());
        panelText.add("Request Queue Size: " + fragmentManager.getRequestQueueSize());
        panelText.add("Recycle Queue Size: " + fragmentManager.getRecycleQueueSize());
        panelText.add("");
        panelText.add("Map Viewer:");
        Point centerLocation = new Point(
            (int) ((float) this.mapViewer.getWidth() * 0.5F),
            (int) ((float) this.mapViewer.getHeight() * 0.5F));
        panelText.add(
            "Map Location: " + (this.map.screenToLocal(centerLocation.getLocation())).x
                + ", "
                + (this.map.screenToLocal(centerLocation.getLocation())).y);
        panelText.add(
            "Map Zoom: "
                + String.format("%.3f [1 pixel = %.1f blocks]", this.map.getZoom(), 1.0D / this.map.getZoom()));
        panelText.add(
            "Map Size: " + this.map.tileWidth
                + "x"
                + this.map.tileHeight
                + " ["
                + (this.map.tileWidth * this.map.tileHeight)
                + "]");
        int width = 0;
        for (int j = 0; j < panelText.size(); j++) {
            int textWidth = this.mapViewer.getFontMetrics()
                .stringWidth(panelText.get(j));
            if (textWidth > width) width = textWidth;
        }
        width += 20;
        int height = panelText.size() * 20 + 10;
        setDimensions(width, height);
        super.draw(g2d, time);
        g2d.setColor(this.textColor);
        for (int i = 0; i < panelText.size(); i++) g2d.drawString(panelText.get(i), this.x + 10, this.y + 20 + i * 20);
    }

    protected boolean onVisibilityCheck() {
        return Options.instance.showDebug.get();
    }
}
