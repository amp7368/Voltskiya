package apple.voltskiya.rotting;

import org.bukkit.Material;

import java.util.HashSet;

public class IsRottable {
    private static HashSet<Material> rottables = new HashSet<>();
    public static void initialize(){
        rottables.add(Material.KELP);
        rottables.add(Material.BROWN_MUSHROOM);
        rottables.add(Material.RED_MUSHROOM);
        rottables.add(Material.BROWN_MUSHROOM_BLOCK);
        rottables.add(Material.RED_MUSHROOM_BLOCK);
        rottables.add(Material.HAY_BLOCK);
        rottables.add(Material.DRIED_KELP_BLOCK);
        rottables.add(Material.WHEAT);
        rottables.add(Material.CAKE);
        rottables.add(Material.COCOA_BEANS);
        rottables.add(Material.SUGAR_CANE);
    }
    public static boolean isRottable(Material type) {
        return type.isEdible() || rottables.contains(type);
    }
}
