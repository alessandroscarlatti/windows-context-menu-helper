package io.github.alessandroscarlatti;

import io.github.alessandroscarlatti.project.Project;
import io.github.alessandroscarlatti.project.ProjectContext;
import io.github.alessandroscarlatti.project.ProjectParser;
import io.github.alessandroscarlatti.model.reg.RegExportUtil;
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
 * @since Saturday, 9/28/2019
 */
public class WindowsContextMenuHelper {

    private static final Logger log = LoggerFactory.getLogger(WindowsContextMenuHelper.class);

    public static void main(String[] args) {
        try {
            String strTask = reqProperty("cmh.project.task");
            Path projectDir = Paths.get(reqProperty("cmh.project.dir")).toAbsolutePath().normalize();

            // set up the project context
            ProjectContext context = new ProjectContext();
            context.setProjectDir(projectDir);
            context.setRegExportUtil(new RegExportUtil(projectDir));
            context.setSyncDir(projectDir.resolve("Sync"));

            // parse the project
            log.info("Parsing project in dir " + projectDir);
            ProjectParser projectParser = new ProjectParser(context);
            Project project = projectParser.parseProject();

            // build the reg specs.
            project.buildRegSpecs();

            // run the correct bat based on the specified task
            if (strTask.equals("sync")) {
                // This actually builds a Sync_ dir with bats
                project.executeSync();
            }

            if (strTask.equals("uninstall")) {
                // This actually builds an Uninstall dir with bats
                project.executeUninstall();
            }

            if (strTask.equals("generate")) {
                // This actually builds a Sync_ dir with bats
                project.executeGenerate();
            }
        } catch (Exception e) {
            log.error("Error running task.", e);
            throw e;
        }
    }

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
