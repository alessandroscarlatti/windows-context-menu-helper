package io.github.alessandroscarlatti.windows.reg;

import io.github.alessandroscarlatti.menu.MenuRegSpec;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.alessandroscarlatti.project.Project.fileTimestamp;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * @author Alessandro Scarlatti
 * @since Friday, 10/4/2019
 */
public class RegExportUtil {

    private static final Logger log = LoggerFactory.getLogger(RegExportUtil.class);

    private Path workingDir; // the temp dir where the restore point files are written during compilation
    private int timeoutMs = 60000;  // how long to wait max for the reg task to complete
    private boolean deleteWorkingFiles = true;  // whether or not to delete the raw reg export files
    private String regExportEncoding = "utf-16";  // the encoding to use when reading the raw reg export file

    public RegExportUtil(Path workingDir) {
        this.workingDir = workingDir;
    }

    public RegExportUtil(Path workingDir, int timeoutMs, boolean deleteWorkingFiles, String regExportEncoding) {
        this.workingDir = workingDir;
        this.timeoutMs = timeoutMs;
        this.deleteWorkingFiles = deleteWorkingFiles;
        this.regExportEncoding = regExportEncoding;
    }

    public String exportToString(RegKey regKey) {
        return exportToString(singletonList(regKey));
    }

    public String exportToString(List<RegKey> regKeys) {
        // export all the reg keys as one reg script
        log.info("Exporting " + regKeys.size() + " reg key(s)");

        // incrementally build script, one key at a time
        StringBuilder sb = new StringBuilder();
        for (RegKey regKey : regKeys) {
            if (regKeyExists(regKey)) {
                String script = exportToStringInternal(regKey);
                sb.append(script);
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private boolean regKeyExists(RegKey regKey) {
        try {
            log.info("Querying if reg key " + regKey + " exists");

            CommandLine cmdLine = new CommandLine("reg");
            cmdLine.addArgument("query");
            cmdLine.addArgument(regKey.getLongKeyName());
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValues(new int[]{0, 1});
            ExecuteWatchdog watchdog = new ExecuteWatchdog(timeoutMs);
            executor.setWatchdog(watchdog);
            int exitCode = executor.execute(cmdLine);

            return exitCode == 0;
        } catch (IOException e) {
            throw new RuntimeException("Error determining if reg key " + regKey + " exists", e);
        }
    }

    public List<RegKey> getChildKeys(RegKey parentKey) {
        try {
            log.info("Querying child keys for " + parentKey);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(baos);

            CommandLine cmdLine = new CommandLine("reg");
            cmdLine.addArgument("query");
            cmdLine.addArgument(parentKey.getLongKeyName());
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValues(new int[]{0, 1});
            executor.setStreamHandler(streamHandler);
            ExecuteWatchdog watchdog = new ExecuteWatchdog(timeoutMs);
            executor.setWatchdog(watchdog);
            int exitCode = executor.execute(cmdLine);

            if (exitCode == 0) {
                // the key exists, parse the output
                String output = baos.toString();
                String[] lines = output.split(System.getProperty("line.separator"));
                List<RegKey> regKeys = new ArrayList<>();
                for (String line : lines) {
                    // ignore empty lines
                    if (!line.isEmpty())
                        regKeys.add(new RegKey(line.trim()));
                }
                return regKeys;
            } else {
                // the key does not exist, return an empty list
                return emptyList();
            }

        } catch (IOException e) {
            throw new RuntimeException("Error querying child keys for" + parentKey, e);
        }
    }

    private String exportToStringInternal(RegKey regKey) {
        try {
            // build params
            // use the reg key name, sanitized
            Map<String, Object> map = new HashMap<>();
            String fileName = regKey.getLongKeyName().replace("\\", "-");
            Path file = workingDir.resolve(fileName + "-" + fileTimestamp() + ".reg").toAbsolutePath();
            map.put("file", file);

            log.info("Exporting reg key " + regKey + " to " + file);

            CommandLine cmdLine = new CommandLine("reg");
            cmdLine.addArgument("export");
            cmdLine.addArgument(regKey.getLongKeyName());
            cmdLine.addArgument("${file}");
            cmdLine.addArgument("/y");  // don't ask if we want to overwrite the file
            cmdLine.setSubstitutionMap(map);
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(0);
            ExecuteWatchdog watchdog = new ExecuteWatchdog(timeoutMs);
            executor.setWatchdog(watchdog);
            executor.execute(cmdLine);

            // read the reg script from the export file
            String script = new String(Files.readAllBytes(file), regExportEncoding);

            // optionally delete the file
            if (deleteWorkingFiles)
                Files.delete(file);

            return script;
        } catch (IOException e) {
            throw new RuntimeException("Error exporting reg key " + regKey, e);
        }
    }

    public String installToString(RegKey regKey) {
        return installToString(singletonList(regKey));
    }

    public String installToString(List<RegKey> regKeys) {
        return RegKey.exportKeys(regKeys);
    }

    public String uninstallToString(RegKey regKey) {
        return uninstallToString(singletonList(regKey));
    }

    public String uninstallToString(List<RegKey> regKeys) {
        // for example: [HKEY_CLASSES_ROOT\Directory\Background\shell\TestMenu2Override.TestMenu2]
        // becomes: [-HKEY_CLASSES_ROOT\Directory\Background\shell\TestMenu2Override.TestMenu2]
        // In .reg scripts, these are the only brackets at the beginning of the line.

        // todo this is going to need to be much smarter, because it needs to be able to delete orphaned keys.
        // Orphaned keys would be left over from a previous build.
        // So we will need to query the registry in all the known places for any keys that have the
        // desired prefix in the right locations.

        return RegKey.exportKeys(regKeys).replaceAll("(?m)^\\[", "[-");
    }
}
