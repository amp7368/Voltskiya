package apple.voltskiya.rotting;

import apple.voltskiya.ymlNavigate.YmlNavigateCommands;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class CoolerCreateCommand implements CommandExecutor {

    public CoolerCreateCommand(JavaPlugin plugin) {
        @Nullable PluginCommand command = plugin.getCommand(YmlNavigateCommands.CREATE_COOLER);
        if (command == null) {
            System.err.println("Could not get command " + YmlNavigateCommands.CREATE_COOLER);
            return;
        }
        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            ItemStack cooler = new ItemStack(Material.CHEST);
            ItemMeta im = cooler.getItemMeta();
            if (im == null) {
                return false;
            }
            im.getPersistentDataContainer().set(RottingMain.cooler, PersistentDataType.INTEGER, 1);
            ((Player) commandSender).getInventory().addItem(cooler);
            cooler.setItemMeta(im);
            Integer coolerInt = cooler.getItemMeta().getPersistentDataContainer().get(RottingMain.cooler, PersistentDataType.INTEGER);
            commandSender.sendMessage(ChatColor.AQUA + "You now have a cooler.");
        }
        return true;
    }
}