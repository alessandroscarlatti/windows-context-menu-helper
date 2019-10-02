package io.github.alessandroscarlatti.command;

import io.github.alessandroscarlatti.windows.reg.RegKey;
import io.github.alessandroscarlatti.windows.reg.RegSpec;
import io.github.alessandroscarlatti.windows.reg.RegValue;

import java.io.IOException;
import java.io.OutputStream;

import static io.github.alessandroscarlatti.windows.reg.RegType.REG_SZ;
import static java.util.Collections.singletonList;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 10/1/2019
 */
public class CommandRegSpec extends RegSpec {

    // The command we are building reg keys for
    private Command rootCommand;

    // This reg key will only exist if this command is directly in the system context menu.
    private RegKey hkeyClassesRootDirectoryBackgroundShell;

    public CommandRegSpec(Command rootCommand) {
        this.rootCommand = rootCommand;
    }

    @Override
    public void buildSpec() {
        // this command is in the system context menu since there is no parent.
        hkeyClassesRootDirectoryBackgroundShell = new RegKey("HKEY_CLASSES_ROOT\\Directory\\Background\\shell\\" + rootCommand.getRegName());
        hkeyClassesRootDirectoryBackgroundShell.addRegValue(new RegValue("MUIVerb", REG_SZ, rootCommand.getText()));

        RegKey commandRegKey = hkeyClassesRootDirectoryBackgroundShell.addChildRegKey("command");
        commandRegKey.addRegValue(new RegValue(null, REG_SZ, rootCommand.getBat().toAbsolutePath().toString()));

        // now build the install .reg file
        String regInstall = RegKey.exportKeys(singletonList(hkeyClassesRootDirectoryBackgroundShell));
        setRegInstall(regInstall);
    }

    @Override
    public void exportSpec(OutputStream os) throws IOException {
        os.write(getRegInstall().getBytes());
    }

    public RegKey getHkeyClassesRootDirectoryBackgroundShell() {
        return hkeyClassesRootDirectoryBackgroundShell;
    }
}
