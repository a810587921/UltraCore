package com.github.skystardust.ultracore.nukkit;

import cn.nukkit.plugin.PluginBase;
import com.github.skystardust.ultracore.core.PluginInstance;

import java.util.logging.Logger;

public class UltraCore extends PluginBase implements PluginInstance {
    @Override
    public void onEnable() {
        System.out.println("Enable");
    }

    @Override
    public Logger getPluginLogger() {
        return Logger.getGlobal();
    }
}
