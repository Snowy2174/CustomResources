package plugin.customresources.commands;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI.CommandType;
import com.palmergames.bukkit.towny.command.BaseCommand;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.AddonCommand;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Translatable;
import com.palmergames.bukkit.towny.object.Translator;
import com.palmergames.bukkit.towny.utils.NameUtil;
import com.palmergames.bukkit.util.ChatTools;
import com.palmergames.util.StringMgmt;

import plugin.customresources.CustomResources;
import plugin.customresources.controllers.TownResourceDiscoveryController;
import plugin.customresources.enums.CustomResourcesPermissionNodes;
import plugin.customresources.metadata.BypassEntries;
import plugin.customresources.util.CustomResourcesMessagingUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TownyAdminResourcesAddon extends BaseCommand implements CommandExecutor, TabCompleter {
	
	public TownyAdminResourcesAddon() {
		AddonCommand townyAdminResourcesCommand = new AddonCommand(CommandType.TOWNYADMIN, "resources", this);
		TownyCommandAddonAPI.addSubCommand(townyAdminResourcesCommand);
	}

	private static final List<String> tabCompletes = Arrays.asList("reload", "reroll_all_resources", "bypass");

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1)
			return NameUtil.filterByStart(tabCompletes, args[0]);
		else if(args.length == 2) {
			switch (args[0].toLowerCase(Locale.ROOT)) {
			case "reroll_all_resources":
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
				case "reroll_all_resources" -> parseReRollCommand(sender, StringMgmt.remFirstArg(args));
				case "bypass" -> bypassExtractionLimitCommand(sender);
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
		sender.sendMessage(ChatTools.formatTitle("/townyadmin resources"));
		sender.sendMessage(ChatTools.formatCommand("Eg", "/ta resources", "reload", translator.of("customresources.admin_help_reload")));
		sender.sendMessage(ChatTools.formatCommand("Eg", "/ta resources", "reroll_all_resources", translator.of("customresources.admin_help_reroll")));
		sender.sendMessage(ChatTools.formatCommand("Eg", "/ta resources", "reroll_all_resources [townname]", translator.of("customresources.admin_help_reroll_one_town")));
		sender.sendMessage(ChatTools.formatCommand("Eg", "/ta resources", "bypass", translator.of("customresources.admin_help_bypass")));
	}

	private void parseReloadCommand(CommandSender sender) {
		if (CustomResources.getPlugin().reloadAll()) {
			CustomResourcesMessagingUtil.sendMsg(sender, Translatable.of("customresources.customresources_reloaded_successfully"));
			return;
		}
		CustomResourcesMessagingUtil.sendErrorMsg(sender, Translatable.of("customresources.customresources_failed_to_reload"));
	}

	private void parseReRollCommand(CommandSender sender, String[] args) throws TownyException {
		if (args.length == 0) {
			Confirmation.runOnAcceptAsync(() -> {
				TownResourceDiscoveryController.reRollAllExistingResources();
				CustomResourcesMessagingUtil.sendGlobalMessage(Translatable.of("customresources.all_resources_rerolled"));
			})
			.setTitle(Translatable.of("customresources.msg_confirm_reroll"))
			.sendTo(sender);
			return;
		}
		
		Town town = getTownOrThrow(args[0]);
		Confirmation.runOnAcceptAsync(() -> {
			TownResourceDiscoveryController.reRollExistingResources(town, false);
			TownyMessaging.sendPrefixedTownMessage(town, Translatable.of("customresources.all_resources_rerolled"));
			CustomResourcesMessagingUtil.sendMsg(sender, Translatable.of("customresources.all_resources_rerolled"));
		})
		.setTitle(Translatable.of("customresources.msg_confirm_reroll_town"))
		.sendTo(sender);
	}

	private void bypassExtractionLimitCommand(CommandSender sender) {
		UUID playerUUID = ((Player) sender).getUniqueId();

		if (BypassEntries.bypassData.contains(playerUUID)) {
			BypassEntries.bypassData.remove(playerUUID);
			CustomResourcesMessagingUtil.sendMsg(sender, Translatable.of("customresources.bypass_off"));
		} else {
			BypassEntries.bypassData.add(playerUUID);
			CustomResourcesMessagingUtil.sendMsg(sender, Translatable.of("customresources.bypass_on"));
		}
	}
}

