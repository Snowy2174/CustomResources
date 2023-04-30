package plugin.customresources.controllers;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;
import plugin.customresources.enums.CustomResourcesMachineState;
import plugin.customresources.objects.Machine;


import java.io.*;
import java.util.*;

import static com.palmergames.bukkit.towny.TownyMessaging.sendMsg;
import static org.bukkit.Bukkit.getEntity;
import static plugin.customresources.controllers.MachinePlacementController.breakMachine;
import static plugin.customresources.util.MachineHologramUtil.*;
import static plugin.customresources.util.MachineGuiUtil.createMachineInterface;
import static plugin.customresources.util.MachineGuiUtil.openInventory;

public class TownMachineManager {


    private static final String DATA_FOLDER = "plugins/ResourceGeneratorPlugin/data";
    private static final String MACHINES_FILE = "machines.json";

    private static Map<UUID, Machine> machineMap = new HashMap<>();

    public static void loadMachines() {
        File dataFolder = new File(DATA_FOLDER);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File machinesFile = new File(DATA_FOLDER + "/" + MACHINES_FILE + ".yml");
        if (machinesFile.exists()) {
            Yaml yaml = new Yaml();
            try (FileInputStream input = new FileInputStream(machinesFile)) {
                Map<String, Machine> machineMap = yaml.load(input);
                machineMap.values().forEach(machine -> TownMachineManager.machineMap.put(machine.getId(), machine));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveMachines() {
        File dataFolder = new File(DATA_FOLDER);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File machinesFile = new File(DATA_FOLDER + "/" + MACHINES_FILE + ".yml");
        Yaml yaml = new Yaml();
        try (FileWriter writer = new FileWriter(machinesFile)) {
            yaml.dump(machineMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createMachine(String type, UUID id) {
        Machine machine = new Machine(id, type, 0);
        machineMap.put(machine.getId(), machine);

        saveMachines();
        sendMsg("Machine created with ID " + machine.getId());
    }

    public static void removeMachine(Machine machine) {
        machineMap.remove(machine.getId());

        saveMachines();
    }

    public static Machine getMachine(UUID id) {
        return machineMap.get(id);
    }

    public static void machineGenerateResources() {
        machineMap.values().stream()
                .filter(machine -> machine.getState() == CustomResourcesMachineState.Active)
                .forEach(machine -> {
                    machine.setState(CustomResourcesMachineState.Finshed);
                    createReadyHologram(machine, getEntity(machine.getId()).getLocation());
                    // TODO: Add any additional logic here that needs to be performed when setting the machine
                });
        saveMachines();
    }

    public static void onMachineInteract(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        CustomFurniture customFurniture = event.getFurniture();

        if (!Objects.equals(customFurniture.getNamespace(), "customresources")) {
            System.out.println("Not part of CustomResources");
            return;
        }

        Machine machine = TownMachineManager.getMachine(customFurniture.getArmorstand().getUniqueId());
        if (machine == null) {
            System.out.println("No Machine Matching found in Datafile");
            return;
        }

        if (!player.isSneaking()) {
            if (machine.getState() == CustomResourcesMachineState.Finshed) {
                // Give resources and remove from town Meta
                removeHologram(String.valueOf(machine.getId()));
            } else {
                // Create hologram (autoremoved) on right click
                createInfoHologram(machine, customFurniture.getArmorstand().getLocation());
            }
        } else {
            // Create new GUI inventory on shift right click
            createMachineInterface(player, machine);
            openInventory(player);
            // Pass the machine instance for removal/upgrade/repair
        }
    }



    public static void onMachineDestroy(FurnitureBreakEvent event) {
        Player player = event.getPlayer();
        CustomFurniture clickedFurniture = event.getFurniture();

        // Check if the clicked entity is part of CustomResources
        if (clickedFurniture.getNamespace().equals("customresources")) {
            // Check if the clicked block is a machine
            Machine machine = TownMachineManager.getMachine(clickedFurniture.getArmorstand().getUniqueId());
            if (machine != null) {
                // Check if player is in creative mode
                if (!player.isOp()) {
                    event.setCancelled(true);
                    sendMsg(player, "&7[&c&l!&7]&c You cannot break this machine, open the machine Menu.");
                } else {
                    breakMachine(clickedFurniture, machine);
                }
                }
            }
        }
    }
