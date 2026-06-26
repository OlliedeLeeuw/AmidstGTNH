package sedridor.amidst.project;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

import javax.imageio.ImageIO;

import sedridor.amidst.map.MapObjectPlayer;

public class SkinManager extends Thread {

    private Stack<MapObjectPlayer> players = new Stack<MapObjectPlayer>();

    public boolean active = true;

    public void addPlayer(MapObjectPlayer p) {
        this.players.push(p);
    }

    public void run() {
        while (this.active) {
            try {
                if (this.players.isEmpty()) {
                    Thread.sleep(50L);
                    continue;
                }
                MapObjectPlayer p = this.players.pop();
                try {
                    URL url = new URL("http://s3.amazonaws.com/MinecraftSkins/" + p.getName() + ".png");
                    BufferedImage img = ImageIO.read(url);
                    BufferedImage pimg = new BufferedImage(20, 20, 2);
                    Graphics2D g2d = pimg.createGraphics();
                    g2d.setColor(Color.black);
                    g2d.fillRect(0, 0, 20, 20);
                    g2d.drawImage(img, 2, 2, 18, 18, 8, 8, 16, 16, null);
                    g2d.dispose();
                    img.flush();
                    p.setMarker(pimg);
                    Thread.sleep(20L);
                } catch (MalformedURLException e) {

                } catch (IOException e) {}
            } catch (InterruptedException e) {}
        }
        if (!this.active) dispose();
    }

    public void dispose() {
        this.players.clear();
        this.players = null;
    }
}
