package io.github.alessandroscarlatti;

import org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class WindowsContextMenuHelper {

    public static String resourceStr(String path) {
        try {
            return IOUtils.resourceToString(path, Charset.forName("utf-8"));
        } catch (Exception e) {
            throw new RuntimeException("Error reading resource at path " + path, e);
        }
    }
}
