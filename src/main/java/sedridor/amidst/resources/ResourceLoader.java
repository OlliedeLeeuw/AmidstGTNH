package sedridor.amidst.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

public class ResourceLoader {

    public static URL getResourceURL(String name) {
        return ResourceLoader.class.getResource(name);
    }

    public static InputStream getResourceStream(String name) {
        return ResourceLoader.class.getResourceAsStream(name);
    }

    public static BufferedImage getImage(String name) {
        try {
            return ImageIO.read(getResourceURL(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
