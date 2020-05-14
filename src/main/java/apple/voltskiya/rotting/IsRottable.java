package apple.voltskiya.rotting;

import org.bukkit.Material;

import java.util.HashSet;

public class IsRottable {
    private static HashSet<Material> rottables = new HashSet<>();
    private static HashSet<Material> nonRottables = new HashSet<>();

    public static void initialize() {
        rottables.add(Material.KELP);
        rottables.add(Material.BROWN_MUSHROOM);
        rottables.add(Material.RED_MUSHROOM);
        rottables.add(Material.BROWN_MUSHROOM_BLOCK);
        rottables.add(Material.RED_MUSHROOM_BLOCK);
        rottables.add(Material.WHEAT);
        rottables.add(Material.CAKE);
        rottables.add(Material.COCOA_BEANS);
        rottables.add(Material.SUGAR_CANE);

        nonRottables.add(Material.SPIDER_EYE);
        nonRottables.add(Material.ROTTEN_FLESH);
        nonRottables.add(Material.GOLDEN_APPLE);
    }

    public static boolean isRottable(Material type) {
        return !nonRottables.contains(type) && (type.isEdible() || rottables.contains(type));
    }
}
