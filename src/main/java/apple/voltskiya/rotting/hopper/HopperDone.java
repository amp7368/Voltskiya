package apple.voltskiya.rotting.hopper;

import apple.voltskiya.rotting.IsRottable;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

import java.util.ArrayList;
import java.util.List;

public class HopperDone {
    public boolean hopperDone;
    public List<InventoryMoveItemEvent> firstWaveCheckEvents;
    public List<InventoryMoveItemEvent> secondWaveMergeEvents;


    public HopperDone(InventoryMoveItemEvent event) {
        hopperDone = false;
        this.firstWaveCheckEvents = new ArrayList<>();
        this.firstWaveCheckEvents.add(event);
        this.secondWaveMergeEvents = new ArrayList<>();
        this.secondWaveMergeEvents.add(event);
    }

    public void add(InventoryMoveItemEvent event) {
        hopperDone = false;
        this.firstWaveCheckEvents.add(event);
        if (IsRottable.isRottable(event.getItem().getType()))
            this.secondWaveMergeEvents.add(event);
    }

    public void clear() {
        firstWaveCheckEvents = new ArrayList<>();
        secondWaveMergeEvents = new ArrayList<>();
    }
    public void clearFirst() {
        firstWaveCheckEvents = new ArrayList<>();
    }
}
