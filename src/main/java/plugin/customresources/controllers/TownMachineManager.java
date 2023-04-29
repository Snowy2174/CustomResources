package plugin.customresources.controllers;

import com.google.gson.reflect.TypeToken;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import plugin.customresources.enums.CustomResourcesMachineState;
import plugin.customresources.objects.Machine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static plugin.customresources.util.HologramUtil.createInfoHologram;
import static plugin.customresources.util.HologramUtil.removeInfoHologram;

public class TownMachineManager {


    private static final String DATA_FOLDER = "plugins/ResourceGeneratorPlugin/data";
    private static final String MACHINES_FILE = "machines.yml";

    private static Map<UUID, Machine> machineMap = new HashMap<>();

    public static void loadMachines() {
        File machinesFile = new File(DATA_FOLDER + "/" + MACHINES_FILE);
        if (machinesFile.exists()) {
            try (Reader reader = new FileReader(machinesFile)) {
                Gson gson = new GsonBuilder().create();
                Map<String, Machine> machineMap = gson.fromJson(reader, new TypeToken<Map<String, Machine>>() {
                }.getType());
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
        File machinesFile = new File(DATA_FOLDER + "/" + MACHINES_FILE);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(machinesFile)) {
            gson.toJson(machineMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void createMachine(Player player, String type, Location location, UUID id) {
        Machine machine = new Machine(id, type, location, 0);
        createInfoHologram(machine);
        machineMap.put(machine.getId(), machine);

        saveMachines();
        player.sendMessage("Machine created with ID " + machine.getId());
    }

    public static void removeMachine(Machine machine) {
        machineMap.remove(machine.getId());
        removeInfoHologram(machine);

        saveMachines();
    }

    public static Machine getMachine(UUID id) {
        return machineMap.get(id);
    }

    public void onMachineInteract(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        CustomFurniture clickedFurniture = event.getFurniture();

        // Check if the clicked entity is part of CustomResources
        if (clickedFurniture.getNamespace().equals("customresources")) {
            // Check if the clicked block is a machine
            Machine machine = TownMachineManager.getMachine(clickedFurniture.getArmorstand().getUniqueId());
            if (machine != null) {
                // TODO: add Logic to modify the hologram
                // TODO: Add logic to check if the machine is in the storing state then generate resources
            }
        }
    }
}
