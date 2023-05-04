package plugin.customresources.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import plugin.customresources.CustomResources;
import plugin.customresources.enums.CustomResourcesMachineState;
import plugin.customresources.objects.Machine;
import plugin.customresources.util.JsonLocSerializerUtil;
import plugin.customresources.util.JsonSerializerUtil;

import java.io.*;
import java.util.*;

import static com.palmergames.bukkit.towny.TownyMessaging.sendMsg;
import static plugin.customresources.controllers.MachinePlacementController.breakMachine;
import static plugin.customresources.controllers.TownResourceDiscoveryController.removeResource;
import static plugin.customresources.util.MachineGuiUtil.createMachineInterface;
import static plugin.customresources.util.MachineGuiUtil.openInventory;
import static plugin.customresources.util.MachineHologramUtil.createHologram;
import static plugin.customresources.util.MachineHologramUtil.removeHologram;

public class TownMachineManager {


    public static ArrayList<Machine> machines = new ArrayList<Machine>();
    private static final File dataFile = new File(CustomResources.getPlugin().getDataFolder().getAbsolutePath() + "/data.json");

    public static void loadMachines() {
        if (dataFile.exists()){
            try {
                Reader reader = new FileReader(dataFile);
                  Machine[] n = getGson().fromJson(reader, Machine[].class);
                machines.addAll(Arrays.asList(n));
                System.out.println("Machines loaded.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(CustomResourcesMachineState.class, new JsonSerializerUtil())
                .registerTypeAdapter(Location.class, new JsonLocSerializerUtil())
                .setPrettyPrinting()
                .create();
    }

    public static void saveMachines() {
        dataFile.getParentFile().mkdir();

        try {
            dataFile.createNewFile();
            Writer writer = new FileWriter(dataFile, false);
            getGson().toJson(machines, writer);
            writer.flush();
            writer.close();
            System.out.println("Data saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public static void createMachineData(String type, String id, Location center) {
        Machine machine = new Machine(id, type, 0, center);
        machines.add(machine);

        saveMachines();
        sendMsg("Machine created with ID " + machine.getId());
    }

    public static void removeMachineData(Machine machine) {
        machines.remove(machine);
        try {
            removeResource(machine);
            removeHologram(String.valueOf(machine.getId()));
        } catch (TownyException e) {
            throw new RuntimeException(e);
        }
        saveMachines();
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
                .filter(machine -> machine.getState() == CustomResourcesMachineState.Active)
                .forEach(machine -> {
                    machine.setState(CustomResourcesMachineState.Finished);
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

        Machine machine = TownMachineManager.getMachine(String.valueOf(customFurniture.getArmorstand().getUniqueId()));
        if (machine == null) {
            System.out.println("No Machine Matching found in Datafile");
            return;
        }

        if (!player.isSneaking()) {
            if (machine.getState() == CustomResourcesMachineState.Finished) {
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
            Machine machine = TownMachineManager.getMachine(String.valueOf(clickedFurniture.getArmorstand().getUniqueId()));
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