package sedridor.amidst.map.layers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import sedridor.amidst.Options;
import sedridor.amidst.map.Fragment;
import sedridor.amidst.map.LiveLayer;

public class GridLayer extends LiveLayer {

    private static Font drawFont = new Font("arial", 1, 16);

    private static StringBuffer textBuffer = new StringBuffer(128);

    private static char[] textCache = new char[128];

    private static final Color gridColor = Color.black;

    public boolean isVisible() {
        return Options.instance.showGrid.get();
    }

    public void drawLive(Fragment fragment, Graphics2D g, AffineTransform inMat) {
        if (Options.instance.metricGrid.get()) {
            drawGrid(fragment, g, inMat);
        } else {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            AffineTransform mat = new AffineTransform(inMat);
            textBuffer.setLength(0);
            textBuffer.append(fragment.getChunkX() << 4);
            textBuffer.append(", ");
            textBuffer.append(fragment.getChunkY() << 4);
            textBuffer.getChars(0, textBuffer.length(), textCache, 0);
            int stride = (int) (0.25D / this.map.getZoom());
            g.setColor(Color.black);
            g.setTransform(mat);
            int gridX = fragment.getFragmentX() % (stride + 1);
            int gridY = fragment.getFragmentY() % (stride + 1);
            if (gridY == 0) g.drawLine(1, 1, 513, 1);
            if (gridY == stride) g.drawLine(1, 513, 513, 513);
            if (gridX == 0) g.drawLine(1, 1, 1, 513);
            if (gridX == stride) g.drawLine(513, 1, 513, 513);
            if (gridX != 0) return;
            if (gridY != 0) return;
            double invZoom = 1.0D / this.map.getZoom();
            mat.scale(invZoom, invZoom);
            g.setTransform(mat);
            g.setFont(drawFont);
            int y = (int) (1.0D / invZoom);
            int x = (int) (1.0D / invZoom);
            g.drawChars(textCache, 0, textBuffer.length(), 12 + x, 17 + y);
            g.drawChars(textCache, 0, textBuffer.length(), 8 + x, 17 + y);
            g.drawChars(textCache, 0, textBuffer.length(), 10 + x, 19 + y);
            g.drawChars(textCache, 0, textBuffer.length(), 10 + x, 15 + y);
            g.setColor(Color.white);
            g.drawChars(textCache, 0, textBuffer.length(), 10 + x, 17 + y);
            g.setTransform(inMat);
        }
    }

    private void drawGrid(Fragment fragment, Graphics2D g, AffineTransform inMat) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        AffineTransform mat = new AffineTransform(inMat);
        int minGridSize = 500;
        if (this.map.getZoom() > 1.5D) {
            minGridSize = 100;
        } else if (this.map.getZoom() > 0.6D) {
            minGridSize = 250;
        }
        int stride = (int) (0.25D / this.map.getZoom());
        g.setColor(gridColor);
        g.setTransform(mat);
        int startX = fragment.getChunkX() << 4;
        int startY = fragment.getChunkY() << 4;
        for (int y = startY; y < startY + 512; y++) {
            int gridY = y / minGridSize % (stride + 1);
            if (y % minGridSize == 0 && gridY == 0) {
                int drawY = (y < 0) ? (512 - -y % 512) : (y % 512);
                g.drawLine(0, drawY + 1, 512, drawY + 1);
            }
        }
        for (int x = startX; x < startX + 512; x++) {
            int gridX = x / minGridSize % (stride + 1);
            if (x % minGridSize == 0 && gridX == 0) {
                int drawX = (x < 0) ? (512 - -x % 512) : (x % 512);
                g.drawLine(drawX + 1, 0, drawX + 1, 512);
            }
        }
        double invZoom = 1.0D / this.map.getZoom();
        mat.scale(invZoom, invZoom);
        g.setTransform(mat);
        g.setFont(drawFont);
        for (int i = startY; i < startY + 512; i++) {
            for (int j = startX; j < startX + 512; j++) {
                int gridX = j / minGridSize % (stride + 1);
                int gridY = i / minGridSize % (stride + 1);
                if (i % minGridSize == 0 && j % minGridSize == 0 && gridY == 0 && gridX == 0)
                    drawText(fragment, g, mat, invZoom, j, i);
            }
        }
        g.setTransform(inMat);
    }

    private void drawText(Fragment fragment, Graphics2D g, AffineTransform mat, double invZoom, int x, int y) {
        textBuffer.setLength(0);
        textBuffer.append(x);
        textBuffer.append(", ");
        textBuffer.append(y);
        textBuffer.getChars(0, textBuffer.length(), textCache, 0);
        y++;
        x++;
        g.setColor(gridColor);
        y = (int) ((y < 0) ? ((double) (512 - -y % 512) / invZoom) : ((double) (y % 512) / invZoom));
        x = (int) ((x < 0) ? ((double) (512 - -x % 512) / invZoom) : ((double) (x % 512) / invZoom));
        g.drawChars(textCache, 0, textBuffer.length(), 12 + x, 17 + y);
        g.drawChars(textCache, 0, textBuffer.length(), 8 + x, 17 + y);
        g.drawChars(textCache, 0, textBuffer.length(), 10 + x, 19 + y);
        g.drawChars(textCache, 0, textBuffer.length(), 10 + x, 15 + y);
        g.setColor(Color.white);
        g.drawChars(textCache, 0, textBuffer.length(), 10 + x, 17 + y);
    }
}
