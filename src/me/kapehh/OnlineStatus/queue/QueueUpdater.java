package me.kapehh.OnlineStatus.queue;

import me.kapehh.OnlineStatus.Main;
import me.kapehh.main.pluginmanager.db.PluginDatabase;
import me.kapehh.main.pluginmanager.db.PluginDatabaseInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karen on 30.03.2015.
 */
public class QueueUpdater extends BukkitRunnable {
    private final Object syncLock = new Object();
    private PluginDatabase dbHelper;
    private PluginDatabaseInfo dbInfo;
    private List<QueueItem> queueItems = new ArrayList<QueueItem>();

    public QueueUpdater(Main main) {
        dbHelper = main.getDbHelper();
        dbInfo = main.getDbInfo();
    }

    public void addInQueue(String worldName, String playerName, int type) {
        synchronized (syncLock) {
            queueItems.add(new QueueItem(playerName, worldName, type));
        }
    }

    public void forceRun() {
        run();
    }

    @Override
    public void run() {
        synchronized (syncLock) {
            for (QueueItem item : queueItems) {
                switch (item.getTypeOfRequest()) {
                    case QueueItem.JOIN:
                        try {
                            dbHelper.prepareQueryUpdate("INSERT INTO " + dbInfo.getTable() + "(name,online) VALUES (?,1) ON DUPLICATE KEY UPDATE online = 1", item.getPlayerName());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case QueueItem.LEAVE:
                        try {
                            dbHelper.prepareQueryUpdate("UPDATE " + dbInfo.getTable() + " SET online = 0 WHERE name = ?", item.getPlayerName());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case QueueItem.NEW_WORLD:
                        try {
                            dbHelper.prepareQueryUpdate("UPDATE " + dbInfo.getTable() + " SET lastworld = ? WHERE name = ?", item.getWorldName(), item.getPlayerName());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
            queueItems.clear();
        }
    }
}
