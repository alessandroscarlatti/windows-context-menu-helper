package io.github.alessandroscarlatti.windows.menu;

import io.github.alessandroscarlatti.windows.reg.AbstractRegSpec;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public interface ContextMenuItem {

    // get reg spec to use for this item
    AbstractRegSpec getRegSpec();

    // get name to use, eg, a dir "Command_Some Command" => "Command_Some Command"
    String getName();
}
