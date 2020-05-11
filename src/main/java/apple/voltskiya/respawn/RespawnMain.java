package apple.voltskiya.respawn;

import org.bukkit.plugin.java.JavaPlugin;

public class RespawnMain {
    public static void enable(JavaPlugin plugin) {
        new RespawnListener(plugin);
        System.out.println("[VoltskiyaApple] [RespawnRegion] enabled");
    }
}
