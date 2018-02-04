package com.github.skystardust.ultracore.database;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.github.skystardust.ultracore.UltraCore;
import com.github.skystardust.ultracore.configuration.SQLConfiguration;
import com.github.skystardust.ultracore.exceptions.ConfigurationException;
import com.github.skystardust.ultracore.exceptions.DatabaseInitException;
import com.github.skystardust.ultracore.utils.FileUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Setter
public class DatabaseManagerBase {
    private SQLConfiguration sqlConfiguration;
    private EbeanServer ebeanServer;
    private ExecutorService executorService;

    private Plugin ownerPlugin;
    private Class<?> modelClass;
    private String name;
    private String configurationFileName;

    private DatabaseManagerBase(Plugin plugin, SQLConfiguration sqlConfiguration, String configurationFileName) {
        this.ownerPlugin = plugin;
        this.sqlConfiguration = sqlConfiguration;
        this.configurationFileName = configurationFileName;
    }

    public static SQLConfiguration setupDatabase(Plugin plugin, String configurationFileName) throws ConfigurationException {
        UltraCore.getUltraCore().getLogger().info("开始初始化 " + plugin.getName() + " 的配置文件!");
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            File sqlConfig = new File(plugin.getDataFolder(), configurationFileName);
            if (!sqlConfig.exists()) {
                FileUtils.writeFileContent(sqlConfig, FileUtils.GSON.toJson(
                        SQLConfiguration.newBuilder()
                                .withUrl("jdbc:mysql://localhost:3306/database").withDriver("com.mysql.jdbc.Driver")
                                .withUsername("root")
                                .withPassword("pwd")
                                .build()
                ));
            }
            return FileUtils.GSON.fromJson(FileUtils.readFileContent(sqlConfig), SQLConfiguration.class);
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), e.getCause());
        }
    }

    public static DatabaseManagerBase createDatabaseManager(Plugin plugin, SQLConfiguration sqlConfiguration, String configurationFileName, Class modelClass, String name) throws DatabaseInitException {
        UltraCore.getUltraCore().getLogger().info("开始初始化 " + plugin.getName() + " 的 " + name + " 数据库!");
        return new DatabaseManagerBase(plugin, sqlConfiguration, configurationFileName).openConnection0(modelClass, name);
    }

    private DatabaseManagerBase openConnection0(Class<?> modelClass, String name) throws DatabaseInitException {
        try {
            this.modelClass = modelClass;
            this.name = name;
            this.executorService = Executors.newCachedThreadPool();
            DataSourceConfig dataSourceConfig = new DataSourceConfig();
            dataSourceConfig.setUsername(sqlConfiguration.getUsername());
            dataSourceConfig.setPassword(sqlConfiguration.getPassword());
            dataSourceConfig.setUrl(sqlConfiguration.getUrl());
            dataSourceConfig.setDriver(sqlConfiguration.getDriver());
            ServerConfig serverConfig = new ServerConfig();
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setUsername(sqlConfiguration.getUsername());
            hikariConfig.setPassword(sqlConfiguration.getPassword());
            hikariConfig.setJdbcUrl(sqlConfiguration.getUrl());
            hikariConfig.setDriverClassName(sqlConfiguration.getDriver());
            HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
            serverConfig.setName(name);
            serverConfig.addClass(modelClass);
            serverConfig.setDataSourceConfig(dataSourceConfig);
            serverConfig.setDataSource(hikariDataSource);
            Thread.currentThread().setContextClassLoader(modelClass.getClassLoader());
            this.ebeanServer = EbeanServerFactory.create(serverConfig);
        } catch (Exception e) {
            throw new DatabaseInitException(e.getMessage(), e.getCause());
        }
        try {
            ebeanServer.find(modelClass).setMaxRows(1).findUnique();
        } catch (Exception e) {
            DdlGenerator gen = SpiEbeanServer.class.cast(ebeanServer).getDdlGenerator();
            gen.runScript(false, gen.generateCreateDdl());
        }
        return this;
    }

    public DatabaseManagerBase reloadDatabase() throws ConfigurationException, DatabaseInitException {
        SQLConfiguration sqlConfiguration = DatabaseManagerBase.setupDatabase(ownerPlugin, configurationFileName);
        return DatabaseManagerBase.createDatabaseManager(ownerPlugin, sqlConfiguration, configurationFileName, modelClass, name);
    }
}
