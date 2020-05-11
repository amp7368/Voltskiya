package apple.voltskiya.powertool;

import apple.voltskiya.ymlNavigate.YmlNavigateCommands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

class PowerToolClearAllCommand implements CommandExecutor {
    private JavaPlugin plugin;

    public PowerToolClearAllCommand(JavaPlugin plugin) {
        @Nullable PluginCommand command = plugin.getCommand(YmlNavigateCommands.POWER_TOOL_CLEAR_ALL);
        if (command == null) {
            System.err.println("Could not get command " + YmlNavigateCommands.POWER_TOOL_CLEAR_ALL);
            return;
        }
        command.setExecutor(this);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = Bukkit.getPlayer(commandSender.getName());
        if (player == null) {
            commandSender.sendMessage("nope. you're not a player.");
            return false;
        }
        for (ItemStack item : player.getInventory().getContents()) {
            // THIS CAN BE NULL
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            ItemMeta mainMeta = item.getItemMeta();
            if (mainMeta == null) {
                continue;
            }
            int i = 0;
            NamespacedKey key = new NamespacedKey(plugin, TagsNavigate.POWER_TOOL_TAG + i);
            while (mainMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                mainMeta.getPersistentDataContainer().remove(key);
                key = new NamespacedKey(plugin, TagsNavigate.POWER_TOOL_TAG + ++i);
            }
            item.setItemMeta(mainMeta);
        }
        player.sendMessage(ChatColor.GREEN + "Your inventory is now cleared of any power.");

        return true;
    }
}
