package sedridor.amidst.project;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.minecraft.client.entity.EntityClientPlayerMP;

import sedridor.amidst.Options;
import sedridor.amidst.gui.PlayerMenuItem;
import sedridor.amidst.logging.Log;
import sedridor.amidst.map.FragmentManager;
import sedridor.amidst.map.IconLayer;
import sedridor.amidst.map.ImageLayer;
import sedridor.amidst.map.LiveLayer;
import sedridor.amidst.map.Map;
import sedridor.amidst.map.MapObject;
import sedridor.amidst.map.MapObjectPlayer;
import sedridor.amidst.map.layers.BiomeLayer;
import sedridor.amidst.map.layers.ClimateLayer;
import sedridor.amidst.map.layers.GridLayer;
import sedridor.amidst.map.layers.NetherFortressLayer;
import sedridor.amidst.map.layers.OceanMonumentLayer;
import sedridor.amidst.map.layers.PlayerLayer;
import sedridor.amidst.map.layers.SlimeLayer;
import sedridor.amidst.map.layers.SpawnLayer;
import sedridor.amidst.map.layers.StrongholdLayer;
import sedridor.amidst.map.layers.TempleLayer;
import sedridor.amidst.map.layers.VillageLayer;
import sedridor.amidst.map.widget.BiomeToggleWidget;
import sedridor.amidst.map.widget.BiomeWidget;
import sedridor.amidst.map.widget.ClimateToggleWidget;
import sedridor.amidst.map.widget.ClimateWidget;
import sedridor.amidst.map.widget.CursorClimateWidget;
import sedridor.amidst.map.widget.CursorInformationWidget;
import sedridor.amidst.map.widget.DebugWidget;
import sedridor.amidst.map.widget.FpsWidget;
import sedridor.amidst.map.widget.PanelWidget;
import sedridor.amidst.map.widget.ProgressWidget;
import sedridor.amidst.map.widget.ScaleWidget;
import sedridor.amidst.map.widget.SeedWidget;
import sedridor.amidst.map.widget.SelectedObjectWidget;
import sedridor.amidst.map.widget.Widget;
import sedridor.amidst.map.widget.WorldTypeWidget;
import sedridor.amidst.minecraft.MinecraftUtil;
import sedridor.amidst.resources.ResourceLoader;
import sedridor.forgeamidst.ForgeAmidst;

public class MapViewer extends JComponent implements MouseListener, MouseWheelListener, KeyListener {

    private static FragmentManager fragmentManager;

    private static PlayerLayer playerLayer;

    private Widget mouseOwner;

    private static BufferedImage dropShadowBottomLeft = ResourceLoader.getImage("dropshadow/inner_bottom_left.png");

    private static BufferedImage dropShadowBottomRight = ResourceLoader.getImage("dropshadow/inner_bottom_right.png");

    private static BufferedImage dropShadowTopLeft = ResourceLoader.getImage("dropshadow/inner_top_left.png");

    private static BufferedImage dropShadowTopRight = ResourceLoader.getImage("dropshadow/inner_top_right.png");

    private static BufferedImage dropShadowBottom = ResourceLoader.getImage("dropshadow/inner_bottom.png");

    private static BufferedImage dropShadowTop = ResourceLoader.getImage("dropshadow/inner_top.png");

    private static BufferedImage dropShadowLeft = ResourceLoader.getImage("dropshadow/inner_left.png");

    private static BufferedImage dropShadowRight = ResourceLoader.getImage("dropshadow/inner_right.png");

    private Project proj;

    private JPopupMenu menu = new JPopupMenu();

    public int strongholdCount;

    public int villageCount;

    private Map worldMap;

    private MapObject selectedObject = null;

    private Point lastMouse;

    public Point lastRightClick = null;

    private Point2D.Double panSpeed;

    private static int zoomLevel = 0;

    private static int zoomTicksRemaining = 0;

    private static double targetZoom = 0.25D;

    private static double curZoom = 0.25D;

    private Point zoomMouse = new Point();

    private Font textFont = new Font("arial", 1, 15);

    private FontMetrics textMetrics;

    private ArrayList<Widget> widgets = new ArrayList<Widget>();

    private long lastTime;

    public void dispose() {
        Log.debug(new Object[] { "Disposing of map viewer." });
        fragmentManager = null;
        this.worldMap.dispose();
        this.worldMap = null;
        this.menu.removeAll();
        this.proj = null;
    }

    MapViewer(Project proj) {
        this.panSpeed = new Point2D.Double();
        this.proj = proj;
        playerLayer = new PlayerLayer();
        if (playerLayer.isEnabled = proj.saveLoaded) {
            playerLayer.setPlayers(proj.save);
            for (MapObjectPlayer player : proj.save.getPlayers())
                this.menu.add((JMenuItem) new PlayerMenuItem(this, player, playerLayer));
        } else if (playerLayer.isEnabled = (ForgeAmidst.getWorld() != null && ForgeAmidst.getServer() == null)) {
            EntityClientPlayerMP thePlayer = (ForgeAmidst.getMC()).thePlayer;
            if (thePlayer != null) {
                playerLayer.thePlayer = new MapObjectPlayer(
                    thePlayer.getDisplayName(),
                    (int) thePlayer.posX,
                    (int) thePlayer.posY);
                this.menu.add((JMenuItem) new PlayerMenuItem(this, playerLayer.thePlayer, playerLayer));
            }
        }
        fragmentManager = createFragmentManager();
        this.worldMap = new Map(fragmentManager);
        this.worldMap.setZoom(curZoom);
        this.widgets.add(new FpsWidget(this).setAnchorPoint(PanelWidget.CornerAnchorPoint.TOP_CENTER));
        this.widgets.add(new SeedWidget(this).setAnchorPoint(PanelWidget.CornerAnchorPoint.TOP_LEFT));
        this.widgets.add(new DebugWidget(this).setAnchorPoint(PanelWidget.CornerAnchorPoint.BOTTOM_RIGHT));
        this.widgets.add(new SelectedObjectWidget(this).setAnchorPoint(PanelWidget.CornerAnchorPoint.TOP_LEFT));
        this.widgets.add(new ScaleWidget(this).setAnchorPoint(PanelWidget.CornerAnchorPoint.BOTTOM_CENTER));
        this.widgets.add(new CursorInformationWidget(this).setAnchorPoint(PanelWidget.CornerAnchorPoint.TOP_RIGHT));
        this.widgets.add(new CursorClimateWidget(this).setAnchorPoint(PanelWidget.CornerAnchorPoint.TOP_RIGHT));
        this.widgets.add(new WorldTypeWidget(this).setAnchorPoint(PanelWidget.CornerAnchorPoint.BOTTOM_LEFT));
        this.widgets.add(new ProgressWidget(this).setAnchorPoint(PanelWidget.CornerAnchorPoint.BOTTOM_CENTER));
        this.widgets.add(new ClimateToggleWidget(this).setAnchorPoint(PanelWidget.CornerAnchorPoint.BOTTOM_RIGHT));
        this.widgets.add(
            ClimateWidget.get(this)
                .setAnchorPoint(PanelWidget.CornerAnchorPoint.NONE));
        this.widgets.add(new BiomeToggleWidget(this).setAnchorPoint(PanelWidget.CornerAnchorPoint.BOTTOM_RIGHT));
        this.widgets.add(
            BiomeWidget.get(this)
                .setAnchorPoint(PanelWidget.CornerAnchorPoint.NONE));
        addMouseListener(this);
        addMouseWheelListener(this);
        setFocusable(true);
        this.lastTime = System.currentTimeMillis();
        this.textMetrics = getFontMetrics(this.textFont);
    }

    private FragmentManager createFragmentManager() {
        FragmentManager result = null;
        result = new FragmentManager(
            new ImageLayer[] { new BiomeLayer(), new ClimateLayer(), new SlimeLayer() },
            new LiveLayer[] { new GridLayer() },
            new IconLayer[] { new VillageLayer(), new StrongholdLayer(), new TempleLayer(), new OceanMonumentLayer(),
                new SpawnLayer(), new NetherFortressLayer(), playerLayer });
        return result;
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        long currentTime = System.currentTimeMillis();
        float time = (float) Math.min(Math.max(0L, currentTime - this.lastTime), 100L) / 1000.0F;
        this.lastTime = currentTime;
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        if (zoomTicksRemaining-- > 0) {
            double lastZoom = curZoom;
            curZoom = (MapViewer.targetZoom + curZoom) * 0.5D;
            Point2D.Double targetZoom = this.worldMap.getScaled(lastZoom, curZoom, this.zoomMouse);
            this.worldMap.moveBy(targetZoom);
            this.worldMap.setZoom(curZoom);
        }
        Point curMouse = getMousePosition();
        if (this.lastMouse != null) {
            if (curMouse != null) {
                double difX = (double) (curMouse.x - this.lastMouse.x);
                double difY = (double) (curMouse.y - this.lastMouse.y);
                this.panSpeed.setLocation(difX * 0.2D, difY * 0.2D);
            }
            this.lastMouse.translate((int) this.panSpeed.x, (int) this.panSpeed.y);
        }
        this.worldMap.moveBy((double) (int) this.panSpeed.x, (double) (int) this.panSpeed.y);
        if (Options.instance.mapFlicking.get()) {
            this.panSpeed.x *= 0.949999988079071D;
            this.panSpeed.y *= 0.949999988079071D;
        } else {
            this.panSpeed.x *= 0.0D;
            this.panSpeed.y *= 0.0D;
        }
        this.worldMap.width = getWidth();
        this.worldMap.height = getHeight();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        this.worldMap.draw((Graphics2D) g2d.create(), time);
        g2d.drawImage(dropShadowTopLeft, 0, 0, null);
        g2d.drawImage(dropShadowTopRight, getWidth() - 10, 0, null);
        g2d.drawImage(dropShadowBottomLeft, 0, getHeight() - 10, null);
        g2d.drawImage(dropShadowBottomRight, getWidth() - 10, getHeight() - 10, null);
        g2d.drawImage(dropShadowTop, 10, 0, getWidth() - 20, 10, null);
        g2d.drawImage(dropShadowBottom, 10, getHeight() - 10, getWidth() - 20, 10, null);
        g2d.drawImage(dropShadowLeft, 0, 10, 10, getHeight() - 20, null);
        g2d.drawImage(dropShadowRight, getWidth() - 10, 10, 10, getHeight() - 20, null);
        g2d.setFont(this.textFont);
        for (Widget widget : this.widgets) {
            if (widget.isVisible()) {
                g2d.setComposite(AlphaComposite.getInstance(3, widget.getAlpha()));
                widget.draw(g2d, time);
            }
        }
    }

    public void centerAt(long x, long y) {
        this.worldMap.centerOn(x, y);
    }

    public void adjustZoom(Point position, int notches) {
        this.zoomMouse = position;
        if (notches > 0) {
            if (zoomLevel < (Options.instance.maxZoom.get() ? 26 : 100)) {
                targetZoom /= 1.1D;
                zoomLevel++;
                zoomTicksRemaining = 100;
            }
        } else if (zoomLevel > -22) {
            targetZoom *= 1.1D;
            zoomLevel--;
            zoomTicksRemaining = 100;
        }
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        Point mouse = getMousePosition();
        if (mouse == null) return;
        for (Widget widget : this.widgets) {
            if (widget.isVisible() && mouse.x > widget.getX()
                && mouse.y > widget.getY()
                && mouse.x < widget.getX() + widget.getWidth()
                && mouse.y < widget.getY() + widget.getHeight())
                if (widget.onMouseWheelMoved(mouse.x - widget.getX(), mouse.y - widget.getY(), notches)) return;
        }
        adjustZoom(getMousePosition(), notches);
    }

    public void mouseClicked(MouseEvent e) {
        if (!e.isMetaDown()) {
            Point mouse = getMousePosition();
            if (mouse == null) return;
            for (Widget widget : this.widgets) {
                if (widget.isVisible() && mouse.x > widget.getX()
                    && mouse.y > widget.getY()
                    && mouse.x < widget.getX() + widget.getWidth()
                    && mouse.y < widget.getY() + widget.getHeight())
                    if (widget.onClick(mouse.x - widget.getX(), mouse.y - widget.getY())) return;
            }
            MapObject object = this.worldMap.getObjectAt(mouse, 20.0D);
            if (this.selectedObject != null) this.selectedObject.localScale = 1.0D;
            if (object != null) object.localScale = 1.5D;
            this.selectedObject = object;
        }
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {
        if (e.isMetaDown()) return;
        Point mouse = getMousePosition();
        if (mouse == null) return;
        for (Widget widget : this.widgets) {
            if (widget.isVisible() && mouse.x > widget.getX()
                && mouse.y > widget.getY()
                && mouse.x < widget.getX() + widget.getWidth()
                && mouse.y < widget.getY() + widget.getHeight())
                if (widget.onMousePressed(mouse.x - widget.getX(), mouse.y - widget.getY())) {
                    this.mouseOwner = widget;
                    return;
                }
        }
        this.lastMouse = mouse;
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger() && MinecraftUtil.getVersion()
            .saveEnabled()) {
            this.lastRightClick = getMousePosition();
            if (this.proj.saveLoaded) this.menu.show(e.getComponent(), e.getX(), e.getY());
        } else if (this.mouseOwner != null) {
            this.mouseOwner.onMouseReleased();
            this.mouseOwner = null;
        } else {
            this.lastMouse = null;
        }
    }

    public PlayerLayer getPlayerLayer() {
        return playerLayer;
    }

    public MapObject getSelectedObject() {
        return this.selectedObject;
    }

    public void movePlayer(String name, ActionEvent e) {}

    public void saveToFile(File f) {
        BufferedImage image = new BufferedImage(this.worldMap.width, this.worldMap.height, 2);
        Graphics2D g2d = image.createGraphics();
        this.worldMap.draw(g2d, 0.0F);
        g2d.setFont(this.textFont);
        for (Widget widget : this.widgets) {
            if (widget.isVisible()) widget.draw(g2d, 0.0F);
        }
        try {
            ImageIO.write(image, "png", f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        g2d.dispose();
        image.flush();
    }

    public void keyPressed(KeyEvent e) {
        Point mouse = getMousePosition();
        if (mouse == null) mouse = new Point(getWidth() >> 1, getHeight() >> 1);
        if (e.getKeyCode() == 61) {
            adjustZoom(mouse, -1);
        } else if (e.getKeyCode() == 45) {
            adjustZoom(mouse, 1);
        }
    }

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public Map getMap() {
        return this.worldMap;
    }

    public FontMetrics getFontMetrics() {
        return this.textMetrics;
    }
}
