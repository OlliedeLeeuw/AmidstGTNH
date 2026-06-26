package sedridor.amidst.map.widget;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import sedridor.amidst.Options;
import sedridor.amidst.Util;
import sedridor.amidst.map.layers.ClimateLayer;
import sedridor.amidst.minecraft.Biome;
import sedridor.amidst.project.MapViewer;

public class ClimateWidget extends PanelWidget {

    private static ClimateWidget instance;

    private static Color innerBoxBgColor = new Color(0.3F, 0.3F, 0.3F, 0.3F);

    private static Color biomeBgColor1 = new Color(0.8F, 0.8F, 0.8F, 0.2F);

    private static Color biomeBgColor2 = new Color(0.6F, 0.6F, 0.6F, 0.2F);

    private static Color biomeLitBgColor1 = new Color(0.8F, 0.8F, 1.0F, 0.7F);

    private static Color biomeLitBgColor2 = new Color(0.6F, 0.6F, 0.8F, 0.7F);

    private static Color innerBoxBorderColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);

    private static Color scrollbarColor = new Color(0.6F, 0.6F, 0.6F, 0.8F);

    private static Color scrollbarLitColor = new Color(0.6F, 0.6F, 0.8F, 0.8F);

    private static Color selectButtonColor = new Color(0.6F, 0.6F, 0.8F, 1.0F);

    private ArrayList<Biome.Climate> climates = new ArrayList<Biome.Climate>();

    private int maxNameWidth = 0;

    private Rectangle innerBox = new Rectangle(0, 0, 1, 1);

    private int biomeListHeight;

    private int biomeListYOffset = 0;

    private boolean scrollbarVisible = false;

    private boolean scrollbarGrabbed = false;

    private int scrollbarHeight = 0, scrollbarWidth = 10, scrollbarY = 0, mouseYOnGrab = 0;

    private int scrollbarYOnGrab;

    public ClimateWidget(MapViewer mapViewer) {
        super(mapViewer);
        FontMetrics fontMetrics = mapViewer.getFontMetrics(this.textFont);
        for (int i = 0; i < Biome.climates.length; i++) {
            if (Biome.climates[i] != null)
                this.maxNameWidth = Math.max(fontMetrics.stringWidth((Biome.climates[i]).name), this.maxNameWidth);
        }
        setDimensions(260, 400);
        this.y = 400;
        forceVisibility(false);
    }

    public void draw(Graphics2D g2d, float time) {
        this.x = this.mapViewer.getWidth() - this.width;
        super.draw(g2d, time);
        g2d.setColor(this.textColor);
        g2d.setFont(this.textFont);
        g2d.drawString("Highlight Climates", this.x + 10, this.y + 20);
        this.innerBox.x = this.x + 8;
        this.innerBox.y = this.y + 30;
        this.innerBox.width = this.width - 16;
        this.innerBox.height = this.height - 58;
        createSortedClimateList();
        this.biomeListHeight = this.climates.size() * 16;
        this.biomeListYOffset = Math
            .min(0, Math.max(-this.biomeListHeight + this.innerBox.height, this.biomeListYOffset));
        if (this.biomeListHeight > this.innerBox.height) {
            this.innerBox.width -= this.scrollbarWidth;
            this.scrollbarVisible = true;
        } else {
            this.scrollbarVisible = false;
        }
        g2d.setColor(innerBoxBgColor);
        g2d.fillRect(this.innerBox.x, this.innerBox.y, this.innerBox.width, this.innerBox.height);
        g2d.setColor(innerBoxBorderColor);
        g2d.drawRect(
            this.innerBox.x - 1,
            this.innerBox.y - 1,
            this.innerBox.width + 1 + (this.scrollbarVisible ? this.scrollbarWidth : 0),
            this.innerBox.height + 1);
        g2d.setClip(this.innerBox);
        for (int i = 0; i < this.climates.size(); i++) {
            Biome.Climate climate = this.climates.get(i);
            if (ClimateLayer.instance.isClimateSelected(climate.index)) {
                g2d.setColor((i % 2 == 1) ? biomeLitBgColor1 : biomeLitBgColor2);
            } else {
                g2d.setColor((i % 2 == 1) ? biomeBgColor1 : biomeBgColor2);
            }
            g2d.fillRect(this.innerBox.x, this.innerBox.y + i * 16 + this.biomeListYOffset, this.innerBox.width, 16);
            g2d.setColor(new Color(climate.color));
            g2d.fillRect(this.innerBox.x, this.innerBox.y + i * 16 + this.biomeListYOffset, 20, 16);
            g2d.setColor(Color.white);
            g2d.drawString(
                Util.capitalizeString(climate.name),
                this.innerBox.x + 25,
                this.innerBox.y + 13 + i * 16 + this.biomeListYOffset);
        }
        g2d.setClip(null);
        if (this.scrollbarVisible) {
            float boxHeight = (float) this.innerBox.height;
            float listHeight = (float) this.biomeListHeight;
            if (this.scrollbarGrabbed) {
                Point mouse = this.mapViewer.getMousePosition();
                if (mouse != null) {
                    int tempScrollbarY = -this.scrollbarYOnGrab - (mouse.y - this.mouseYOnGrab);
                    this.biomeListYOffset = (int) (listHeight / boxHeight * (float) tempScrollbarY);
                    this.biomeListYOffset = Math
                        .min(0, Math.max(-this.biomeListHeight + this.innerBox.height, this.biomeListYOffset));
                } else {
                    this.scrollbarGrabbed = false;
                }
            }
            float yOffset = (float) -this.biomeListYOffset;
            this.scrollbarY = (int) (yOffset / listHeight * boxHeight);
            this.scrollbarHeight = (int) Math.ceil((double) (boxHeight * boxHeight / listHeight));
            g2d.setColor(this.scrollbarGrabbed ? scrollbarLitColor : scrollbarColor);
            g2d.fillRect(
                this.innerBox.x + this.innerBox.width,
                this.innerBox.y + this.scrollbarY,
                this.scrollbarWidth,
                this.scrollbarHeight);
        }
        g2d.setColor(Color.white);
        g2d.drawString("Select:", this.x + 8, this.y + this.height - 10);
        g2d.setColor(selectButtonColor);
        g2d.drawString("All  Special  None", this.x + 120, this.y + this.height - 10);
    }

    public boolean onMouseWheelMoved(int mouseX, int mouseY, int notches) {
        if (mouseX > this.innerBox.x - this.x && mouseX < this.innerBox.x - this.x + this.innerBox.width
            && mouseY > this.innerBox.y - this.y
            && mouseY < this.innerBox.y - this.y + this.innerBox.height)
            this.biomeListYOffset = Math
                .min(0, Math.max(-this.biomeListHeight + this.innerBox.height, this.biomeListYOffset - notches * 35));
        return true;
    }

    public void onMouseReleased() {
        this.scrollbarGrabbed = false;
    }

    public boolean onMousePressed(int mouseX, int mouseY) {
        if (this.scrollbarVisible) if (mouseX >= this.innerBox.x - this.x + this.innerBox.width
            && mouseX < this.innerBox.x - this.x + this.innerBox.width + this.scrollbarWidth
            && mouseY >= this.innerBox.y - this.y + this.scrollbarY
            && mouseY < this.innerBox.y - this.y + this.scrollbarY + this.scrollbarHeight) {
                this.mouseYOnGrab = mouseY + this.y;
                this.scrollbarYOnGrab = this.scrollbarY;
                this.scrollbarGrabbed = true;
            } else if (mouseX >= this.innerBox.x - this.x + this.innerBox.width
                && mouseX < this.innerBox.x - this.x + this.innerBox.width + this.scrollbarWidth
                && mouseY > this.innerBox.y - this.y + this.scrollbarY + this.scrollbarHeight
                && mouseY < 512) {
                    this.biomeListYOffset = Math
                        .min(0, Math.max(-this.biomeListHeight + this.innerBox.height, this.biomeListYOffset - 400));
                } else if (mouseX >= this.innerBox.x - this.x + this.innerBox.width
                    && mouseX < this.innerBox.x - this.x + this.innerBox.width + this.scrollbarWidth
                    && mouseY >= 30
                    && mouseY < this.innerBox.y - this.y + this.scrollbarY) {
                        this.biomeListYOffset = Math.min(
                            0,
                            Math.max(-this.biomeListHeight + this.innerBox.height, this.biomeListYOffset + 400));
                    }
        boolean needsRedraw = false;
        if (mouseX > this.innerBox.x - this.x && mouseX < this.innerBox.x - this.x + this.innerBox.width
            && mouseY > this.innerBox.y - this.y
            && mouseY < this.innerBox.y - this.y + this.innerBox.height) {
            int id = (mouseY - (this.innerBox.y - this.y) - this.biomeListYOffset) / 16;
            if (id < this.climates.size()) {
                ClimateLayer.instance.toggleClimateSelect(((Biome.Climate) this.climates.get(id)).index);
                needsRedraw = true;
            }
        }
        if (mouseY > this.height - 25 && mouseY < this.height - 9) if (mouseX > 117 && mouseX < 139) {
            ClimateLayer.instance.selectAllClimates();
            needsRedraw = true;
        } else if (mouseX > 143 && mouseX < 197) {
            for (int i = 1; i < 5; i++) {
                if (Biome.climates[i] != null) ClimateLayer.instance.selectClimate(i);
            }
            needsRedraw = true;
        } else if (mouseX > 203 && mouseX < 242) {
            ClimateLayer.instance.deselectAllClimates();
            needsRedraw = true;
        }
        if (needsRedraw) new Thread(new Runnable() {

            public void run() {
                ClimateWidget.this.map.resetImageLayer(ClimateLayer.instance.getLayerId());
            }
        }).start();
        return true;
    }

    public boolean onVisibilityCheck() {
        this.height = Math.max(200, this.mapViewer.getHeight() - 500);
        if (Options.instance.colorByClimate.get())
            if (ClimateToggleWidget.isClimateWidgetVisible & ((this.height > 200))) return true;
        return false;
    }

    private void setMapViewer(MapViewer mapViewer) {
        this.mapViewer = mapViewer;
        this.map = mapViewer.getMap();
        this.scrollbarGrabbed = false;
    }

    public static ClimateWidget get(MapViewer mapViewer) {
        if (instance == null) {
            instance = new ClimateWidget(mapViewer);
        } else {
            instance.setMapViewer(mapViewer);
        }
        return instance;
    }

    public void createSortedClimateList() {
        if (this.climates.size() > 1 && ((Biome.Climate) this.climates.get(1)).index == Biome.COOL.index) return;
        this.climates.clear();
        this.climates.add(Biome.SNOWY);
        this.climates.add(Biome.COOL);
        this.climates.add(Biome.MEDIUM);
        this.climates.add(Biome.PLAINS);
        this.climates.add(Biome.WARM);
        this.climates.add(Biome.HOT);
        this.climates.add(Biome.LAND);
        this.climates.add(Biome.WATER);
        this.climates.add(Biome.OCEAN);
        this.climates.add(Biome.DEEP_OCEAN);
        this.climates.add(Biome.UNKNOWN);
    }
}
