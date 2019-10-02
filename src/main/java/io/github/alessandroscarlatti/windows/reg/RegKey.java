package io.github.alessandroscarlatti.windows.reg;

import java.util.*;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class RegKey {
    private String shortKeyName; // just the name of this key, relative to its parent, eg, SubMenu
    private String longKeyName; // long path of the key, eg, HKEY_CLASSES_ROOT\Directory\Background\shell\SubMenu
    private Map<String, RegValue> regValues = new HashMap<>(); // any reg values underneath this key, by value name
    private Map<String, RegKey> regKeys = new HashMap<>();  // any reg keys underneath this key, by key name

    public RegKey() {
    }

    public RegKey(String longKeyName) {
        setKeyName(longKeyName);
    }

    public RegKey addChildRegKey(String shortKeyName) {
        RegKey regKey = new RegKey(getLongKeyName() + "\\" + shortKeyName);
        addRegKey(regKey);
        return regKey;
    }

    public void setKeyName(String name) {
        // parse the key name
        String[] tokens = name.split("\\\\");
        shortKeyName = tokens[tokens.length - 1];
        longKeyName = name;
    }

    public void addRegValue(RegValue regValue) {
        regValues.put(regValue.getName(), regValue);
    }

    public void addRegKey(RegKey regKey) {
        regKeys.put(regKey.getShortKeyName(), regKey);
    }

    public static String exportKeys(List<RegKey> regKeys) {
        StringBuilder sb = new StringBuilder();
        sb.append("Windows Registry Editor Version 5.00");
        sb.append("\n\n");
        for (RegKey regKey : regKeys) {
            sb.append(regKey.export());
            sb.append("\n\n");
        }
        return sb.toString();
    }

    public String export() {
        // turn this key into a string of the format:
        // Windows Registry Editor Version 5.00
        //
        // [HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\showme4]
        // @="ShowMe4"
        // "Icon"="cmd.exe,0"
        //
        // [HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\showme4\command]
        // @="cmd.exe /c pause"

        StringBuilder sb = new StringBuilder();
        sb.append("[{key}]".replace("{key}", getLongKeyName()));
        sb.append("\n");
        addValues(sb);
        addSubKeys(sb);
        return sb.toString().trim();
    }

    private void addSubKeys(StringBuilder sb) {
        List<RegKey> sortedRegKeys = new ArrayList<>(regKeys.values());
        sortedRegKeys.sort(Comparator.comparing(RegKey::getLongKeyName));

        for (RegKey regKey : sortedRegKeys) {
            String regKeyExport = regKey.export();

            sb.append("\n");
            sb.append(regKeyExport);
            sb.append("\n");
        }
    }

    private void addValues(StringBuilder sb) {
        List<RegValue> sortedRegValues = new ArrayList<>(regValues.values());
        sortedRegValues.sort((o1, o2) -> {
            if (o1.getName() == null)
                return -1;
            if (o2.getName() == null)
                return +1;
            return o1.getName().compareTo(o2.getName());
        });

        for (RegValue regValue : sortedRegValues) {
            String name;
            if (regValue.getName() == null)
                name = "@";
            else
                name = "\"" + regValue.getName() + "\"";

            String setter = "{name}=\"{value}\""
                .replace("{name}", name)
                .replace("{value}", escapeData(regValue.getData()));

            sb.append(setter);
            sb.append("\n");
        }
    }

    private static String escapeData(String data) {
        return data.replace("\\", "\\\\");
    }

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

    public String getLongKeyName() {
        return longKeyName;
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
