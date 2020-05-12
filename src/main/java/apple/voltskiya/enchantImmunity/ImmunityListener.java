package apple.voltskiya.enchantImmunity;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

class ImmunityListener implements Listener {

    private static final String BANE_TAG = "immune.bane";
    private static final String SMITE_TAG = "immune.smite";
    private static final String BANE_HIT_TAG = "immune.banehit";
    private static final String SMITE_HIT_TAG = "immune.smitehit";
    private static final double MULTIPLIER = 2.5;
    private JavaPlugin plugin;

    public ImmunityListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        @NotNull Set<String> tags = event.getEntity().getScoreboardTags();
        if (tags.contains(BANE_TAG)) {
            if (event.getDamager() instanceof Player) {
                Player player = ((Player) event.getDamager());
                int levelEnchant = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS);
                double extraDamage = levelEnchant * MULTIPLIER;
                if (levelEnchant > 0) {
                    Entity entity = event.getEntity();
                    entity.addScoreboardTag(BANE_HIT_TAG);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,()->entity.removeScoreboardTag(BANE_HIT_TAG),2);
                }
                event.setDamage(Math.max(0, event.getDamage() - extraDamage));
            }
        } else if (tags.contains(SMITE_TAG)) {
            if (event.getDamager() instanceof Player) {
                Player player = ((Player) event.getDamager());
                int levelEnchant = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD);
                double extraDamage = levelEnchant * MULTIPLIER;
                if (levelEnchant > 0) {
                    Entity entity = event.getEntity();
                    entity.addScoreboardTag(SMITE_HIT_TAG);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,()->entity.removeScoreboardTag(SMITE_HIT_TAG),2);
                }
                event.setDamage(Math.max(0, event.getDamage() - extraDamage));
            }
        }
    }
}
