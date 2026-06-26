package sedridor.amidst.map;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import sedridor.amidst.map.layers.BiomeLayer;
import sedridor.amidst.map.layers.ClimateLayer;

public class Map {

    public static Map instance = null;

    private static final boolean START = true;

    private static final boolean END = false;

    private FragmentManager fragmentManager;

    private Fragment startNode = new Fragment(new ImageLayer[0]);

    private double scale = 0.25D;

    private Point2D.Double start;

    public int tileWidth;

    public int tileHeight;

    public int width = 1;

    public int height = 1;

    private final Object resizeLock = new Object();

    private final Object drawLock = new Object();

    private AffineTransform mat;

    private boolean firstDraw = true;

    public Map(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        fragmentManager.setMap(this);
        this.mat = new AffineTransform();
        this.start = new Point2D.Double();
        addStart(0, 0);
        instance = this;
    }

    public void resetImageLayer(int id) {
        Fragment frag = this.startNode;
        while (frag.hasNext) {
            frag = frag.nextFragment;
            this.fragmentManager.repaintFragmentLayer(frag, id);
        }
    }

    public void resetFragments() {
        Fragment frag = this.startNode;
        while (frag.hasNext) {
            frag = frag.nextFragment;
            this.fragmentManager.repaintFragment(frag);
        }
    }

    public void reloadLayers() {
        this.fragmentManager.reloadLayers();
    }

    public void draw(Graphics2D g, float time) {
        AffineTransform originalTransform = g.getTransform();
        if (this.firstDraw) {
            this.firstDraw = false;
            centerOn(0L, 0L);
        }
        synchronized (this.drawLock) {
            int size = (int) (512.0D * this.scale);
            int w = this.width / size + 2;
            int h = this.height / size + 2;
            while (this.tileWidth < w) addColumn(false);
            while (this.tileWidth > w) removeColumn(false);
            while (this.tileHeight < h) addRow(false);
            while (this.tileHeight > h) removeRow(false);
            while (this.start.x > 0.0D) {
                this.start.x -= (double) size;
                addColumn(true);
                removeColumn(false);
            }
            while (this.start.x < (double) -size) {
                this.start.x += (double) size;
                addColumn(false);
                removeColumn(true);
            }
            while (this.start.y > 0.0D) {
                this.start.y -= (double) size;
                addRow(true);
                removeRow(false);
            }
            while (this.start.y < (double) -size) {
                this.start.y += (double) size;
                addRow(false);
                removeRow(true);
            }
            Fragment frag = this.startNode;
            size = 512;
            if (frag.hasNext) {
                this.mat.setToIdentity();
                this.mat.concatenate(originalTransform);
                this.mat.translate(this.start.x, this.start.y);
                this.mat.scale(this.scale, this.scale);
                while (frag.hasNext) {
                    frag = frag.nextFragment;
                    frag.drawImageLayers(time, g, this.mat);
                    this.mat.translate((double) size, 0.0D);
                    if (frag.endOfLine) this.mat.translate((double) (-size * w), (double) size);
                }
            }
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            this.fragmentManager.updateLayers(time);
            frag = this.startNode;
            if (frag.hasNext) {
                this.mat.setToIdentity();
                this.mat.concatenate(originalTransform);
                this.mat.translate(this.start.x, this.start.y);
                this.mat.scale(this.scale, this.scale);
                while (frag.hasNext) {
                    frag = frag.nextFragment;
                    frag.drawLiveLayers(time, g, this.mat);
                    this.mat.translate((double) size, 0.0D);
                    if (frag.endOfLine) this.mat.translate((double) (-size * w), (double) size);
                }
            }
            frag = this.startNode;
            if (frag.hasNext) {
                this.mat.setToIdentity();
                this.mat.concatenate(originalTransform);
                this.mat.translate(this.start.x, this.start.y);
                this.mat.scale(this.scale, this.scale);
                while (frag.hasNext) {
                    frag = frag.nextFragment;
                    frag.drawObjects(g, this.mat);
                    this.mat.translate((double) size, 0.0D);
                    if (frag.endOfLine) this.mat.translate((double) (-size * w), (double) size);
                }
            }
            g.setTransform(originalTransform);
        }
    }

    public void addStart(int x, int y) {
        synchronized (this.resizeLock) {
            Fragment start = this.fragmentManager.requestFragment(x, y);
            start.endOfLine = true;
            this.startNode.setNext(start);
            this.tileWidth = 1;
            this.tileHeight = 1;
        }
    }

    public void addColumn(boolean start) {
        synchronized (this.resizeLock) {
            int x = 0;
            Fragment frag = this.startNode;
            if (start) {
                x = frag.nextFragment.blockX - 512;
                Fragment newFrag = this.fragmentManager.requestFragment(x, frag.nextFragment.blockY);
                newFrag.setNext(this.startNode.nextFragment);
                this.startNode.setNext(newFrag);
            }
            while (frag.hasNext) {
                frag = frag.nextFragment;
                if (frag.endOfLine) {
                    if (start) {
                        if (frag.hasNext) {
                            Fragment fragment = this.fragmentManager.requestFragment(x, frag.blockY + 512);
                            fragment.setNext(frag.nextFragment);
                            frag.setNext(fragment);
                            frag = fragment;
                        }
                        continue;
                    }
                    Fragment newFrag = this.fragmentManager.requestFragment(frag.blockX + 512, frag.blockY);
                    if (frag.hasNext) newFrag.setNext(frag.nextFragment);
                    newFrag.endOfLine = true;
                    frag.endOfLine = false;
                    frag.setNext(newFrag);
                    frag = newFrag;
                }
            }
            this.tileWidth++;
        }
    }

    public void removeRow(boolean start) {
        synchronized (this.resizeLock) {
            if (start) {
                for (int i = 0; i < this.tileWidth; i++) {
                    Fragment frag = this.startNode.nextFragment;
                    frag.remove();
                    this.fragmentManager.returnFragment(frag);
                }
            } else {
                Fragment frag = this.startNode;
                while (frag.hasNext) frag = frag.nextFragment;
                for (int i = 0; i < this.tileWidth; i++) {
                    frag.remove();
                    this.fragmentManager.returnFragment(frag);
                    frag = frag.prevFragment;
                }
            }
            this.tileHeight--;
        }
    }

    public void addRow(boolean start) {
        synchronized (this.resizeLock) {
            int y;
            Fragment frag = this.startNode;
            if (start) {
                frag = this.startNode.nextFragment;
                y = frag.blockY - 512;
            } else {
                while (frag.hasNext) frag = frag.nextFragment;
                y = frag.blockY + 512;
            }
            this.tileHeight++;
            Fragment newFrag = this.fragmentManager.requestFragment(this.startNode.nextFragment.blockX, y);
            Fragment chainFrag = newFrag;
            for (int i = 1; i < this.tileWidth; i++) {
                Fragment tempFrag = this.fragmentManager.requestFragment(chainFrag.blockX + 512, chainFrag.blockY);
                chainFrag.setNext(tempFrag);
                chainFrag = tempFrag;
                if (i == this.tileWidth - 1) chainFrag.endOfLine = true;
            }
            if (start) {
                chainFrag.setNext(frag);
                this.startNode.setNext(newFrag);
            } else {
                frag.setNext(newFrag);
            }
        }
    }

    public void removeColumn(boolean start) {
        synchronized (this.resizeLock) {
            Fragment frag = this.startNode;
            if (start) {
                this.fragmentManager.returnFragment(frag.nextFragment);
                this.startNode.nextFragment.remove();
            }
            while (frag.hasNext) {
                frag = frag.nextFragment;
                if (frag.endOfLine) {
                    if (start) {
                        if (frag.hasNext) {
                            Fragment tempFrag = frag.nextFragment;
                            tempFrag.remove();
                            this.fragmentManager.returnFragment(tempFrag);
                        }
                        continue;
                    }
                    frag.prevFragment.endOfLine = true;
                    frag.remove();
                    this.fragmentManager.returnFragment(frag);
                    frag = frag.prevFragment;
                }
            }
            this.tileWidth--;
        }
    }

    public void moveBy(Point2D.Double speed) {
        moveBy(speed.x, speed.y);
    }

    public void moveBy(double x, double y) {
        this.start.x += x;
        this.start.y += y;
    }

    public void centerOn(long x, long y) {
        long fragOffsetX = x % 512L;
        long fragOffsetY = y % 512L;
        long startX = x - fragOffsetX;
        long startY = y - fragOffsetY;
        synchronized (this.drawLock) {
            while (this.tileHeight > 1) removeRow(false);
            while (this.tileWidth > 1) removeColumn(false);
            Fragment frag = this.startNode.nextFragment;
            frag.remove();
            this.fragmentManager.returnFragment(frag);
            double offsetX = (double) (this.width >> 1);
            double offsetY = (double) (this.height >> 1);
            offsetX -= (double) fragOffsetX * this.scale;
            offsetY -= (double) fragOffsetY * this.scale;
            this.start.x = offsetX;
            this.start.y = offsetY;
            addStart((int) startX, (int) startY);
        }
    }

    public void setZoom(double scale) {
        this.scale = scale;
    }

    public double getZoom() {
        return this.scale;
    }

    public Point2D.Double getScaled(double oldScale, double newScale, Point p) {
        double baseX = (double) p.x - this.start.x;
        double scaledX = baseX - baseX / oldScale * newScale;
        double baseY = (double) p.y - this.start.y;
        double scaledY = baseY - baseY / oldScale * newScale;
        return new Point2D.Double(scaledX, scaledY);
    }

    public void dispose() {
        synchronized (this.drawLock) {
            this.fragmentManager.reset();
        }
    }

    public Fragment getFragmentAt(Point position) {
        Fragment frag = this.startNode;
        Point cornerPosition = new Point(position.x >> 9, position.y >> 9);
        Point fragmentPosition = new Point();
        while (frag.hasNext) {
            frag = frag.nextFragment;
            fragmentPosition.x = frag.getFragmentX();
            fragmentPosition.y = frag.getFragmentY();
            if (cornerPosition.equals(fragmentPosition)) return frag;
        }
        return null;
    }

    public MapObject getObjectAt(Point position, double maxRange) {
        double x = this.start.x;
        double y = this.start.y;
        MapObject closestObject = null;
        double closestDistance = maxRange;
        Fragment frag = this.startNode;
        int size = (int) (512.0D * this.scale);
        while (frag.hasNext) {
            frag = frag.nextFragment;
            for (int i = 0; i < frag.objectsLength; i++) {
                if ((frag.objects[i]).parentLayer.isVisible()) {
                    Point objPosition = frag.objects[i].getLocation();
                    objPosition.x = (int) ((double) objPosition.x * this.scale);
                    objPosition.y = (int) ((double) objPosition.y * this.scale);
                    objPosition.x = (int) ((double) objPosition.x + x);
                    objPosition.y = (int) ((double) objPosition.y + y);
                    double distance = objPosition.distance(position);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestObject = frag.objects[i];
                    }
                }
            }
            x += (double) size;
            if (frag.endOfLine) {
                x = this.start.x;
                y += (double) size;
            }
        }
        return closestObject;
    }

    public Point screenToLocal(Point inPoint) {
        Point point = inPoint.getLocation();
        point.x = (int) ((double) point.x - this.start.x);
        point.y = (int) ((double) point.y - this.start.y);
        point.x = (int) ((double) point.x / this.scale);
        point.y = (int) ((double) point.y / this.scale);
        point.x += this.startNode.nextFragment.blockX;
        point.y += this.startNode.nextFragment.blockY;
        return point;
    }

    public String getBiomeNameAt(Point point) {
        Fragment frag = this.startNode;
        while (frag.hasNext) {
            frag = frag.nextFragment;
            if (frag.blockX <= point.x && frag.blockY <= point.y
                && frag.blockX + 512 > point.x
                && frag.blockY + 512 > point.y) {
                int x = point.x - frag.blockX;
                int y = point.y - frag.blockY;
                return BiomeLayer.getBiomeNameForFragment(frag, x, y);
            }
        }
        return "Unknown";
    }

    public String getBiomeAliasAt(Point point) {
        Fragment frag = this.startNode;
        while (frag.hasNext) {
            frag = frag.nextFragment;
            if (frag.blockX <= point.x && frag.blockY <= point.y
                && frag.blockX + 512 > point.x
                && frag.blockY + 512 > point.y) {
                int x = point.x - frag.blockX;
                int y = point.y - frag.blockY;
                return BiomeLayer.getBiomeAliasForFragment(frag, x, y);
            }
        }
        return "Unknown";
    }

    public String getClimateAt(Point point) {
        Fragment frag = this.startNode;
        while (frag.hasNext) {
            frag = frag.nextFragment;
            if (frag.blockX <= point.x && frag.blockY <= point.y
                && frag.blockX + 512 > point.x
                && frag.blockY + 512 > point.y) {
                int x = point.x - frag.blockX;
                int y = point.y - frag.blockY;
                return ClimateLayer.getClimateForFragment(frag, x, y);
            }
        }
        return "Unknown";
    }
}
