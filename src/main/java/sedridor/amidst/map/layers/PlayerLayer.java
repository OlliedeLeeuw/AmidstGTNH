package sedridor.amidst.map.layers;

import java.util.List;

import sedridor.amidst.Options;
import sedridor.amidst.map.Fragment;
import sedridor.amidst.map.IconLayer;
import sedridor.amidst.map.MapObjectPlayer;
import sedridor.amidst.project.SaveLoader;
import sedridor.amidst.project.SkinManager;

public class PlayerLayer extends IconLayer {

    public SaveLoader saveLoader;

    public static SkinManager skinManager = new SkinManager();

    public boolean isEnabled;

    public MapObjectPlayer thePlayer;

    static {
        skinManager.start();
    }

    public boolean isVisible() {
        return Options.instance.showPlayers.get();
    }

    public void generateMapObjects(Fragment frag) {
        if (!this.isEnabled) return;
        if (this.saveLoader != null) {
            List<MapObjectPlayer> players = this.saveLoader.getPlayers();
            for (MapObjectPlayer player : players) {
                if (player.globalX >= frag.blockX && player.globalX < frag.blockX + 512
                    && player.globalY >= frag.blockY
                    && player.globalY < frag.blockY + 512) {
                    player.parentLayer = this;
                    player.parentFragment = frag;
                    frag.addObject(player);
                }
            }
        } else if (this.thePlayer != null) {
            MapObjectPlayer player = this.thePlayer;
            if (player.globalX >= frag.blockX && player.globalX < frag.blockX + 512
                && player.globalY >= frag.blockY
                && player.globalY < frag.blockY + 512) {
                player.parentLayer = this;
                player.parentFragment = frag;
                frag.addObject(player);
            }
        }
    }

    public void clearMapObjects(Fragment frag) {
        for (int i = 0; i < frag.objectsLength; i++) {
            if (frag.objects[i] instanceof MapObjectPlayer) ((MapObjectPlayer) frag.objects[i]).parentFragment = null;
        }
        super.clearMapObjects(frag);
    }

    public void setPlayers(SaveLoader save) {
        this.saveLoader = save;
        for (MapObjectPlayer player : this.saveLoader.getPlayers()) skinManager.addPlayer(player);
    }
}
