package plugin.customresources.util;

import dev.lone.itemsadder.api.CustomStack;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.inventory.ItemStack;

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
