package plugin.customresources.controllers;

import dev.lone.itemsadder.api.CustomFurniture;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import plugin.customresources.objects.Machine;

import static plugin.customresources.controllers.TownMachineManager.*;
import static plugin.customresources.settings.CustomResourcesMachineConfig.MACHINES;

public class MachinePlacementController {


    public static boolean isMachinePlaced(Location location) {
        return false;
    }

    public static void placeMachine(Location centre, String machineName) {
        Entity machine = CustomFurniture.spawnPreciseNonSolid(machineName, centre.subtract(0.0, 1.0, 0.0)).getArmorstand();

        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int yOffset = -1; yOffset <= 1; yOffset++) {
                // Create a new location based on the offset from the center
                Location blockLoc = centre.clone().add(xOffset, yOffset, 0);
                // Place a barrier block at the new location
                blockLoc.getBlock().setType(Material.BARRIER);
            }
        }
        createMachine(machineName, machine.getUniqueId());
    }

    public static void breakMachine(CustomFurniture furniture, Machine machine){
                furniture.remove(false);
                removeMachine(machine);
            }

    public static boolean isValidMachine(String machineName) {
        return MACHINES.containsKey(machineName);
    }
}
