package plugin.customresources.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.command.BaseCommand;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.*;
import com.palmergames.bukkit.towny.utils.NameUtil;
import com.palmergames.bukkit.util.ChatTools;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import plugin.customresources.controllers.TownResourceDiscoveryController;
import plugin.customresources.enums.CustomResourcesPermissionNodes;
import plugin.customresources.metadata.CustomResourcesGovernmentMetaDataController;
import plugin.customresources.objects.Machine;
import plugin.customresources.settings.CustomResourcesSettings;
import plugin.customresources.util.CustomResourcesMessagingUtil;
import static plugin.customresources.settings.CustomResourcesMachineConfig.MACHINES;
import plugin.customresources.objects.MachineConfig;

import java.util.*;

import static plugin.customresources.controllers.TownMachineManager.*;
import static plugin.customresources.settings.CustomResourcesMachineConfig.*;

public class TownMachineAddon extends BaseCommand implements TabExecutor {

    public TownMachineAddon(){
        AddonCommand townMachinesCommand = new AddonCommand(TownyCommandAddonAPI.CommandType.TOWN, "machines", this);
        TownyCommandAddonAPI.addSubCommand(townMachinesCommand);
    }

    private static final List<String> customResourcesTabCompletes = Arrays.asList("construct", "upgrade", "repair", "upgrademachinerylevel", "destroy");

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1)
            return NameUtil.filterByStart(customResourcesTabCompletes, args[0]);
        else if (args.length == 2)
            return NameUtil.filterByStart(getAllMachineNames(), args[0]);
        else
            return Collections.emptyList();
    }

    private void showTownMachinesHelp(CommandSender sender) {
        Translator translator = Translator.locale(sender);
        sender.sendMessage(ChatTools.formatTitle("/town machines"));
        sender.sendMessage(ChatTools.formatCommand("Eg", "/t machines", "construct", translator.of("customresources.help_survey")));
        sender.sendMessage(ChatTools.formatCommand("Eg", "/t machines", "upgrade", translator.of("customresources.help_towncollect")));
        sender.sendMessage(ChatTools.formatCommand("Eg", "/t machines", "destroy", translator.of("customresources.help_towncollect")));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player player && args.length > 0)
            parseTownMachinesCommand(player, args);
        else
            showTownMachinesHelp(sender);
        return true;
    }

    private void parseTownMachinesCommand(Player player, String[] args) {
        try {

            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "construct" -> parseMachineConstructCommand(player, args); // todo: if players get access to this command, implement tab completion for buildings
                case "upgrade" -> parseMachineUpgradeCommand(player);
                case "repair" -> parseMachineRepair(player);
                case "destroy" -> parseMachineDestroyCommand(player); // todo: if players get access to this command, implement tab completion for buildings
                case "upgrademachinerylevel" -> parseMachineryLevelUpgrade(player); // todo: replace with better title or move command somewhere else
                default -> showTownMachinesHelp(player);
            }

        } catch (TownyException te) {
            //Expected type of exception (e.g. not enough money)
            CustomResourcesMessagingUtil.sendErrorMsg(player, te.getMessage(player));
        } catch (Exception e) {
            //Unexpected exception
            CustomResourcesMessagingUtil.sendErrorMsg(player, e.getMessage());
        }
    }


    public static void parseMachineConstructCommand(Player player, String[] args) throws TownyException {
        checkPermOrThrow(player, CustomResourcesPermissionNodes.CUSTOM_RESOURCES_SURVEY.getNode());

//        Player targetPlayer = Bukkit.getPlayer(args[1]);
//       if (targetPlayer == null) {
//       CustomResourcesMessagingUtil.sendErrorMsg(player, "The specified player is not online or does not exist.");
//            return;
//        }

        String machineType = args[1];
        if (!isValidMachine(machineType)) {
            CustomResourcesMessagingUtil.sendErrorMsg(player, "The specified machine name is not valid.");
            return;
        }

        Location location = player.getLocation();
        if (args.length > 2) {
            try {
                double x = Double.parseDouble(args[2]);
                double y = Double.parseDouble(args[3]);
                double z = Double.parseDouble(args[4]);
                float yaw = Float.parseFloat(args[5]);
                location = new Location(location.getWorld(), x, y, z, yaw, 0);
            } catch (NumberFormatException e) {
                CustomResourcesMessagingUtil.sendErrorMsg(player, "Invalid coordinates or yaw specified.");
                return;
            }
        }

        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        //Check if there is a town here
        if(town == null)
            throw new TownyException(Translatable.of("customresources.msg_err_no_town"));

        if (!town.hasResident(player))
            throw new TownyException(Translatable.of("customresources.not_your_town"));

        if(isMachinePlacedInChunk(location))
            throw new TownyException(Translatable.of("customresources.msg_err_already_placed_chunk"));

        //Check location to see if it's on a chunk border
        if(!locationChunkChecker(location))
            throw new TownyException(Translatable.of("customresources.msg_err_not_same_chunk"));

        //Check if town meets the level requirement of the machine
        MachineConfig config = MACHINES.get(machineType);
        Integer townMachineLevel = CustomResourcesGovernmentMetaDataController.getTownMachineryLevel(town);

        Location finalLocation = location;

        if (config.getTownLevel() != townMachineLevel)
            throw new TownyException(Translatable.of("customresources.msg_err_town_machine_level_req_not_met", config.getTownLevel(), townMachineLevel));

        Confirmation.runOnAcceptAsync(() -> {
            placeMachine(finalLocation, machineType);
        }).sendTo(player);
    }

    public static void parseMachineUpgradeCommand(Player player) throws TownyException {

        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        //Check if there is a town here
        if(town == null)
            throw new TownyException(Translatable.of("customresources.msg_err_no_town"));

        if (!town.hasResident(player))
            throw new TownyException(Translatable.of("customresources.not_your_town"));

        if (!isMachinePlacedInChunk(player.getLocation()))
            throw new TownyException(Translatable.of("customresources.no_machine_here"));

        Machine machine = getMachineByChunk(player.getLocation());

        CustomResourcesMessagingUtil.sendMsg(player, Translatable.of("customresources.msg_confirm_upgrade", town.getName()));

        Confirmation.runOnAcceptAsync(() -> {
                    machine.upgrade();
                })
                .sendTo(player);
    }

    public static void parseMachineDestroyCommand(Player player) throws TownyException {

        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        //Check if there is a town here
        if(town == null)
            throw new TownyException(Translatable.of("customresources.msg_err_no_town"));

        if (!town.hasResident(player))
            throw new TownyException(Translatable.of("customresources.not_your_town"));

        if (!isMachinePlacedInChunk(player.getLocation()))
            throw new TownyException(Translatable.of("customresources.no_machine_here"));

        Machine machine = getMachineByChunk(player.getLocation());

        CustomResourcesMessagingUtil.sendMsg(player, Translatable.of("customresources.msg_confirm_destroy", town.getName()));

        Confirmation.runOnAcceptAsync(() -> {
                    breakMachine(machine);
                })
                .sendTo(player);

    }

    public static void parseMachineRepair(Player player) throws TownyException {

        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        //Check if there is a town here
        if(town == null)
            throw new TownyException(Translatable.of("customresources.msg_err_no_town"));

        if (!town.hasResident(player))
            throw new TownyException(Translatable.of("customresources.not_your_town"));

        if (!isMachinePlacedInChunk(player.getLocation()))
            throw new TownyException(Translatable.of("customresources.no_machine_here"));

        Machine machine = getMachineByChunk(player.getLocation());

        CustomResourcesMessagingUtil.sendMsg(player, Translatable.of("customresources.msg_confirm_repair", town.getName()));

        Confirmation.runOnAcceptAsync(() -> {
            machine.repair();
        })
                .sendTo(player);
    }

    public static void parseMachineryLevelUpgrade(Player player) throws TownyException {
        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        if (town == null)
            throw new TownyException(Translatable.of("customresources.msg_err_no_town"));

        if (!town.hasResident(player))
            throw new TownyException(Translatable.of("customresources.not_your_town"));

        CustomResourcesMessagingUtil.sendMsg(player, Translatable.of("commandexecuted")); // debug
        CustomResourcesGovernmentMetaDataController.calculateMachineryLevelUpgradeCost(town);
    }

    public static boolean locationChunkChecker(Location location) {
        Chunk chunk = location.getChunk();

        int x = location.getBlockX();
        int z = location.getBlockZ();
        int y = location.getBlockY();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue; // Skip the center block
                }

                Location neighbor = new Location(location.getWorld(), x + dx, y, z + dz);

                if (!neighbor.getChunk().equals(chunk)) {
                    return false;
                }
            }
        }
        return true;
    }

}
