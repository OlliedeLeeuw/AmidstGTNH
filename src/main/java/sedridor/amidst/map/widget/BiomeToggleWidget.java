package sedridor.amidst.map.widget;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import sedridor.amidst.map.layers.BiomeLayer;
import sedridor.amidst.project.MapViewer;
import sedridor.amidst.resources.ResourceLoader;

public class BiomeToggleWidget extends PanelWidget {

    private static BufferedImage highlighterIcon = ResourceLoader.getImage("highlighter.png");

    public static boolean isBiomeWidgetVisible = false;

    public BiomeToggleWidget(MapViewer mapViewer) {
        super(mapViewer);
        setDimensions(36, 36);
    }

    public void draw(Graphics2D g2d, float time) {
        super.draw(g2d, time);
        g2d.drawImage(highlighterIcon, this.x, this.y, 36, 36, null);
    }

    public boolean onMousePressed(int x, int y) {
        isBiomeWidgetVisible = !isBiomeWidgetVisible;
        BiomeLayer.instance.setHighlightMode(isBiomeWidgetVisible);
        new Thread(new Runnable() {

            public void run() {
                BiomeToggleWidget.this.map.resetImageLayer(BiomeLayer.instance.getLayerId());
            }
        }).start();
        return true;
    }
}
