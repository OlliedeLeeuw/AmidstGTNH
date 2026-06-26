package sedridor.amidst.map.layers;

import sedridor.amidst.Options;
import sedridor.amidst.Util;
import sedridor.amidst.map.Fragment;
import sedridor.amidst.map.ImageLayer;
import sedridor.amidst.minecraft.Biome;

public class BiomeLayer extends ImageLayer {

    public static BiomeLayer instance;

    protected static int size = 128;

    protected boolean[] selectedBiomes = new boolean[Biome.biomes.length];

    private boolean inHighlightMode = false;

    public BiomeLayer() {
        super(size);
        instance = this;
        deselectAllBiomes();
    }

    public boolean isVisible() {
        return !Options.instance.colorByClimate.get();
    }

    public void selectAllBiomes() {
        setSelectedAllBiomes(true);
    }

    public void deselectAllBiomes() {
        setSelectedAllBiomes(false);
    }

    public void selectBiome(int id) {
        setSelected(id, true);
    }

    public void deselectBiome(int id) {
        setSelected(id, false);
    }

    public void setHighlightMode(boolean enabled) {
        this.inHighlightMode = enabled;
    }

    public void toggleBiomeSelect(int id) {
        setSelected(id, !this.selectedBiomes[id]);
    }

    public void setSelected(int id, boolean value) {
        this.selectedBiomes[id] = value;
    }

    public void setSelectedAllBiomes(boolean value) {
        for (int i = 0; i < this.selectedBiomes.length; i++) this.selectedBiomes[i] = value;
    }

    public void drawToCache(Fragment fragment) {
        int[] dataCache = Fragment.getIntArray();
        if (this.inHighlightMode) {
            for (int i = 0; i < size * size; i++) {
                int color = (fragment.biomeData[i] >= 0 && Biome.biomes[fragment.biomeData[i]] != null)
                    ? Biome.biomes[fragment.biomeData[i]].getColor()
                    : Util.makeColor(0, 0, 0);
                if (!this.selectedBiomes[fragment.biomeData[i]]) {
                    dataCache[i] = Util.deselectColor(color);
                } else {
                    dataCache[i] = color;
                }
            }
        } else {
            for (int i = 0; i < size * size; i++) {
                int color = (fragment.biomeData[i] >= 0 && Biome.biomes[fragment.biomeData[i]] != null)
                    ? Biome.biomes[fragment.biomeData[i]].getColor()
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

    public boolean isBiomeSelected(int id) {
        return this.selectedBiomes[id];
    }
}
