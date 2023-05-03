package plugin.customresources.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI.CommandType;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.command.BaseCommand;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.*;
import com.palmergames.bukkit.towny.utils.NameUtil;
import com.palmergames.bukkit.util.ChatTools;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import plugin.customresources.CustomResources;
import plugin.customresources.controllers.MachinePlacementController;
import plugin.customresources.controllers.TownResourceDiscoveryController;
import plugin.customresources.enums.CustomResourcesPermissionNodes;
import plugin.customresources.metadata.CustomResourcesGovernmentMetaDataController;
import plugin.customresources.objects.Machine;
import plugin.customresources.settings.CustomResourcesSettings;
import plugin.customresources.util.CustomResourcesMessagingUtil;

import java.util.*;

import static plugin.customresources.commands.TownMachineAddon.locationChunkChecker;
import static plugin.customresources.controllers.MachinePlacementController.breakMachine;
import static plugin.customresources.controllers.MachinePlacementController.isMachinePlacedInChunk;
import static plugin.customresources.controllers.TownMachineManager.getMachineByChunk;
import static plugin.customresources.metadata.CustomResourcesGovernmentMetaDataController.setTownMachineryLevel;

public class TownyAdminMachinesAddon extends BaseCommand implements CommandExecutor, TabCompleter {

	public TownyAdminMachinesAddon() {
		AddonCommand townyAdminResourcesCommand = new AddonCommand(CommandType.TOWNYADMIN, "machines", this);
		TownyCommandAddonAPI.addSubCommand(townyAdminResourcesCommand);
	}

	private static final List<String> tabCompletes = Arrays.asList("reload", "buildMachine", "setTownMachineryLevel", "breakMachine");

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1)
			return NameUtil.filterByStart(tabCompletes, args[0]);
		else if(args.length == 2) {
			switch (args[0].toLowerCase(Locale.ROOT)) {
			case "setTownMachineryLevel":
				return getTownyStartingWith(args[1], "t");
			}
		}
		return Collections.emptyList();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (args.length > 0)
			parseAdminCommand(sender, args);
		else
			showHelp(sender);
		return true;
	}

	private void parseAdminCommand(CommandSender sender, String[] args) {
		/*
		 * Parse Command.
		 */
	 	try {
			//This permission check handles all the perms checks
			if (sender instanceof Player)
				checkPermOrThrow(sender, CustomResourcesPermissionNodes.TOWNY_RESOURCES_ADMIN_COMMAND.getNode(args[0]));

			switch (args[0]) {
				case "reload" -> parseReloadCommand(sender);
				case "buildMachine" -> parseBuildMachineCommand(sender, args);
				case "setTownMachineryLevel" -> parseSetTownMachineryLevelCommand(sender, args[1]);
				case "breakMachine" -> parseMachineDestroyCommand((Player) sender);
				/*
				 * Show help if no command found.
				 */
				default -> showHelp(sender);
			}
		} catch (TownyException e) {
			CustomResourcesMessagingUtil.sendErrorMsg(sender, e.getMessage(sender));
		}
	}

	private void showHelp(CommandSender sender) {
		Translator translator = Translator.locale(sender);
		sender.sendMessage(ChatTools.formatTitle("/townyadmin machines"));
		sender.sendMessage(ChatTools.formatCommand("Eg", "/ta machines", "reload", translator.of("customresources.admin_help_reload")));
		sender.sendMessage(ChatTools.formatCommand("Eg", "/ta machines", "buildMachine [machinename] [x] [y] [z] ", translator.of("customresources.admin_help_buildMachine")));
		sender.sendMessage(ChatTools.formatCommand("Eg", "/ta machines", "setTownMachineryLevel [townname] [machinerylevel]", translator.of("customresources.admin_help_setTownMachineryLevel")));
		sender.sendMessage(ChatTools.formatCommand("Eg", "/ta machines", "breakMachine", translator.of("customresources.admin_help_breakMachine")));
	}

	private void parseReloadCommand(CommandSender sender) {
		if (CustomResources.getPlugin().reloadAll()) {
			CustomResourcesMessagingUtil.sendMsg(sender, Translatable.of("customresources.customresources_reloaded_successfully"));
			return;
		}
		CustomResourcesMessagingUtil.sendErrorMsg(sender, Translatable.of("customresources.customresources_failed_to_reload"));
	}

	public static void parseMachineDestroyCommand(CommandSender sender) throws TownyException {
        Player player = (Player) sender;

		Town town = TownyAPI.getInstance().getTown(player.getLocation());
		//Check if there is a town here
		if(town == null)
			throw new TownyException(Translatable.of("customresources.msg_err_no_town"));

		if (!isMachinePlacedInChunk(player.getLocation()))
			throw new TownyException(Translatable.of("customresources.no_machine_here"));

		Machine machine = getMachineByChunk(player.getLocation());

		Confirmation.runOnAcceptAsync(() -> {
					breakMachine(machine);
				})
				.sendTo(player);

	}

	public static void parseSetTownMachineryLevelCommand(CommandSender sender, String level) throws TownyException {
		Player player = (Player) sender;

		Town town = TownyAPI.getInstance().getTown(player.getLocation());
		//Check if there is a town here
		if (town == null)
			throw new TownyException(Translatable.of("customresources.msg_err_no_town"));

		setTownMachineryLevel(town, Integer.parseInt(level));
	}

	public static void parseBuildMachineCommand(CommandSender sender, String[] args) throws TownyException {
		Player player = (Player) sender;

//        Player targetPlayer = Bukkit.getPlayer(args[1]);
//       if (targetPlayer == null) {
//       CustomResourcesMessagingUtil.sendErrorMsg(player, "The specified player is not online or does not exist.");
//            return;
//        }

		String machineType = args[1];
		if (!MachinePlacementController.isValidMachine(machineType)) {
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
				throw new TownyException(Translatable.of("customresources.msg_err_coord_format"));
			}
		}

		Town town = TownyAPI.getInstance().getTown(location);
		//Check if there is a town here
		if(town == null)
			throw new TownyException(Translatable.of("customresources.msg_err_no_town"));

		if(MachinePlacementController.isMachinePlacedInChunk(location))
			throw new TownyException(Translatable.of("customresources.msg_err_already_placed_chunk"));

		//Check location to see if it's on a chunk border
		if(!locationChunkChecker(location))
			throw new TownyException(Translatable.of("customresources.msg_err_not_same_chunk"));

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

		Location finalLocation = location;
		Confirmation.runOnAcceptAsync(() -> {
					try {
						MachinePlacementController.placeMachine(finalLocation, machineType);
						TownResourceDiscoveryController.discoverNewResource(resident, town, machineType, surveyLevel, surveyCost, discoveredResources);
					} catch (TownyException te) {
						CustomResourcesMessagingUtil.sendErrorMsg(player, te.getMessage(player));
					}
				})
				.sendTo(player);
	}
}

