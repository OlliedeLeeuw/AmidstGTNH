package sedridor.amidst.preferences;

import java.util.prefs.Preferences;

import javax.swing.JToggleButton;

public class SelectPrefModel implements PrefModel<String> {

    private Preferences preferences;

    private String key;

    private String selected;

    private SelectButtonModel[] buttonModels;

    public class SelectButtonModel extends JToggleButton.ToggleButtonModel {

        private SelectPrefModel model;

        public String name;

        public SelectButtonModel(SelectPrefModel model, String name) {
            this.model = model;
            this.name = name;
            super.setSelected(false);
        }

        public boolean isSelected() {
            return this.model.get()
                .equals(this.name);
        }

        public void setSelected(boolean value) {
            super.setSelected(value);
            if (value) this.model.set(this.name);
        }

        public String getName() {
            return this.name;
        }
    }

    public SelectPrefModel(Preferences pref, String key, String selected, String[] names) {
        this.key = key;
        this.preferences = pref;
        this.selected = selected;
        this.buttonModels = new SelectButtonModel[names.length];
        for (int i = 0; i < this.buttonModels.length; i++) this.buttonModels[i] = new SelectButtonModel(this, names[i]);
        set(pref.get(key, selected));
    }

    public String getKey() {
        return this.key;
    }

    public String get() {
        return this.selected;
    }

    public SelectButtonModel[] getButtonModels() {
        return this.buttonModels;
    }

    public void set(String value) {
        this.preferences.put(this.key, value);
        this.selected = value;
        for (int i = 0; i < this.buttonModels.length; i++) {
            if (!value.equals((this.buttonModels[i]).name)) this.buttonModels[i].setSelected(false);
        }
    }
}
