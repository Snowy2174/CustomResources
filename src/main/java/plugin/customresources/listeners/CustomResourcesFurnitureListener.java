package plugin.customresources.listeners;

import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import plugin.customresources.controllers.TownMachineManager;

public class CustomResourcesFurnitureListener implements Listener {
    private TownMachineManager machineManager;

    public CustomResourcesFurnitureListener() {
    }

    @EventHandler
    public void onFurnitureInteract(FurnitureInteractEvent event) {
        machineManager.onMachineInteract(event);
    }
}
