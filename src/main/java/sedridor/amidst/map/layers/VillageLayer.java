package sedridor.amidst.map.layers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenVillage;

import sedridor.amidst.Options;
import sedridor.amidst.map.Fragment;
import sedridor.amidst.map.IconLayer;
import sedridor.amidst.map.MapObjectVillage;
import sedridor.amidst.minecraft.Biome;
import sedridor.amidst.minecraft.MinecraftUtil;

public class VillageLayer extends IconLayer {

    public static List<Biome> validBiomes = Arrays.<Biome>asList(Biome.plains, Biome.desert, Biome.savanna);

    private Random random = new Random();

    public VillageLayer() {
        validBiomes = new ArrayList<Biome>();
        for (int i = 0; i < MapGenVillage.villageSpawnBiomes.size(); i++) {
            BiomeGenBase biome = MapGenVillage.villageSpawnBiomes.get(i);
            if (Biome.biomes[biome.biomeID] != null && !validBiomes.contains(Biome.biomes[biome.biomeID]))
                validBiomes.add(Biome.biomes[biome.biomeID]);
        }
    }

    public boolean isVisible() {
        return Options.instance.showVillages.get();
    }

    public void generateMapObjects(Fragment frag) {
        int size = 32;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int chunkX = x + frag.getChunkX();
                int chunkY = y + frag.getChunkY();
                if (checkChunk(chunkX, chunkY))
                    frag.addObject(new MapObjectVillage((x << 4) + 4, (y << 4) + 4).setParent(this));
            }
        }
    }

    public boolean checkChunk(int chunkX, int chunkY) {
        byte maxDistanceBetweenScatteredFeatures = 32;
        byte minDistanceBetweenScatteredFeatures = 8;
        int structureSize = 0;
        int k = chunkX;
        int m = chunkY;
        if (chunkX < 0) chunkX -= maxDistanceBetweenScatteredFeatures - 1;
        if (chunkY < 0) chunkY -= maxDistanceBetweenScatteredFeatures - 1;
        int n = chunkX / maxDistanceBetweenScatteredFeatures;
        int i1 = chunkY / maxDistanceBetweenScatteredFeatures;
        long positionSeed = (long) n * 341873128712L + (long) i1 * 132897987541L + Options.instance.seed + 10387312L;
        this.random.setSeed(positionSeed);
        n *= maxDistanceBetweenScatteredFeatures;
        i1 *= maxDistanceBetweenScatteredFeatures;
        n += this.random.nextInt(maxDistanceBetweenScatteredFeatures - minDistanceBetweenScatteredFeatures);
        i1 += this.random.nextInt(maxDistanceBetweenScatteredFeatures - minDistanceBetweenScatteredFeatures);
        chunkX = k;
        chunkY = m;
        if (chunkX == n && chunkY == i1) {
            int chunkCenterBlockX = chunkX * 16 + 8;
            int chunkCenterBlockY = chunkY * 16 + 8;
            boolean canSpawnStructureAtCoords = MinecraftUtil
                .isValidBiome(chunkCenterBlockX, chunkCenterBlockY, structureSize, validBiomes);
            if (canSpawnStructureAtCoords) {
                int wellSize = 6;
                int x1 = chunkX * 16 + 2;
                int y1 = chunkY * 16 + 2;
                int x2 = x1 + wellSize - 1;
                int y2 = y1 + wellSize - 1;
                int wellX = (x1 + x2) / 2;
                int wellY = (y1 + y2) / 2;
                int arbitraryConstant = 2;
                int wellStructureSize = (x2 - x1) / 2 + arbitraryConstant;
                boolean canSpawnWellAtCoords = MinecraftUtil.isValidBiome(wellX, wellY, wellStructureSize, validBiomes);
                return canSpawnWellAtCoords;
            }
        }
        return false;
    }
}
