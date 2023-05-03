package plugin.customresources.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;
import plugin.customresources.CustomResources;
import plugin.customresources.enums.CustomResourcesMachineState;
import plugin.customresources.objects.Machine;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import static com.palmergames.bukkit.towny.TownyMessaging.sendMsg;
import static org.bukkit.Bukkit.getEntity;
import static plugin.customresources.CustomResources.severe;
import static plugin.customresources.controllers.MachinePlacementController.breakMachine;
import static plugin.customresources.controllers.TownResourceDiscoveryController.removeResource;
import static plugin.customresources.util.MachineGuiUtil.createMachineInterface;
import static plugin.customresources.util.MachineGuiUtil.openInventory;
import static plugin.customresources.util.MachineHologramUtil.createHologram;
import static plugin.customresources.util.MachineHologramUtil.removeHologram;

public class TownMachineManager {


    private static final String DATA_FOLDER = "plugins/CustomResources/data";
    private static final String MACHINES_FILE = "data.json";

    public static Map<UUID, Machine> machineMap = new HashMap<>();

    public static void loadMachines() {

        Gson gson = new Gson();
        File file = new File(CustomResources.getPlugin().getDataFolder().getAbsolutePath() + "/machines.json");
        if (file.exists()){
            try {
                Reader reader = new FileReader(file);
               // Machine[] n = gson.fromJson(reader, Machine[].class);
                //for (Machine machine : n) {
                //    machineMap.put(machine.getId(), machine);
              //  }
                System.out.println("Machines loaded.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public static void saveMachines() {
        Gson gson = new Gson();
        System.out.println(CustomResources.getPlugin().getDataFolder().getAbsolutePath());
        File file = new File(CustomResources.getPlugin().getDataFolder().getAbsolutePath() + "/machines.json");
        file.getParentFile().mkdir();
        try {
            file.createNewFile();
            Writer writer = new FileWriter(file, false);
            //Machine[] machines = machineMap.values().toArray(new Machine[0]);
            int[] machines = { 1 };
            gson.toJson(machines, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public static void createMachineData(String type, UUID id, Location center) {
        Machine machine = new Machine(id, type, 0, center);
        machineMap.put(machine.getId(), machine);

        saveMachines();
        sendMsg("Machine created with ID " + machine.getId());
    }

    public static void removeMachineData(Machine machine) {
        try {
            machineMap.remove(machine.getId());
            removeResource(machine);
            removeHologram(String.valueOf(machine.getId()));
        } catch (TownyException e) {
            throw new RuntimeException(e);
        }

        saveMachines();
    }

    public static Machine getMachine(UUID id) {
        return machineMap.get(id);
    }

    public static Machine getMachineByChunk(Location location) {
        Chunk chunk = location.getChunk();
        for (Machine machine : machineMap.values()) {
            if (machine.getLocation().getChunk().equals(chunk)) {
                return machine;
            }
        }
        return null;
    }

    public static void machineGenerateResources() {
        machineMap.values().stream()
                .filter(machine -> machine.getState() == CustomResourcesMachineState.Active)
                .forEach(machine -> {
                    machine.setState(CustomResourcesMachineState.Finshed);
                    createHologram(machine, true);
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
                createHologram(machine, false);
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
                    breakMachine(machine);
                }
            }
        }
    }
}