package apple.voltskiya.disabledCrafting;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

public class DisabledCraftingListener implements Listener {

    private HashSet<Material> disabled;

    public DisabledCraftingListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        disabled = new HashSet<>();
        disabled.add(Material.WHEAT);
        disabled.add(Material.DRIED_KELP);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) {
            return;
        }
        if (inventory.getType() == InventoryType.CRAFTING || inventory.getType() == InventoryType.WORKBENCH) {
            if (event.getSlot() == 0) {
                ItemStack item = event.getCurrentItem();
                if (item == null)
                    return;
                // check what the output it is
                if (disabled.contains(item.getType())) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(ChatColor.RED + "That recipe is currently disabled.");
                }
            }
        }
    }

}
