package plugin.customresources.util;

import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.Callable;

public class MMOItemGetter implements Callable<ItemStack> {
	private final MMOItem mmoItem;
	public MMOItemGetter(MMOItem mmoItem) {
		this.mmoItem = mmoItem;
	}

	@Override
	public ItemStack call() {
		return mmoItem.newBuilder().build();
	}
}
