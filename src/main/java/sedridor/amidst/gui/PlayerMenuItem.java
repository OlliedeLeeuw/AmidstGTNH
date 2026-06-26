package sedridor.amidst.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import sedridor.amidst.map.Fragment;
import sedridor.amidst.map.Map;
import sedridor.amidst.map.MapObjectPlayer;
import sedridor.amidst.map.layers.PlayerLayer;
import sedridor.amidst.project.MapViewer;

public class PlayerMenuItem extends JMenuItem implements ActionListener {

    private PlayerLayer playerLayer;

    private MapObjectPlayer player;

    private MapViewer mapViewer;

    public PlayerMenuItem(MapViewer mapViewer, MapObjectPlayer player, PlayerLayer playerLayer) {
        super(player.getName());
        this.playerLayer = playerLayer;
        this.player = player;
        this.mapViewer = mapViewer;
        addActionListener(this);
    }

    public void actionPerformed(ActionEvent event) {
        Map map = this.playerLayer.getMap();
        if (this.player.parentFragment != null) this.player.parentFragment.removeObject(this.player);
        Point location = map.screenToLocal(this.mapViewer.lastRightClick);
        this.player.setPosition(location.x, location.y);
        Fragment fragment = map.getFragmentAt(location);
        fragment.addObject(this.player);
        this.player.parentFragment = fragment;
    }
}
