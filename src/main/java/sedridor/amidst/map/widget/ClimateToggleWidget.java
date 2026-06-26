package sedridor.amidst.map.widget;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import sedridor.amidst.Options;
import sedridor.amidst.map.layers.ClimateLayer;
import sedridor.amidst.project.MapViewer;
import sedridor.amidst.resources.ResourceLoader;

public class ClimateToggleWidget extends PanelWidget {

    private static BufferedImage highlighterIcon = ResourceLoader.getImage("highlighter.png");

    public static boolean isClimateWidgetVisible = false;

    public ClimateToggleWidget(MapViewer mapViewer) {
        super(mapViewer);
        setDimensions(36, 36);
    }

    public void draw(Graphics2D g2d, float time) {
        super.draw(g2d, time);
        g2d.drawImage(highlighterIcon, this.x, this.y, 36, 36, null);
    }

    public boolean isVisible() {
        return (super.isVisible() && Options.instance.colorByClimate.get());
    }

    public boolean onMousePressed(int x, int y) {
        isClimateWidgetVisible = !isClimateWidgetVisible;
        ClimateLayer.instance.setHighlightMode(isClimateWidgetVisible);
        new Thread(new Runnable() {

            public void run() {
                ClimateToggleWidget.this.map.resetImageLayer(ClimateLayer.instance.getLayerId());
            }
        }).start();
        return true;
    }
}
