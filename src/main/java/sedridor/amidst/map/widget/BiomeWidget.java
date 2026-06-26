package sedridor.amidst.map.widget;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import sedridor.amidst.Options;
import sedridor.amidst.map.layers.BiomeLayer;
import sedridor.amidst.minecraft.Biome;
import sedridor.amidst.project.MapViewer;

public class BiomeWidget extends PanelWidget {

    private static BiomeWidget instance;

    private static Color innerBoxBgColor = new Color(0.3F, 0.3F, 0.3F, 0.3F);

    private static Color biomeBgColor1 = new Color(0.8F, 0.8F, 0.8F, 0.2F);

    private static Color biomeBgColor2 = new Color(0.6F, 0.6F, 0.6F, 0.2F);

    private static Color biomeLitBgColor1 = new Color(0.8F, 0.8F, 1.0F, 0.7F);

    private static Color biomeLitBgColor2 = new Color(0.6F, 0.6F, 0.8F, 0.7F);

    private static Color innerBoxBorderColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);

    private static Color scrollbarColor = new Color(0.6F, 0.6F, 0.6F, 0.8F);

    private static Color scrollbarLitColor = new Color(0.6F, 0.6F, 0.8F, 0.8F);

    private static Color selectButtonColor = new Color(0.6F, 0.6F, 0.8F, 1.0F);

    private ArrayList<Biome> biomes = new ArrayList<Biome>();

    private int maxNameWidth = 0;

    private Rectangle innerBox = new Rectangle(0, 0, 1, 1);

    private int biomeListHeight;

    private int biomeListYOffset = 0;

    private boolean scrollbarVisible = false;

    private boolean scrollbarGrabbed = false;

    private int scrollbarHeight = 0, scrollbarWidth = 10, scrollbarY = 0, mouseYOnGrab = 0;

    private int scrollbarYOnGrab;

    public BiomeWidget(MapViewer mapViewer) {
        super(mapViewer);
        FontMetrics fontMetrics = mapViewer.getFontMetrics(this.textFont);
        for (int i = 0; i < Biome.biomes.length; i++) {
            if (Biome.biomes[i] != null)
                this.maxNameWidth = Math.max(fontMetrics.stringWidth((Biome.biomes[i]).name), this.maxNameWidth);
        }
        setDimensions(260, 400);
        this.y = 100;
        forceVisibility(false);
    }

    public void draw(Graphics2D g2d, float time) {
        this.x = this.mapViewer.getWidth() - this.width;
        super.draw(g2d, time);
        g2d.setColor(this.textColor);
        g2d.setFont(this.textFont);
        g2d.drawString("Highlight Biomes", this.x + 10, this.y + 20);
        this.innerBox.x = this.x + 8;
        this.innerBox.y = this.y + 30;
        this.innerBox.width = this.width - 16;
        this.innerBox.height = this.height - 58;
        createSortedBiomeList(
            Options.instance.sortedBiomeList.get()
                .booleanValue());
        this.biomeListHeight = this.biomes.size() * 16;
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
        for (int i = 0; i < this.biomes.size(); i++) {
            Biome biome = this.biomes.get(i);
            if (BiomeLayer.instance.isBiomeSelected(biome.index)) {
                g2d.setColor((i % 2 == 1) ? biomeLitBgColor1 : biomeLitBgColor2);
            } else {
                g2d.setColor((i % 2 == 1) ? biomeBgColor1 : biomeBgColor2);
            }
            g2d.fillRect(this.innerBox.x, this.innerBox.y + i * 16 + this.biomeListYOffset, this.innerBox.width, 16);
            g2d.setColor(new Color(biome.color));
            g2d.fillRect(this.innerBox.x, this.innerBox.y + i * 16 + this.biomeListYOffset, 20, 16);
            g2d.setColor(Color.white);
            if (Options.instance.showBiomeIDs.get()) {
                if (Options.instance.sortedBiomeList.get()) {
                    g2d.drawString(
                        "[" + biome.index + "]",
                        this.innerBox.x + 25,
                        this.innerBox.y + 13 + i * 16 + this.biomeListYOffset);
                    g2d.drawString(
                        biome.name,
                        this.innerBox.x + 65,
                        this.innerBox.y + 13 + i * 16 + this.biomeListYOffset);
                } else {
                    g2d.drawString(
                        "[" + biome.index + "] " + biome.name,
                        this.innerBox.x + 25,
                        this.innerBox.y + 13 + i * 16 + this.biomeListYOffset);
                }
            } else {
                g2d.drawString(biome.name, this.innerBox.x + 25, this.innerBox.y + 13 + i * 16 + this.biomeListYOffset);
            }
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
            if (id < this.biomes.size()) {
                BiomeLayer.instance.toggleBiomeSelect(((Biome) this.biomes.get(id)).index);
                needsRedraw = true;
            }
        }
        if (mouseY > this.height - 25 && mouseY < this.height - 9) if (mouseX > 117 && mouseX < 139) {
            BiomeLayer.instance.selectAllBiomes();
            needsRedraw = true;
        } else if (mouseX > 143 && mouseX < 197) {
            for (int i = 128; i < Biome.biomes.length; i++) {
                if (Biome.biomes[i] != null) BiomeLayer.instance.selectBiome(i);
            }
            needsRedraw = true;
        } else if (mouseX > 203 && mouseX < 242) {
            BiomeLayer.instance.deselectAllBiomes();
            needsRedraw = true;
        }
        if (needsRedraw) new Thread(new Runnable() {

            public void run() {
                BiomeWidget.this.map.resetImageLayer(BiomeLayer.instance.getLayerId());
            }
        }).start();
        return true;
    }

    public boolean onVisibilityCheck() {
        this.height = Math.max(200, this.mapViewer.getHeight() - 200);
        if (!Options.instance.colorByClimate.get())
            if (BiomeToggleWidget.isBiomeWidgetVisible & ((this.height > 200))) return true;
        return false;
    }

    private void setMapViewer(MapViewer mapViewer) {
        this.mapViewer = mapViewer;
        this.map = mapViewer.getMap();
        this.scrollbarGrabbed = false;
    }

    public static BiomeWidget get(MapViewer mapViewer) {
        if (instance == null) {
            instance = new BiomeWidget(mapViewer);
        } else {
            instance.setMapViewer(mapViewer);
        }
        return instance;
    }

    public void createSortedBiomeList(boolean sortedBiomeList) {
        if (sortedBiomeList) {
            if (this.biomes.size() > 1 && ((Biome) this.biomes.get(1)).index == Biome.deepOcean.index) return;
            this.biomes.clear();
            this.biomes.add(Biome.ocean);
            this.biomes.add(Biome.deepOcean);
            this.biomes.add(Biome.frozenOcean);
            this.biomes.add(Biome.plains);
            this.biomes.add(Biome.forest);
            this.biomes.add(Biome.forestHills);
            this.biomes.add(Biome.birchForest);
            this.biomes.add(Biome.birchForestHills);
            this.biomes.add(Biome.taiga);
            this.biomes.add(Biome.taigaHills);
            this.biomes.add(Biome.coldTaiga);
            this.biomes.add(Biome.coldTaigaHills);
            this.biomes.add(Biome.megaTaiga);
            this.biomes.add(Biome.megaTaigaHills);
            this.biomes.add(Biome.megaSpruceTaiga);
            this.biomes.add(Biome.megaSpurceTaigaHills);
            this.biomes.add(Biome.icePlains);
            this.biomes.add(Biome.icePlainsSpikes);
            this.biomes.add(Biome.iceMountains);
            this.biomes.add(Biome.extremeHills);
            this.biomes.add(Biome.extremeHillsEdge);
            this.biomes.add(Biome.extremeHillsPlus);
            this.biomes.add(Biome.jungle);
            this.biomes.add(Biome.jungleHills);
            this.biomes.add(Biome.jungleEdge);
            this.biomes.add(Biome.savanna);
            this.biomes.add(Biome.savannaPlateau);
            this.biomes.add(Biome.desert);
            this.biomes.add(Biome.desertHills);
            this.biomes.add(Biome.mesa);
            this.biomes.add(Biome.mesaPlateau);
            this.biomes.add(Biome.mesaPlateauF);
            this.biomes.add(Biome.roofedForest);
            this.biomes.add(Biome.swampland);
            this.biomes.add(Biome.beach);
            this.biomes.add(Biome.coldBeach);
            this.biomes.add(Biome.stoneBeach);
            this.biomes.add(Biome.river);
            this.biomes.add(Biome.frozenRiver);
            this.biomes.add(Biome.mushroomIsland);
            this.biomes.add(Biome.mushroomIslandShore);
            this.biomes.add(Biome.sunflowerPlains);
            this.biomes.add(Biome.flowerForest);
            this.biomes.add(Biome.birchForestM);
            this.biomes.add(Biome.birchForestHillsM);
            this.biomes.add(Biome.taigaM);
            this.biomes.add(Biome.coldTaigaM);
            this.biomes.add(Biome.extremeHillsM);
            this.biomes.add(Biome.extremeHillsPlusM);
            this.biomes.add(Biome.jungleM);
            this.biomes.add(Biome.jungleEdgeM);
            this.biomes.add(Biome.savannaM);
            this.biomes.add(Biome.savannaPlateauM);
            this.biomes.add(Biome.desertM);
            this.biomes.add(Biome.mesaPlateauM);
            this.biomes.add(Biome.mesaPlateauFM);
            this.biomes.add(Biome.mesaBryce);
            this.biomes.add(Biome.roofedForestM);
            this.biomes.add(Biome.swamplandM);
            for (int i = 40; i < Biome.biomes.length; i++) {
                if (Biome.biomes[i] != null && !this.biomes.contains(Biome.biomes[i])) this.biomes.add(Biome.biomes[i]);
            }
        } else {
            if (this.biomes.size() > 1 && ((Biome) this.biomes.get(1)).index == (Biome.biomes[1]).index) return;
            this.biomes.clear();
            for (int i = 0; i < Biome.biomes.length; i++) {
                if (Biome.biomes[i] != null) this.biomes.add(Biome.biomes[i]);
            }
        }
    }
}
