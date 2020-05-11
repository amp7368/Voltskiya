package apple.voltskiya.noDamage;

import org.bukkit.plugin.java.JavaPlugin;

public class NoDamageMain {

    public static void enable(JavaPlugin plugin) {
        new DamageListener(plugin);
        System.out.println("[VoltskiyaApple] [NoDamageCooldown] enabled");
    }
}
