package com.github.skystardust.ultracore.configuration;

import com.github.skystardust.ultracore.UltraCore;
import com.github.skystardust.ultracore.utils.FileUtils;
import com.google.common.collect.Maps;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Data
public class ConfigurationManager {
    private Plugin ownPlugin;
    private Map<String, Object> configurationModels;

    public ConfigurationManager(Plugin ownPlugin) {
        this.ownPlugin = ownPlugin;
        this.configurationModels = new HashMap<>();
    }

    public ConfigurationManager registerConfiguration(String name, Supplier o) {
        this.configurationModels.put(name, o.get());
        return this;
    }

    public Map<String, Object> init() {
        if (!ownPlugin.getDataFolder().exists()) {
            ownPlugin.getDataFolder().mkdirs();
        }
        UltraCore.sendMessage(Bukkit.getConsoleSender(), "正在初始化 " + ownPlugin.getName() + " 的配置文件!");
        HashMap<String, Object> objectObjectHashMap = Maps.newHashMap();
        configurationModels.forEach((fileName, result) -> {
            UltraCore.sendMessage(Bukkit.getConsoleSender(), "初始化配置文件 " + fileName + " 中,请稍候..!");
            File file = new File(ownPlugin.getDataFolder(), fileName + ".conf");
            if (!file.exists()) {
                UltraCore.sendMessage(Bukkit.getConsoleSender(), "正在创建配置文件 " + fileName + " 的模板.");
                FileUtils.writeFileContent(file, FileUtils.GSON.toJson(result));
                UltraCore.sendMessage(Bukkit.getConsoleSender(), "创建 " + fileName + " 的模板完成!");
            }
            UltraCore.sendMessage(Bukkit.getConsoleSender(), "正在读取配置文件 " + fileName + " 的现有存档.");
            objectObjectHashMap.put(fileName, FileUtils.GSON.fromJson(FileUtils.readFileContent(file), result.getClass()));
            UltraCore.sendMessage(Bukkit.getConsoleSender(), "读取配置文件 " + fileName + " 已成功.");
        });
        return objectObjectHashMap;
    }
}
