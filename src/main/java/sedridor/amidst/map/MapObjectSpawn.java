package sedridor.amidst.map;

public class MapObjectSpawn extends MapObject {

    public int globalX;

    public int globalY;

    public MapObjectSpawn(int x, int y) {
        super(MapMarkers.SPAWN, ((x < 0) ? 512 : 0) + x % 512, ((y < 0) ? 512 : 0) + y % 512);
        this.globalX = x;
        this.globalY = y;
    }

    public String toString() {
        return "Spawn point at (" + this.x + ", " + this.y + ")";
    }
}
