package sedridor.amidst.preferences;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class FilePrefModel implements PrefModel<File> {

    private final String key;

    private final Preferences pref;

    public FilePrefModel(Preferences pref, String key, File init) {
        this.pref = pref;
        this.key = key;
        if (pref.get(key, null) == null) set(init);
    }

    public String getKey() {
        return this.key;
    }

    public File get() {
        String path = this.pref.get(this.key, null);
        assert path != null;
        return new File(path);
    }

    public void set(File value) {
        try {
            this.pref.put(this.key, value.getCanonicalPath());
        } catch (IOException ignored) {
            this.pref.put(this.key, value.getPath());
        }
    }
}
