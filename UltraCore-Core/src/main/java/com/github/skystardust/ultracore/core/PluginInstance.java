package com.github.skystardust.ultracore.core;

import java.io.File;
import java.util.logging.Logger;

public interface PluginInstance {
    Logger getLogger();

    String getName();

    File getDataFolder();
}
