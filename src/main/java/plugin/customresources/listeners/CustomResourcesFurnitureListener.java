package plugin.customresources.listeners;

import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import plugin.customresources.controllers.TownMachineManager;
import plugin.customresources.util.MachineGuiUtil;

public class CustomResourcesFurnitureListener implements Listener {
    private TownMachineManager machineManager;
    private MachineGuiUtil machineGui;

    public CustomResourcesFurnitureListener() {
    }

    @EventHandler
    public void onFurnitureInteract(FurnitureInteractEvent event) {
        TownMachineManager.onMachineInteract(event);
    }

    @EventHandler
    public void onFurnitureBreak(FurnitureBreakEvent event) {
        TownMachineManager.onMachineDestroy(event);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        System.out.println("Inventory Click!");
        MachineGuiUtil.onInterfaceInteract(event);
    }
}
