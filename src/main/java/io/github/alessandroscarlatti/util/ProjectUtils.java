package io.github.alessandroscarlatti.util;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

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
}
