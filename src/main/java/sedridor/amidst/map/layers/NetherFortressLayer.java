package sedridor.amidst.map.layers;

import java.util.Random;

import sedridor.amidst.Options;
import sedridor.amidst.map.Fragment;
import sedridor.amidst.map.IconLayer;
import sedridor.amidst.map.MapObjectNether;

public class NetherFortressLayer extends IconLayer {

    private Random random = new Random();

    public boolean isVisible() {
        return Options.instance.showNetherFortresses.get();
    }

    public void generateMapObjects(Fragment frag) {
        int size = 32;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int chunkX = x + frag.getChunkX();
                int chunkY = y + frag.getChunkY();
                if (checkChunk(chunkX, chunkY))
                    frag.addObject(new MapObjectNether((x << 4) + 8, (y << 4) + 8).setParent(this));
            }
        }
    }

    public boolean checkChunk(int chunkX, int chunkY) {
        int i = chunkX >> 4;
        int j = chunkY >> 4;
        this.random.setSeed((long) (i ^ j << 4) ^ Options.instance.seed);
        this.random.nextInt();
        if (this.random.nextInt(3) != 0) return false;
        if (chunkX != (i << 4) + 4 + this.random.nextInt(8)) return false;
        return (chunkY == (j << 4) + 4 + this.random.nextInt(8));
    }
}
