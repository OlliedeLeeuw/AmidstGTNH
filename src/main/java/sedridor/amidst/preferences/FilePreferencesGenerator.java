package sedridor.amidst.preferences;

import java.io.File;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

public class FilePreferencesGenerator implements PreferencesFactory {

    private static final Logger log = Logger.getLogger(FilePreferencesGenerator.class.getName());

    private static File preferencesFile;

    private Preferences rootPreferences;

    public static final String SYSTEM_PROPERTY_FILE = "amidst.preferences.FilePreferencesGenerator.file";

    public Preferences systemRoot() {
        return userRoot();
    }

    public Preferences userRoot() {
        if (this.rootPreferences == null) {
            log.finer("Instantiating root preferences");
            this.rootPreferences = new FilePreferences(null, "");
        }
        return this.rootPreferences;
    }

    public static File getPreferencesFile() {
        if (preferencesFile == null) {
            String prefsFile = System.getProperty("amidst.preferences.FilePreferencesGenerator.file");
            if (prefsFile == null || prefsFile.length() == 0)
                prefsFile = System.getProperty("user.dir") + File.separator + "amidst.cfg";
            preferencesFile = new File(prefsFile).getAbsoluteFile();
            log.finer("Preferences file is " + preferencesFile);
        }
        return preferencesFile;
    }
}
