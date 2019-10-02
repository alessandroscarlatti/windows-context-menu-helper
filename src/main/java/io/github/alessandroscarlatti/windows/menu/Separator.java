package io.github.alessandroscarlatti.windows.menu;

import io.github.alessandroscarlatti.windows.reg.RegSpec;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class Separator implements ContextMenuItem {

    @Override
    public RegSpec getRegSpec() {
        // there is no reg spec right now for a separator
        // since we won't allow it to be a root level context menu item
        return null;
    }
}
