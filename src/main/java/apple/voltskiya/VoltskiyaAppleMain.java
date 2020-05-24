package apple.voltskiya;

import apple.voltskiya.disabledCrafting.DisabledCraftingMain;
import apple.voltskiya.enchantImmunity.ImmunityMain;
import apple.voltskiya.hostile.HostileMain;
import apple.voltskiya.mobGear.EquipMain;
import apple.voltskiya.noDamage.NoDamageMain;
import apple.voltskiya.noRegen.NoRegenMain;
import apple.voltskiya.powertool.PowerToolMain;
import apple.voltskiya.respawn.RespawnMain;
import apple.voltskiya.rotting.RottingMain;
import org.bukkit.plugin.java.JavaPlugin;

public class VoltskiyaAppleMain extends JavaPlugin {
    @Override
    public void onEnable() {
        NoDamageMain.enable(this);
        NoRegenMain.enable(this);
        RespawnMain.enable(this);
        EquipMain.enable(this);
        PowerToolMain.enable(this);
        ImmunityMain.enable(this);
        RottingMain.enable(this);
        DisabledCraftingMain.enable(this);
//        HostileMain.enable(this);

        System.out.println("[VoltskiyaApple] enabled");
    }
}
