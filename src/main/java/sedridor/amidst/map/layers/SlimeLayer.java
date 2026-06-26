package sedridor.amidst.map.layers;

import java.util.Random;

import sedridor.amidst.Options;
import sedridor.amidst.map.Fragment;
import sedridor.amidst.map.ImageLayer;

public class SlimeLayer extends ImageLayer {

    private static int size = 32;

    private Random random = new Random();

    public SlimeLayer() {
        super(size);
    }

    public boolean isVisible() {
        return Options.instance.showSlimeChunks.get();
    }

    public void drawToCache(Fragment fragment) {
        int[] dataCache = Fragment.getIntArray();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int xPosition = fragment.getChunkX() + x;
                int yPosition = fragment.getChunkY() + y;
                this.random.setSeed(
                    Options.instance.seed + (long) (xPosition * xPosition * 4987142)
                        + (long) (xPosition * 5947611)
                        + (long) (yPosition * yPosition) * 4392871L
                        + (long) (yPosition * 389711) ^ 0x3AD8025FL);
                dataCache[y * size + x] = (this.random.nextInt(10) == 0) ? -1593900801 : 0;
            }
        }
        fragment.setImageData(this.layerId, dataCache);
    }
}
