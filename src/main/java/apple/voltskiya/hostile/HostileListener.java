package apple.voltskiya.hostile;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

class HostileListener implements Listener {
    private JavaPlugin plugin;

    public HostileListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event ){
        @NotNull LivingEntity creature = event.getEntity();
        if(creature instanceof Cow){
            event.setCancelled(true);
        }
    }
}
