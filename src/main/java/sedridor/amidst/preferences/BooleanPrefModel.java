package sedridor.amidst.preferences;

import java.util.prefs.Preferences;

import javax.swing.JToggleButton;

public class BooleanPrefModel extends JToggleButton.ToggleButtonModel implements PrefModel<Boolean> {

    private static final long serialVersionUID = -2291122955784916836L;

    private final String key;

    private final Preferences pref;

    public BooleanPrefModel(Preferences pref, String key, boolean selected) {
        this.pref = pref;
        this.key = key;
        set(Boolean.valueOf(pref.getBoolean(key, selected)));
    }

    public String getKey() {
        return this.key;
    }

    public Boolean get() {
        assert this.pref.get(this.key, null) != null && this.pref.getBoolean(this.key, false) == super.isSelected();
        return super.isSelected();
    }

    public boolean isSelected() {
        return get();
    }

    public void set(Boolean value) {
        super.setSelected(value.booleanValue());
        this.pref.putBoolean(this.key, value.booleanValue());
    }

    public void setSelected(boolean value) {
        set(Boolean.valueOf(value));
    }
}
