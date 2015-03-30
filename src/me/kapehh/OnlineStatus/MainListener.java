package me.kapehh.OnlineStatus;

import me.kapehh.OnlineStatus.queue.QueueItem;
import me.kapehh.OnlineStatus.queue.QueueUpdater;
import me.kapehh.main.pluginmanager.db.PluginDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

/**
 * Created by Karen on 17.03.2015.
 */
public class MainListener implements Listener {
    private QueueUpdater queueUpdater;

    public QueueUpdater getQueueUpdater() {
        return queueUpdater;
    }

    public void setQueueUpdater(QueueUpdater queueUpdater) {
        this.queueUpdater = queueUpdater;
    }

    public void forceAddLeave() {
        if (queueUpdater == null) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            queueUpdater.addInQueue(null, player.getName(), QueueItem.LEAVE);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        /*try {
            dbHelper.prepareQueryUpdate("INSERT INTO players(name,online) VALUES (?,1) ON DUPLICATE KEY UPDATE online = 1", event.getPlayer().getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        if (queueUpdater == null) return;
        queueUpdater.addInQueue(null, event.getPlayer().getName(), QueueItem.JOIN);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        /*try {
            dbHelper.prepareQueryUpdate("UPDATE players SET online = 0 WHERE name = ?", event.getPlayer().getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        if (queueUpdater == null) return;
        queueUpdater.addInQueue(null, event.getPlayer().getName(), QueueItem.LEAVE);
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        /*try {
            dbHelper.prepareQueryUpdate("UPDATE players SET lastworld = ? WHERE name = ?", event.getPlayer().getWorld().getName(), event.getPlayer().getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        if (queueUpdater == null) return;
        queueUpdater.addInQueue(event.getPlayer().getWorld().getName(), event.getPlayer().getName(), QueueItem.NEW_WORLD);
    }
}
