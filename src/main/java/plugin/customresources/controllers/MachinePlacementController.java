package plugin.customresources.controllers;

import dev.lone.itemsadder.api.CustomFurniture;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import plugin.customresources.CustomResources;
import plugin.customresources.objects.Machine;

import static plugin.customresources.controllers.TownMachineManager.*;
import static plugin.customresources.settings.CustomResourcesMachineConfig.MACHINES;

public class MachinePlacementController {


    public static boolean isMachinePlacedInChunk(Location location) {
        Chunk chunk = location.getChunk();

        for (Machine machine : machines) {
            if (machine.getLocation().getChunk().equals(chunk)) {
                return true;
            }
        }
        return false;
    }

    public static void placeMachine(Location center, String machineName) {
        // Wrap the spawnPreciseNonSolid call and subsequent operations in a Bukkit runTask method call
        Bukkit.getScheduler().runTask(CustomResources.getPlugin(), () -> {
            Location location = center.add(0,0, 0);
            Entity machine = CustomFurniture.spawnPreciseNonSolid(machineName, location).getArmorstand();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        Location loc = new Location(machine.getWorld(), location.getBlockX() + i, location.getBlockY() + j, location.getBlockZ() + k);
                        loc.getBlock().setType(Material.BARRIER);
                    }
                }
            }
            createMachineData(machineName, machine.getUniqueId().toString(), center);
        });
    }


    public static void breakMachine(Machine machine) {
        Location center = machine.getLocation();

        for (Entity entity : center.getChunk().getEntities()) {
            if (entity.getUniqueId().equals(machine.getId())) {

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        for (int k = -1; k <= 1; k++) {
                            Location loc = new Location(entity.getWorld(), center.getBlockX() + i, center.getBlockY() + j, center.getBlockZ() + k);
                            loc.getBlock().setType(Material.AIR);
                        }
                    }
                }
                // Wrap the remove furniture call and subsequent operations in a Bukkit runTask method call
                Bukkit.getScheduler().runTask(CustomResources.getPlugin(), () -> {
                    CustomFurniture.remove(entity, false);
                    removeMachineData(machine);
                });
            }
        }
    }

    public static boolean isValidMachine(String machineName) {
        return MACHINES.containsKey(machineName);
    }
}
