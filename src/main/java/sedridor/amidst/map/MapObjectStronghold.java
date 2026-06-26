package sedridor.amidst.map;

public class MapObjectStronghold extends MapObject {

    public MapObjectStronghold(int x, int y) {
        super(MapMarkers.STRONGHOLD, x, y);
    }

    public String toString() {
        return "Stronghold at (" + this.x + ", " + this.y + ")";
    }
}
