package sedridor.amidst.map;

import java.awt.image.BufferedImage;

import sedridor.amidst.minecraft.MinecraftUtil;
import sedridor.amidst.project.FinderWindow;

public class MapObjectPlayer extends MapObject {

    public String name;

    public boolean needSave;

    private BufferedImage marker;

    public int globalX;

    public int globalY;

    public Fragment parentFragment = null;

    public MapObjectPlayer(String name, int x, int y) {
        super(MapMarkers.PLAYER, ((x < 0) ? 512 : 0) + x % 512, ((y < 0) ? 512 : 0) + y % 512);
        this.globalX = x;
        this.globalY = y;
        this.marker = this.type.image;
        this.needSave = false;
        this.name = name;
    }

    public int getWidth() {
        return (int) ((double) this.marker.getWidth() * this.localScale);
    }

    public int getHeight() {
        return (int) ((double) this.marker.getHeight() * this.localScale);
    }

    public void setPosition(int x, int y) {
        this.globalX = x;
        this.globalY = y;
        this.x = ((x < 0) ? 512 : 0) + x % 512;
        this.y = ((y < 0) ? 512 : 0) + y % 512;
        this.needSave = true;
        if (!FinderWindow.instance.menuBar.saveLevel.isEnabled()) FinderWindow.instance.menuBar.saveLevel.setEnabled(
            (MinecraftUtil.getVersion()
                .saveEnabled() && FinderWindow.instance.curProject.saveLoaded));
    }

    public BufferedImage getImage() {
        return this.marker;
    }

    public void setMarker(BufferedImage img) {
        this.marker = img;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return "Player \"" + this.name + "\" at (" + this.globalX + ", " + this.globalY + ")";
    }
}
