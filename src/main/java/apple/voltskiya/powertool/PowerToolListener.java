package apple.voltskiya.powertool;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class PowerToolListener implements Listener {
    private JavaPlugin plugin;

    private Map<UUID, Long> cooldown = new HashMap<>();
    private static final int millisCooldown = 200;

    public PowerToolListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Action action = e.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            ItemMeta mainMeta = mainHand.getItemMeta();
            if (mainMeta != null) {
                if (cooldown.containsKey(player.getUniqueId()) && millisCooldown >= System.currentTimeMillis() - cooldown.get(player.getUniqueId()))
                    return;
                cooldown.put(player.getUniqueId(), System.currentTimeMillis());
                int i = 0;
                NamespacedKey key = new NamespacedKey(plugin, TagsNavigate.POWER_TOOL_TAG + i);
                String comamndToExecute = mainMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                while (comamndToExecute != null) {
                    player.performCommand(comamndToExecute);
                    key = new NamespacedKey(plugin, TagsNavigate.POWER_TOOL_TAG + ++i);
                    comamndToExecute = mainMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                }
            }
        }
    }
}
