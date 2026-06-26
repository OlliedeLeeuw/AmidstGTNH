package sedridor.amidst.map.layers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeManager;

import sedridor.amidst.Options;
import sedridor.amidst.map.Fragment;
import sedridor.amidst.map.IconLayer;
import sedridor.amidst.map.MapObjectStronghold;
import sedridor.amidst.minecraft.Biome;
import sedridor.amidst.minecraft.MinecraftUtil;
import sedridor.amidst.version.VersionInfo;

public class StrongholdLayer extends IconLayer {

    public static StrongholdLayer instance;

    private static final Biome[] biomesDefault = new Biome[] { Biome.desert, Biome.forest, Biome.extremeHills,
        Biome.swampland };

    private static final Biome[] biomes1_0 = new Biome[] { Biome.desert, Biome.forest, Biome.extremeHills,
        Biome.swampland, Biome.taiga, Biome.icePlains, Biome.iceMountains };

    private static final Biome[] biomes1_1 = new Biome[] { Biome.desert, Biome.forest, Biome.extremeHills,
        Biome.swampland, Biome.taiga, Biome.icePlains, Biome.iceMountains, Biome.desertHills, Biome.forestHills,
        Biome.extremeHillsEdge };

    private static final Biome[] biomes1_2 = new Biome[] { Biome.desert, Biome.forest, Biome.extremeHills,
        Biome.swampland, Biome.taiga, Biome.icePlains, Biome.iceMountains, Biome.desertHills, Biome.forestHills,
        Biome.extremeHillsEdge, Biome.jungle, Biome.jungleHills };

    private MapObjectStronghold[] strongholds = new MapObjectStronghold[3];

    public StrongholdLayer() {
        instance = this;
    }

    public boolean isVisible() {
        return Options.instance.showStrongholds.get();
    }

    public void generateMapObjects(Fragment frag) {
        int size = 32;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int chunkX = x + frag.getChunkX();
                int chunkY = y + frag.getChunkY();
                if (checkChunk(chunkX, chunkY)) frag.addObject(new MapObjectStronghold(x << 4, y << 4).setParent(this));
            }
        }
    }

    public void findStrongholds() {
        Random random = new Random();
        random.setSeed(Options.instance.seed);
        Biome[] validBiomes = biomesDefault;
        if (MinecraftUtil.getVersion() == VersionInfo.V1_0) validBiomes = biomes1_0;
        if (MinecraftUtil.getVersion() == VersionInfo.V1_1) validBiomes = biomes1_1;
        if (MinecraftUtil.getVersion()
            .isAtLeast(VersionInfo.V1_2_2)) validBiomes = biomes1_2;
        List<Biome> biomeArrayList = Arrays.<Biome>asList(validBiomes);
        if (MinecraftUtil.getVersion()
            .isAtLeast(VersionInfo.V1_7_2)) {
            biomeArrayList = new ArrayList<Biome>();
            for (int j = 0; j < Biome.biomes.length; j++) {
                if (Biome.biomes[j] != null && (Biome.biomes[j]).height.baseHeight > 0.0F
                    && !BiomeManager.strongHoldBiomesBlackList.contains(Biome.getBiomeGenBase(j)))
                    biomeArrayList.add(Biome.biomes[j]);
                for (BiomeGenBase biome : (Iterable<BiomeGenBase>) BiomeManager.strongHoldBiomes) {
                    if (Biome.biomes[biome.biomeID] != null && !biomeArrayList.contains(Biome.biomes[biome.biomeID]))
                        biomeArrayList.add(Biome.biomes[biome.biomeID]);
                }
            }
        }
        double angle = random.nextDouble() * Math.PI * 2.0D;
        for (int i = 0; i < 3; i++) {
            double distance = (1.25D + random.nextDouble()) * 32.0D;
            int x = (int) Math.round(Math.cos(angle) * distance);
            int y = (int) Math.round(Math.sin(angle) * distance);
            Point strongholdLocation = MinecraftUtil
                .findValidLocation((x << 4) + 8, (y << 4) + 8, 112, biomeArrayList, random);
            if (strongholdLocation != null) {
                x = strongholdLocation.x >> 4;
                y = strongholdLocation.y >> 4;
            }
            this.strongholds[i] = new MapObjectStronghold((x << 4) + 8, (y << 4) + 8);
            angle += 2.0943951023931953D;
        }
    }

    public boolean checkChunk(int chunkX, int chunkY) {
        for (int i = 0; i < 3; i++) {
            int strongholdChunkX = (this.strongholds[i]).x >> 4;
            int strongholdChunkY = (this.strongholds[i]).y >> 4;
            if (strongholdChunkX == chunkX && strongholdChunkY == chunkY) return true;
        }
        return false;
    }

    public MapObjectStronghold[] getStrongholds() {
        return this.strongholds;
    }

    public void reload() {
        findStrongholds();
    }
}
