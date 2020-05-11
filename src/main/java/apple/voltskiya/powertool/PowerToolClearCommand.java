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

class PowerToolClearCommand implements CommandExecutor {
    private JavaPlugin plugin;

    public PowerToolClearCommand(JavaPlugin plugin) {
        @Nullable PluginCommand command = plugin.getCommand(YmlNavigateCommands.POWER_TOOL_CLEAR);
        if (command == null) {
            System.err.println("Could not get command " + YmlNavigateCommands.POWER_TOOL_CLEAR);
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
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "Nothing is in your hand.");
            return false;
        }
        ItemMeta mainMeta = mainHand.getItemMeta();
        if (mainMeta == null) {
            player.sendMessage(ChatColor.RED + "Could not get the item meta of that item.");
            return false;
        }

        int i = 0;
        NamespacedKey key = new NamespacedKey(plugin, TagsNavigate.POWER_TOOL_TAG + i);
        while (mainMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            mainMeta.getPersistentDataContainer().remove(key);
            key = new NamespacedKey(plugin, TagsNavigate.POWER_TOOL_TAG + ++i);
        }
        mainHand.setItemMeta(mainMeta);

        player.sendMessage(ChatColor.GREEN + "This tool is now cleared of any power.");
        return true;
    }
}
