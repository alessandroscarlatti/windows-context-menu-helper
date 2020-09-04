package io.github.alessandroscarlatti.util;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

import static java.util.stream.Collectors.toList;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 9/3/2020
 */
public class ProjectUtils {

    private static final Logger log = LoggerFactory.getLogger(ProjectUtils.class);

    public static String resourceStr(String path) {
        try {
            return IOUtils.resourceToString(path, Charset.forName("utf-8"));
        } catch (Exception e) {
            throw new RuntimeException("Error reading resource at path " + path, e);
        }
    }

    public static String reqProperty(String property) {
        String prop = System.getProperty(property);
        Objects.requireNonNull(prop, "Missing required property " + property);
        return prop;
    }

    public static void executeBat(Path bat) {
        try {
            // make sure to use absolute path to bat
            bat = bat.toAbsolutePath();
            CommandLine cmdLine = new CommandLine(getCmdPath());
            cmdLine.addArgument("/c");
            cmdLine.addArgument(bat.getFileName().toString());

            log.info("Executing " + cmdLine + " in " + bat.getParent());

            DefaultExecutor executor = new DefaultExecutor();
            executor.setWorkingDirectory(bat.getParent().toFile());
            executor.setExitValue(0);
            executor.execute(cmdLine);
            log.info("Finished executing " + bat);
        } catch (Exception e) {
            throw new RuntimeException("Error executing bat " + bat, e);
        }
    }

    private static String getCmdPath() {
        Path sysnativeCmd = Paths.get(System.getenv("windir") + "\\sysnative\\cmd.exe");
        if (Files.exists(sysnativeCmd)) {
            // this process must be 32-bit, so get access to the native system cmd
            return sysnativeCmd.toString();
        } else {
            // this process must be 64-bit, so cannot use sysnative
            return "cmd";
        }
    }

    public static Path findFile(Path baseDir, String name) {
        // return the file if it exists, otherwise return null
        try {
            Path file = baseDir.resolve(name);
            if (Files.exists(file))
                return file;
            else
                return null;
        } catch (Exception e) {
            throw new RuntimeException("Error finding file " + name + " + in dir " + baseDir, e);
        }
    }

    /**
     * Query the directory for the first file with a particular extension.
     *
     * @param baseDir tbe dir to query
     * @param example the example file (including the period), eg, "icon.ico"
     * @return the file if found. If only one file exists with the extension, returns that file.
     * If multiple files exist with the extension and none match the example file, returns null.
     */
    public static Path findFirstFileByExample(Path baseDir, String example) {
        // return the file if it exists, otherwise return null
        try {
            // if we can find the example file, use it
            Path exampleFile = findFile(baseDir, example);
            if (exampleFile != null)
                return exampleFile;

            // if we cannot find the example file,
            // search for the first file matching the extension.
            String extension = example.replaceAll(".+(\\..+?$)", "$1");
            for (Path file : Files.list(baseDir).collect(toList())) {
                if (Files.isRegularFile(file) && file.getFileName().toString().endsWith(extension)) {
                    log.info("Found file " + file + " with extension " + extension);
                    return file;
                }
            }

            // if we cannot find any files matching the extension
            // return null
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error finding file by example " + example + " + in dir " + baseDir, e);
        }
    }

    public static Properties readPropertiesFile(Path file) {
        try (InputStream is = new FileInputStream(file.toFile())) {
            Properties properties = new Properties();
            properties.load(is);
            return properties;
        } catch (Exception e) {
            throw new RuntimeException("Error reading properties file " + file, e);
        }
    }

    public static void setProperty(Path file, String property, String value) {
        // non-destructively edit a properties file
        // this is not a perfect implementation, but it works for simple text values
        try {
            String props = new String(Files.readAllBytes(file));
            props = props.replaceAll("(?m)^" + property.replace(".", "\\.") + "=.*$", property + "=" + value);
            Files.write(file, props.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Error editing properties file " + file, e);
        }
    }

    public static Properties overlayProperties(Properties[] arrProps) {
        // overlay each prop in the list on top of the previous
        Properties targetProps = new Properties();

        if (arrProps == null)
            return targetProps;

        for (Properties props : arrProps) {
            // ignore null properties objects
            if (props == null)
                continue;
            for (Object key : props.keySet()) {
                targetProps.put(key, props.get(key));
            }
        }

        return targetProps;
    }

    public static boolean parseBoolean(String val) {
        if (val == null)
            return false;

        if (val.trim().replaceAll("\\s", "s").isEmpty())
            return false;

        return Boolean.parseBoolean(val);
    }
}
