package sedridor.amidst.preferences;

import java.util.prefs.Preferences;

public class StringPreference {

    private Preferences preferences;

    private String key;

    private String value;

    public StringPreference(Preferences preferences, String key, String defaultValue) {
        this.preferences = preferences;
        this.key = key;
        this.value = preferences.get(key, defaultValue);
    }

    public String get() {
        return this.value;
    }

    public void set(String value) {
        this.preferences.put(this.key, value);
    }
}
