package apple.voltskiya.noRegen;

import org.bukkit.plugin.java.JavaPlugin;

public class NoRegenMain {
    public static void enable(JavaPlugin plugin) {
        new RegenListener(plugin);
        System.out.println("[VoltskiyaApple] [RegenCooldown] enabled");
    }
}
