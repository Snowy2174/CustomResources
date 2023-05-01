package plugin.customresources.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI.CommandType;
import com.palmergames.bukkit.towny.command.BaseCommand;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.*;
import com.palmergames.bukkit.util.ChatTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import plugin.customresources.controllers.TownResourceCollectionController;
import plugin.customresources.metadata.CustomResourcesGovernmentMetaDataController;
import plugin.customresources.util.CustomResourcesMessagingUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NationCollectAddon extends BaseCommand implements TabExecutor {

	public NationCollectAddon() {
		AddonCommand nationCollectCommand = new AddonCommand(CommandType.NATION, "collectresources", this);
		TownyCommandAddonAPI.addSubCommand(nationCollectCommand);
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return Collections.emptyList();
	}

	private void showCustomResourcesHelp(CommandSender sender) {
		Translator translator = Translator.locale(sender);
		sender.sendMessage(ChatTools.formatTitle("/nation collectresources"));
		sender.sendMessage(ChatTools.formatCommand("Eg", "/nation", "collectresources", translator.of("customresources.help_nationcollect")));
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player player)
			parseNationCollectResourcesCommand(player);
		else
			showCustomResourcesHelp(sender);
		return true;
	}

	private void parseNationCollectResourcesCommand(Player player) {
		try {

			checkPermOrThrow(player, "customresources.command.nationcollect");
			parseNationCollectCommand(player);

		} catch (TownyException te) {
			//Expected type of exception (e.g. not enough money)
			CustomResourcesMessagingUtil.sendErrorMsg(player, te.getMessage(player));
		} catch (Exception e) {
			//Unexpected exception
			CustomResourcesMessagingUtil.sendErrorMsg(player, e.getMessage());
		}
	}

	private static void parseNationCollectCommand(Player player) throws TownyException {
		//Ensure player a town member
		Resident resident = TownyAPI.getInstance().getResident(player.getUniqueId());
		if(!resident.hasTown())
			throw new TownyException(Translatable.of("customresources.msg_err_cannot_nationcollect_not_a_town_member"));

		//Ensure player is a nation member
		if(!resident.hasNation())
			throw new TownyException(Translatable.of("customresources.msg_err_cannot_nationcollect_not_a_nation_member"));

		Nation nation = resident.getNationOrNull();

		//Ensure player is actually in the capital.
		Town town = TownyAPI.getInstance().getTown(player.getLocation());
		if(town == null || !(nation.hasTown(town) && town.isCapital()))
			throw new TownyException(Translatable.of("customresources.msg_err_cannot_nationcollect_not_in_capital"));

		//Ensure some resources are available
		Map<String, Integer> availableForCollection = CustomResourcesGovernmentMetaDataController.getAvailableForCollectionAsMap(nation);
		if(availableForCollection.isEmpty())
			throw new TownyException(Translatable.of("customresources.msg_err_cannot_nationcollect_no_resources_available"));

		//Collect resources
		TownResourceCollectionController.collectAvailableNationResources(player, nation, availableForCollection);
	}

}