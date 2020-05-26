package apple.voltskiya.rotting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

class RenameListener implements Listener {

    public RenameListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        // check if the event has been cancelled by another plugin
        if (!event.isCancelled()) {
            // see if the event is about an anvil
            if (event.getInventory() instanceof AnvilInventory) {

                InventoryView view = event.getView();
                int rawSlot = event.getRawSlot();
                // compare the raw slot with the inventory view to make sure we are talking about the upper inventory
                if (rawSlot == view.convertSlot(rawSlot)) {
                        /*
                        slot 0 = left item slot
                        slot 1 = right item slot
                        slot 2 = result item slot

                        see if the player clicked in the result item slot of the anvil inventory
                        */
                    if (rawSlot == 2) {
                        ItemStack item = event.getCurrentItem();
                        if (item == null || item.getType() == Material.AIR)
                            // I don't care if you're not renaming anything
                            return;
                        ItemMeta im = item.getItemMeta();
                        if (im == null)
                            // I don't care about this weird item
                            return;
                        im.getPersistentDataContainer().set(RottingMain.vanilla, PersistentDataType.INTEGER, 1);
                        item.setItemMeta(im);
                    }
                }
            }
        }
    }
}