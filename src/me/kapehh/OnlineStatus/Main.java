package me.kapehh.OnlineStatus;

import me.kapehh.main.pluginmanager.config.EventPluginConfig;
import me.kapehh.main.pluginmanager.config.EventType;
import me.kapehh.main.pluginmanager.config.PluginConfig;
import me.kapehh.main.pluginmanager.db.PluginDatabase;
import me.kapehh.main.pluginmanager.db.PluginDatabaseInfo;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

/**
 * Created by Karen on 17.03.2015.
 */
public class Main extends JavaPlugin {
    PluginDatabase dbHelper;
    PluginDatabaseInfo dbInfo = new PluginDatabaseInfo();;

    @EventPluginConfig(EventType.LOAD)
    public void onConfigLoad(FileConfiguration cfg) {
        if (dbHelper != null) {
            try {
                dbHelper.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        dbInfo.setIp(cfg.getString("connect.ip", ""));
        dbInfo.setDb(cfg.getString("connect.db", ""));
        dbInfo.setLogin(cfg.getString("connect.login", ""));
        dbInfo.setPassword(cfg.getString("connect.password", ""));
        dbInfo.setTable(cfg.getString("connect.table", ""));

        // коннектимся
        try {
            // создаем экземпляр класса для соединения с БД
            dbHelper = new PluginDatabase(
                    dbInfo.getIp(),
                    dbInfo.getDb(),
                    dbInfo.getLogin(),
                    dbInfo.getPassword()
            );

            dbHelper.connect();
            getLogger().info("Success connect to MySQL!");
        } catch (SQLException e) {
            dbHelper = null;
            e.printStackTrace();
        }

        getLogger().info("Complete!");
    }

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("PluginManager") == null) {
            getLogger().info("PluginManager not found!!!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        PluginConfig pluginConfig = new PluginConfig(this);
        pluginConfig.addEventClasses(this).setup().loadData();

        getServer().getPluginManager().registerEvents(new MainListener(this), this);
    }

    @Override
    public void onDisable() {
        if (dbHelper != null) {
            try {
                dbHelper.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public PluginDatabase getDbHelper() {
        return dbHelper;
    }
}
