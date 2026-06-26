package sedridor.amidst.map.layers;

import sedridor.amidst.Options;
import sedridor.amidst.Util;
import sedridor.amidst.map.Fragment;
import sedridor.amidst.map.ImageLayer;
import sedridor.amidst.minecraft.Biome;

public class ClimateLayer extends ImageLayer {

    public static ClimateLayer instance;

    protected static int size = 128;

    protected boolean[] selectedClimates = new boolean[Biome.climates.length];

    private boolean inHighlightMode = false;

    public ClimateLayer() {
        super(size);
        instance = this;
        deselectAllClimates();
    }

    public boolean isVisible() {
        return Options.instance.colorByClimate.get();
    }

    public void selectAllClimates() {
        setSelectedAllClimates(true);
    }

    public void deselectAllClimates() {
        setSelectedAllClimates(false);
    }

    public void selectClimate(int id) {
        setSelected(id, true);
    }

    public void deselectClimate(int id) {
        setSelected(id, false);
    }

    public void setHighlightMode(boolean enabled) {
        this.inHighlightMode = enabled;
    }

    public void toggleClimateSelect(int id) {
        setSelected(id, !this.selectedClimates[id]);
    }

    public void setSelected(int id, boolean value) {
        this.selectedClimates[id] = value;
    }

    public void setSelectedAllClimates(boolean value) {
        for (int i = 0; i < this.selectedClimates.length; i++) this.selectedClimates[i] = value;
    }

    public void drawToCache(Fragment fragment) {
        int[] dataCache = Fragment.getIntArray();
        if (this.inHighlightMode) {
            for (int i = 0; i < size * size; i++) {
                int color = (fragment.biomeData[i] >= 0 && Biome.biomes[fragment.biomeData[i]] != null)
                    ? Biome.biomes[fragment.biomeData[i]].getClimateColor()
                    : Util.makeColor(0, 0, 0);
                if (!this.selectedClimates[Biome.biomes[fragment.biomeData[i]].getClimate()
                    .getId()]) {
                    dataCache[i] = Util.deselectColor(color);
                } else {
                    dataCache[i] = color;
                }
            }
        } else {
            for (int i = 0; i < size * size; i++) {
                int color = (fragment.biomeData[i] >= 0 && Biome.biomes[fragment.biomeData[i]] != null)
                    ? Biome.biomes[fragment.biomeData[i]].getClimateColor()
                    : Util.makeColor(0, 0, 0);
                dataCache[i] = color;
            }
        }
        fragment.setImageData(this.layerId, dataCache);
    }

    public static int getBiomeForFragment(Fragment frag, int blockX, int blockY) {
        return frag.biomeData[(blockY >> 2) * 128 + (blockX >> 2)];
    }

    public static String getBiomeNameForFragment(Fragment frag, int blockX, int blockY) {
        return (Biome.biomes[getBiomeForFragment(frag, blockX, blockY)] != null)
            ? (Biome.biomes[getBiomeForFragment(frag, blockX, blockY)]).name
            : ("BIOME_" + getBiomeForFragment(frag, blockX, blockY));
    }

    public static String getBiomeAliasForFragment(Fragment frag, int blockX, int blockY) {
        return Options.instance.biomeColorProfile.getAliasForId(getBiomeForFragment(frag, blockX, blockY));
    }

    public static String getClimateForFragment(Fragment frag, int blockX, int blockY) {
        return (Biome.biomes[getBiomeForFragment(frag, blockX, blockY)] != null)
            ? Biome.biomes[getBiomeForFragment(frag, blockX, blockY)].getClimate()
                .getClimateName()
            : "Unknown";
    }

    public boolean isClimateSelected(int id) {
        return this.selectedClimates[id];
    }
}
