package plugin.customresources.controllers;

import com.google.gson.reflect.TypeToken;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Translatable;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import plugin.customresources.enums.CustomResourcesMachineState;
import plugin.customresources.enums.CustomResourcesPermissionNodes;
import plugin.customresources.metadata.CustomResourcesGovernmentMetaDataController;
import plugin.customresources.objects.Machine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import plugin.customresources.settings.CustomResourcesSettings;
import plugin.customresources.util.CustomResourcesMessagingUtil;
import plugin.customresources.util.MachineGuiUtil;


import java.io.*;
import java.util.*;

import static com.palmergames.bukkit.towny.TownyMessaging.sendMsg;
import static com.palmergames.bukkit.towny.command.BaseCommand.checkPermOrThrow;
import static org.bukkit.Bukkit.getEntity;
import static plugin.customresources.controllers.MachinePlacementController.breakMachine;
import static plugin.customresources.util.HologramUtil.*;
import static plugin.customresources.util.MachineGuiUtil.createMachineInterface;
import static plugin.customresources.util.MachineGuiUtil.openInventory;

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

    public void onMachineInteract(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        CustomFurniture clickedFurniture = event.getFurniture();

        // Check if the clicked entity is part of CustomResources
        if (clickedFurniture.getNamespace().equals("customresources")) {
            // Check if the clicked block is a machine
            Machine machine = TownMachineManager.getMachine(clickedFurniture.getArmorstand().getUniqueId());
            if (machine != null) {
                if (!player.isSneaking()) {

                    if (machine.getState() == CustomResourcesMachineState.Finshed) {
                        // TODO: Add logic to check if the machine is in the storing state then give resources
                        removeHologram(String.valueOf(machine.getId()));

                    } else {
                        //Create hologram
                        createInfoHologram(machine, clickedFurniture.getArmorstand().getLocation());
                        // TODO: add Logic to modify the hologram

                    }
                } else {
                    // Create new GUI inventory
                    createMachineInterface(player, machine);
                    openInventory(player);
                    // Pass the machine instance for removal/upgrade/repair

                }
            }
        }
    }


    public void onMachineDestroy(FurnitureBreakEvent event) {
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
