package io.github.alessandroscarlatti.windows.reg;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alessandro Scarlatti
 * @since Friday, 10/4/2019
 */
public class RegExportUtil {

    private Path workingDir; // the temp dir where the restore point files are written during compilation
    private int timeoutMs = 60000;  // how long to wait max for the reg task to complete

    public RegExportUtil(Path workingDir) {
        this.workingDir = workingDir;
    }

    public String exportToString(List<RegKey> regKeys) {
        // export all the reg keys as one reg script
        System.out.println("Exporting " + regKeys.size() + " reg key(s)");

        // incrementally build script, one key at a time
        StringBuilder sb = new StringBuilder();
        for (RegKey regKey : regKeys) {
            String script = exportToString(regKey);
            sb.append(script);
            sb.append("\n");
        }
        return sb.toString();
    }

    private String exportToString(RegKey regKey) {
        try {
            System.out.println("Exporting reg key " + regKey);
            // build params
            // use the reg key name, sanitized
            Map<String, Object> map = new HashMap<>();
            String fileName = regKey.getLongKeyName().replace("\\", "-");
            Path file = workingDir.resolve(fileName);
            map.put("file", file);

            CommandLine cmdLine = new CommandLine("reg");
            cmdLine.addArgument("export");
            cmdLine.addArgument(regKey.getLongKeyName());
            cmdLine.addArgument("${file}");
            cmdLine.setSubstitutionMap(map);
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(0);
            ExecuteWatchdog watchdog = new ExecuteWatchdog(timeoutMs);
            executor.setWatchdog(watchdog);
            executor.execute(cmdLine);

            // read the reg script from the export file
            String script = new String(Files.readAllBytes(file));

            // delete the file
            Files.delete(file);

            return script;
        } catch (IOException e) {
            throw new RuntimeException("Error exporting reg key " + regKey, e);
        }
    }
}
