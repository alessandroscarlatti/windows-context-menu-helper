package io.github.alessandroscarlatti.project;

import io.github.alessandroscarlatti.windows.reg.RegExportUtil;
import io.github.alessandroscarlatti.windows.reg.RegKey;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alessandro Scarlatti
 * @since Friday, 10/4/2019
 */
public class RegExportUtilTest {

    @Test
    public void testRegExportUtil() {
        RegKey regKey = new RegKey("HKEY_CLASSES_ROOT\\Directory\\Background\\shell\\HelloUniverse.HelloUniverse");
        RegExportUtil regExportUtil = new RegExportUtil(Paths.get("sandbox"));
        String script = regExportUtil.exportToString(regKey);
        System.out.println(script);
    }

    @Test
    public void testRegExportUtilTwoKeys() {
        RegKey regKey1 = new RegKey("HKEY_CLASSES_ROOT\\Directory\\Background\\shell\\HelloUniverse.HelloUniverse");
        RegKey regKey2 = new RegKey("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CommandStore\\shell\\TestMenu2Override.TestSubMenu1");
        RegExportUtil regExportUtil = new RegExportUtil(Paths.get("sandbox"), 2000, false, "utf-16");
        String script = regExportUtil.exportToString(Arrays.asList(regKey1, regKey2));
        System.out.println(script);
    }

    @Test
    public void testRegExportUtilGetChildKeys() {
        RegExportUtil regExportUtil = new RegExportUtil(Paths.get("sandbox"));
        List<RegKey> regKeys = regExportUtil.getChildKeys(new RegKey("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CommandStore\\shell\\"));
        System.out.println(regKeys);
    }
}
