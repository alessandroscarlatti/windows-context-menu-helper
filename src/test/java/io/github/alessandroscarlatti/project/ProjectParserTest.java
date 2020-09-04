package io.github.alessandroscarlatti.project;

import io.github.alessandroscarlatti.util.RegExportUtil;
import io.github.alessandroscarlatti.parser.ProjectParser;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class ProjectParserTest {

    @Test
    public void testProjectParser() {
        ProjectParser projectParser = new ProjectParser(Paths.get("TestProjects/TestProject1"));
        Project project = projectParser.parseProject();
        project.setRegExportUtil(new RegExportUtil(Paths.get("sandbox")));

        System.out.println("done");
    }

    @Test
    public void testMenuRegSpec() {
        ProjectParser projectParser = new ProjectParser(Paths.get("TestProjects/TestProject1"));
        Project project = projectParser.parseProject();
        project.setRegExportUtil(new RegExportUtil(Paths.get("sandbox")));
        project.setSyncDir(project.getProjectDir().resolve("Sync"));

        project.buildRegSpecs();
        project.exportRegSpecs(Paths.get("sandbox/Sync_"));

        System.out.println("done");
    }

    @Test
    public void testMenuRegSpec2() {
        ProjectParser projectParser = new ProjectParser(Paths.get("TestProjects/TestProject2"));
        Project project = projectParser.parseProject();
        project.setRegExportUtil(new RegExportUtil(Paths.get("sandbox")));
        project.setSyncDir(project.getProjectDir().resolve("Sync"));

        project.buildRegSpecs();
        System.out.println("done");
    }
}
