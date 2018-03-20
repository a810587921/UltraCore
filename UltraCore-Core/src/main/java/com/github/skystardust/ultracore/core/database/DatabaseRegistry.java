package com.github.skystardust.ultracore.core.database;

import com.github.skystardust.ultracore.core.exceptions.ConfigurationException;
import com.github.skystardust.ultracore.core.exceptions.DatabaseInitException;

import java.util.HashMap;
import java.util.Map;

public class DatabaseRegistry {
    private static Map<String, DatabaseManagerBase> pluginDatabaseManagerBaseMap = new HashMap<>();

    public static void registerPluginDatabase(String plugin, DatabaseManagerBase databaseManagerBase) {
        DatabaseRegistry.pluginDatabaseManagerBaseMap.put(plugin, databaseManagerBase);
    }

    public static void reloadPluginDatabase(String databaseName) throws DatabaseInitException, ConfigurationException {
        if (databaseName == null) {
            throw new DatabaseInitException("未找到该数据库");
        }
        DatabaseManagerBase databaseManagerBase = pluginDatabaseManagerBaseMap.get(databaseName);
        if (databaseManagerBase == null) {
            throw new DatabaseInitException("未找到该数据库");
        }
        if (!DatabaseListenerRegistry.hasDatabase(databaseName)) {
            throw new DatabaseInitException("该数据库不支持重载!");
        }
        DatabaseManagerBase reloaded = databaseManagerBase.reloadDatabase();
        registerPluginDatabase(databaseName, reloaded);
        DatabaseListenerRegistry.getPluginDatabaseListener(databaseName).ifPresent(databaseListener -> {
            databaseListener.notifyRealod(reloaded);
        });
    }
}
