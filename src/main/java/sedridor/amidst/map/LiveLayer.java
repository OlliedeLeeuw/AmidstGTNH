package sedridor.amidst.map;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public abstract class LiveLayer extends Layer {

    public abstract void drawLive(Fragment paramFragment, Graphics2D paramGraphics2D,
        AffineTransform paramAffineTransform);
}
