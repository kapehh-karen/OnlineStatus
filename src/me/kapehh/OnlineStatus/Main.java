package me.kapehh.OnlineStatus;

import me.kapehh.OnlineStatus.queue.QueueUpdater;
import me.kapehh.main.pluginmanager.config.EventPluginConfig;
import me.kapehh.main.pluginmanager.config.EventType;
import me.kapehh.main.pluginmanager.config.PluginConfig;
import me.kapehh.main.pluginmanager.constants.ConstantSystem;
import me.kapehh.main.pluginmanager.db.PluginDatabase;
import me.kapehh.main.pluginmanager.db.PluginDatabaseInfo;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karen on 17.03.2015.
 */
public class Main extends JavaPlugin {
    PluginDatabase dbHelper;
    PluginDatabaseInfo dbInfo = new PluginDatabaseInfo();
    QueueUpdater queueUpdater;
    MainListener mainListener;

    @EventPluginConfig(EventType.LOAD)
    public void onConfigLoad(FileConfiguration cfg) {
        if (queueUpdater != null) {
            queueUpdater.cancel();
            queueUpdater = null;
            mainListener.setQueueUpdater(null);
        }
        if (dbHelper != null) {
            try {
                dbHelper.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbHelper = null;
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
            queueUpdater = new QueueUpdater(dbHelper);
            queueUpdater.runTaskTimerAsynchronously(this, ConstantSystem.ticksPerSec, ConstantSystem.ticksPerSec * 2 /* 2 seconds */);
            mainListener.setQueueUpdater(queueUpdater);
            getLogger().info("Success connect to MySQL!");
        } catch (SQLException e) {
            mainListener.setQueueUpdater(null);
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

        mainListener = new MainListener();

        PluginConfig pluginConfig = new PluginConfig(this);
        pluginConfig.addEventClasses(this).setup().loadData();

        getServer().getPluginManager().registerEvents(mainListener, this);
    }

    @Override
    public void onDisable() {
        if (queueUpdater != null) {
            queueUpdater.cancel();
            mainListener.forceAddLeave(); // всех онлайн игроков добавляем в список
            queueUpdater.forceRun(); // при выключении сервера чтоб все запросы выполнить
        }

        if (dbHelper != null) {
            try {
                dbHelper.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        queueUpdater = null;
        dbHelper = null;
    }

    public PluginDatabase getDbHelper() {
        return dbHelper;
    }
}
