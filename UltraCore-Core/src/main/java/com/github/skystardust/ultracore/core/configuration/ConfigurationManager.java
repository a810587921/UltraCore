package com.github.skystardust.ultracore.core.configuration;

import com.github.skystardust.ultracore.core.database.PluginInstance;
import com.github.skystardust.ultracore.core.utils.FileUtils;
import lombok.Data;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Data
public class ConfigurationManager {
    private PluginInstance ownPlugin;
    private Map<String, Object> configurationModels;
    private Map<String, Object> data;

    public ConfigurationManager(PluginInstance ownPlugin) {
        this.ownPlugin = ownPlugin;
        this.configurationModels = new HashMap<>();
        this.data = new HashMap<>();
    }

    public ConfigurationManager registerConfiguration(String name, Supplier o) {
        this.configurationModels.put(name, o.get());
        return this;
    }

    public ConfigurationClassSetter init(Class clazz, @Nullable Object o) {
        if (!ownPlugin.getDataFolder().exists()) {
            ownPlugin.getDataFolder().mkdirs();
        }
        ownPlugin.getLogger().info("正在初始化 " + ownPlugin.getName() + " 的配置文件!");
        configurationModels.forEach((fileName, result) -> {
            ownPlugin.getLogger().info("初始化配置文件 " + fileName + " 中,请稍候..!");
            File file = new File(ownPlugin.getDataFolder(), fileName + ".conf");
            if (!file.exists()) {
                ownPlugin.getLogger().info("正在创建配置文件 " + fileName + " 的模板.");
                FileUtils.writeFileContent(file, FileUtils.GSON.toJson(result));
                ownPlugin.getLogger().info("创建 " + fileName + " 的模板完成!");
            }
            ownPlugin.getLogger().info("正在读取配置文件 " + fileName + " 的现有存档.");
            data.put(fileName, FileUtils.GSON.fromJson(FileUtils.readFileContent(file), result.getClass()));
            ownPlugin.getLogger().info("读取配置文件 " + fileName + " 已成功.");
        });
        ownPlugin.getLogger().info("初始化 " + ownPlugin.getName() + " 已全部成功!");
        return ConfigurationClassSetter.builder()
                .classToSet(clazz)
                .classInstance(o)
                .configurationData(data).build();
    }

    public void saveFile(String name) {
        Object o = configurationModels.get(name);
        if (o != null) {
            File file = new File(ownPlugin.getDataFolder(), name + ".conf");
            FileUtils.writeFileContent(file, FileUtils.GSON.toJson(data.get(name)));
        }
    }

    public void saveFiles() {
        data.forEach((key, value) -> saveFile(key));
    }
}
