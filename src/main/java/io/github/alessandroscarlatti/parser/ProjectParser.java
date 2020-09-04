package io.github.alessandroscarlatti.parser;

import io.github.alessandroscarlatti.model.menu.Command;
import io.github.alessandroscarlatti.model.menu.ContextMenuItem;
import io.github.alessandroscarlatti.model.menu.Menu;
import io.github.alessandroscarlatti.project.Project;
import io.github.alessandroscarlatti.util.RegExportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
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

    private static final Logger log = LoggerFactory.getLogger(ProjectParser.class);

    private Path projectDir;  // the directory containing this project

    private static final String PROP_REG_ID = "project.reg.id";
    private static final String PROP_REG_ID_AUTO_GENERATE = "project.reg.id.autogenerate";
    private static final String PROP_PROJECT_NAME = "project.name";

    public ProjectParser(Path projectDir) {
        this.projectDir = projectDir;
    }

    // build a list of context menu items found within the project dir.
    public Project parseProject() {
        // parse the project config
        Project project = new Project();

        Properties projectProperties = overlayProperties(new Properties[]{
            defaultProjectProperties(), // hardcoded defaults
            userProjectProperties()     // overlay any user-provided properties
        });

        project.setProjectDir(projectDir);
        project.setRegExportUtil(new RegExportUtil(projectDir));
        project.setSyncDir(projectDir.resolve("Sync"));
        project.setRegId(projectProperties.getProperty(PROP_REG_ID));
        project.setOldRegId(projectProperties.getProperty(PROP_REG_ID));
        project.setAutoGenerateRegId(parseBoolean(projectProperties.getProperty(PROP_REG_ID_AUTO_GENERATE)));
        project.setProjectName(projectProperties.getProperty(PROP_PROJECT_NAME));

        // configure the actual project name
        buildActualProjectName(project);

        // optionally auto-generate a reg id
        optionallyAutoGenerateRegId(project);

        // find the dirs in the project that contain menus or commands
        List<Path> menuDirs = findMenuDirs(projectDir);
        List<Path> commandDirs = findCommandDirs(projectDir);

        List<ContextMenuItem> contextMenuItems = new ArrayList<>();

        // now parse these into context menu items
        for (Path menuDir : menuDirs) {
            Menu menu = new MenuParser(menuDir, project, null).parseMenu();
            contextMenuItems.add(menu);
        }

        for (Path commandDir : commandDirs) {
            Command command = new CommandParser(commandDir, project, null).parseCommand();
            contextMenuItems.add(command);
        }

        // sort the items, since they are still unordered
        sortContextMenuItems(contextMenuItems);

        // create the project
        project.setContextMenuItems(contextMenuItems);
        return project;
    }

    private Properties defaultProjectProperties() {
        Properties props = new Properties();
        props.setProperty(PROP_REG_ID_AUTO_GENERATE, "true");
        props.setProperty(PROP_REG_ID, "");  // blank so that it will print in the properties file if we have to generate it ourselves
        props.setProperty(PROP_PROJECT_NAME, "");  // blank so that by defualt will use project dir name
        return props;
    }

    private Properties userProjectProperties() {
        // can read from a .properties file (if exists)
        Path configFile = ProjectParser.findFirstFileByExample(projectDir, "project.properties");

        // user-provided properties file is not required
        if (configFile == null)
            throw new IllegalStateException("project.properties config file required.");
        else
            // parse the menu config file
            return ProjectParser.readPropertiesFile(configFile);
    }

    private void buildActualProjectName(Project project) {
        if (project.getProjectName().isEmpty()) {
            // the prop is empty so we need to build it based off of the project dir
            project.setProjectName(projectDir.getFileName().toString().replaceAll("\\s", ""));
        }
    }

    private void optionallyAutoGenerateRegId(Project project) {
        if (!project.getAutoGenerateRegId())
            return;

        log.info("Auto-Generating project registry ID.");
        String regId = projectDir.getFileName().toString().replaceAll("\\s", "");

        log.info("Using project reg ID " + regId);
        project.setRegId(regId);

//        // search the registry
//        MenuRegSpec menuRegSpec = new MenuRegSpec(null, projectContext);
//        CommandRegSpec commandRegSpec = new CommandRegSpec(null, projectContext);
//
//        for (int i = 0; /* no loop control */ ; i++) {
//            String tryId = projectConfig.getProjectName();
//            if (i > 0)
//                tryId = projectConfig.getProjectName() + i; // only use increment if original name is not found
//
//            if (i == 10)
//                throw new IllegalStateException("Could not find a unique reg id. Please rename the directory containing this project.");
//
//            // look for existing items with this name
//            List<RegKey> regKeys = new ArrayList<>();
//            regKeys.addAll(menuRegSpec.getAllRegKeysByPrefix(tryId));
//            regKeys.addAll(commandRegSpec.getAllRegKeysByPrefix(tryId));
//
//            if (regKeys.size() == 0) {
//                log.info("Using project reg ID " + tryId);
//
//                // nothing exists with this ID, so it's safe to use
//                projectConfig.setRegId(tryId);
//
//                // persist the actual reg ID into the project props file
//                setProperty(configFile, "project.reg.id", tryId);
//                return;
//            }
//
//            // otherwise...
//            // stuff already exists in the registry for this project
//            // so go around and keep looking.
//        }
    }

    public static void sortContextMenuItems(List<ContextMenuItem> items) {
        // do nothing for now
    }

    // find dirs containing Menu_
    public static List<Path> findMenuDirs(Path baseDir) {
        try {
            List<Path> menuDirs = new ArrayList<>();
            for (Path dir : Files.list(baseDir).collect(toList())) {
                // _ is a "commented out" folder
                if (Files.isDirectory(dir) &&
                    dir.getFileName().toString().contains("Menu_") &&
                    !dir.getFileName().toString().startsWith("_")) {
                    log.info("Found menu dir " + dir);
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
                // _ is a "commented out" folder
                if (Files.isDirectory(dir) &&
                    dir.getFileName().toString().contains("Command_") &&
                    !dir.getFileName().toString().startsWith("_")) {
                    log.info("Found command dir " + dir);
                    commandDirs.add(dir);
                }
            }
            return commandDirs;
        } catch (Exception e) {
            throw new RuntimeException("Error iterating dir " + baseDir, e);
        }
    }

    // find dirs containing Group_
    public static List<Path> findGroupDirs(Path baseDir) {
        try {
            List<Path> groupDirs = new ArrayList<>();
            for (Path dir : Files.list(baseDir).collect(toList())) {
                // _ is a "commented out" folder
                if (Files.isDirectory(dir) &&
                    dir.getFileName().toString().contains("Group_") &&
                    !dir.getFileName().toString().startsWith("_")) {
                    log.info("Found group dir " + dir);
                    groupDirs.add(dir);
                }
            }
            return groupDirs;
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
