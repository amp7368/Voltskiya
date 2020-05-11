package apple.voltskiya.disabledCrafting;

import org.bukkit.plugin.java.JavaPlugin;

public class DisabledCraftingMain {
    public static void enable(JavaPlugin plugin) {
        new DisabledCraftingListener(plugin);
        System.out.println("[VoltskiyaApple] [DisabledCrafting] enabled");

    }
}
