package io.github.alessandroscarlatti;

import io.github.alessandroscarlatti.parser.ProjectParser;
import io.github.alessandroscarlatti.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

import static io.github.alessandroscarlatti.util.ProjectUtils.reqProperty;

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
//            ProjectContext context = new ProjectContext();
//            context.setProjectDir(projectDir);  // handled now in the project parser
//            context.setRegExportUtil(new RegExportUtil(projectDir));  // handled now in the project parser
//            context.setSyncDir(projectDir.resolve("Sync"));  // handled now in the project parser

            // parse the project
            log.info("Parsing project in dir " + projectDir);
            ProjectParser projectParser = new ProjectParser(projectDir);

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
}
