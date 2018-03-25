package com.github.skystardust.ultracore.bukkit;

import com.github.skystardust.ultracore.bukkit.commands.MainCommandSpec;
import com.github.skystardust.ultracore.bukkit.commands.SubCommandSpec;
import com.github.skystardust.ultracore.core.PluginInstance;
import com.github.skystardust.ultracore.core.database.DatabaseListenerRegistry;
import com.github.skystardust.ultracore.core.database.DatabaseRegistry;
import com.github.skystardust.ultracore.core.exceptions.ConfigurationException;
import com.github.skystardust.ultracore.core.exceptions.DatabaseInitException;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

public final class UltraCore extends JavaPlugin implements PluginInstance {
    @Getter
    private static UltraCore ultraCore;

    public static void sendMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage("[UltraCore] " + message);
    }

    @Override
    public void onLoad() {
        UltraCore.ultraCore = this;
    }

    @Override
    public void onEnable() {
        MainCommandSpec.newBuilder()
                .addAlias("test")
                .addAlias("t")
                .withDescription("test command")
                .withCommandSpecExecutor((commandSender, args) -> {
                    commandSender.sendMessage("test command execute");
                    return true;
                })
                .childCommandSpec(SubCommandSpec.newBuilder()
                        .addAlias("arg1")
                        .addAlias("a1")
                        .withDescription("test arg1")
                        .withPermission("permission")
                        .childCommandSpec(SubCommandSpec.newBuilder()
                                .addAlias("arg2")
                                .addAlias("a2")
                                .withDescription("test arg2")
                                .withPermission("permission")
                                .withCommandSpecExecutor((commandSender, args) -> {
                                    commandSender.sendMessage("test args2");
                                    return true;
                                })
                                .build())
                        .withCommandSpecExecutor((commandSender, args) -> {
                            commandSender.sendMessage("test args1");
                            return true;
                        })
                        .build())
                .build()
                .register();
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

    @Override
    public Logger getPluginLogger() {
        return getLogger();
    }
}
