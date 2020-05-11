package apple.voltskiya.mobGear;

import org.bukkit.plugin.java.JavaPlugin;

public class EquipMain {
    public static void enable(JavaPlugin plugin){
        new EquipListener(plugin);
        System.out.println("[VoltskiyaApple] [EntityEquip] enabled");
    }
}
