package plugin.customresources.controllers;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import plugin.customresources.CustomResources;
import plugin.customresources.objects.Machine;

import java.util.Objects;

import static com.palmergames.bukkit.towny.TownyMessaging.sendMsg;
import static plugin.customresources.interfaces.MachineGui.createMachineInterface;
import static plugin.customresources.interfaces.MachineGui.openInventory;
import static plugin.customresources.interfaces.MachineHologram.createHologram;
import static plugin.customresources.interfaces.MachineHologram.removeHologram;
import static plugin.customresources.settings.CustomResourcesMachineConfig.MACHINES;
import static plugin.customresources.settings.MachineDataHandler.*;

public class TownMachineManager {

    public static boolean isMachinePlacedInChunk(Location location) {
        Chunk chunk = location.getChunk();

        for (Machine machine : machines) {
            if (machine.getLocation().getChunk().equals(chunk)) {
                return true;
            }
        }
        return false;
    }

    public static void placeMachine(Location machineLocation, String machineName) {
        // Wrap the spawnPreciseNonSolid call and subsequent operations in a Bukkit runTask method call
        Bukkit.getScheduler().runTask(CustomResources.getPlugin(), () -> {
            Location location = machineLocation.add(0, 1, 0);
            
            location.getWorld().spawnParticle(Particle.END_ROD, location, 50, 0, 0, 0, 1);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getLocation().distance(location) <= 10) {
                    player.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    player.playSound(location, Sound.BLOCK_ANVIL_USE, 1, 1);
                }
            }
            
            Entity machine = CustomFurniture.spawnPreciseNonSolid(machineName, location).getArmorstand();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        Location loc = new Location(machine.getWorld(), location.getBlockX() + i, location.getBlockY() + j, location.getBlockZ() + k);
                        loc.getBlock().setType(Material.BARRIER);
                    }
                }
            }
            createMachineData(machineName, machine.getUniqueId().toString(), machineLocation);
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

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getLocation().distance(center) <= 10) {
                        player.playSound(center, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        player.playSound(center, Sound.BLOCK_ANVIL_PLACE, 1, 1);
                    }
                }

                // Wrap the remove furniture call and subsequent operations in a Bukkit runTask method call
                Bukkit.getScheduler().runTask(CustomResources.getPlugin(), () -> {
                    center.getWorld().spawnParticle(Particle.FLASH, center, 1, 0, 0, 0, 0.1);
                    center.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, center, 20, 0, 0, 0, 1);
                    CustomFurniture.remove(entity, false);
                    removeMachineData(machine);
                });
            }
        }
    }

    public static boolean isValidMachine(String machineName) {
        return MACHINES.containsKey(machineName);
    }


    public static Machine getMachine(String id) {
        for (Machine machine : machines) {
            if (machine.getId().equalsIgnoreCase(id)) {
                return machine;
            }
        }
        return null;
    }

    public static Machine getMachineByChunk(Location location) {
        Chunk chunk = location.getChunk();
        for (Machine machine : machines) {
            if (machine.getLocation().getChunk().equals(chunk)) {
                return machine;
            }
        }
        return null;
    }

    public static void machineGenerateResources() {
        machines.stream()
                .filter(machine -> machine.getState() == Machine.CustomResourcesMachineState.Active)
                .forEach(machine -> {
                    int maxResources = MACHINES.get(machine.getType()).getTiers().get(machine.getTier()).getResourceStorage();
                    machine.incrementStoredResources(maxResources);
                    createHologram(machine, true);
                    // TODO: Add any additional logic here that needs to be performed when setting the machine
                });
        saveMachines();
    }

    public static void onMachineInteract(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        CustomFurniture customFurniture = event.getFurniture();
        if (!Objects.equals(customFurniture.getNamespace(), "customresources")) {
            return;
        }
        Machine machine = TownMachineManager.getMachine(String.valueOf(customFurniture.getArmorstand().getUniqueId()));
        if (machine == null) {
            System.out.println("No Machine Matching found in Datafile");
            return;
        }

        if (!player.isSneaking()) {
            if (machine.getStoredResourcesInteger() > 0) {
                // Give resources and remove from town Meta
                removeHologram(String.valueOf(machine.getId()));
            } else {
                // Create hologram (autoremoved) on right click
                createHologram(machine, false);
            }
        } else {
            // Create new GUI inventory on shift right click
            createMachineInterface(player, machine);
            // Pass the machine instance for removal/upgrade/repair
        }
    }


    public static void onMachineDestroy(FurnitureBreakEvent event) {
        Player player = event.getPlayer();
        CustomFurniture clickedFurniture = event.getFurniture();

        // Check if the clicked entity is part of CustomResources
        if (clickedFurniture.getNamespace().equals("customresources")) {
            // Check if the clicked block is a machine
            Machine machine = TownMachineManager.getMachine(String.valueOf(clickedFurniture.getArmorstand().getUniqueId()));
            if (machine != null) {
                // Check if player is an operator
                if (!player.isOp()) {
                    event.setCancelled(true);
                    sendMsg(player, "&7[&c&l!&7]&c You cannot break this machine, open the machine Menu.");
                } else {
                    breakMachine(machine);
                }
            }
        }
    }
}