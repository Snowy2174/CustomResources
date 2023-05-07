package plugin.customresources.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import org.bukkit.Location;
import plugin.customresources.CustomResources;
import plugin.customresources.objects.Machine;
import plugin.customresources.util.JsonLocationUtil;
import plugin.customresources.util.JsonSerializerUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import static com.palmergames.bukkit.towny.TownyMessaging.sendMsg;
import static plugin.customresources.controllers.TownResourceDiscoveryController.discoverNewResource;
import static plugin.customresources.controllers.TownResourceDiscoveryController.removeResource;
import static plugin.customresources.interfaces.MachineHologram.removeHologram;

public class MachineDataHandler {

    public static ArrayList<Machine> machines = new ArrayList<Machine>();
    private static final File dataFile = new File(CustomResources.getPlugin().getDataFolder().getAbsolutePath() + "/data.json");

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Machine.CustomResourcesMachineState.class, new JsonSerializerUtil())
                .registerTypeAdapter(Location.class, new JsonLocationUtil())
                .setPrettyPrinting()
                .create();
    }

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

    public static void reloadMachines() {
        saveMachines();
        loadMachines();
    }

    public static void createMachineData(String type, String id, Location center) {
        Machine machine = new Machine(id, type, 0, center);
        machines.add(machine);
        try {
            discoverNewResource(machine);
        } catch (TownyException e) {
            throw new RuntimeException(e);
        }

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
}
