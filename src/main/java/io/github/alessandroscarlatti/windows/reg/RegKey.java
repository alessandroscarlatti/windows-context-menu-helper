package io.github.alessandroscarlatti.windows.reg;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class RegKey {
    private String shortKeyName; // just the name of this key, relative to its parent, eg, SubMenu
    private String longKeyName; // long path of the key, eg, HKEY_CLASSES_ROOT\Directory\Background\shell\SubMenu
    private Map<String, RegValue> regValues = new HashMap<>(); // any reg values underneath this key, by value name
    private Map<String, RegKey> regKeys = new HashMap<>();  // any reg keys underneath this key, by key name

    @Override
    public String toString() {
        return "RegKey{" +
            "shortKeyName='" + shortKeyName + '\'' +
            ", longKeyName='" + longKeyName + '\'' +
            ", regValues=" + regValues +
            ", regKeys=" + regKeys +
            '}';
    }

    public String getShortKeyName() {
        return shortKeyName;
    }

    public void setShortKeyName(String shortKeyName) {
        this.shortKeyName = shortKeyName;
    }

    public String getLongKeyName() {
        return longKeyName;
    }

    public void setLongKeyName(String longKeyName) {
        this.longKeyName = longKeyName;
    }

    public Map<String, RegValue> getRegValues() {
        return regValues;
    }

    public void setRegValues(Map<String, RegValue> regValues) {
        this.regValues = regValues;
    }

    public Map<String, RegKey> getRegKeys() {
        return regKeys;
    }

    public void setRegKeys(Map<String, RegKey> regKeys) {
        this.regKeys = regKeys;
    }
}
