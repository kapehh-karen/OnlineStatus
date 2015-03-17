package me.kapehh.OnlineStatus;

import me.kapehh.main.pluginmanager.db.PluginDatabase;
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
    private Main main;
    private PluginDatabase dbHelper;

    public MainListener(Main main) {
        this.main = main;
        this.dbHelper = main.getDbHelper();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            dbHelper.prepareQueryUpdate("INSERT INTO players(name,online) VALUES (?,1) ON DUPLICATE KEY UPDATE online = 1", event.getPlayer().getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        try {
            dbHelper.prepareQueryUpdate("UPDATE players SET online = 0 WHERE name = ?", event.getPlayer().getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        try {
            dbHelper.prepareQueryUpdate("UPDATE players SET lastworld = ? WHERE name = ?", event.getPlayer().getWorld().getName(), event.getPlayer().getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
