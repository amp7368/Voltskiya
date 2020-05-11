package apple.voltskiya.noRegen;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

class RegenListener implements Listener {
    HashMap<UUID, Long> noRegenPlayers;
    long noRegenTime;

    public RegenListener(JavaPlugin plugin) {
        noRegenPlayers = new HashMap<>();
        File file = new File(String.format("%s%s%s%s%s", plugin.getDataFolder(), File.separator, "noImmediateRegen", File.separator, "config.yml"));
        if (!file.exists()) {
            System.err.println(String.format("%s%s%s%s%s%s", plugin.getDataFolder(), File.separator, "noImmediateRegen", File.separator, "config.yml", " does not exist!"));
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        noRegenTime = config.getLong("noRegenTime");
        if (noRegenTime == 0) {
            System.err.println(String.format("%s%s%s%s%s%s", plugin.getDataFolder(), File.separator, "noImmediateRegen", File.separator, "config.yml:noRegenTime", " does not exist or is set to 0!"));
            return;
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            noRegenPlayers.put(entity.getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED ||
                event.getRegainReason() == EntityRegainHealthEvent.RegainReason.EATING ||
                event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN) {
            Entity entity = event.getEntity();
            if (entity instanceof Player) {
                UUID uid = entity.getUniqueId();
                if (noRegenPlayers.containsKey(uid)) {
                    long lastHit = noRegenPlayers.get(uid);
                    if (System.currentTimeMillis() - lastHit < noRegenTime) {
                        event.setCancelled(true);
                    } else {
                        noRegenPlayers.remove(uid);
                    }
                }
            }
        }
    }
}
