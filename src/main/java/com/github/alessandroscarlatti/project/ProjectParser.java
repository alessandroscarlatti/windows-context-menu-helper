package com.github.alessandroscarlatti.project;

import com.github.alessandroscarlatti.command.Command;
import com.github.alessandroscarlatti.command.CommandParser;
import com.github.alessandroscarlatti.menu.Menu;
import com.github.alessandroscarlatti.menu.MenuParser;
import com.github.alessandroscarlatti.windows.ContextMenuItem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.util.stream.Collectors.toList;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class ProjectParser {

    private Path projectDir;  // the directory containing this project
    private ProjectConfig projectConfig;  // the config read from project.properties

    public ProjectParser(Path projectDir) {
        this.projectDir = projectDir;
    }

    // build a list of context menu items found within the project dir.
    public Project parseProject() {

        // find the dirs in the project that contain menus or commands
        List<Path> menuDirs = findMenuDirs(projectDir);
        List<Path> commandDirs = findCommandDirs(projectDir);

        List<ContextMenuItem> contextMenuItems = new ArrayList<>();

        // now parse these into context menu items
        for (Path menuDir : menuDirs) {
            Menu menu = new MenuParser(menuDir).parseMenu();
            contextMenuItems.add(menu);
        }

        for (Path commandDir : commandDirs) {
            Command command = new CommandParser(commandDir).parseCommand();
            contextMenuItems.add(command);
        }

        // sort the items, since they are still unordered
        sortContextMenuItems(contextMenuItems);

        // create the project
        Project project = new Project();
        project.setContextMenuItems(contextMenuItems);
        return project;
    }

    public static void sortContextMenuItems(List<ContextMenuItem> items) {
        // do nothing for now
    }

    // find dirs containing Menu_
    public static List<Path> findMenuDirs(Path baseDir) {
        try {
            List<Path> menuDirs = new ArrayList<>();
            for (Path dir : Files.list(baseDir).collect(toList())) {
                if (Files.isDirectory(dir) && dir.getFileName().toString().contains("Menu_")) {
                    System.out.println("Found menu dir " + dir);
                    menuDirs.add(dir);
                }
            }
            return menuDirs;
        } catch (Exception e) {
            throw new RuntimeException("Error iterating dir " + baseDir, e);
        }
    }

    // find dirs containing Menu_
    public static List<Path> findCommandDirs(Path baseDir) {
        try {
            List<Path> commandDirs = new ArrayList<>();
            for (Path dir : Files.list(baseDir).collect(toList())) {
                if (Files.isDirectory(dir) && dir.getFileName().toString().contains("Command_")) {
                    System.out.println("Found command dir " + dir);
                    commandDirs.add(dir);
                }
            }
            return commandDirs;
        } catch (Exception e) {
            throw new RuntimeException("Error iterating dir " + baseDir, e);
        }
    }

    public static Path findIconFile(Path baseDir) {
        return findFirstFileByExample(baseDir, ".ico");
    }

    public static Path findConfigFile(Path baseDir) {
        return findFirstFileByExample(baseDir, ".properties");
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
            String extension = example.replace(".+(\\..+?$)", "$1");
            for (Path file : Files.list(baseDir).collect(toList())) {
                if (Files.isRegularFile(file) && file.getFileName().toString().endsWith(extension)) {
                    System.out.println("Found file " + file + " with extension " + extension);
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
}
