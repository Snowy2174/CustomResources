package plugin.customresources.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Translatable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugin.customresources.controllers.MachinePlacementController;
import plugin.customresources.controllers.TownMachineManager;
import plugin.customresources.controllers.TownResourceDiscoveryController;
import plugin.customresources.enums.CustomResourcesPermissionNodes;
import plugin.customresources.metadata.CustomResourcesGovernmentMetaDataController;
import plugin.customresources.settings.CustomResourcesSettings;
import plugin.customresources.util.CustomResourcesMessagingUtil;

import java.util.List;
import java.util.UUID;

import static com.palmergames.bukkit.towny.command.BaseCommand.checkPermOrThrow;
import static plugin.customresources.controllers.MachinePlacementController.placeMachine;

public class MachineAddon implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            CustomResourcesMessagingUtil.sendErrorMsg(sender, "Only players can use this command.");
            return true;
        }

        String targetPlayerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

        if (args.length < 2) {
            CustomResourcesMessagingUtil.sendErrorMsg(targetPlayer, "Usage: /placeMachine <playerName> <machineName> [x] [y] [z] [yaw]");
            return false;
        }

        if (targetPlayer == null) {
            CustomResourcesMessagingUtil.sendErrorMsg(targetPlayer, "The specified player is not online or does not exist.");
            return false;
        }

        String machineName = args[1];
        if (!MachinePlacementController.isValidMachine(machineName)) {
            CustomResourcesMessagingUtil.sendErrorMsg(targetPlayer, "The specified machine name is not valid.");
            return false;
        }

        double x = targetPlayer.getLocation().getX();
        double y = targetPlayer.getLocation().getY();
        double z = targetPlayer.getLocation().getZ();
        float yaw = targetPlayer.getLocation().getYaw();
        if (args.length > 2) {
            try {
                x = Double.parseDouble(args[2]);
                y = Double.parseDouble(args[3]);
                z = Double.parseDouble(args[4]);
                yaw = Float.parseFloat(args[5]);

            } catch (NumberFormatException e) {
                CustomResourcesMessagingUtil.sendErrorMsg(targetPlayer, "Invalid coordinates or yaw specified.");
                return false;
            }
        }

        Location location = new Location(targetPlayer.getWorld(), x, y, z, yaw, 0);

        if (MachinePlacementController.isMachinePlaced(location)) {
        CustomResourcesMessagingUtil.sendErrorMsg(targetPlayer, "A machine is already placed at this location.");
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

        //Check if there are resources left to discover at the town
        List<String> discoveredResources = CustomResourcesGovernmentMetaDataController.getDiscoveredAsList(town);
        List<Integer> costPerResourceLevel = CustomResourcesSettings.getSurveyCostsPerResourceLevel();

        List<Integer> requiredNumTownblocksPerResourceLevel = CustomResourcesSettings.getSurveyNumTownblocksRequirementsPerResourceLevel();

        if(discoveredResources.size() >= costPerResourceLevel.size())
            throw new TownyException(Translatable.of("customresources.msg_err_survey_all_resources_already_discovered"));
        if(discoveredResources.size() >= requiredNumTownblocksPerResourceLevel.size())
            throw new TownyException(Translatable.of("customresources.msg_err_survey_all_resources_already_discovered"));

        //Check if the town has enough townblocks
        int indexOfNextResourceLevel = discoveredResources.size();
        int requiredNumTownblocks = requiredNumTownblocksPerResourceLevel.get(indexOfNextResourceLevel);
        int currentNumTownblocks = town.getTownBlocks().size();

        if(currentNumTownblocks < requiredNumTownblocks)
            throw new TownyException(Translatable.of("customresources.msg_err_survey_not_enough_townblocks",
                    requiredNumTownblocks, currentNumTownblocks));

        //Get survey level & cost
        int surveyLevel = indexOfNextResourceLevel+1;
        double surveyCost = costPerResourceLevel.get(indexOfNextResourceLevel);

        //Send confirmation request message
        String surveyCostFormatted = "0";
        if(TownyEconomyHandler.isActive())
            surveyCostFormatted = TownyEconomyHandler.getFormattedBalance(surveyCost);

        CustomResourcesMessagingUtil.sendMsg(player, Translatable.of("customresources.msg_confirm_survey", town.getName(), surveyLevel, surveyCostFormatted));

        //Send warning message if town level is too low
        int requiredTownLevel = CustomResourcesSettings.getProductionTownLevelRequirementPerResourceLevel().get(indexOfNextResourceLevel);
        int actualTownLevel = town.getLevel();
        if(actualTownLevel < requiredTownLevel) {
            CustomResourcesMessagingUtil.sendMsg(player, Translatable.of("customresources.msg_confirm_survey_town_level_warning", requiredTownLevel, actualTownLevel));
        }
        Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
        Confirmation.runOnAcceptAsync(() -> {
                    try {
                        MachinePlacementController.placeMachine(location, machineType);
                        TownResourceDiscoveryController.discoverNewResource(resident, town, machineType, surveyLevel, surveyCost, discoveredResources);
                    } catch (TownyException te) {
                        CustomResourcesMessagingUtil.sendErrorMsg(player, te.getMessage(player));
                    }
                })
                .sendTo(player);
    }


}