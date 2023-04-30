package plugin.customresources.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI.CommandType;
import com.palmergames.bukkit.towny.command.BaseCommand;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.AddonCommand;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Translatable;
import com.palmergames.bukkit.towny.object.Translator;
import com.palmergames.bukkit.towny.utils.NameUtil;
import com.palmergames.bukkit.util.ChatTools;
import plugin.customresources.controllers.TownResourceCollectionController;
import plugin.customresources.controllers.TownResourceDiscoveryController;
import plugin.customresources.enums.CustomResourcesPermissionNodes;
import plugin.customresources.metadata.CustomResourcesGovernmentMetaDataController;
import plugin.customresources.settings.CustomResourcesSettings;
import plugin.customresources.util.CustomResourcesMessagingUtil;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TownResourcesAddon extends BaseCommand implements TabExecutor {
	
	public TownResourcesAddon() {
		AddonCommand townResourcesCommand = new AddonCommand(CommandType.TOWN, "resources", this);
		TownyCommandAddonAPI.addSubCommand(townResourcesCommand);
	}

	private static final List<String> customResourcesTabCompletes = Arrays.asList("survey", "collect");
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1)
			return NameUtil.filterByStart(customResourcesTabCompletes, args[0]);
		else
			return Collections.emptyList();
	}

	private void showTownResourcesHelp(CommandSender sender) {
		Translator translator = Translator.locale(sender);
		sender.sendMessage(ChatTools.formatTitle("/town resources"));
		sender.sendMessage(ChatTools.formatCommand("Eg", "/t resources", "survey", translator.of("customresources.help_survey")));
		sender.sendMessage(ChatTools.formatCommand("Eg", "/t resources", "collect", translator.of("customresources.help_towncollect")));
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player player && args.length > 0)
			parseTownResourcesCommand(player, args);
		else 
			showTownResourcesHelp(sender);
		return true;
	}

	private void parseTownResourcesCommand(Player player, String[] args) {
		try {

			switch (args[0].toLowerCase(Locale.ROOT)) {
			case "collect" -> parseTownCollectCommand(player);
			default -> showTownResourcesHelp(player);
			}

		} catch (TownyException te) {
			//Expected type of exception (e.g. not enough money)
			CustomResourcesMessagingUtil.sendErrorMsg(player, te.getMessage(player));
		} catch (Exception e) {
			//Unexpected exception
			CustomResourcesMessagingUtil.sendErrorMsg(player, e.getMessage());
		}
	}
	
	private static void parseTownCollectCommand(Player player) throws TownyException {
		checkPermOrThrow(player, CustomResourcesPermissionNodes.TOWNY_RESOURCES_COMMAND_TOWN_COLLECT.getNode());

		//Ensure player a town member
		Resident resident = TownyAPI.getInstance().getResident(player.getUniqueId());
		if(!resident.hasTown()) 
			throw new TownyException(Translatable.of("customresources.msg_err_cannot_towncollect_not_a_town_member"));
		
		//Ensure player is actually in their own town
		Town town = TownyAPI.getInstance().getTown(player.getLocation());
		if(town == null || !town.hasResident(resident))
			throw new TownyException(Translatable.of("customresources.msg_err_cannot_towncollect_not_in_own_town"));
		
		//Ensure some resources are available
		Map<String, Integer> availableForCollection = CustomResourcesGovernmentMetaDataController.getAvailableForCollectionAsMap(town);
		if(availableForCollection.isEmpty())
			throw new TownyException(Translatable.of("customresources.msg_err_cannot_towncollect_no_resources_available"));
		
		//Collect resources
		TownResourceCollectionController.collectAvailableTownResources(player, town, availableForCollection);
	}}