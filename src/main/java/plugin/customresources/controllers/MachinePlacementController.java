package plugin.customresources.controllers;

import dev.lone.itemsadder.api.CustomFurniture;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import plugin.customresources.CustomResources;
import plugin.customresources.objects.Machine;

import static plugin.customresources.controllers.TownMachineManager.*;
import static plugin.customresources.settings.CustomResourcesMachineConfig.MACHINES;

public class MachinePlacementController {


    public static boolean isMachinePlaced(Location location) {
        return false;
    }

    public static void placeMachine(Location center, String machineName) {
        // Wrap the spawnPreciseNonSolid call and subsequent operations in a Bukkit runTask method call
        Bukkit.getScheduler().runTask(CustomResources.getPlugin(), () -> {
            Entity machine = CustomFurniture.spawnPreciseNonSolid(machineName, center).getArmorstand();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        Location loc = new Location(machine.getWorld(), center.getBlockX() + i, center.getBlockY() + j, center.getBlockZ() + k);
                        loc.getBlock().setType(Material.BARRIER);
                    }
                }
            }
            createMachine(machineName, machine.getUniqueId());
        });
    }


    public static void breakMachine(CustomFurniture furniture, Machine machine){
        Location center = furniture.getArmorstand().getLocation().add(0, 1,0 );

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    Location loc = new Location(furniture.getArmorstand().getWorld(), center.getBlockX() + i, center.getBlockY() + j, center.getBlockZ() + k);
                    loc.getBlock().setType(Material.AIR);
                }
            }
        }
                furniture.remove(false);
                removeMachine(machine);
            }

    public static boolean isValidMachine(String machineName) {
        return MACHINES.containsKey(machineName);
    }
}
