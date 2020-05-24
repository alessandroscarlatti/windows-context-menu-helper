package io.github.alessandroscarlatti;

import io.github.alessandroscarlatti.project.Project;
import io.github.alessandroscarlatti.project.ProjectContext;
import io.github.alessandroscarlatti.project.ProjectParser;
import io.github.alessandroscarlatti.windows.reg.RegExportUtil;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
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
            // This actually builds a Sync_ dir with bats
            project.buildRegSpecs();

            // run the correct bat based on the specified task
            if (strTask.equals("sync")) {
                project.executeSync();
            }

            if (strTask.equals("uninstall")) {
                project.executeUninstall();
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
            log.info("Executing " + bat);

            CommandLine cmdLine = new CommandLine(bat.toString());
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(0);
            ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
            executor.setWatchdog(watchdog);
            executor.execute(cmdLine);
        } catch (Exception e) {
            throw new RuntimeException("Error executing bat " + bat, e);
        }
    }
}
