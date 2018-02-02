package com.github.skystardust.ultracore;

import com.github.skystardust.ultracore.database.DatabaseListenerRegistry;
import com.github.skystardust.ultracore.database.DatabaseRegistry;
import com.github.skystardust.ultracore.exceptions.ConfigurationException;
import com.github.skystardust.ultracore.exceptions.DatabaseInitException;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public final class UltraCore extends JavaPlugin {
    @Getter
    private static UltraCore ultraCore;

    @Override
    public void onLoad() {
        UltraCore.ultraCore = this;
    }

    @Override
    public void onEnable() {
        PluginCommand uc = getServer().getPluginCommand("uc");
        uc.setTabCompleter((sender, command, alias, args) -> {
            if (args.length == 1) {
                return Arrays.asList("reload", "list");
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("reload")) {
                return new ArrayList<>(DatabaseListenerRegistry.getPluginDatabaseListenerMap()
                        .keySet());
            }
            return Collections.emptyList();
        });
        uc.setExecutor((sender, command, label, args) -> {
            if (!sender.isOp()) {
                return true;
            }
            if (args.length == 0) {
                sendMessage(sender, "请输入参数");
                return true;
            }
            if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "list":
                        DatabaseListenerRegistry.getPluginDatabaseListenerMap()
                                .keySet()
                                .forEach(s -> sendMessage(sender, "* " + s));
                        break;
                    case "reload":
                        sendMessage(sender, "uc reload <Plugin Name>");
                        break;
                }
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "reload":
                    try {
                        DatabaseRegistry.reloadPluginDatabase(args[1]);
                        sendMessage(sender, "重载数据库 " + args[1] + " 成功!");
                    } catch (DatabaseInitException | ConfigurationException e) {
                        sendMessage(sender, "错误! " + e.getMessage());
                    }
                    break;
            }
            return true;
        });
    }

    private void sendMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage("[UltraCore] " + message);
    }
}
