package sedridor.amidst.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

public class FilePreferences extends AbstractPreferences {

    private static final Logger log = Logger.getLogger(FilePreferencesGenerator.class.getName());

    private Map<String, String> root;

    private Map<String, FilePreferences> children;

    private boolean isRemoved = false;

    public FilePreferences(AbstractPreferences parent, String name) {
        super(parent, name);
        log.finest("Instantiating node " + name);
        this.root = new TreeMap<String, String>();
        this.children = new TreeMap<String, FilePreferences>();
        try {
            sync();
        } catch (BackingStoreException e) {
            log.log(Level.SEVERE, "Unable to sync on creation of node " + name, (Throwable) e);
        }
    }

    protected void putSpi(String key, String value) {
        this.root.put(key, value);
        try {
            flush();
        } catch (BackingStoreException e) {
            log.log(Level.SEVERE, "Unable to flush after putting " + key, (Throwable) e);
        }
    }

    protected String getSpi(String key) {
        return this.root.get(key);
    }

    protected void removeSpi(String key) {
        this.root.remove(key);
        try {
            flush();
        } catch (BackingStoreException e) {
            log.log(Level.SEVERE, "Unable to flush after removing " + key, (Throwable) e);
        }
    }

    protected void removeNodeSpi() throws BackingStoreException {
        this.isRemoved = true;
        flush();
    }

    protected String[] keysSpi() throws BackingStoreException {
        return (String[]) this.root.keySet()
            .toArray(
                new String[this.root.keySet()
                    .size()]);
    }

    protected String[] childrenNamesSpi() throws BackingStoreException {
        return (String[]) this.children.keySet()
            .toArray(
                new String[this.children.keySet()
                    .size()]);
    }

    protected FilePreferences childSpi(String name) {
        FilePreferences child = this.children.get(name);
        if (child == null || child.isRemoved()) {
            child = new FilePreferences(this, name);
            this.children.put(name, child);
        }
        return child;
    }

    protected void syncSpi() throws BackingStoreException {
        if (isRemoved()) return;
        File file = FilePreferencesGenerator.getPreferencesFile();
        if (!file.exists()) return;
        synchronized (file) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(file));
                StringBuilder sb = new StringBuilder();
                getPath(sb);
                String path = sb.toString();
                Enumeration<?> pnen = p.propertyNames();
                while (pnen.hasMoreElements()) {
                    String propKey = (String) pnen.nextElement();
                    if (propKey.startsWith(path)) {
                        String subKey = propKey.substring(path.length());
                        if (subKey.indexOf('.') == -1) this.root.put(subKey, p.getProperty(propKey));
                    }
                }
            } catch (IOException e) {
                throw new BackingStoreException(e);
            }
        }
    }

    private void getPath(StringBuilder sb) {
        FilePreferences parent = (FilePreferences) parent();
        if (parent == null) return;
        parent.getPath(sb);
        sb.append(name())
            .append('.');
    }

    protected void flushSpi() throws BackingStoreException {
        File file = FilePreferencesGenerator.getPreferencesFile();
        synchronized (file) {
            Properties p = new Properties();
            try {
                StringBuilder sb = new StringBuilder();
                getPath(sb);
                String path = sb.toString();
                if (file.exists()) {
                    p.load(new FileInputStream(file));
                    List<String> toRemove = new ArrayList<String>();
                    Enumeration<?> pnen = p.propertyNames();
                    while (pnen.hasMoreElements()) {
                        String propKey = (String) pnen.nextElement();
                        if (propKey.startsWith(path)) {
                            String subKey = propKey.substring(path.length());
                            if (subKey.indexOf('.') == -1) toRemove.add(propKey);
                        }
                    }
                    for (String propKey : toRemove) p.remove(propKey);
                }
                if (!this.isRemoved) for (String s : this.root.keySet()) p.setProperty(path + s, this.root.get(s));
                p.store(new FileOutputStream(file), "FilePreferences");
            } catch (IOException e) {
                throw new BackingStoreException(e);
            }
        }
    }
}
