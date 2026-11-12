package me.rexsystems.rexVaults.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class SchedulerUtils {

    private SchedulerUtils() {
    }

    public static void runSync(Plugin plugin, Runnable task) {
        Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> task.run());
    }

    public static void runAtLocation(Plugin plugin, Location location, Runnable task) {
        if (location == null || location.getWorld() == null) {
            runSync(plugin, task);
            return;
        }
        Bukkit.getRegionScheduler().run(plugin, location, scheduledTask -> task.run());
    }

    public static void runForPlayer(Plugin plugin, Player player, Runnable task) {
        if (player == null || task == null) {
            return;
        }
        player.getScheduler().run(plugin, scheduledTask -> task.run(), null);
    }

    public static void runForPlayerLater(Plugin plugin, Player player, Runnable task, long delayTicks) {
        if (player == null || task == null) {
            return;
        }
        long delay = Math.max(1L, delayTicks);
        player.getScheduler().runDelayed(plugin, scheduledTask -> task.run(), null, delay);
    }

    public static void runForEntity(Plugin plugin, Entity entity, Runnable task) {
        if (entity == null || task == null) {
            return;
        }
        entity.getScheduler().run(plugin, scheduledTask -> task.run(), null);
    }
}
