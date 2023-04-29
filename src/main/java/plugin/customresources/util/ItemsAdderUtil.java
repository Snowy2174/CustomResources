package plugin.customresources.util;

import dev.lone.itemsadder.api.CustomStack;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugin.customresources.CustomResources;
import plugin.customresources.settings.CustomResourcesSettings;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

public class ItemsAdderUtil {

	public static String getMaterialNameForDisplay(String materialName) {
		return CustomStack.getInstance(materialName).getDisplayName();
	}

	public static boolean isValidItem(String materialName) {
		return CustomStack.isInRegistry(materialName);
	}

	public static ItemStack getItemsAdderItemStack(String materialName) {
		return CustomStack.getInstance(materialName).getItemStack();
	}
	

	public static Type getType(String name) {
		return Type.get(name.split(":")[0]);
	}

	public static String getID(String name) {
		return name.split(":")[1];
	}
}
