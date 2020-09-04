package io.github.alessandroscarlatti.project;

import io.github.alessandroscarlatti.model.reg.RegExportUtil;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class ProjectParserTest {

    @Test
    public void testProjectParser() {
        ProjectContext context = new ProjectContext();
        context.setProjectDir(Paths.get("TestProjects/TestProject1"));
        context.setRegExportUtil(new RegExportUtil(Paths.get("sandbox")));

        ProjectParser projectParser = new ProjectParser(context);
        Project project = projectParser.parseProject();

        System.out.println("done");
    }

    @Test
    public void testMenuRegSpec() {
        ProjectContext context = new ProjectContext();
        context.setProjectDir(Paths.get("TestProjects/TestProject1"));
        context.setRegExportUtil(new RegExportUtil(Paths.get("sandbox")));
        context.setSyncDir(context.getProjectDir().resolve("Sync"));

        ProjectParser projectParser = new ProjectParser(context);
        Project project = projectParser.parseProject();
        project.buildRegSpecs();
        project.exportRegSpecs("Sync_");

        System.out.println("done");
    }

    @Test
    public void testMenuRegSpec2() {
        ProjectContext context = new ProjectContext();
        context.setProjectDir(Paths.get("TestProjects/TestProject2"));
        context.setRegExportUtil(new RegExportUtil(Paths.get("sandbox")));
        context.setSyncDir(context.getProjectDir().resolve("Sync"));

        ProjectParser projectParser = new ProjectParser(context);
        Project project = projectParser.parseProject();
        project.buildRegSpecs();

        System.out.println("done");
    }
}
