package sedridor.amidst.map.layers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

import sedridor.amidst.Options;
import sedridor.amidst.logging.Log;
import sedridor.amidst.map.Fragment;
import sedridor.amidst.map.IconLayer;
import sedridor.amidst.map.MapObjectSpawn;
import sedridor.amidst.minecraft.Biome;
import sedridor.amidst.minecraft.MinecraftUtil;
import sedridor.forgeamidst.ForgeAmidst;

public class SpawnLayer extends IconLayer {

    private MapObjectSpawn spawnObject;

    public static final ArrayList<Biome> validBiomes = new ArrayList<Biome>(
        Arrays.<Biome>asList(
            Biome.forest,
            Biome.plains,
            Biome.taiga,
            Biome.taigaHills,
            Biome.forestHills,
            Biome.jungle,
            Biome.jungleHills));

    public SpawnLayer() {
        for (BiomeGenBase biome : (Iterable<BiomeGenBase>) WorldChunkManager.allowedBiomes) {
            if (Biome.biomes[biome.biomeID] != null && !validBiomes.contains(Biome.biomes[biome.biomeID]))
                validBiomes.add(Biome.biomes[biome.biomeID]);
        }
    }

    public boolean isVisible() {
        return Options.instance.showSpawn.get();
    }

    public void generateMapObjects(Fragment frag) {
        if (this.spawnObject.globalX >= frag.blockX && this.spawnObject.globalX < frag.blockX + 512
            && this.spawnObject.globalY >= frag.blockY
            && this.spawnObject.globalY < frag.blockY + 512) {
            this.spawnObject.parentLayer = this;
            frag.addObject(this.spawnObject);
        }
    }

    private Point getSpawnPosition() {
        if (ForgeAmidst.getWorld() != null) return new Point(
            ForgeAmidst.getWorld()
                .getWorldInfo()
                .getSpawnX(),
            ForgeAmidst.getWorld()
                .getWorldInfo()
                .getSpawnZ());
        Random random = new Random(Options.instance.seed);
        Point location = MinecraftUtil.findValidLocation(0, 0, 256, validBiomes, random);
        int x = 0;
        int y = 0;
        if (location != null) {
            x = location.x;
            y = location.y;
        } else {
            Log.debug(new Object[] { "Unable to find spawn biome." });
        }
        return new Point(x, y);
    }

    public void reload() {
        Point spawnCenter = getSpawnPosition();
        this.spawnObject = new MapObjectSpawn(spawnCenter.x, spawnCenter.y);
    }
}
