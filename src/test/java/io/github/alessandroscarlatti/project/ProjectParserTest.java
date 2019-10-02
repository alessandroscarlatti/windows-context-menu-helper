package io.github.alessandroscarlatti.project;

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

        System.out.println("done");
    }

    @Test
    public void testMenuRegSpec() {
        ProjectParser projectParser = new ProjectParser(Paths.get("TestProjects/TestProject1"));
        Project project = projectParser.parseProject();
        project.buildRegSpecs();
        project.exportRegSpecs();

        System.out.println("done");
    }
}
