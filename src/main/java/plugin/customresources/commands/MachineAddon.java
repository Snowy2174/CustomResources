package plugin.customresources.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.command.BaseCommand;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Translatable;
import com.palmergames.bukkit.towny.object.metadata.IntegerDataField;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugin.customresources.controllers.MachinePlacementController;
import plugin.customresources.controllers.TownResourceDiscoveryController;
import plugin.customresources.enums.CustomResourcesPermissionNodes;
import plugin.customresources.metadata.CustomResourcesGovernmentMetaDataController;
import plugin.customresources.settings.CustomResourcesSettings;
import plugin.customresources.util.CustomResourcesMessagingUtil;

import java.util.List;

import static plugin.customresources.settings.CustomResourcesMachineConfig.MACHINES;
import plugin.customresources.objects.MachineConfig;

public class MachineAddon extends BaseCommand implements CommandExecutor {

    public MachineAddon(){
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            CustomResourcesMessagingUtil.sendErrorMsg(sender, "Only players can use this command.");
            return true;
        }

        if (args.length < 2) {
            CustomResourcesMessagingUtil.sendErrorMsg(sender, "Usage: /placeMachine <playerName> <machineName> [x] [y] [z] [yaw]");
            return false;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            CustomResourcesMessagingUtil.sendErrorMsg(sender, "The specified player is not online or does not exist.");
            return false;
        }

        String machineName = args[1];
        if (!MachinePlacementController.isValidMachine(machineName)) {
            CustomResourcesMessagingUtil.sendErrorMsg(sender, "The specified machine name is not valid.");
            return false;
        }

        Location location = targetPlayer.getLocation();
        if (args.length > 2) {
            try {
                double x = Double.parseDouble(args[2]);
                double y = Double.parseDouble(args[3]);
                double z = Double.parseDouble(args[4]);
                float yaw = Float.parseFloat(args[5]);
                location = new Location(location.getWorld(), x, y, z, yaw, 0);
            } catch (NumberFormatException e) {
                CustomResourcesMessagingUtil.sendErrorMsg(sender, "Invalid coordinates or yaw specified.");
                return false;
            }
        }

        if (MachinePlacementController.isMachinePlaced(location)) {
            CustomResourcesMessagingUtil.sendErrorMsg(sender, "A machine is already placed at this location.");
            return false;
        }

        try {
            attemptConstruction(targetPlayer, location, machineName);
        } catch (TownyException e) {
            throw new RuntimeException(e);
        }

        CustomResourcesMessagingUtil.sendMsg(targetPlayer, Translatable.of("Machine placed successfully."));
        return true;
    }

    public static void attemptConstruction(Player player, Location location, String machineType) throws TownyException {
        checkPermOrThrow(player, CustomResourcesPermissionNodes.CUSTOM_RESOURCES_SURVEY.getNode());

        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        //Check if there is a town here
        if(town == null)
            throw new TownyException(Translatable.of("customresources.msg_err_no_town"));

        if (!town.hasResident(player))
            throw new TownyException(Translatable.of("customresources.not_your_town"));

        MachineConfig config = MACHINES.get(machineType);
        IntegerDataField idf = (IntegerDataField) town.getMetadata("customresources_townMachineLevel");
        Integer townMachineLevel = idf.getValue();

        //Check if town meets the level requirement of the machine
        if (config.getTownLevel() != townMachineLevel)
            throw new TownyException(Translatable.of("customresources.msg_err_town_machine_level_req_not_met", config.getTownLevel(), townMachineLevel));

        Confirmation.runOnAcceptAsync(() -> {
                MachinePlacementController.placeMachine(location, machineType);
        })
        .sendTo(player);
    }


}
