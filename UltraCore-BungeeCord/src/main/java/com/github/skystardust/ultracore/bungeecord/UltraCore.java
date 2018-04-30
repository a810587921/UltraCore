package com.github.skystardust.ultracore.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;

public final class UltraCore extends Plugin {
    private static UltraCore ultraCore;

    public static UltraCore getUltraCore() {
        return ultraCore;
    }

    @Override
    public void onEnable() {
        UltraCore.ultraCore = this;
    }
}
