package net.sf.jremoterun.utilities.nonjdk.shell.core;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

@CompileStatic
public class DummyPrefs extends Preferences {
    public Properties getProps() {
        return props;
    }


    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public Properties props = new Properties();
    public Preferences nativePrefs;

    @Override
    public void put(String key, String value) {
        nativePrefs.put(key, value);
    }

    @Override
    public String get(String key, String def3) {
        if(props.containsKey(key)){
            return props.get(key)
        }
        return nativePrefs.get(key, def3);
    }

    @Override
    public void remove(String key) {
        nativePrefs.remove(key);
    }

    @Override
    public void clear() throws BackingStoreException {
        nativePrefs.clear();
    }

    @Override
    public void putInt(String key, int value) {
        nativePrefs.putInt(key, value);
    }

    @Override
    public int getInt(String key, int def3) {
        return nativePrefs.getInt(key, def3);
    }

    @Override
    public void putLong(String key, long value) {
        nativePrefs.putLong(key, value);
    }

    @Override
    public long getLong(String key, long def3) {
        return nativePrefs.getLong(key, def3);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        nativePrefs.putBoolean(key, value);
    }

    @Override
    public boolean getBoolean(String key, boolean def3) {
        return nativePrefs.getBoolean(key, def3);
    }

    @Override
    public void putFloat(String key, float value) {
        nativePrefs.putFloat(key, value);
    }

    @Override
    public float getFloat(String key, float def3) {
        return nativePrefs.getFloat(key, def3);
    }

    @Override
    public void putDouble(String key, double value) {
        nativePrefs.putDouble(key, value);
    }

    @Override
    public double getDouble(String key, double def3) {
        return nativePrefs.getDouble(key, def3);
    }

    @Override
    public void putByteArray(String key, byte[] value) {
        nativePrefs.putByteArray(key, value);
    }

    @Override
    public byte[] getByteArray(String key, byte[] def3) {
        return nativePrefs.getByteArray(key, def3);
    }

    @Override
    public String[] keys() throws BackingStoreException {
        return nativePrefs.keys();
    }

    @Override
    public String[] childrenNames() throws BackingStoreException {
        return nativePrefs.childrenNames();
    }

    @Override
    public Preferences parent() {
        return nativePrefs.parent();
    }

    @Override
    public Preferences node(String pathName) {
        return nativePrefs.node(pathName);
    }

    @Override
    public boolean nodeExists(String pathName) throws BackingStoreException {
        return nativePrefs.nodeExists(pathName);
    }

    @Override
    public void removeNode() throws BackingStoreException {
        nativePrefs.removeNode();
    }

    @Override
    public String name() {
        return nativePrefs.name();
    }

    @Override
    public String absolutePath() {
        return nativePrefs.absolutePath();
    }

    @Override
    public boolean isUserNode() {
        return nativePrefs.isUserNode();
    }

    @Override
    public String toString() {
        return nativePrefs.toString();
    }

    @Override
    public void flush() throws BackingStoreException {
        nativePrefs.flush();
    }

    @Override
    public void sync() throws BackingStoreException {
        nativePrefs.sync();
    }

    @Override
    public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
        nativePrefs.addPreferenceChangeListener(pcl);
    }

    @Override
    public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
        nativePrefs.removePreferenceChangeListener(pcl);
    }

    @Override
    public void addNodeChangeListener(NodeChangeListener ncl) {
        nativePrefs.addNodeChangeListener(ncl);
    }

    @Override
    public void removeNodeChangeListener(NodeChangeListener ncl) {
        nativePrefs.removeNodeChangeListener(ncl);
    }

    @Override
    public void exportNode(OutputStream os) throws IOException, BackingStoreException {
        nativePrefs.exportNode(os);
    }

    @Override
    public void exportSubtree(OutputStream os) throws IOException, BackingStoreException {
        nativePrefs.exportSubtree(os);
    }

}
