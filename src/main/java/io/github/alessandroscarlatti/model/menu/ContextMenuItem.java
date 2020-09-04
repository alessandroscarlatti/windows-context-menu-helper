package io.github.alessandroscarlatti.model.menu;

import io.github.alessandroscarlatti.reg.RegSpec;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public interface ContextMenuItem {

    // get reg spec to use for this item
    RegSpec getRegSpec();

    // get name to use, eg, a dir "Command_Some Command" => "Command_Some Command"
    String getName();
}
