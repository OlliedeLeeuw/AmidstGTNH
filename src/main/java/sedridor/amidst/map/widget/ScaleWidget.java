package sedridor.amidst.map.widget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import sedridor.amidst.Options;
import sedridor.amidst.project.MapViewer;

public class ScaleWidget extends PanelWidget {

    public static int scaleLengthMaxPx = 200;

    public static int margin = 8;

    protected Stroke lineStroke1 = new BasicStroke(1.0F);

    protected Stroke lineStroke2 = new BasicStroke(2.0F, 0, 0);

    public ScaleWidget(MapViewer mapViewer) {
        super(mapViewer);
        setDimensions(100, 34);
        forceVisibility(false);
    }

    public void draw(Graphics2D g2d, float time) {
        int scaleBlocks = scaleLengthBlocks();
        int scaleWidthPx = (int) ((double) scaleBlocks * this.mapViewer.getMap()
            .getZoom());
        String message = scaleBlocks + " meters";
        int stringWidth = this.mapViewer.getFontMetrics()
            .stringWidth(message);
        setWidth(Math.max(scaleWidthPx, stringWidth) + margin * 2);
        super.draw(g2d, time);
        g2d.setColor(this.textColor);
        g2d.setFont(this.textFont);
        g2d.drawString(message, this.x + 1 + (this.width - stringWidth >> 1), this.y + 18);
        g2d.setColor(Color.white);
        g2d.setStroke(this.lineStroke2);
        g2d.drawLine(this.x + margin, this.y + 26, this.x + margin + scaleWidthPx, this.y + 26);
        g2d.setStroke(this.lineStroke1);
        g2d.drawLine(this.x + margin, this.y + 23, this.x + margin, this.y + 28);
        g2d.drawLine(this.x + margin + scaleWidthPx, this.y + 23, this.x + margin + scaleWidthPx, this.y + 28);
    }

    protected boolean onVisibilityCheck() {
        return Options.instance.showScale.get();
    }

    private int scaleLengthBlocks() {
        double scale = this.mapViewer.getMap()
            .getZoom();
        int result = 5000;
        if ((double) result * scale > (double) scaleLengthMaxPx) {
            result = 2000;
            if ((double) result * scale > (double) scaleLengthMaxPx) {
                result = 1000;
                if ((double) result * scale > (double) scaleLengthMaxPx) {
                    result = 500;
                    if ((double) result * scale > (double) scaleLengthMaxPx) {
                        result = 200;
                        if ((double) result * scale > (double) scaleLengthMaxPx) result = 100;
                    }
                }
            }
        }
        return result;
    }
}
