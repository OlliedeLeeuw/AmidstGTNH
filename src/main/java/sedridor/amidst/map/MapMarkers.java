package sedridor.amidst.map;

import java.awt.image.BufferedImage;

import sedridor.amidst.resources.ResourceLoader;

public enum MapMarkers {

    NETHER_FORTRESS,
    PLAYER,
    SLIME,
    STRONGHOLD,
    JUNGLE_TEMPLE,
    DESERT_TEMPLE,
    VILLAGE,
    SPAWN,
    WITCH_HUT,
    OCEAN_MONUMENT,
    IGLOO;

    public final BufferedImage image;

    MapMarkers() {
        String fileName = toString().toLowerCase() + ".png";
        this.image = ResourceLoader.getImage(fileName);
    }
}
