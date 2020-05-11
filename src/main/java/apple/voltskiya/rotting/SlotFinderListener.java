package apple.voltskiya.rotting;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

class SlotFinderListener implements Listener {
    public SlotFinderListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void slotFinder(InventoryClickEvent event) {
        System.out.println(event.getSlot());
    }
}
