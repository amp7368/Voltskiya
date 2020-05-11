package apple.voltskiya.hostile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class HostileListener implements Listener {
    private JavaPlugin plugin;

    public HostileListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        System.out.println("interact triggered");
        @NotNull List<Entity> nearbyEntities = event.getPlayer().getNearbyEntities(5, 5, 5);
        for (Entity entity : nearbyEntities) {
            if (entity.getType() == EntityType.COW) {

                HostileTarget.target((Mob) entity, event.getPlayer(), plugin);
            }
        }
    }
}
