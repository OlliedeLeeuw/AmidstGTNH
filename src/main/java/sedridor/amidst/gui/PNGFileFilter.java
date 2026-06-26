package sedridor.amidst.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class PNGFileFilter extends FileFilter {

    public boolean accept(File file) {
        if (file.isDirectory()) return true;
        String[] st = file.getName()
            .split("\\.");
        return st[st.length - 1].equalsIgnoreCase("png");
    }

    public String getDescription() {
        return "Portable Network Graphic (*.PNG)";
    }
}
